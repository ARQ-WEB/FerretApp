import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductoDTO } from '../modelos/interfaces';
import { ProductoVendidoDTO } from '../modelos/interfaces';

export interface DashboardDTO {
  totalProductos: number;
  stockTotal: number;
  productosStockBajo: number;
  ventasUltimos7Dias: number;
  ingresosUltimos7Dias: number;
  alertasStockBajo: ProductoDTO[];
  productosMasVendidos: ProductoVendidoDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getResumen(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(`${this.API_URL}/dashboard/resumen`);
  }
}