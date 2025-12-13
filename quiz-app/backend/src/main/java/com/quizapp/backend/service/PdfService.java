package com.quizapp.backend.service;

import com.quizapp.backend.model.Question;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import java.util.regex.Pattern;

@Service
public class PdfService {

    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\d+\\..*");

    public List<Question> parsePdf(MultipartFile file) throws IOException {
        List<Question> questions = new ArrayList<>();
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Simple parsing logic based on "Question" keyword and "Answer:" keyword
            // This is a naive implementation for demonstration.
            // Real-world PDF parsing is much more complex.

            String[] lines = text.split("\\r?\\n");
            Question currentQuestion = null;
            List<String> currentOptions = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (line.startsWith("Question") || QUESTION_PATTERN.matcher(line).matches()) {
                    if (currentQuestion != null) {
                        if (currentQuestion.getCorrectOptionIndex() == -1) {
                            // Try to infer answer if not found explicitly (advanced logic could go here)
                            // For now, valid question must have an answer
                            System.err.println(
                                    "Warning: No answer found for question: " + currentQuestion.getQuestionText());
                        }
                        currentQuestion.setOptions(new ArrayList<>(currentOptions));
                        questions.add(currentQuestion);
                    }
                    currentQuestion = new Question();
                    currentQuestion.setQuestionText(line);
                    currentOptions.clear();
                } else if (line.matches("^[A-D]\\).*")) {
                    currentOptions.add(line);
                } else if (line.matches("(?i)^(Answer|Ans|Correct Option)[:\\s-]*[A-D].*")) {
                    if (currentQuestion != null) {
                        String answer = line.replaceAll("(?i)^(Answer|Ans|Correct Option)[:\\s-]*", "").trim()
                                .substring(0, 1);
                        int correctIndex = -1;
                        if (answer.equalsIgnoreCase("A"))
                            correctIndex = 0;
                        else if (answer.equalsIgnoreCase("B"))
                            correctIndex = 1;
                        else if (answer.equalsIgnoreCase("C"))
                            correctIndex = 2;
                        else if (answer.equalsIgnoreCase("D"))
                            correctIndex = 3;
                        currentQuestion.setCorrectOptionIndex(correctIndex);
                    }
                }
            }
            // Add the last question
            if (currentQuestion != null) {
                currentQuestion.setOptions(new ArrayList<>(currentOptions));
                questions.add(currentQuestion);
            }
        }
        return questions;
    }
}
