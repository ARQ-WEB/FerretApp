package com.ferretapp.servicios;

import com.ferretapp.dtos.ClienteDTO;
import com.ferretapp.entidades.Cliente;
import com.ferretapp.repositorios.ClienteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    // ── Listar activos ───────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarActivos() {
        return clienteRepositorio.findByEliminadoFalse()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Listar todos ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Buscar por nombre ────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepositorio
                .findByNombreCompletoContainingIgnoreCaseAndEliminadoFalse(nombre)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Crear ────────────────────────────────────────────────
    @Transactional
    public ClienteDTO crear(ClienteDTO dto) {
        // Validar email duplicado
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (clienteRepositorio.existsByEmailIgnoreCase(dto.getEmail())) {
                throw new IllegalArgumentException(
                        "Ya existe un cliente con el email: " + dto.getEmail());
            }
        }

        Cliente cliente = Cliente.builder()
                .nombreCompleto(dto.getNombreCompleto().trim())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();

        return toDTO(clienteRepositorio.save(cliente));
    }

    // ── Actualizar ───────────────────────────────────────────
    @Transactional
    public ClienteDTO actualizar(Integer id, ClienteDTO dto) {
        Cliente cliente = buscarOFallar(id);

        // Validar email duplicado solo si cambió
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            boolean emailCambio = !dto.getEmail().equalsIgnoreCase(cliente.getEmail());
            if (emailCambio && clienteRepositorio.existsByEmailIgnoreCase(dto.getEmail())) {
                throw new IllegalArgumentException(
                        "Ya existe un cliente con el email: " + dto.getEmail());
            }
        }

        cliente.setNombreCompleto(dto.getNombreCompleto().trim());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());

        return toDTO(clienteRepositorio.save(cliente));
    }

    // ── Eliminar lógico ──────────────────────────────────────
    @Transactional
    public void eliminar(Integer id) {
        Cliente cliente = buscarOFallar(id);
        cliente.setEliminado(true);
        clienteRepositorio.save(cliente);
    }

    // ── Helpers ──────────────────────────────────────────────
    public Cliente buscarOFallar(Integer id) {
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cliente no encontrado: " + id));
    }

    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .idCliente(c.getIdCliente())
                .nombreCompleto(c.getNombreCompleto())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .eliminado(c.getEliminado())
                .build();
    }
}
