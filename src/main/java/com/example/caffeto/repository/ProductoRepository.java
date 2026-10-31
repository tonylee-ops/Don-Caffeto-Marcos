package com.example.caffeto.repository;

import com.example.caffeto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("select p from Producto p join fetch p.categoria order by p.categoria.nombre, p.nombre")
    List<Producto> findAllConCategoria();

    @Query("select p from Producto p join fetch p.categoria where p.activo = true order by p.categoria.nombre, p.nombre")
    List<Producto> findActivosConCategoria();

    @Query("select p from Producto p join fetch p.categoria where p.id = :id")
    java.util.Optional<Producto> findByIdConCategoria(@Param("id") Long id);

    long countByCategoriaId(Long categoriaId);
}
