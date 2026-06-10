import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { AuditoriaService } from '../../servicios/auditoria.service';
import { AuditoriaDTO } from '../../modelos/interfaces';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './auditoria.html',
  styleUrl: './auditoria.css'
})
export class Auditoria implements OnInit, AfterViewInit {

  auditorias: AuditoriaDTO[] = [];
  dataSource = new MatTableDataSource<AuditoriaDTO>();
  displayedColumns = ['fecha', 'usuario', 'accion', 'entidad', 'descripcion'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  cargando = true;

  filtroUsuario = '';
  filtroAccion = '';
  filtroEntidad = '';
  filtroDesde = '';
  filtroHasta = '';

  constructor(private auditoriaService: AuditoriaService) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.auditoriaService.getAuditorias().subscribe({
      next: (data) => {
        this.auditorias = data;
        this.dataSource.data = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  aplicarFiltros(): void {
    let filtradas = [...this.auditorias];

    if (this.filtroUsuario.trim()) {
      const texto = this.filtroUsuario.toLowerCase();
      filtradas = filtradas.filter(a =>
        (a.nombreUsuario ?? '').toLowerCase().includes(texto)
      );
    }

    if (this.filtroAccion) {
      filtradas = filtradas.filter(a =>
        (a.accion ?? '').toUpperCase() === this.filtroAccion.toUpperCase()
      );
    }

    if (this.filtroEntidad.trim()) {
      const texto = this.filtroEntidad.toLowerCase();
      filtradas = filtradas.filter(a =>
        (a.entidad ?? '').toLowerCase().includes(texto)
      );
    }

    if (this.filtroDesde) {
      const desde = new Date(this.filtroDesde + 'T00:00:00');
      filtradas = filtradas.filter(a => a.fecha && new Date(a.fecha) >= desde);
    }

    if (this.filtroHasta) {
      const hasta = new Date(this.filtroHasta + 'T23:59:59');
      filtradas = filtradas.filter(a => a.fecha && new Date(a.fecha) <= hasta);
    }

    this.dataSource.data = filtradas;
  }

  limpiarFiltros(): void {
    this.filtroUsuario = '';
    this.filtroAccion = '';
    this.filtroEntidad = '';
    this.filtroDesde = '';
    this.filtroHasta = '';
    this.dataSource.data = this.auditorias;
  }

  get totalAcciones(): number {
    return this.auditorias.length;
  }

  get accionesHoy(): number {
    const hoy = new Date().toISOString().split('T')[0];
    return this.auditorias.filter(a => (a.fecha ?? '').startsWith(hoy)).length;
  }

  get usuariosActivos(): number {
    const ids = new Set(this.auditorias.map(a => a.idUsuario).filter(id => id !== undefined));
    return ids.size;
  }

  get ultimaAccion(): string {
    if (this.auditorias.length === 0) return '—';
    const ordenadas = [...this.auditorias].sort((a, b) =>
      (b.fecha ?? '').localeCompare(a.fecha ?? '')
    );
    return this.formatearTiempoRelativo(ordenadas[0].fecha);
  }

  claseAccion(accion: string | undefined): string {
    const a = (accion ?? '').toUpperCase();
    if (a.includes('CREAR') || a.includes('CREATE')) return 'badge-crear';
    if (a.includes('ACTUALIZAR') || a.includes('EDITAR') || a.includes('UPDATE')) return 'badge-editar';
    if (a.includes('ELIMINAR') || a.includes('DELETE')) return 'badge-eliminar';
    if (a.includes('LOGIN')) return 'badge-login';
    if (a.includes('LOGOUT')) return 'badge-logout';
    return 'badge-otro';
  }

  formatearFecha(fecha: string | undefined): string {
    if (!fecha) return '—';
    const d = new Date(fecha);
    const dia = String(d.getDate()).padStart(2, '0');
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const anio = d.getFullYear();
    const hora = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${dia}/${mes}/${anio} ${hora}:${min}`;
  }

  private formatearTiempoRelativo(fecha: string | undefined): string {
    if (!fecha) return '—';
    const ahora = new Date();
    const f = new Date(fecha);
    const diffMin = Math.floor((ahora.getTime() - f.getTime()) / 60000);

    if (diffMin < 1) return 'Hace un momento';
    if (diffMin < 60) return `Hace ${diffMin} min`;
    if (diffMin < 1440) return `Hace ${Math.floor(diffMin / 60)} h`;
    return `Hace ${Math.floor(diffMin / 1440)} d`;
  }

  inicialUsuario(nombre: string | undefined): string {
    return (nombre ?? '').trim().charAt(0).toUpperCase() || '?';
  }
}
