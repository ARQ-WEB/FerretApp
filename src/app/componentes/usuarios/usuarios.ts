import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { UsuarioService } from '../../servicios/usuario.service';
import { AuthService } from '../../servicios/auth.service';
import { UsuarioDTO, RolDTO } from '../../modelos/interfaces';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {

  usuarios: UsuarioDTO[] = [];
  roles: RolDTO[] = [];
  dataSource = new MatTableDataSource<UsuarioDTO>();
  displayedColumns = ['usuario', 'email', 'rol', 'permisos', 'acciones'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  cargando = true;
  mostrarModal = false;
  modoEdicion = false;
  guardando = false;

  usuarioForm: UsuarioDTO = this.formVacio();
  rolSeleccionado: number | null = null;

  constructor(
    private usuarioService: UsuarioService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
    this.usuarioService.getListaCambio().subscribe(data => {
      this.usuarios = data;
      this.dataSource.data = data;
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.dataSource.data = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });

    this.usuarioService.getRoles().subscribe({
      next: (data) => this.roles = data
    });
  }

  get totalUsuarios(): number {
    return this.usuarios.length;
  }

  get totalAdmins(): number {
    return this.usuarios.filter(u => u.nombreRol === 'ADMINISTRADOR').length;
  }

  get totalVendedores(): number {
    return this.usuarios.filter(u => u.nombreRol === 'VENDEDOR').length;
  }

  esAdmin(usuario: UsuarioDTO): boolean {
    return usuario.nombreRol === 'ADMINISTRADOR';
  }

  esTuCuenta(usuario: UsuarioDTO): boolean {
    return usuario.email === this.authService.getUsuarioActual()?.username;
  }

  inicialNombre(usuario: UsuarioDTO): string {
    const nombre = usuario.nombreCompleto ?? usuario.nombre ?? '';
    return nombre.charAt(0).toUpperCase();
  }

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.usuarioForm = this.formVacio();
    this.rolSeleccionado = null;
    this.mostrarModal = true;
  }

  abrirModalEditar(usuario: UsuarioDTO): void {
    this.modoEdicion = true;
    this.usuarioForm = { ...usuario };
    this.rolSeleccionado = usuario.idRol ?? null;
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardar(): void {
    this.guardando = true;

    const usuarioData = {
      nombreCompleto: this.usuarioForm.nombreCompleto,
      email: this.usuarioForm.email,
      contrasena: this.usuarioForm.contrasena,
      idRol: this.rolSeleccionado,
      eliminado: false
    };

    if (this.modoEdicion && this.usuarioForm.idUsuario) {
      this.usuarioService.actualizar(this.usuarioForm.idUsuario, usuarioData as any).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          this.usuarioService.getUsuarios().subscribe(data => {
            this.usuarioService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    } else {
      this.usuarioService.crear(usuarioData as any).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          this.usuarioService.getUsuarios().subscribe(data => {
            this.usuarioService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    }
  }

  eliminar(usuario: UsuarioDTO): void {
    if (confirm(`¿Eliminar usuario "${usuario.nombreCompleto}"?`)) {
      this.usuarioService.eliminar(usuario.idUsuario!).subscribe({
        next: () => {
          this.usuarioService.getUsuarios().subscribe(data => {
            this.usuarioService.setList(data);
          });
        }
      });
    }
  }

  formVacio(): UsuarioDTO {
    return {
      nombreCompleto: '',
      email: '',
      contrasena: '',
      roles: []
    };
  }
}