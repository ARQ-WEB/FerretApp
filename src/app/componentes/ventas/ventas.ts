import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VentaService } from '../../servicios/venta.service';
import { ProductoService } from '../../servicios/producto.service';
import { AuthService } from '../../servicios/auth.service';
import { VentaDTO, ProductoDTO, DetalleVentaDTO } from '../../modelos/interfaces';

interface ItemCarrito {
  idProducto: number;
  nombreProducto: string;
  precioUnitario: number;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
  styleUrl: './ventas.css'
})
export class Ventas implements OnInit {

  productos: ProductoDTO[] = [];
  ventasRecientes: VentaDTO[] = [];
  carrito: ItemCarrito[] = [];

  productoSeleccionado: number | null = null;
  cantidad = 1;
  cargando = true;
  procesando = false;

  ventaDetalle: VentaDTO | null = null;
  mostrarDetalle = false;

  constructor(
    private ventaService: VentaService,
    private productoService: ProductoService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => this.productos = data
    });

    this.ventaService.getVentasRecientes().subscribe({
      next: (data) => {
        this.ventasRecientes = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  agregarAlCarrito(): void {
    if (!this.productoSeleccionado) return;

    const producto = this.productos.find(p => p.idProducto === +this.productoSeleccionado!);
    if (!producto) return;

    const existente = this.carrito.find(i => i.idProducto === producto.idProducto);
    if (existente) {
      existente.cantidad += this.cantidad;
      existente.subtotal = existente.cantidad * existente.precioUnitario;
    } else {
      this.carrito.push({
        idProducto: producto.idProducto!,
        nombreProducto: producto.nombre,
        precioUnitario: producto.precioVenta,
        cantidad: this.cantidad,
        subtotal: this.cantidad * producto.precioVenta
      });
    }

    this.productoSeleccionado = null;
    this.cantidad = 1;
  }

  eliminarDelCarrito(index: number): void {
    this.carrito.splice(index, 1);
  }

  get totalCarrito(): number {
    return this.carrito.reduce((acc, i) => acc + i.subtotal, 0);
  }

  get unidadesTotales(): number {
    return this.carrito.reduce((acc, i) => acc + i.cantidad, 0);
  }

  completarVenta(): void {
    if (this.carrito.length === 0) return;

    const usuario = this.authService.getUsuarioActual();
    this.procesando = true;

    const venta: VentaDTO = {
      idUsuario: 1,
      detalles: this.carrito.map(i => ({
        idProducto: i.idProducto,
        cantidad: i.cantidad
      }))
    };

    this.ventaService.crear(venta).subscribe({
      next: () => {
        this.carrito = [];
        this.procesando = false;
        this.cargarDatos();
      },
      error: () => this.procesando = false
    });
  }

  verDetalle(venta: VentaDTO): void {
    this.ventaService.getVenta(venta.idVenta!).subscribe({
      next: (data) => {
        this.ventaDetalle = data;
        this.mostrarDetalle = true;
      }
    });
  }

  cerrarDetalle(): void {
    this.mostrarDetalle = false;
    this.ventaDetalle = null;
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatearMoneda(valor: number): string {
    return `S/${valor.toFixed(2)}`;
  }
}
