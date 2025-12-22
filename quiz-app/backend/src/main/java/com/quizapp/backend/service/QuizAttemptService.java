package com.quizapp.backend.service;

import com.quizapp.backend.model.QuizAttempt;
import com.quizapp.backend.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
    }

    public QuizAttempt startQuizAttempt(String studentId, String studentName, String quizId, String quizTitle) {
        // Check if student already has an active attempt for this quiz
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository.findByStudentIdAndQuizIdAndIsActiveTrue(studentId,
                quizId);
        if (existingAttempt.isPresent()) {
            return existingAttempt.get(); // Return existing attempt
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudentId(studentId);
        attempt.setStudentName(studentName);
        attempt.setQuizId(quizId);
        attempt.setQuizTitle(quizTitle);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setActive(true);
        attempt.setTabSwitchCount(0);

        return quizAttemptRepository.save(attempt);
    }

    public QuizAttempt incrementTabSwitch(String attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        attempt.setTabSwitchCount(attempt.getTabSwitchCount() + 1);
        return quizAttemptRepository.save(attempt);
    }

    public void endQuizAttempt(String attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        attempt.setActive(false);
        quizAttemptRepository.save(attempt);
    }

    public void endQuizAttemptByStudent(String studentId, String quizId) {
        Optional<QuizAttempt> attempt = quizAttemptRepository.findByStudentIdAndQuizIdAndIsActiveTrue(studentId,
                quizId);
        attempt.ifPresent(a -> {
            a.setActive(false);
            quizAttemptRepository.save(a);
        });
    }

    public List<QuizAttempt> getActiveAttempts(String quizId) {
        return quizAttemptRepository.findByQuizIdAndIsActiveTrue(quizId);
    }

    public List<QuizAttempt> getAllActiveAttempts() {
        return quizAttemptRepository.findByIsActiveTrue();
    }
}
