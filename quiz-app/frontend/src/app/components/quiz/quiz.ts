import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz.html',
  styleUrls: ['./quiz.css']
})
export class QuizComponent implements OnInit {
  quiz: any = null;
  answers: number[] = [];
  result: any = null;
  loading = true;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly api: ApiService,
    private readonly auth: AuthService,
    private readonly router: Router
  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadQuiz(id);
    }
  }

  loadQuiz(id: string) {
    this.api.getQuiz(id).subscribe({
      next: (data) => {
        this.quiz = data;
        this.answers = new Array(data.questions.length).fill(-1);
        this.loading = false;
      },
      error: () => {
        console.error('Failed to load quiz');
        this.loading = false;
      }
    });
  }

  submitQuiz() {
    if (!this.quiz) return;

    const user = this.auth.getUser();

    this.api.submitQuiz(
      this.quiz.id,
      user.id,
      user.username,
      this.answers
    ).subscribe({
      next: (result) => {
        this.result = result;
      },
      error: () => {
        console.error('Failed to submit quiz');
      }
    });
  }

  goBack() {
    this.router.navigate(['/student']);
  }
}
