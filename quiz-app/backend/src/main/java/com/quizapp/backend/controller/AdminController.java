package com.quizapp.backend.controller;

import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.QuizAttempt;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.service.QuizService;
import com.quizapp.backend.service.QuizAttemptService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    public AdminController(QuizService quizService, QuizAttemptService quizAttemptService) {
        this.quizService = quizService;
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/upload-quiz")
    public ResponseEntity<Quiz> uploadQuiz(@RequestParam("title") String title,
            @RequestParam("questions") MultipartFile questions,
            @RequestParam(value = "answers", required = false) MultipartFile answers) {
        try {
            return ResponseEntity.ok(quizService.createQuizFromPdf(title, questions, answers));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<Result>> getAllResults() {
        return ResponseEntity.ok(quizService.getAllResults());
    }

    @GetMapping("/quiz/{id}/preview")
    public ResponseEntity<byte[]> getQuizPreview(@PathVariable String id) {
        try {
            Quiz quiz = quizService.getQuiz(id);
            if (quiz.getFileContent() == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equals(quiz.getFileType())) {
                headers.setContentType(MediaType.APPLICATION_PDF);
            } else if ("docx".equals(quiz.getFileType())) {
                headers.setContentType(
                        MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            }
            headers.setContentDispositionFormData("inline", quiz.getOriginalFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(quiz.getFileContent());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/quiz/{id}/active-students")
    public ResponseEntity<List<QuizAttempt>> getActiveStudents(@PathVariable String id) {
        return ResponseEntity.ok(quizAttemptService.getActiveAttempts(id));
    }

    @GetMapping("/active-attempts")
    public ResponseEntity<List<QuizAttempt>> getAllActiveAttempts() {
        return ResponseEntity.ok(quizAttemptService.getAllActiveAttempts());
    }

    @PutMapping("/quiz/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable String id, @RequestBody Quiz quiz) {
        try {
            return ResponseEntity.ok(quizService.updateQuiz(id, quiz));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/quiz/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable String id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
