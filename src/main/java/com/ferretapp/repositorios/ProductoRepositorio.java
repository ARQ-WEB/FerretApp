package com.ferretapp.repositorios;

import com.ferretapp.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

    List<Producto> findByEliminadoFalse();

    Optional<Producto> findBySkuAndEliminadoFalse(String sku);

    boolean existsBySku(String sku);

    // Productos con stock por debajo del mínimo
    @Query("SELECT p FROM Producto p WHERE p.eliminado = false AND p.stockActual < p.stockMinimo")
    List<Producto> findProductosConStockBajo();

    // Productos de una categoría específica (4FN)
    @Query("SELECT p FROM Producto p JOIN p.categorias c WHERE c.idCategoria = :idCategoria AND p.eliminado = false")
    List<Producto> findByCategoriaId(Integer idCategoria);

    // Productos de un proveedor específico (4FN)
    @Query("SELECT p FROM Producto p JOIN p.proveedores pv WHERE pv.idProveedor = :idProveedor AND p.eliminado = false")
    List<Producto> findByProveedorId(Integer idProveedor);
}