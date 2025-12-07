package com.quizapp.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class Question {
    private String questionText;
    private List<String> options;
    private int correctOptionIndex; // 0-based index
}
