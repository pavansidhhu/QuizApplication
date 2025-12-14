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
            String fullText = stripper.getText(document);

            String[] lines = fullText.split("\\r?\\n");
            Question currentQuestion = null;
            List<String> currentOptions = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                // Flexible matching for headings
                if (line.matches("(?i)^(Question|Q)[:\\.]?.*") || QUESTION_PATTERN.matcher(line).matches()) {
                    if (currentQuestion != null) {
                        if (currentQuestion.getCorrectOptionIndex() == -1) {
                            System.err.println(
                                    "Warning: No answer found for question: " + currentQuestion.getQuestionText());
                        }
                        currentQuestion.setOptions(new ArrayList<>(currentOptions));
                        questions.add(currentQuestion);
                    }
                    currentQuestion = new Question();
                    currentQuestion.setQuestionText(line);
                    currentOptions.clear(); // Flexible option matching: A) A. (A) a) a. (a)
                } else if (line.matches("^\\s*[\\(]?[A-Da-d][\\)\\.]\\s+.*")) {
                    currentOptions.add(line); // Flexible answer matching: Answer:, Ans:, Key:, Correct:, with/without
                                              // parens
                } else if (line
                        .matches("(?i).*\\b(Answer|Ans|Correct|Correct Option|Key)[:\\s-]*[\\(]?[A-Da-d][\\)]?.*")) {
                    if (currentQuestion != null) {
                        String answerLine = line.trim();
                        char answerChar = ' ';

                        // Extract the letter using a capturing group
                        java.util.regex.Matcher m = java.util.regex.Pattern
                                .compile("(?i)(Answer|Ans|Correct|Correct Option|Key)[:\\s-]*([\\(]?[A-Da-d][\\)]?)")
                                .matcher(answerLine);
                        while (m.find()) {
                            String captured = m.group(2).replaceAll("[\\(\\)]", ""); // Remove parens
                            if (!captured.isEmpty()) {
                                answerChar = captured.charAt(0);
                            }
                        }

                        if (answerChar != ' ') {
                            int correctIndex = -1;
                            if (answerChar == 'A' || answerChar == 'a')
                                correctIndex = 0;
                            else if (answerChar == 'B' || answerChar == 'b')
                                correctIndex = 1;
                            else if (answerChar == 'C' || answerChar == 'c')
                                correctIndex = 2;
                            else if (answerChar == 'D' || answerChar == 'd')
                                correctIndex = 3;
                            currentQuestion.setCorrectOptionIndex(correctIndex);
                        }
                    }
                }
            }
            if (currentQuestion != null) {
                if (currentQuestion.getCorrectOptionIndex() == -1) {
                    System.err.println(
                            "Warning: No answer found for last question: " + currentQuestion.getQuestionText());
                }
                currentQuestion.setOptions(new ArrayList<>(currentOptions));
                questions.add(currentQuestion);
            }
        }
        return questions;
    }
}
