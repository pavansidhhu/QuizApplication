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

    public Quiz createQuizFromPdf(String title, MultipartFile questionsFile, MultipartFile answersFile)
            throws IOException {
        List<Question> questions;

        if (answersFile != null && !answersFile.isEmpty()) {
            // Parse questions and answers from separate files
            questions = documentService.parseSplitPdfs(questionsFile, answersFile);
        } else {
            // Parse from single file containing both questions and answers
            questions = documentService.parseDocument(questionsFile);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);

        // Store questions file for preview
        quiz.setOriginalFileName(questionsFile.getOriginalFilename());
        quiz.setFileContent(questionsFile.getBytes());

        String filename = questionsFile.getOriginalFilename();
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

        System.out.println("=== QUIZ SUBMISSION DEBUG ===");
        System.out.println("Quiz ID: " + quizId);
        System.out.println("Student: " + studentName);
        System.out.println("Total Questions: " + questions.size());
        System.out.println("Answers received: " + answers);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Object answer = answers.get(i);

            System.out.println("\n--- Question " + (i + 1) + " ---");
            System.out.println("Question Type: " + question.getQuestionType());
            System.out.println("Question Text: " + question.getQuestionText());
            System.out.println("Correct Option Index: " + question.getCorrectOptionIndex());
            System.out.println("Student Answer (raw): " + answer + " (type: "
                    + (answer != null ? answer.getClass().getSimpleName() : "null") + ")");

            if (answer == null) {
                System.out.println("Answer is null, skipping");
                continue;
            }

            boolean isCorrect = false;

            // Validate based on question type
            if (question.getQuestionType() == QuestionType.MCQ ||
                    question.getQuestionType() == QuestionType.TRUE_FALSE) {
                // For MCQ and TRUE_FALSE, answer should be an integer (option index)
                if (answer instanceof Integer) {
                    int studentAnswer = (Integer) answer;
                    isCorrect = studentAnswer == question.getCorrectOptionIndex();
                    System.out.println("Integer comparison: " + studentAnswer + " == "
                            + question.getCorrectOptionIndex() + " ? " + isCorrect);
                } else if (answer instanceof String) {
                    try {
                        int answerIndex = Integer.parseInt((String) answer);
                        isCorrect = answerIndex == question.getCorrectOptionIndex();
                        System.out.println("String->Int comparison: " + answerIndex + " == "
                                + question.getCorrectOptionIndex() + " ? " + isCorrect);
                    } catch (NumberFormatException e) {
                        // Invalid answer format
                        isCorrect = false;
                        System.out.println("Failed to parse answer as integer: " + answer);
                    }
                } else if (answer instanceof Double) {
                    // JSON might send numbers as Double
                    int answerIndex = ((Double) answer).intValue();
                    isCorrect = answerIndex == question.getCorrectOptionIndex();
                    System.out.println("Double->Int comparison: " + answerIndex + " == "
                            + question.getCorrectOptionIndex() + " ? " + isCorrect);
                }
            } else if (question.getQuestionType() == QuestionType.FILL_UP) {
                // For FILL_UP, answer should be a string
                if (answer instanceof String) {
                    String studentAnswer = ((String) answer).trim().toLowerCase();
                    String correctAnswer = question.getCorrectAnswer().trim().toLowerCase();
                    isCorrect = studentAnswer.equals(correctAnswer);
                    System.out.println(
                            "String comparison: '" + studentAnswer + "' == '" + correctAnswer + "' ? " + isCorrect);
                }
            }

            if (isCorrect) {
                score++;
                System.out.println("✓ CORRECT");
            } else {
                System.out.println("✗ INCORRECT");
            }
        }

        System.out.println("\n=== FINAL SCORE: " + score + "/" + questions.size() + " ===\n");

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
