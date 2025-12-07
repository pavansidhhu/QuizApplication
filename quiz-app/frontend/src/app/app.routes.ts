import { Routes } from '@angular/router';
// Routes configuration
import { authGuard, adminGuard } from './guards/auth-guard';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    {
        path: 'login',
        loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent)
    },
    {
        path: 'admin',
        canActivate: [adminGuard],

        loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
    },
    {
        path: 'student',
        canActivate: [authGuard],
        loadComponent: () => import('./components/student-dashboard/student-dashboard.component').then(m => m.StudentDashboardComponent)
    },
    {
        path: 'quiz/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./components/take-quiz/take-quiz.component').then(m => m.TakeQuizComponent)
    }
];
