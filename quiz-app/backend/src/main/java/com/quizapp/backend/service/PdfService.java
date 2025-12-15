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

                // Question line
                if (line.matches("(?i)^(Question|Q)[:\\.]?.*")
                        || QUESTION_PATTERN.matcher(line).matches()) {

                    if (currentQuestion != null) {
                        if (currentQuestion.getCorrectOptionIndex() == -1) {
                            System.err.println(
                                    "Warning: No answer found for question: "
                                            + currentQuestion.getQuestionText());
                        }
                        currentQuestion.setOptions(new ArrayList<>(currentOptions));
                        questions.add(currentQuestion);
                    }

                    currentQuestion = new Question();
                    currentQuestion.setQuestionText(line);
                    currentQuestion.setCorrectOptionIndex(-1);
                    currentOptions.clear();
                }

                // Option line  Pavan Sidhhujbjukbduovburovbhuorvbhuorhvbuorhuvbo
               else if (line.matches("^\\s*[A-Da-d][\\)\\.]\\s+.+")
        && !line.toLowerCase().startsWith("correct")) {
{

                    currentOptions.add(
                            line.replaceFirst("^[A-Da-d][\\)\\.\\s]+", "").trim()
                    );
                }

                // Correct answer line
                else if (line.toLowerCase().startsWith("correct answer")) {
                    if (currentQuestion != null) {
                        java.util.regex.Matcher matcher = java.util.regex.Pattern
                                .compile("(?i)correct answer\\s*[:\\-]?\\s*([A-D])")
                                .matcher(line);

                        if (matcher.find()) {
                            char answerChar = matcher.group(1).charAt(0);
                            int correctIndex = answerChar - 'A'; // A=0, B=1, C=2, D=3
                            currentQuestion.setCorrectOptionIndex(correctIndex);
                        }
                    }
                }
            }

            // add last question
            if (currentQuestion != null) {
                if (currentQuestion.getCorrectOptionIndex() == -1) {
                    System.err.println(
                            "Warning: No answer found for last question: "
                                    + currentQuestion.getQuestionText());
                }
                currentQuestion.setOptions(new ArrayList<>(currentOptions));
                questions.add(currentQuestion);
            }
        }

        return questions;
    }
}
