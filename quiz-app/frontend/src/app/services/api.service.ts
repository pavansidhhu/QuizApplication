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
    questionText: string;
    options: string[];
    correctOptionIndex: number;
}

export interface Quiz {
    id?: string;
    title: string;
    questions: Question[];
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
    uploadQuiz(title: string, file: File): Observable<Quiz> {
        const formData = new FormData();
        formData.append('title', title);
        formData.append('file', file);
        return this.http.post<Quiz>(`${this.baseUrl}/admin/upload-quiz`, formData);
    }

    getAllResults(): Observable<Result[]> {
        return this.http.get<Result[]>(`${this.baseUrl}/admin/results`);
    }

    // Student endpoints
    getAllQuizzes(): Observable<Quiz[]> {
        return this.http.get<Quiz[]>(`${this.baseUrl}/student/quizzes`);
    }

    getQuiz(id: string): Observable<Quiz> {
        return this.http.get<Quiz>(`${this.baseUrl}/student/quiz/${id}`);
    }

    submitQuiz(quizId: string, studentId: string, studentName: string, answers: number[]): Observable<Result> {
        return this.http.post<Result>(`${this.baseUrl}/student/submit-quiz`, {
            quizId,
            studentId,
            studentName,
            answers
        });
    }
}
