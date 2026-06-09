import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { ProveedorService } from '../../servicios/proveedor.service';
import { ProveedorDTO } from '../../modelos/interfaces';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './proveedores.html',
  styleUrl: './proveedores.css'
})
export class Proveedores implements OnInit {

  proveedores: ProveedorDTO[] = [];
  proveedoresFiltrados: ProveedorDTO[] = [];
  dataSource = new MatTableDataSource<ProveedorDTO>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  cargando = true;
  busqueda = '';
  mostrarModal = false;
  modoEdicion = false;
  guardando = false;

  proveedorForm: ProveedorDTO = this.formVacio();

  constructor(private proveedorService: ProveedorService) {}

  ngOnInit(): void {
    this.cargarDatos();
    this.proveedorService.getListaCambio().subscribe(data => {
      this.dataSource.data = data;
      this.proveedores = data;
      this.proveedoresFiltrados = data;
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.proveedorService.getProveedores().subscribe({
      next: (data) => {
        this.proveedores = data;
        this.proveedoresFiltrados = data;
        this.dataSource.data = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  filtrar(): void {
    const texto = this.busqueda.toLowerCase();
    this.proveedoresFiltrados = this.proveedores.filter(p =>
      p.nombreEmpresa.toLowerCase().includes(texto) ||
      (p.nombreContacto?.toLowerCase().includes(texto) ?? false) ||
      (p.email?.toLowerCase().includes(texto) ?? false)
    );
    this.dataSource.data = this.proveedoresFiltrados;
  }

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.proveedorForm = this.formVacio();
    this.mostrarModal = true;
  }

  abrirModalEditar(proveedor: ProveedorDTO): void {
    this.modoEdicion = true;
    this.proveedorForm = { ...proveedor };
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardar(): void {
    this.guardando = true;

    if (this.modoEdicion && this.proveedorForm.idProveedor) {
      this.proveedorService.actualizar(this.proveedorForm.idProveedor, this.proveedorForm).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          this.proveedorService.getProveedores().subscribe(data => {
            this.proveedorService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    } else {
      this.proveedorService.crear(this.proveedorForm).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          this.proveedorService.getProveedores().subscribe(data => {
            this.proveedorService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    }
  }

  eliminar(proveedor: ProveedorDTO): void {
    if (confirm(`¿Eliminar "${proveedor.nombreEmpresa}"?`)) {
      this.proveedorService.eliminar(proveedor.idProveedor!).subscribe({
        next: () => {
          this.proveedorService.getProveedores().subscribe(data => {
            this.proveedorService.setList(data);
          });
        }
      });
    }
  }

  formVacio(): ProveedorDTO {
    return {
      nombreEmpresa: '',
      nombreContacto: '',
      email: '',
      telefono: '',
      direccion: ''
    };
  }
}