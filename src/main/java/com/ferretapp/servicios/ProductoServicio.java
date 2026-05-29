package com.ferretapp.servicios;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.entidades.Categoria;
import com.ferretapp.entidades.Producto;
import com.ferretapp.entidades.Proveedor;
import com.ferretapp.repositorios.CategoriaRepositorio;
import com.ferretapp.repositorios.ProductoRepositorio;
import com.ferretapp.repositorios.ProveedorRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final ProveedorRepositorio proveedorRepositorio;

    // ── Listar ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepositorio.findByEliminadoFalse()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarConStockBajo() {
        return productoRepositorio.findProductosConStockBajo()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(Integer idCategoria) {
        return productoRepositorio.findByCategoriaId(idCategoria)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorProveedor(Integer idProveedor) {
        return productoRepositorio.findByProveedorId(idProveedor)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorSku(String sku) {
        Producto p = productoRepositorio.findBySkuAndEliminadoFalse(sku)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con SKU: " + sku));
        return toDTO(p);
    }

    // ── Crear ────────────────────────────────────────────────
    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        if (productoRepositorio.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Ya existe un producto con SKU: " + dto.getSku());
        }

        Producto producto = Producto.builder()
                .sku(dto.getSku())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .stockActual(dto.getStockActual() != null ? dto.getStockActual() : 0)
                .stockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 0)
                .precioCosto(dto.getPrecioCosto())
                .precioVenta(dto.getPrecioVenta())
                .build();

        // 4FN: asignar categorías
        if (dto.getIdCategorias() != null && !dto.getIdCategorias().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>(categoriaRepositorio.findAllById(dto.getIdCategorias()));
            producto.setCategorias(categorias);
        }

        // 4FN: asignar proveedores
        if (dto.getIdProveedores() != null && !dto.getIdProveedores().isEmpty()) {
            Set<Proveedor> proveedores = new HashSet<>(proveedorRepositorio.findAllById(dto.getIdProveedores()));
            producto.setProveedores(proveedores);
        }

        return toDTO(productoRepositorio.save(producto));
    }

    // ── Actualizar ───────────────────────────────────────────
    @Transactional
    public ProductoDTO actualizar(Integer id, ProductoDTO dto) {
        Producto producto = buscarOFallar(id);
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setPrecioCosto(dto.getPrecioCosto());
        producto.setPrecioVenta(dto.getPrecioVenta());

        if (dto.getIdCategorias() != null) {
            Set<Categoria> categorias = new HashSet<>(categoriaRepositorio.findAllById(dto.getIdCategorias()));
            producto.setCategorias(categorias);
        }
        if (dto.getIdProveedores() != null) {
            Set<Proveedor> proveedores = new HashSet<>(proveedorRepositorio.findAllById(dto.getIdProveedores()));
            producto.setProveedores(proveedores);
        }

        return toDTO(productoRepositorio.save(producto));
    }

    // ── Actualizar stock ─────────────────────────────────────
    @Transactional
    public void actualizarStock(Integer idProducto, int cantidad) {
        Producto producto = buscarOFallar(idProducto);
        int nuevoStock = producto.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + idProducto);
        }
        producto.setStockActual(nuevoStock);
        productoRepositorio.save(producto);
    }

    // ── Eliminar lógico ──────────────────────────────────────
    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarOFallar(id);
        producto.setEliminado(true);
        productoRepositorio.save(producto);
    }

    // ── Helpers ──────────────────────────────────────────────
    public Producto buscarOFallar(Integer id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
    }

    private ProductoDTO toDTO(Producto p) {
        Set<Integer> idCategorias = p.getCategorias().stream()
                .map(Categoria::getIdCategoria).collect(Collectors.toSet());
        Set<Integer> idProveedores = p.getProveedores().stream()
                .map(Proveedor::getIdProveedor).collect(Collectors.toSet());

        return ProductoDTO.builder()
                .idProducto(p.getIdProducto())
                .sku(p.getSku())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo())
                .precioCosto(p.getPrecioCosto())
                .precioVenta(p.getPrecioVenta())
                .eliminado(p.getEliminado())
                .idCategorias(idCategorias)
                .idProveedores(idProveedores)
                .build();
    }
}