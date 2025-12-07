package com.quizapp.backend.service;

import com.quizapp.backend.model.Question;
import com.quizapp.backend.model.Quiz;
import com.quizapp.backend.model.Result;
import com.quizapp.backend.repository.QuizRepository;
import com.quizapp.backend.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ResultRepository resultRepository;
    private final PdfService pdfService;

    public QuizService(QuizRepository quizRepository, ResultRepository resultRepository, PdfService pdfService) {
        this.quizRepository = quizRepository;
        this.resultRepository = resultRepository;
        this.pdfService = pdfService;
    }

    public Quiz createQuizFromPdf(String title, MultipartFile file) throws IOException {
        List<Question> questions = pdfService.parsePdf(file);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuiz(String id) {
        return quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Result submitQuiz(String quizId, String studentId, String studentName, List<Integer> answers) {
        Quiz quiz = getQuiz(quizId);
        int score = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            if (i < answers.size() && answers.get(i) == questions.get(i).getCorrectOptionIndex()) {
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
