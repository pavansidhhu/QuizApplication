package com.quizapp.backend.controller;

import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final QuizService quizService;

    public StudentController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/quiz/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuiz(id));
    }

    @PostMapping("/submit-quiz")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Result> submitQuiz(@RequestBody Map<String, Object> payload) {
        String quizId = (String) payload.get("quizId");
        String studentId = (String) payload.get("studentId");
        String studentName = (String) payload.get("studentName");
        List<Integer> answers = (List<Integer>) payload.get("answers");

        return ResponseEntity.ok(quizService.submitQuiz(quizId, studentId, studentName, answers));
    }
}
