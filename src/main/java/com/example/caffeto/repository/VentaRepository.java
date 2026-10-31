package com.example.caffeto.repository;

import com.example.caffeto.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("select distinct v from Venta v left join fetch v.detalles d left join fetch d.producto order by v.fecha desc")
    List<Venta> findAllConDetalles();

    @Query("select v from Venta v left join fetch v.detalles d left join fetch d.producto where v.id = :id")
    Optional<Venta> findByIdConDetalles(Long id);

    @Query("select coalesce(sum(v.total), 0) from Venta v")
    BigDecimal sumarVentas();

    @Query("select count(v) from Venta v")
    long contarVentas();
}
