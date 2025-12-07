import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Result } from '../../services/api.service';
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
    selectedFile: File | null = null;
    results: Result[] = [];
    groupedResults: { [quizTitle: string]: Result[] } = {};
    quizTitles: string[] = [];
    loading = false;
    uploadMessage = '';

    constructor(
        private apiService: ApiService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.loadResults();
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

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file && file.type === 'application/pdf') {
            this.selectedFile = file;
        } else {
            alert('Please select a PDF file');
            event.target.value = '';
        }
    }

    uploadQuiz() {
        if (!this.quizTitle || !this.selectedFile) {
            alert('Please enter a title and select a PDF file');
            return;
        }

        this.loading = true;
        this.uploadMessage = '';

        this.apiService.uploadQuiz(this.quizTitle, this.selectedFile).subscribe({
            next: (quiz) => {
                this.uploadMessage = 'Quiz uploaded successfully!';
                this.quizTitle = '';
                this.selectedFile = null;
                const fileInput = document.getElementById('pdfFile') as HTMLInputElement;
                if (fileInput) fileInput.value = '';
                this.loading = false;
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

    logout() {
        this.authService.logout();
    }
}
