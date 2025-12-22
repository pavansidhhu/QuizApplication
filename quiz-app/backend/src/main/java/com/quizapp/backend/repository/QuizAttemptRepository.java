package com.quizapp.backend.repository;

import com.quizapp.backend.model.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByQuizIdAndIsActiveTrue(String quizId);

    Optional<QuizAttempt> findByStudentIdAndQuizIdAndIsActiveTrue(String studentId, String quizId);

    List<QuizAttempt> findByIsActiveTrue();
}
