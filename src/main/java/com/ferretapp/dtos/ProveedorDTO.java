package com.ferretapp.dtos;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProveedorDTO {

    private Integer idProveedor;
    private String nombreEmpresa;
    private String nombreContacto;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean eliminado;
}