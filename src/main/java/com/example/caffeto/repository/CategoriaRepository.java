package com.example.caffeto.repository;

import com.example.caffeto.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("select distinct c from Categoria c left join fetch c.productos order by c.nombre")
    List<Categoria> findAllConProductos();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
