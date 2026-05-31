import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductoDTO } from '../modelos/interfaces';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProductos(): Observable<ProductoDTO[]> {
    return this.http.get<ProductoDTO[]>(`${this.API_URL}/productos`);
  }

  getStockBajo(): Observable<ProductoDTO[]> {
    return this.http.get<ProductoDTO[]>(`${this.API_URL}/productos/stock-bajo`);
  }
}