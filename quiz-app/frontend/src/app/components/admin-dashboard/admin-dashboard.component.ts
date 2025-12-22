import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Result, QuizAttempt, Quiz } from '../../services/api.service';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-admin-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-dashboard.component.html',
    styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
    quizTitle = '';
    selectedQuestionsFile: File | null = null;
    selectedAnswersFile: File | null = null;
    results: Result[] = [];
    quizzes: Quiz[] = [];
    groupedResults: { [quizTitle: string]: Result[] } = {};
    quizTitles: string[] = [];
    loading = false;
    uploadMessage = '';

    // Data Log modal
    showDataLogModal = false;
    activeStudents: QuizAttempt[] = [];
    selectedQuizTitle = '';

    constructor(
        private apiService: ApiService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.loadResults();
        this.loadQuizzes();
    }

    groupResultsByQuiz() {
        this.groupedResults = {};
        this.results.forEach(result => {
            if (!this.groupedResults[result.quizTitle]) {
                this.groupedResults[result.quizTitle] = [];
            }
            this.groupedResults[result.quizTitle].push(result);
        });
        this.quizTitles = Object.keys(this.groupedResults);
    }

    onQuestionsFileSelected(event: any) {
        const file = event.target.files[0];
        if (file && file.type === 'application/pdf') {
            this.selectedQuestionsFile = file;
        } else {
            alert('Please select a PDF file for questions');
            event.target.value = '';
        }
    }

    onAnswersFileSelected(event: any) {
        const file = event.target.files[0];
        if (file && file.type === 'application/pdf') {
            this.selectedAnswersFile = file;
        } else {
            alert('Please select a PDF file for answers');
            event.target.value = '';
        }
    }

    uploadQuiz() {
        if (!this.quizTitle || !this.selectedQuestionsFile) {
            alert('Please enter a title and select the Questions PDF');
            return;
        }

        this.loading = true;
        this.uploadMessage = '';

        this.apiService.uploadQuiz(this.quizTitle, this.selectedQuestionsFile, this.selectedAnswersFile).subscribe({
            next: (quiz) => {
                this.uploadMessage = 'Quiz uploaded successfully!';
                this.quizTitle = '';
                this.selectedQuestionsFile = null;
                this.selectedAnswersFile = null;
                const qInput = document.getElementById('questionsFile') as HTMLInputElement;
                const aInput = document.getElementById('answersFile') as HTMLInputElement;
                if (qInput) qInput.value = '';
                if (aInput) aInput.value = '';
                this.loading = false;
                this.loadQuizzes(); // Reload quizzes
            },
            error: (err) => {
                alert('Failed to upload quiz: ' + (err.error?.message || 'Unknown error'));
                this.loading = false;
            }
        });
    }

    loadResults() {
        this.apiService.getAllResults().subscribe({
            next: (results) => {
                this.results = results;
                this.groupResultsByQuiz();
            },
            error: (err) => {
                console.error('Failed to load results', err);
            }
        });
    }

    loadQuizzes() {
        this.apiService.getAllQuizzes().subscribe({
            next: (quizzes) => {
                this.quizzes = quizzes;
            },
            error: (err) => {
                console.error('Failed to load quizzes', err);
            }
        });
    }

    previewQuiz(quizId: string) {
        this.apiService.getQuizPreview(quizId).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                window.open(url, '_blank');
            },
            error: () => {
                alert('Failed to load quiz preview');
            }
        });
    }

    showDataLog(quizId: string, quizTitle: string) {
        this.selectedQuizTitle = quizTitle;
        this.apiService.getActiveStudents(quizId).subscribe({
            next: (attempts) => {
                this.activeStudents = attempts;
                this.showDataLogModal = true;
            },
            error: () => {
                alert('Failed to load active students');
            }
        });
    }

    closeDataLogModal() {
        this.showDataLogModal = false;
        this.activeStudents = [];
        this.selectedQuizTitle = '';
    }

    logout() {
        this.authService.logout();
    }
}
