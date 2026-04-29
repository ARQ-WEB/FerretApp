package com.ferretapp.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_PEDIDO")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_PEDIDO")
    private Integer idDetallePedido;

    // Relación con Pedido
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PEDIDO", nullable = false)
    private Pedido pedido;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRODUCTO", nullable = false)
    private Producto producto;

    // 5FN: ID_PROVEEDOR desnormalizado para FK compuesta hacia PRODUCTO_PROVEEDOR
    // Debe coincidir con el proveedor del encabezado (validado por trigger en BD)
    @Column(name = "ID_PROVEEDOR", nullable = false)
    private Integer idProveedor;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // subtotal es columna GENERATED en la BD → insertable=false, updatable=false
    @Column(name = "subtotal", insertable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}