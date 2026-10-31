package com.example.caffeto.service;

import com.example.caffeto.model.Categoria;
import com.example.caffeto.repository.CategoriaRepository;
import com.example.caffeto.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAllConProductos();
    }

    @Transactional(readOnly = true)
    public Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
    }

    /** Valida que el nombre no se repita (sin distinguir mayúsculas). */
    public boolean nombreDisponible(String nombre, Long idActual) {
        if (idActual == null) {
            return !categoriaRepository.existsByNombreIgnoreCase(nombre);
        }
        return !categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idActual);
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /** No permite eliminar una categoría que tenga productos asociados. */
    public void eliminar(Long id) {
        if (productoRepository.countByCategoriaId(id) > 0) {
            throw new IllegalStateException("No se puede eliminar: la categoría tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }
}
