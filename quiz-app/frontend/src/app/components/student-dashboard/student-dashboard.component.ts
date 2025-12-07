import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService, Quiz } from '../../services/api.service';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-student-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './student-dashboard.component.html',
    styleUrls: ['./student-dashboard.component.css']
})
export class StudentDashboardComponent implements OnInit {
    quizzes: Quiz[] = [];
    loading = true;

    constructor(
        private apiService: ApiService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit() {
        this.loadQuizzes();
    }

    loadQuizzes() {
        this.apiService.getAllQuizzes().subscribe({
            next: (quizzes) => {
                this.quizzes = quizzes;
                this.loading = false;
            },
            error: (err) => {
                alert('Failed to load quizzes');
                this.loading = false;
            }
        });
    }

    takeQuiz(quizId: string) {
        this.router.navigate(['/quiz', quizId]);
    }

    logout() {
        this.authService.logout();
    }
}
