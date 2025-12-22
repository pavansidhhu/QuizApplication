import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
    id?: string;
    username: string;
    password: string;
    role: 'ADMIN' | 'STUDENT';
}

export interface Question {
    questionType: 'MCQ' | 'FILL_UP' | 'TRUE_FALSE';
    questionText: string;
    options: string[];
    correctOptionIndex: number;
    correctAnswer?: string;
}

export interface Quiz {
    id?: string;
    title: string;
    questions: Question[];
    originalFileName?: string;
    fileType?: string;
}

export interface Result {
    id?: string;
    quizId: string;
    quizTitle: string;
    studentId: string;
    studentName: string;
    score: number;
    totalQuestions: number;
}

export interface QuizAttempt {
    id?: string;
    studentId: string;
    studentName: string;
    quizId: string;
    quizTitle: string;
    startTime: string;
    isActive: boolean;
    tabSwitchCount: number;
}

import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private baseUrl = environment.apiUrl;

    constructor(private http: HttpClient) { }

    // Auth endpoints
    login(username: string, password: string): Observable<User> {
        return this.http.post<User>(`${this.baseUrl}/auth/login`, { username, password });
    }

    register(user: User): Observable<User> {
        return this.http.post<User>(`${this.baseUrl}/auth/register`, user);
    }

    // Admin endpoints
    uploadQuiz(title: string, questionsFile: File, answersFile: File | null): Observable<Quiz> {
        const formData = new FormData();
        formData.append('title', title);
        formData.append('questions', questionsFile);
        if (answersFile) {
            formData.append('answers', answersFile);
        }
        return this.http.post<Quiz>(`${this.baseUrl}/admin/upload-quiz`, formData);
    }

    getAllResults(): Observable<Result[]> {
        return this.http.get<Result[]>(`${this.baseUrl}/admin/results`);
    }

    getQuizPreview(quizId: string): Observable<Blob> {
        return this.http.get(`${this.baseUrl}/admin/quiz/${quizId}/preview`, { responseType: 'blob' });
    }

    getActiveStudents(quizId: string): Observable<QuizAttempt[]> {
        return this.http.get<QuizAttempt[]>(`${this.baseUrl}/admin/quiz/${quizId}/active-students`);
    }

    getAllActiveAttempts(): Observable<QuizAttempt[]> {
        return this.http.get<QuizAttempt[]>(`${this.baseUrl}/admin/active-attempts`);
    }

    updateQuiz(quizId: string, quiz: Quiz): Observable<Quiz> {
        return this.http.put<Quiz>(`${this.baseUrl}/admin/quiz/${quizId}`, quiz);
    }

    deleteQuiz(quizId: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/admin/quiz/${quizId}`);
    }

    // Student endpoints
    getAllQuizzes(): Observable<Quiz[]> {
        return this.http.get<Quiz[]>(`${this.baseUrl}/student/quizzes`);
    }

    getQuiz(id: string): Observable<Quiz> {
        return this.http.get<Quiz>(`${this.baseUrl}/student/quiz/${id}`);
    }

    startQuizAttempt(studentId: string, studentName: string, quizId: string, quizTitle: string): Observable<QuizAttempt> {
        return this.http.post<QuizAttempt>(`${this.baseUrl}/student/start-quiz`, {
            studentId,
            studentName,
            quizId,
            quizTitle
        });
    }

    recordTabSwitch(attemptId: string): Observable<QuizAttempt> {
        return this.http.post<QuizAttempt>(`${this.baseUrl}/student/tab-switch`, { attemptId });
    }

    submitQuiz(quizId: string, studentId: string, studentName: string, answers: any[], attemptId?: string): Observable<Result> {
        const payload: any = {
            quizId,
            studentId,
            studentName,
            answers
        };

        if (attemptId) {
            payload.attemptId = attemptId;
        }

        return this.http.post<Result>(`${this.baseUrl}/student/submit-quiz`, payload);
    }
}
