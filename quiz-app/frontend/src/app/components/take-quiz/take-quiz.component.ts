import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService, Quiz } from '../../services/api.service';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-take-quiz',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './take-quiz.component.html',
    styleUrls: ['./take-quiz.component.css']
})
export class TakeQuizComponent implements OnInit {
    quiz: Quiz | null = null;
    answers: number[] = [];
    submitted = false;
    score = 0;
    totalQuestions = 0;
    loading = true;

    constructor(
        private readonly apiService: ApiService,
        private readonly authService: AuthService,
        private readonly route: ActivatedRoute,
        private readonly router: Router
    ) { }

    ngOnInit() {
        const quizId = this.route.snapshot.paramMap.get('id');
        if (quizId) {
            this.loadQuiz(quizId);
        }
    }

    loadQuiz(quizId: string) {
        this.apiService.getQuiz(quizId).subscribe({
            next: (quiz) => {
                this.quiz = quiz;
                this.totalQuestions = quiz.questions.length;
                this.answers = new Array(quiz.questions.length).fill(-1);
                this.loading = false;
            },
            error: () => {
                alert('Failed to load quiz');
                this.router.navigate(['/student']);
            }
        });
    }

    selectAnswer(questionIndex: number, optionIndex: number) {
        this.answers[questionIndex] = optionIndex;
    }

    submitQuiz() {
        if (this.answers.includes(-1)) {
            alert('Please answer all questions before submitting');
            return;
        }

        if (!confirm('Are you sure you want to submit the quiz?')) {
            return;
        }

        const user = this.authService.getUser();
        if (!user || !this.quiz) return;

        this.apiService.submitQuiz(
            this.quiz.id!,
            user.id || '',
            user.username,
            this.answers
        ).subscribe({
            next: (result) => {
                this.score = result.score;
                this.submitted = true;
            },
            error: () => {
                alert('Failed to submit quiz');
            }
        });
    }

    backToDashboard() {
        this.router.navigate(['/student']);
    }

    getOptionLetter(index: number): string {
        return String.fromCharCode(65 + index);
    }
}
