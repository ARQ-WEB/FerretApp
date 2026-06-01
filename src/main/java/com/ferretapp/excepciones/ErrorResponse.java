package com.ferretapp.excepciones;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> errores
) {
    public ErrorResponse(int status, String error, String mensaje, String ruta) {
        this(LocalDateTime.now(), status, error, mensaje, ruta, null);
    }

    public ErrorResponse(int status, String error, String mensaje, String ruta,
                         Map<String, String> errores) {
        this(LocalDateTime.now(), status, error, mensaje, ruta, errores);
    }
}
