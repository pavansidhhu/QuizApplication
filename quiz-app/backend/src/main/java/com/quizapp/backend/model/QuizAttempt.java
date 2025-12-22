package com.quizapp.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@Document(collection = "quiz_attempts")
public class QuizAttempt {
    @Id
    private String id;
    @Indexed
    private String studentId;
    private String studentName;
    @Indexed
    private String quizId;
    private String quizTitle;
    private LocalDateTime startTime;
    private boolean isActive = true;
    private int tabSwitchCount = 0;
}
