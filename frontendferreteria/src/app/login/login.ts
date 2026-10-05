import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrl: './login.css',
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class LoginComponent implements OnInit {
  usuario = '';
  password = '';
  errorMessage = '';
  serverOnline = false;
  serverChecking = true;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/app']);
      return;
    }
    this.authService.checkServerHealth().subscribe(online => {
      this.serverOnline = online;
      this.serverChecking = false;
    });
  }

  login() {
    this.errorMessage = '';
    this.authService.login(this.usuario.trim(), this.password).subscribe({
      next: () => this.router.navigate(['/app']),
      error: (error) => {
        this.errorMessage = error.status === 401
          ? 'Credenciales incorrectas.'
          : 'No se pudo iniciar sesión.';
      }
    });
  }
}
