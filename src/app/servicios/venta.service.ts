import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VentaDTO } from '../modelos/interfaces';

@Injectable({
  providedIn: 'root'
})
export class VentaService {

  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getVentas(): Observable<VentaDTO[]> {
    return this.http.get<VentaDTO[]>(`${this.API_URL}/ventas`);
  }

  getVentasRecientes(): Observable<VentaDTO[]> {
    return this.http.get<VentaDTO[]>(`${this.API_URL}/ventas/recientes`);
  }

  getVenta(id: number): Observable<VentaDTO> {
    return this.http.get<VentaDTO>(`${this.API_URL}/ventas/${id}`);
  }

  crear(venta: VentaDTO): Observable<VentaDTO> {
    return this.http.post<VentaDTO>(`${this.API_URL}/ventas`, venta);
  }
}