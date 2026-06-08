import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { VentaDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VentaService {

  private readonly API_URL = environment.apiUrl;
  private listaCambio: Subject<VentaDTO[]> = new Subject<VentaDTO[]>();

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

  setList(ventas: VentaDTO[]): void {
    this.listaCambio.next(ventas);
  }

  getListaCambio(): Observable<VentaDTO[]> {
    return this.listaCambio.asObservable();
  }
}