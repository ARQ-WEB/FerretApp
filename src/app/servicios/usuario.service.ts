import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { UsuarioDTO, RolDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private readonly API_URL = environment.apiUrl;
  private listaCambio: Subject<UsuarioDTO[]> = new Subject<UsuarioDTO[]>();

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<UsuarioDTO[]> {
    return this.http.get<UsuarioDTO[]>(`${this.API_URL}/usuarios`);
  }

  getRoles(): Observable<RolDTO[]> {
    return this.http.get<RolDTO[]>(`${this.API_URL}/roles`);
  }

  crear(usuario: UsuarioDTO): Observable<UsuarioDTO> {
    return this.http.post<UsuarioDTO>(`${this.API_URL}/usuarios`, usuario);
  }

  actualizar(id: number, usuario: UsuarioDTO): Observable<UsuarioDTO> {
    return this.http.put<UsuarioDTO>(`${this.API_URL}/usuarios/${id}`, usuario);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/usuarios/${id}`);
  }

  setList(usuarios: UsuarioDTO[]): void {
    this.listaCambio.next(usuarios);
  }

  getListaCambio(): Observable<UsuarioDTO[]> {
    return this.listaCambio.asObservable();
  }
}