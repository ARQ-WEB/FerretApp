package com.ferretapp.controladores;

import com.ferretapp.dtos.UsuarioDTO;
import com.ferretapp.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioServicio.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioServicio.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Integer id,
                                                 @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioServicio.eliminar(id);
        log.info("Usuario eliminado lógicamente: id={}", id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/usuarios/conteo-por-rol
    @GetMapping("/conteo-por-rol")
    public ResponseEntity<Map<String, Long>> conteoPorRol() {
        return ResponseEntity.ok(usuarioServicio.conteoPorRol());
    }

    // PATCH /api/usuarios/{id}/password?nuevaPassword=...
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Integer id,
                                                @RequestParam String nuevaPassword) {
        usuarioServicio.cambiarPassword(id, nuevaPassword);
        log.info("Contraseña actualizada para usuario id={}", id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/usuarios/{id}/estado?eliminado=true|false
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO> cambiarEstado(@PathVariable Integer id,
                                                    @RequestParam Boolean eliminado) {
        UsuarioDTO dto = usuarioServicio.cambiarEstado(id, eliminado);
        log.info("Estado de usuario id={} cambiado a eliminado={}", id, eliminado);
        return ResponseEntity.ok(dto);
    }
}