import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email    = '';
  password = '';
  error    = '';
  cargando = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
  this.error = '';

  if (!this.email || !this.password) {
    this.error = 'Por favor completa todos los campos';
    return;
  }

  this.cargando = true;

  this.authService.login(this.email, this.password).subscribe({
    next: () => {
      this.cargando = false;
      this.router.navigate(['/dashboard']);
    },
    error: () => {
      this.cargando = false;
      this.error = 'Email o contraseña incorrectos';
    }
  });
}
}