import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, UsuarioSesion } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = environment.apiUrl;
  private readonly TOKEN_KEY = 'ferretapp_token';
  private readonly USER_KEY  = 'ferretapp_user';

  constructor(private http: HttpClient, private router: Router) {}

  // ── Login ──────────────────────────────────────────────
  login(email: string, password: string): Observable<AuthResponse> {
    const body: AuthRequest = { username: email, password };

    return this.http.post<AuthResponse>(`${this.API_URL}/authenticate`, body).pipe(
      tap(response => {
        const sesion: UsuarioSesion = {
          username: email,
          roles: Array.from(response.roles),
          token: response.jwt
        };
        localStorage.setItem(this.TOKEN_KEY, response.jwt);
        localStorage.setItem(this.USER_KEY, JSON.stringify(sesion));
      })
    );
  }

  // ── Logout ─────────────────────────────────────────────
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  // ── Token ──────────────────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // ── Usuario actual ─────────────────────────────────────
  getUsuarioActual(): UsuarioSesion | null {
    const data = localStorage.getItem(this.USER_KEY);
    return data ? JSON.parse(data) : null;
  }

  // ── Verificar autenticación ────────────────────────────
  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  // ── Verificar roles ────────────────────────────────────
  esAdministrador(): boolean {
    const usuario = this.getUsuarioActual();
    return usuario?.roles?.includes('ROLE_ADMINISTRADOR') ?? false;
  }

  esVendedor(): boolean {
    const usuario = this.getUsuarioActual();
    return usuario?.roles?.includes('ROLE_VENDEDOR') ?? false;
  }
}