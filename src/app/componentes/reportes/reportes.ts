import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../servicios/reporte.service';
import { ReporteDTO } from '../../modelos/interfaces';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class Reportes implements OnInit {

  @ViewChild('barChart') barChartRef!: ElementRef;
  @ViewChild('pieChart') pieChartRef!: ElementRef;

  reporte: ReporteDTO | null = null;
  cargando = false;

  barChart: Chart | null = null;
  pieChart: Chart | null = null;

  fechaInicio = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
  fechaFin = new Date().toISOString().split('T')[0];

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.cargarReporte();
  }

  cargarReporte(): void {
    this.cargando = true;
    this.reporteService.getReporteVentas(this.fechaInicio, this.fechaFin).subscribe({
      next: (data) => {
        this.reporte = data;
        this.cargando = false;
        setTimeout(() => this.renderizarGraficos(), 100);
      },
      error: () => this.cargando = false
    });
  }

  renderizarGraficos(): void {
    if (!this.reporte) return;
    this.renderizarBarras();
    this.renderizarTorta();
  }

  renderizarBarras(): void {
    if (this.barChart) {
      this.barChart.destroy();
      this.barChart = null;
    }

    const canvas = this.barChartRef?.nativeElement;
    if (!canvas) return;

    const labels = Object.keys(this.reporte!.ventasPorDia);
    const valores = Object.values(this.reporte!.ventasPorDia).map(v => Number(v));

    this.barChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Ingresos (S/)',
          data: valores,
          backgroundColor: '#3b82f6',
          borderRadius: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom' } },
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  renderizarTorta(): void {
    if (this.pieChart) {
      this.pieChart.destroy();
      this.pieChart = null;
    }

    const canvas = this.pieChartRef?.nativeElement;
    if (!canvas) return;

    const labels = Object.keys(this.reporte!.ventasPorCategoria);
    const valores = Object.values(this.reporte!.ventasPorCategoria).map(v => Number(v));
    const colores = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];

    this.pieChart = new Chart(canvas, {
      type: 'pie',
      data: {
        labels,
        datasets: [{
          data: valores,
          backgroundColor: colores.slice(0, labels.length)
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'bottom' },
          tooltip: {
            callbacks: {
              label: (context) => `S/${Number(context.raw).toFixed(2)}`
            }
          }
        }
      }
    });
  }

  formatearMoneda(valor: number): string {
    return `S/${valor.toFixed(2)}`;
  }
}