package com.ferretapp.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PRODUCTO")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCTO")
    private Integer idProducto;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "stock_actual", nullable = false)
    @Builder.Default
    private Integer stockActual = 0;

    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Column(name = "precio_costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCosto;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @Column(name = "eliminado", nullable = false)
    @Builder.Default
    private Boolean eliminado = false;

    // 4FN: tabla de unión PRODUCTO_CATEGORIA
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "PRODUCTO_CATEGORIA",
            joinColumns        = @JoinColumn(name = "ID_PRODUCTO"),
            inverseJoinColumns = @JoinColumn(name = "ID_CATEGORIA")
    )
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    // 4FN: tabla de unión PRODUCTO_PROVEEDOR
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "PRODUCTO_PROVEEDOR",
            joinColumns        = @JoinColumn(name = "ID_PRODUCTO"),
            inverseJoinColumns = @JoinColumn(name = "ID_PROVEEDOR")
    )
    @Builder.Default
    private Set<Proveedor> proveedores = new HashSet<>();

    // Detalles de pedidos relacionados
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<DetallePedido> detallesPedido = new HashSet<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaCreacion = ahora;
        this.fechaModificacion = ahora;
        if (this.eliminado == null) this.eliminado = false;
        if (this.stockActual == null) this.stockActual = 0;
        if (this.stockMinimo == null) this.stockMinimo = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}