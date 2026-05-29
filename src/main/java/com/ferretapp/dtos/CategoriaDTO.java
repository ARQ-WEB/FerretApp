package com.ferretapp.dtos;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaDTO {

    private Integer idCategoria;
    private String nombre;
    private Boolean eliminado;
}