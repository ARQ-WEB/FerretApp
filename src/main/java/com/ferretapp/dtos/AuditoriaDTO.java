package com.ferretapp.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditoriaDTO {

    private Integer idAuditoria;
    private Integer idUsuario;
    private String nombreUsuario;
    private String accion;
    private String entidad;
    private String detalle;
    private LocalDateTime fechaHora;
}