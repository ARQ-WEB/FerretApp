import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { PedidoDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private readonly API_URL = environment.apiUrl;
  private listaCambio: Subject<PedidoDTO[]> = new Subject<PedidoDTO[]>();

  constructor(private http: HttpClient) {}

  getPedidos(): Observable<PedidoDTO[]> {
    return this.http.get<PedidoDTO[]>(`${this.API_URL}/pedidos`);
  }

  getPedido(id: number): Observable<PedidoDTO> {
    return this.http.get<PedidoDTO>(`${this.API_URL}/pedidos/${id}`);
  }

  crear(pedido: PedidoDTO): Observable<PedidoDTO> {
    return this.http.post<PedidoDTO>(`${this.API_URL}/pedidos`, pedido);
  }

  cambiarEstado(id: number, estado: string): Observable<PedidoDTO> {
    return this.http.patch<PedidoDTO>(`${this.API_URL}/pedidos/${id}/estado?estado=${estado}`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/pedidos/${id}`);
  }

  setList(pedidos: PedidoDTO[]): void {
    this.listaCambio.next(pedidos);
  }

  getListaCambio(): Observable<PedidoDTO[]> {
    return this.listaCambio.asObservable();
  }
}