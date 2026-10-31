package com.example.caffeto.service;

import com.example.caffeto.model.Producto;
import com.example.caffeto.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAllConCategoria();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findActivosConCategoria();
    }

    @Transactional(readOnly = true)
    public Producto buscar(Long id) {
        return productoRepository.findByIdConCategoria(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    /** Guarda el producto y lo vuelve a leer con su categoría cargada. */
    public Producto guardar(Producto producto) {
        Producto guardado = productoRepository.save(producto);
        return buscar(guardado.getId());
    }

    /** Baja lógica: el producto deja de venderse pero se conserva el historial de ventas. */
    public void desactivar(Long id) {
        Producto p = buscar(id);
        p.setActivo(false);
        productoRepository.save(p);
    }
}
