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
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            String fullText = new PDFTextStripper().getText(document);
            return parseFromText(fullText, null);
        }
    }

    public List<Question> parseSplitPdfs(MultipartFile questionsFile, MultipartFile answersFile) throws IOException {
        String questionsText;
        try (PDDocument document = PDDocument.load(questionsFile.getInputStream())) {
            questionsText = new PDFTextStripper().getText(document);
        }

        String answersText = "";
        if (answersFile != null && !answersFile.isEmpty()) {
            try (PDDocument document = PDDocument.load(answersFile.getInputStream())) {
                answersText = new PDFTextStripper().getText(document);
            }
        }

        return parseFromText(questionsText, answersText);
    }

    private List<Question> parseFromText(String questionsText, String answersText) {
        List<Question> questions = new ArrayList<>();
        String[] lines = questionsText.split("\\r?\\n");
        Question currentQuestion = null;
        List<String> currentOptions = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            if (line.matches("(?i)^(Question|Q)[:\\.]?.*") || QUESTION_PATTERN.matcher(line).matches()) {
                if (currentQuestion != null) {
                    currentQuestion.setOptions(new ArrayList<>(currentOptions));
                    questions.add(currentQuestion);
                }
                currentQuestion = new Question();
                currentQuestion.setQuestionText(line);
                currentQuestion.setCorrectOptionIndex(-1);
                currentOptions.clear();
            } else if (line.matches("^\\s*[\\(]?[A-Da-d][\\)\\.]\\s+.*")) {
                currentOptions.add(line);
            } else if (answersText == null && line.matches("(?i).*\\b(Answer|Ans|Correct|Key)[:\\s-]*[A-Da-d].*")) {
                // Inline answer parsing (only if answersText is null)
                if (currentQuestion != null) {
                    currentQuestion.setCorrectOptionIndex(extractAnswerFromLine(line));
                }
            }
        }
        if (currentQuestion != null) {
            currentQuestion.setOptions(new ArrayList<>(currentOptions));
            questions.add(currentQuestion);
        }

        // Apply external answers if provided
        if (answersText != null && !answersText.isEmpty()) {
            List<Integer> answers = extractAnswersFromText(answersText);
            for (int i = 0; i < questions.size() && i < answers.size(); i++) {
                questions.get(i).setCorrectOptionIndex(answers.get(i));
            }
        }

        return questions;
    }

    private int extractAnswerFromLine(String line) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(?i)(Answer|Ans|Correct|Key)[:\\s-]*([\\(]?[A-Da-d][\\)]?)")
                .matcher(line);
        if (m.find()) {
            String captured = m.group(2).replaceAll("[\\(\\)]", "").toUpperCase();
            if (!captured.isEmpty()) {
                return captured.charAt(0) - 'A';
            }
        }
        return -1;
    }

    private List<Integer> extractAnswersFromText(String text) {
        List<Integer> answers = new ArrayList<>();
        // Look for patterns like "1. A", "1) B", "Q1: C" or just a list of "A", "B",
        // "C"
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(?i)(?:^|\\s)(?:Question|Q)?\\s?\\d+[:\\.)\\s-]+([A-D])(?:$|\\s|\\))")
                .matcher(text);
        while (m.find()) {
            answers.add(m.group(1).toUpperCase().charAt(0) - 'A');
        }

        if (answers.isEmpty()) {
            // Fallback: just look for A, B, C, D in order
            java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("(?i)\\b([A-D])\\b").matcher(text);
            while (m2.find()) {
                answers.add(m2.group(1).toUpperCase().charAt(0) - 'A');
            }
        }
        return answers;
    }
}
