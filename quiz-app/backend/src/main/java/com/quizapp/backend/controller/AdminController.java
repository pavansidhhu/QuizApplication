package com.quizapp.backend.controller;

import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QuizService quizService;

    public AdminController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/upload-quiz")
    public ResponseEntity<Quiz> uploadQuiz(@RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(quizService.createQuizFromPdf(title, file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<Result>> getAllResults() {
        return ResponseEntity.ok(quizService.getAllResults());
    }
}
