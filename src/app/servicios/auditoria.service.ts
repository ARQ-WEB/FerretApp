import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditoriaDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {

  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAuditorias(): Observable<AuditoriaDTO[]> {
    return this.http.get<AuditoriaDTO[]>(`${this.API_URL}/auditoria`);
  }
}
