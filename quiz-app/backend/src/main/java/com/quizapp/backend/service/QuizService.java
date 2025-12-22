package com.quizapp.backend.service;

import com.quizapp.backend.model.Question;
import com.quizapp.backend.model.QuestionType;
import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.repository.QuizRepository;
import com.quizapp.backend.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ResultRepository resultRepository;
    private final DocumentService documentService;

    public QuizService(QuizRepository quizRepository, ResultRepository resultRepository,
            DocumentService documentService) {
        this.quizRepository = quizRepository;
        this.resultRepository = resultRepository;
        this.documentService = documentService;
    }

    public Quiz createQuizFromDocument(String title, MultipartFile file) throws IOException {
        List<Question> questions = documentService.parseDocument(file);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);

        // Store file for preview
        quiz.setOriginalFileName(file.getOriginalFilename());
        quiz.setFileContent(file.getBytes());

        String filename = file.getOriginalFilename();
        if (filename != null) {
            if (filename.toLowerCase().endsWith(".pdf")) {
                quiz.setFileType("pdf");
            } else if (filename.toLowerCase().endsWith(".docx")) {
                quiz.setFileType("docx");
            }
        }

        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuiz(String id) {
        return quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Quiz updateQuiz(String id, Quiz updatedQuiz) {
        Quiz existingQuiz = getQuiz(id);
        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setQuestions(updatedQuiz.getQuestions());
        return quizRepository.save(existingQuiz);
    }

    public void deleteQuiz(String id) {
        quizRepository.deleteById(id);
    }

    public Result submitQuiz(String quizId, String studentId, String studentName, Map<Integer, Object> answers) {
        Quiz quiz = getQuiz(quizId);
        int score = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Object answer = answers.get(i);

            if (answer == null)
                continue;

            boolean isCorrect = false;

            // Validate based on question type
            if (question.getQuestionType() == QuestionType.MCQ ||
                    question.getQuestionType() == QuestionType.TRUE_FALSE) {
                // For MCQ and TRUE_FALSE, answer should be an integer (option index)
                if (answer instanceof Integer) {
                    isCorrect = answer.equals(question.getCorrectOptionIndex());
                } else if (answer instanceof String) {
                    try {
                        int answerIndex = Integer.parseInt((String) answer);
                        isCorrect = answerIndex == question.getCorrectOptionIndex();
                    } catch (NumberFormatException e) {
                        // Invalid answer format
                        isCorrect = false;
                    }
                }
            } else if (question.getQuestionType() == QuestionType.FILL_UP) {
                // For FILL_UP, answer should be a string
                if (answer instanceof String) {
                    String studentAnswer = ((String) answer).trim().toLowerCase();
                    String correctAnswer = question.getCorrectAnswer().trim().toLowerCase();
                    isCorrect = studentAnswer.equals(correctAnswer);
                }
            }

            if (isCorrect) {
                score++;
            }
        }

        Result result = new Result();
        result.setQuizId(quizId);
        result.setQuizTitle(quiz.getTitle());
        result.setStudentId(studentId);
        result.setStudentName(studentName);
        result.setScore(score);
        result.setTotalQuestions(questions.size());

        return resultRepository.save(result);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
}
