package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.ProductoCategoria;
import com.uniquindio.CINEMAX.Persistencia.Entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    boolean existsBySku(String sku);
    List<ProductoEntity> findByActivoTrueOrderByNombreAsc();
    List<ProductoEntity> findByActivoTrueAndCategoriaOrderByNombreAsc(ProductoCategoria categoria);
}