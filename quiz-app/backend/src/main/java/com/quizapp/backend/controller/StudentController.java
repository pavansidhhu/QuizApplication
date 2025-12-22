package com.quizapp.backend.controller;

import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.QuizAttempt;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.service.QuizService;
import com.quizapp.backend.service.QuizAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    public StudentController(QuizService quizService, QuizAttemptService quizAttemptService) {
        this.quizService = quizService;
        this.quizAttemptService = quizAttemptService;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/quiz/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuiz(id));
    }

    @PostMapping("/start-quiz")
    public ResponseEntity<QuizAttempt> startQuiz(@RequestBody Map<String, String> payload) {
        String studentId = payload.get("studentId");
        String studentName = payload.get("studentName");
        String quizId = payload.get("quizId");
        String quizTitle = payload.get("quizTitle");

        QuizAttempt attempt = quizAttemptService.startQuizAttempt(studentId, studentName, quizId, quizTitle);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/tab-switch")
    public ResponseEntity<QuizAttempt> recordTabSwitch(@RequestBody Map<String, String> payload) {
        String attemptId = payload.get("attemptId");
        QuizAttempt attempt = quizAttemptService.incrementTabSwitch(attemptId);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/submit-quiz")
    public ResponseEntity<Result> submitQuiz(@RequestBody Map<String, Object> payload) {
        String quizId = (String) payload.get("quizId");
        String studentId = (String) payload.get("studentId");
        String studentName = (String) payload.get("studentName");

        // Convert answers to Map<Integer, Object> for flexible answer types
        @SuppressWarnings("unchecked")
        List<Object> answersList = (List<Object>) payload.get("answers");
        Map<Integer, Object> answersMap = new HashMap<>();

        for (int i = 0; i < answersList.size(); i++) {
            answersMap.put(i, answersList.get(i));
        }

        Result result = quizService.submitQuiz(quizId, studentId, studentName, answersMap);

        // End the quiz attempt
        if (payload.containsKey("attemptId")) {
            String attemptId = (String) payload.get("attemptId");
            quizAttemptService.endQuizAttempt(attemptId);
        } else {
            // Fallback: end by studentId and quizId
            quizAttemptService.endQuizAttemptByStudent(studentId, quizId);
        }

        return ResponseEntity.ok(result);
    }
}
