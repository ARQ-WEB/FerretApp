import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteDTO } from '../modelos/interfaces';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {

  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getReporteVentas(desde: string, hasta: string): Observable<ReporteDTO> {
  const desdeDateTime = `${desde}T00:00:00`;
  const hastaDateTime = `${hasta}T23:59:59`;
  return this.http.get<ReporteDTO>(`${this.API_URL}/reportes/ventas?desde=${desdeDateTime}&hasta=${hastaDateTime}`);
}
}