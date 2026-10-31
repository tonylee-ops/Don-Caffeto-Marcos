package com.example.caffeto.web.api;

import com.example.caffeto.model.Producto;

import java.math.BigDecimal;

/** Representación del producto que se envía como JSON. Evita exponer las relaciones perezosas. */
public record ProductoDto(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        boolean activo,
        Long categoriaId,
        String categoriaNombre
) {
    public static ProductoDto desde(Producto p) {
        return new ProductoDto(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.isActivo(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre()
        );
    }
}
