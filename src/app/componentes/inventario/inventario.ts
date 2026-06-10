import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../servicios/producto.service';
import { CategoriaService } from '../../servicios/categoria.service';
import { ProveedorService } from '../../servicios/proveedor.service';
import { ProductoDTO, CategoriaDTO, ProveedorDTO } from '../../modelos/interfaces';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css'
})
export class Inventario implements OnInit {

  productos: ProductoDTO[] = [];
  productosFiltrados: ProductoDTO[] = [];
  categorias: CategoriaDTO[] = [];
  proveedores: ProveedorDTO[] = [];

  cargando = true;
  busqueda = '';
  mostrarModal = false;
  modoEdicion = false;
  guardando = false;

  productoForm: ProductoDTO = this.formVacio();

  constructor(
    private productoService: ProductoService,
    private categoriaService: CategoriaService,
    private proveedorService: ProveedorService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();

    
    this.productoService.getListaCambio().subscribe(data => {
      this.productos = data;
      this.productosFiltrados = data;
    });
  }

  cargarDatos(): void {
    this.cargando = true;
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.productosFiltrados = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });

    this.categoriaService.getCategorias().subscribe({
      next: (data) => this.categorias = data
    });

    this.proveedorService.getProveedores().subscribe({
      next: (data) => this.proveedores = data
    });
  }

  filtrar(): void {
    const texto = this.busqueda.toLowerCase();
    this.productosFiltrados = this.productos.filter(p =>
      p.nombre.toLowerCase().includes(texto) ||
      p.sku.toLowerCase().includes(texto)
    );
  }

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.productoForm = this.formVacio();
    this.mostrarModal = true;
  }

  abrirModalEditar(producto: ProductoDTO): void {
  this.modoEdicion = true;
  this.productoForm = {
    ...producto,
    idCategorias: producto.idCategorias ? Array.from(producto.idCategorias) : [],
    idProveedores: producto.idProveedores ? Array.from(producto.idProveedores) : []
  };
  this.mostrarModal = true;
}

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardar(): void {
    this.guardando = true;

    if (this.modoEdicion && this.productoForm.idProducto) {
      this.productoService.actualizar(this.productoForm.idProducto, this.productoForm).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
      
          this.productoService.getProductos().subscribe(data => {
            this.productoService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    } else {
      this.productoService.crear(this.productoForm).subscribe({
        next: () => {
          this.guardando = false;
          this.cerrarModal();
          
          this.productoService.getProductos().subscribe(data => {
            this.productoService.setList(data);
          });
        },
        error: () => this.guardando = false
      });
    }
  }

  eliminar(producto: ProductoDTO): void {
    if (confirm(`¿Eliminar "${producto.nombre}"?`)) {
      this.productoService.eliminar(producto.idProducto!).subscribe({
        next: () => {
          
          this.productoService.getProductos().subscribe(data => {
            this.productoService.setList(data);
          });
        }
      });
    }
  }

  stockBajo(producto: ProductoDTO): boolean {
    return producto.stockActual <= producto.stockMinimo;
  }

  getNombreCategoria(producto: ProductoDTO): string {
  if (!producto.idCategorias || producto.idCategorias.length === 0) return '—';
  const idCat = producto.idCategorias[0];
  return this.categorias.find(c => c.idCategoria === idCat)?.nombre ?? '—';
  }

  get categoriaSeleccionada(): number | null {
    return this.productoForm.idCategorias?.[0] ?? null;
  }

  set categoriaSeleccionada(val: number | null) {
    this.productoForm.idCategorias = val ? [val] : [];
  }

  get proveedorSeleccionado(): number | null {
    return this.productoForm.idProveedores?.[0] ?? null;
  }

  set proveedorSeleccionado(val: number | null) {
    this.productoForm.idProveedores = val ? [+val] : [];
  }

  formVacio(): ProductoDTO {
    return {
      sku: '',
      nombre: '',
      descripcion: '',
      stockActual: 0,
      stockMinimo: 0,
      precioCosto: 0,
      precioVenta: 0,
      idCategorias: [],
      idProveedores: []
    };
  }
}
