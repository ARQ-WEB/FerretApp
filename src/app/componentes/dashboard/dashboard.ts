import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardDTO } from '../../servicios/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  dashboard: DashboardDTO | null = null;
  cargando = true;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.dashboardService.getResumen().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  get hayStockBajo(): boolean {
    return (this.dashboard?.productosStockBajo ?? 0) > 0;
  }

  formatearMoneda(valor: number): string {
    return `S/${valor.toFixed(2)}`;
  }
}