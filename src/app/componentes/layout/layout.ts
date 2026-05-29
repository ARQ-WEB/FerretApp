import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class Layout {

  constructor(public authService: AuthService) {}

  get usuario() {
    return this.authService.getUsuarioActual();
  }

  get esAdmin(): boolean {
    return this.authService.esAdministrador();
  }

  get inicialUsuario(): string {
    const nombre = this.usuario?.username ?? '';
    return nombre.charAt(0).toUpperCase();
  }

  cerrarSesion(): void {
    this.authService.logout();
  }
}