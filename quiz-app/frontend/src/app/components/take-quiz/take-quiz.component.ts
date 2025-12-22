import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService, Quiz, QuizAttempt } from '../../services/api.service';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-take-quiz',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './take-quiz.component.html',
    styleUrls: ['./take-quiz.component.css']
})
export class TakeQuizComponent implements OnInit, OnDestroy {
    quiz: Quiz | null = null;
    answers: any[] = [];
    submitted = false;
    score = 0;
    totalQuestions = 0;
    loading = true;

    // Tab switching tracking
    tabSwitchCount = 0;
    maxTabSwitches = 2;
    quizAttempt: QuizAttempt | null = null;
    showTabWarning = false;
    tabWarningMessage = '';

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

        // Add visibility change listener for tab switching detection
        document.addEventListener('visibilitychange', this.handleVisibilityChange.bind(this));
    }

    ngOnDestroy() {
        // Clean up listener
        document.removeEventListener('visibilitychange', this.handleVisibilityChange.bind(this));
    }

    handleVisibilityChange() {
        if (document.hidden && this.quiz && !this.submitted && this.quizAttempt) {
            // User switched to another tab
            this.tabSwitchCount++;

            // Record tab switch in backend
            this.apiService.recordTabSwitch(this.quizAttempt.id!).subscribe({
                next: (attempt) => {
                    this.quizAttempt = attempt;
                },
                error: () => console.error('Failed to record tab switch')
            });

            if (this.tabSwitchCount >= this.maxTabSwitches) {
                // Auto-submit quiz
                this.showTabWarning = true;
                this.tabWarningMessage = 'You have exceeded the maximum allowed tab switches. Your quiz will be auto-submitted.';
                setTimeout(() => {
                    this.submitQuiz(true);
                }, 2000);
            } else {
                this.showTabWarning = true;
                const remaining = this.maxTabSwitches - this.tabSwitchCount;
                this.tabWarningMessage = `Warning: ${remaining} tab switch${remaining === 1 ? '' : 'es'} remaining. Quiz will auto-submit if you switch again.`;
                setTimeout(() => {
                    this.showTabWarning = false;
                }, 5000);
            }
        }
    }

    loadQuiz(quizId: string) {
        this.apiService.getQuiz(quizId).subscribe({
            next: (quiz) => {
                this.quiz = quiz;
                this.totalQuestions = quiz.questions.length;
                this.answers = new Array(quiz.questions.length).fill(null);
                this.loading = false;

                // Initialize answers based on question type
                for (let i = 0; i < quiz.questions.length; i++) {
                    const qType = quiz.questions[i].questionType;
                    if (qType === 'MCQ' || qType === 'TRUE_FALSE') {
                        this.answers[i] = -1; // -1 means not answered
                    } else if (qType === 'FILL_UP') {
                        this.answers[i] = ''; // Empty string for fill-up
                    }
                }

                // Start quiz attempt
                const user = this.authService.getUser();
                if (user && quiz.id) {
                    this.apiService.startQuizAttempt(user.id || '', user.username, quiz.id, quiz.title).subscribe({
                        next: (attempt) => {
                            this.quizAttempt = attempt;
                        },
                        error: () => console.error('Failed to start quiz attempt')
                    });
                }
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

    updateFillUpAnswer(questionIndex: number, value: string) {
        this.answers[questionIndex] = value;
    }

    submitQuiz(autoSubmit: boolean = false) {
        // Check if all questions are answered (unless auto-submit)
        if (!autoSubmit) {
            for (let i = 0; i < this.answers.length; i++) {
                const answer = this.answers[i];
                const qType = this.quiz?.questions[i].questionType;

                if (qType === 'MCQ' || qType === 'TRUE_FALSE') {
                    if (answer === -1 || answer === null) {
                        alert('Please answer all questions before submitting');
                        return;
                    }
                } else if (qType === 'FILL_UP') {
                    if (!answer || answer.trim() === '') {
                        alert('Please answer all questions before submitting');
                        return;
                    }
                }
            }

            if (!confirm('Are you sure you want to submit the quiz?')) {
                return;
            }
        }

        const user = this.authService.getUser();
        if (!user || !this.quiz) return;

        this.apiService.submitQuiz(
            this.quiz.id!,
            user.id || '',
            user.username,
            this.answers,
            this.quizAttempt?.id
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
