import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    username = '';
    password = '';
    error = '';
    loading = false;

    constructor(
        private apiService: ApiService,
        private authService: AuthService,
        private router: Router
    ) { }

    onSubmit() {
        console.log('Login submit triggered', this.username, this.password);
        if (!this.username || !this.password) {
            this.error = 'Please enter username and password';
            return;
        }

        this.loading = true;
        this.error = '';

        console.log('Calling API login...');
        this.apiService.login(this.username, this.password).subscribe({
            next: (user) => {
                console.log('Login success:', user);
                this.authService.login(user);
                this.loading = false;
            },
            error: (err) => {
                console.error('Login error:', err);
                this.error = 'Invalid username or password';
                this.loading = false;
            }
        });
    }
}
