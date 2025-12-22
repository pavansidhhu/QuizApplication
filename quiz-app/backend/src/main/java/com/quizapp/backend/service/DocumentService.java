package com.quizapp.backend.service;

import com.quizapp.backend.model.Question;
import com.quizapp.backend.model.QuestionType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentService {

    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\d+\\..*");
    private static final Pattern OPTION_PATTERN = Pattern.compile("^\\s*[\\(]?[A-Da-d][\\)\\.]\\s+.*");
    private static final Pattern ANSWER_PATTERN = Pattern
            .compile("(?i).*(Answer|Ans|Correct|Correct Option|Key)[:\\s-]*[\\(]?[A-Da-d][\\)]?.*");

    public List<Question> parseDocument(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IOException("File name is null");
        }

        if (filename.toLowerCase().endsWith(".pdf")) {
            return parsePdf(file);
        } else if (filename.toLowerCase().endsWith(".docx")) {
            return parseDocx(file);
        } else if (filename.toLowerCase().endsWith(".doc")) {
            throw new IOException("Legacy .doc format is not supported. Please use .docx");
        } else {
            throw new IOException("Unsupported file format. Only PDF and DOCX are supported.");
        }
    }

    private List<Question> parsePdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);
            return parseText(fullText);
        }
    }

    private List<Question> parseDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder fullText = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                fullText.append(paragraph.getText()).append("\n");
            }
            return parseText(fullText.toString());
        }
    }

    private List<Question> parseText(String fullText) {
        List<Question> questions = new ArrayList<>();
        String[] lines = fullText.split("\\r?\\n");
        Question currentQuestion = null;
        List<String> currentOptions = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            // Check if this is a question line
            if (line.matches("(?i)^(Question|Q)[:\\.]?.*") || QUESTION_PATTERN.matcher(line).matches()) {
                // Save previous question if exists
                if (currentQuestion != null) {
                    finalizeQuestion(currentQuestion, currentOptions, questions);
                }

                // Start new question
                currentQuestion = new Question();
                currentQuestion.setQuestionText(line);
                currentOptions.clear();

                // Detect question type from the question text
                detectQuestionType(currentQuestion, line);

            } else if (OPTION_PATTERN.matcher(line).matches()) {
                // This is an option line
                currentOptions.add(line);

            } else if (ANSWER_PATTERN.matcher(line).matches()) {
                // This is an answer line
                if (currentQuestion != null) {
                    parseAnswer(currentQuestion, line, currentOptions);
                }
            } else if (currentQuestion != null && currentQuestion.getQuestionType() == QuestionType.FILL_UP) {
                // For fill-up questions, the next line might be the answer
                if (line.matches("(?i)^(Answer|Ans)[:\\s-].*")) {
                    String answer = line.replaceFirst("(?i)^(Answer|Ans)[:\\s-]+", "").trim();
                    currentQuestion.setCorrectAnswer(answer);
                }
            }
        }

        // Don't forget the last question
        if (currentQuestion != null) {
            finalizeQuestion(currentQuestion, currentOptions, questions);
        }

        return questions;
    }

    private void detectQuestionType(Question question, String questionText) {
        String lowerText = questionText.toLowerCase();

        // Check for True/False question
        if (lowerText.contains("true or false") || lowerText.contains("true/false") ||
                lowerText.contains("t/f") || lowerText.endsWith("?")) {
            // Will determine later based on number of options
            question.setQuestionType(QuestionType.MCQ); // Default, will update if only 2 options
        }
        // Check for Fill-up question
        else if (lowerText.contains("fill in") || lowerText.contains("fill up") ||
                questionText.contains("___") || questionText.contains("_____") ||
                lowerText.contains("complete the")) {
            question.setQuestionType(QuestionType.FILL_UP);
        }
        // Default to MCQ
        else {
            question.setQuestionType(QuestionType.MCQ);
        }
    }

    private void finalizeQuestion(Question question, List<String> options, List<Question> questions) {
        // Determine final question type based on options
        if (options.size() == 2 &&
                (options.get(0).toLowerCase().contains("true") || options.get(0).toLowerCase().contains("false")) &&
                (options.get(1).toLowerCase().contains("true") || options.get(1).toLowerCase().contains("false"))) {
            question.setQuestionType(QuestionType.TRUE_FALSE);
        } else if (options.isEmpty() && question.getQuestionType() != QuestionType.FILL_UP) {
            // If no options and not identified as fill-up, might be fill-up
            question.setQuestionType(QuestionType.FILL_UP);
        }

        question.setOptions(new ArrayList<>(options));

        // Validate question before adding
        if (question.getQuestionType() == QuestionType.FILL_UP) {
            if (question.getCorrectAnswer() == null || question.getCorrectAnswer().isEmpty()) {
                System.err.println("Warning: No answer found for fill-up question: " + question.getQuestionText());
            }
        } else {
            if (question.getCorrectOptionIndex() == -1) {
                System.err.println("Warning: No answer found for question: " + question.getQuestionText());
            }
        }

        questions.add(question);
    }

    private void parseAnswer(Question question, String answerLine, List<String> options) {
        if (question.getQuestionType() == QuestionType.FILL_UP) {
            // For fill-up, extract the text answer
            String answer = answerLine.replaceFirst("(?i).*(Answer|Ans|Correct|Key)[:\\s-]+", "").trim();
            question.setCorrectAnswer(answer);
        } else {
            // For MCQ and TRUE_FALSE, extract the option letter
            char answerChar = ' ';
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(?i)(Answer|Ans|Correct|Correct Option|Key)[:\\s-]*([\\(]?[A-Da-d][\\)]?)")
                    .matcher(answerLine);

            while (m.find()) {
                String captured = m.group(2).replaceAll("[\\(\\)]", "");
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

                question.setCorrectOptionIndex(correctIndex);
            }
        }
    }
}
