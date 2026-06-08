import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { ProveedorDTO } from '../modelos/interfaces';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {

  private readonly API_URL = environment.apiUrl;
  private listaCambio: Subject<ProveedorDTO[]> = new Subject<ProveedorDTO[]>();

  constructor(private http: HttpClient) {}

  getProveedores(): Observable<ProveedorDTO[]> {
    return this.http.get<ProveedorDTO[]>(`${this.API_URL}/proveedores`);
  }

  crear(proveedor: ProveedorDTO): Observable<ProveedorDTO> {
    return this.http.post<ProveedorDTO>(`${this.API_URL}/proveedores`, proveedor);
  }

  actualizar(id: number, proveedor: ProveedorDTO): Observable<ProveedorDTO> {
    return this.http.put<ProveedorDTO>(`${this.API_URL}/proveedores/${id}`, proveedor);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/proveedores/${id}`);
  }

  setList(proveedores: ProveedorDTO[]): void {
    this.listaCambio.next(proveedores);
  }

  getListaCambio(): Observable<ProveedorDTO[]> {
    return this.listaCambio.asObservable();
  }
}