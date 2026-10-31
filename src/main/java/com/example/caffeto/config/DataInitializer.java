package com.example.caffeto.config;

import com.example.caffeto.model.Categoria;
import com.example.caffeto.model.Producto;
import com.example.caffeto.repository.CategoriaRepository;
import com.example.caffeto.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Carga datos de demostración la primera vez que se inicia la aplicación. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            return;
        }
        Categoria cafes = categoriaRepository.save(new Categoria("Cafés", "Bebidas calientes de café"));
        Categoria tes = categoriaRepository.save(new Categoria("Infusiones", "Tés e infusiones"));
        Categoria postres = categoriaRepository.save(new Categoria("Postres", "Pastelería del día"));

        productoRepository.save(producto("Espresso", "Café concentrado", "6.50", 40, cafes));
        productoRepository.save(producto("Capuccino", "Espresso con leche vaporizada", "9.00", 30, cafes));
        productoRepository.save(producto("Latte de vainilla", "Espresso, leche y vainilla", "10.50", 25, cafes));
        productoRepository.save(producto("Té de manzanilla", "Infusión natural", "5.00", 20, tes));
        productoRepository.save(producto("Chocolate caliente", "Cacao con leche", "8.00", 3, tes));
        productoRepository.save(producto("Brownie", "Brownie de chocolate", "7.50", 12, postres));
        productoRepository.save(producto("Cheesecake", "Porción de maracuyá", "11.00", 4, postres));
    }

    private Producto producto(String nombre, String descripcion, String precio, int stock, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(new BigDecimal(precio));
        p.setStock(stock);
        p.setCategoria(categoria);
        return p;
    }
}
