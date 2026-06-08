import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { CategoriaDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private readonly API_URL = environment.apiUrl;
  private listaCambio: Subject<CategoriaDTO[]> = new Subject<CategoriaDTO[]>();

  constructor(private http: HttpClient) {}

  getCategorias(): Observable<CategoriaDTO[]> {
    return this.http.get<CategoriaDTO[]>(`${this.API_URL}/categorias`);
  }

  crear(categoria: CategoriaDTO): Observable<CategoriaDTO> {
    return this.http.post<CategoriaDTO>(`${this.API_URL}/categorias`, categoria);
  }

  setList(categorias: CategoriaDTO[]): void {
    this.listaCambio.next(categorias);
  }

  getListaCambio(): Observable<CategoriaDTO[]> {
    return this.listaCambio.asObservable();
  }
}