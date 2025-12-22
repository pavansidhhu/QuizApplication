package com.quizapp.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class Question {
    private QuestionType questionType = QuestionType.MCQ; // Default to MCQ for backward compatibility
    private String questionText;
    private List<String> options; // For MCQ and TRUE_FALSE
    private int correctOptionIndex = -1; // For MCQ and TRUE_FALSE
    private String correctAnswer; // For FILL_UP questions
}
