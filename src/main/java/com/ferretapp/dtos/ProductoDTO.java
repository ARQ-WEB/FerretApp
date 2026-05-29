package com.ferretapp.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductoDTO {

    private Integer idProducto;
    private String sku;
    private String nombre;
    private String descripcion;
    private Integer stockActual;
    private Integer stockMinimo;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private Boolean eliminado;

    // IDs de categorías y proveedores asociados (4FN)
    private Set<Integer> idCategorias;
    private Set<Integer> idProveedores;
}