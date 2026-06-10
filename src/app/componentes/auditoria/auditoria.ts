import { Component, OnInit, ViewChild } from '@angular/core';
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
export class Auditoria implements OnInit {

  auditorias: AuditoriaDTO[] = [];
  dataSource = new MatTableDataSource<AuditoriaDTO>();
  displayedColumns = ['fecha', 'hora', 'usuario', 'accion', 'detalles'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  cargando = true;
  busqueda = '';
  fechaInicio = '';
  fechaFin = '';

  constructor(private auditoriaService: AuditoriaService) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  ngAfterViewInit(): void {
    
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = (data: AuditoriaDTO, filter: string) => {
      const texto = filter.toLowerCase();
      return (
        (data.nombreUsuario?.toLowerCase().includes(texto) ?? false) ||
        (data.accion?.toLowerCase().includes(texto) ?? false) ||
        (data.detalle?.toLowerCase().includes(texto) ?? false)
      );
    };
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

  filtrar(): void {
  const texto = this.busqueda.trim().toLowerCase();
  
  this.dataSource.filterPredicate = (data: AuditoriaDTO, filter: string) => {
    const textoMatch = !texto || (
      (data.nombreUsuario?.toLowerCase().includes(texto) ?? false) ||
      (data.accion?.toLowerCase().includes(texto) ?? false) ||
      (data.detalle?.toLowerCase().includes(texto) ?? false)
    );

    const fechaRegistro = data.fechaHora ? new Date(data.fechaHora) : null;
    const desdeMatch = !this.fechaInicio || (fechaRegistro !== null && fechaRegistro >= new Date(this.fechaInicio));
    const hastaMatch = !this.fechaFin || (fechaRegistro !== null && fechaRegistro <= new Date(this.fechaFin + 'T23:59:59'));

    return textoMatch && desdeMatch && hastaMatch;
  };

  this.dataSource.filter = texto || this.fechaInicio || this.fechaFin ? 'activo' : '';
  }
  
  get registrosFiltrados(): number {
  return this.dataSource.filteredData.length;
  }

  get totalAcciones(): number {
    return this.auditorias.length;
  }

  get usuariosActivos(): number {
    const usuarios = new Set(this.auditorias.map(a => a.nombreUsuario));
    return usuarios.size;
  }

  get accionesHoy(): number {
    const hoy = new Date().toISOString().split('T')[0];
    return this.auditorias.filter(a => a.fechaHora?.startsWith(hoy)).length;
  }

  formatearFecha(fechaHora: string): string {
    if (!fechaHora) return '—';
    const d = new Date(fechaHora);
    return `${d.getDate()}/${d.getMonth() + 1}/${d.getFullYear()}`;
  }

  formatearHora(fechaHora: string): string {
    if (!fechaHora) return '—';
    const d = new Date(fechaHora);
    return d.toTimeString().split(' ')[0];
  }

  inicialNombre(nombre: string): string {
    return nombre?.charAt(0).toUpperCase() ?? '?';
  }
}