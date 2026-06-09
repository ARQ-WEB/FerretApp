import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../../servicios/pedido.service';
import { ProductoService } from '../../servicios/producto.service';
import { ProveedorService } from '../../servicios/proveedor.service';
import { PedidoDTO, DetallePedidoDTO, ProductoDTO, ProveedorDTO } from '../../modelos/interfaces';

interface ItemPedido {
  idProducto: number;
  nombreProducto: string;
  precioCosto: number;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos implements OnInit {

  pedidos: PedidoDTO[] = [];
  productos: ProductoDTO[] = [];
  proveedores: ProveedorDTO[] = [];
  cargando = true;
  mostrarModal = false;
  guardando = false;

  proveedorSeleccionado: number | null = null;
  fechaEntrega = '';
  itemsPedido: ItemPedido[] = [];
  productoSeleccionado: number | null = null;
  cantidadItem = 1;

  constructor(
    private pedidoService: PedidoService,
    private productoService: ProductoService,
    private proveedorService: ProveedorService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
    this.pedidoService.getListaCambio().subscribe(data => {
      this.pedidos = data;
    });
  }

  cargarDatos(): void {
    this.cargando = true;
    this.pedidoService.getPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });

    this.productoService.getProductos().subscribe({
      next: (data) => this.productos = data
    });

    this.proveedorService.getProveedores().subscribe({
      next: (data) => this.proveedores = data
    });
  }

  abrirModal(): void {
    this.proveedorSeleccionado = null;
    this.fechaEntrega = '';
    this.itemsPedido = [];
    this.productoSeleccionado = null;
    this.cantidadItem = 1;
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  agregarItem(): void {
    if (!this.productoSeleccionado) return;
    const producto = this.productos.find(p => p.idProducto === +this.productoSeleccionado!);
    if (!producto) return;

    const existente = this.itemsPedido.find(i => i.idProducto === producto.idProducto);
    if (existente) {
      existente.cantidad += this.cantidadItem;
      existente.subtotal = existente.cantidad * existente.precioCosto;
    } else {
      this.itemsPedido.push({
        idProducto: producto.idProducto!,
        nombreProducto: producto.nombre,
        precioCosto: producto.precioCosto,
        cantidad: this.cantidadItem,
        subtotal: this.cantidadItem * producto.precioCosto
      });
    }
    this.productoSeleccionado = null;
    this.cantidadItem = 1;
  }

  quitarItem(index: number): void {
    this.itemsPedido.splice(index, 1);
  }

  get totalPedido(): number {
    return this.itemsPedido.reduce((acc, i) => acc + i.subtotal, 0);
  }

  crearPedido(): void {
    if (!this.proveedorSeleccionado || !this.fechaEntrega || this.itemsPedido.length === 0) return;
    this.guardando = true;

    const pedido: PedidoDTO = {
      idProveedor: this.proveedorSeleccionado,
      fechaEntregaEsperada: this.fechaEntrega + 'T00:00:00',
      estado: 'Pendiente',
      detalles: this.itemsPedido.map(i => ({
        idProducto: i.idProducto,
        idProveedor: this.proveedorSeleccionado!,
        cantidad: i.cantidad,
        precioUnitario: i.precioCosto
      }))
    };

    this.pedidoService.crear(pedido).subscribe({
      next: () => {
        this.guardando = false;
        this.cerrarModal();
        this.pedidoService.getPedidos().subscribe(data => {
          this.pedidoService.setList(data);
        });
      },
      error: () => this.guardando = false
    });
  }

  marcarRecibido(pedido: PedidoDTO): void {
    this.pedidoService.cambiarEstado(pedido.idPedido!, 'Recibido').subscribe({
      next: () => {
        this.pedidoService.getPedidos().subscribe(data => {
          this.pedidoService.setList(data);
        });
      }
    });
  }

  eliminar(pedido: PedidoDTO): void {
    if (confirm(`¿Eliminar pedido #${pedido.idPedido}?`)) {
      this.pedidoService.eliminar(pedido.idPedido!).subscribe({
        next: () => {
          this.pedidoService.getPedidos().subscribe(data => {
            this.pedidoService.setList(data);
          });
        }
      });
    }
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-PE');
  }

  formatearMoneda(valor: number): string {
    return `S/${valor.toFixed(2)}`;
  }

  nombreProveedor(id: number): string {
    return this.proveedores.find(p => p.idProveedor === id)?.nombreEmpresa ?? '—';
  }
}
