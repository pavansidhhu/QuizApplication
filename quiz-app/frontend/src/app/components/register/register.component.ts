import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService, User } from '../../services/api.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    username = '';
    password = '';
    confirmPassword = '';
    role: 'ADMIN' | 'STUDENT' = 'STUDENT';
    error = '';
    loading = false;

    constructor(
        private apiService: ApiService,
        private router: Router
    ) { }

    onSubmit() {
        if (!this.username || !this.password || !this.confirmPassword) {
            this.error = 'Please fill in all fields';
            return;
        }

        if (this.password !== this.confirmPassword) {
            this.error = 'Passwords do not match';
            return;
        }

        if (this.password.length < 6) {
            this.error = 'Password must be at least 6 characters';
            return;
        }

        this.loading = true;
        this.error = '';

        const user: User = {
            username: this.username,
            password: this.password,
            role: this.role
        };

        this.apiService.register(user).subscribe({
            next: () => {
                alert('Registration successful! Please login.');
                this.router.navigate(['/login']);
            },
            error: (err) => {
                this.error = err.error?.message || 'Registration failed. Username might already exist.';
                this.loading = false;
            }
        });
    }
}
