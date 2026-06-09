// --- AUTH ---
export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  jwt: string;
  roles: string[];
}

export interface UsuarioSesion {
  username: string;
  roles: string[];
  token: string;
}

// --- CATEGORÍA ---
export interface CategoriaDTO {
  idCategoria?: number;
  nombre: string;
  eliminado?: boolean;
}

// --- PROVEEDOR ---
export interface ProveedorDTO {
  idProveedor?: number;
  nombreEmpresa: string;
  nombreContacto?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  eliminado?: boolean;
}

// --- PRODUCTO ---
export interface ProductoDTO {
  idProducto?: number;
  sku: string;
  nombre: string;
  descripcion?: string;
  stockActual: number;
  stockMinimo: number;
  precioCosto: number;
  precioVenta: number;
  eliminado?: boolean;
  idCategorias?: number[];
  idProveedores?: number[];
}

// --- CLIENTE ---
export interface ClienteDTO {
  idCliente?: number;
  nombreCompleto: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  eliminado?: boolean;
}

// --- VENTA ---
export interface DetalleVentaDTO {
  idDetalleVenta?: number;
  idProducto: number;
  nombreProducto?: string;
  cantidad: number;
  precioUnitario?: number;
  subtotal?: number;
}

export interface VentaDTO {
  idVenta?: number;
  idUsuario: number;
  nombreUsuario?: string;
  idCliente?: number;
  nombreCliente?: string;
  fechaVenta?: string;
  total?: number;
  detalles?: DetalleVentaDTO[];
}

// --- PEDIDO ---
export interface DetallePedidoDTO {
  idDetallePedido?: number;
  idProducto: number;
  idProveedor: number;
  nombreProducto?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal?: number;
}

export interface PedidoDTO {
  idPedido?: number;
  idProveedor: number;
  nombreProveedor?: string;
  fechaPedido?: string;
  fechaEntregaEsperada?: string;
  estado: 'Pendiente' | 'Recibido' | 'Cancelado';
  total?: number;
  detalles: DetallePedidoDTO[];
}

// --- USUARIO ---
export interface RolDTO {
  idRol?: number;
  nombre: string;
}

export interface UsuarioDTO {
  idUsuario?: number;
  idRol?: number;
  nombreRol?: string;
  nombreCompleto?: string;
  nombre?: string;
  email: string;
  contrasena?: string;
  password?: string;
  eliminado?: boolean;
  roles?: RolDTO[];
}

// --- AUDITORÍA ---
export interface AuditoriaDTO {
  idAuditoria?: number;
  idUsuario?: number;
  nombreUsuario?: string;
  accion?: string;
  entidad?: string;
  descripcion?: string;
  fecha?: string;
}

// --- REPORTE ---
export interface ProductoVendidoDTO {
  nombreProducto: string;
  unidadesVendidas: number;
  ingresoGenerado: number;
}

export interface ReporteDTO {
  ingresosTotales: number;
  ventasTotales: number;
  unidadesVendidas: number;
  ventaPromedio: number;
  ventasPorDia: { [fecha: string]: number };
  ventasPorCategoria: { [fecha: string]: number };
  productosMasVendidos: ProductoVendidoDTO[];
  productosStockBajo: ProductoDTO[];
}