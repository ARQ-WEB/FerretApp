import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteService } from '../../servicios/reporte.service';
import { ProductoService } from '../../servicios/producto.service';
import { ReporteDTO, ProductoDTO } from '../../modelos/interfaces';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  reporte: ReporteDTO | null = null;
  stockBajo: ProductoDTO[] = [];
  cargando = true;

  // Fechas últimos 7 días
  hasta = new Date().toISOString().split('T')[0];
  desde = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

  constructor(
    private reporteService: ReporteService,
    private productoService: ProductoService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;

    this.reporteService.getReporteVentas(this.desde, this.hasta).subscribe({
      next: (data) => {
        this.reporte = data;
        this.stockBajo = data.productosStockBajo;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  formatearMoneda(valor: number): string {
    return `S/${valor.toFixed(2)}`;
  }

  get totalProductos(): number {
    return this.reporte?.productosMasVendidos?.length ?? 0;
  }

  get hayStockBajo(): boolean {
    return this.stockBajo.length > 0;
  }
}