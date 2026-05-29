package com.ferretapp.dtos;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RolDTO {

    private Integer idRol;
    private String nombre;
}
