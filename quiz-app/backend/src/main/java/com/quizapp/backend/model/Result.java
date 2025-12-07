package com.quizapp.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Document(collection = "results")
public class Result {
    @Id
    private String id;
    @Indexed
    private String studentId;
    private String studentName;
    @Indexed
    private String quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
}
