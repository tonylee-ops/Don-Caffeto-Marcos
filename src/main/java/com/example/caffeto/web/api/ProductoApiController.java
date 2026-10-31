package com.example.caffeto.web.api;

import com.example.caffeto.model.Categoria;
import com.example.caffeto.model.Producto;
import com.example.caffeto.service.CategoriaService;
import com.example.caffeto.service.ProductoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * API REST de productos (operaciones CRUD en JSON).
 * Ejemplo: GET http://localhost:8080/api/productos
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoApiController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    /** Datos que llegan en el cuerpo de la petición POST y PUT. */
    public record ProductoRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String nombre,
            @Size(max = 200) String descripcion,
            @NotNull(message = "El precio es obligatorio")
            @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
            @Digits(integer = 8, fraction = 2) BigDecimal precio,
            @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
            @NotNull(message = "La categoría es obligatoria") Long categoriaId
    ) {}

    @GetMapping
    public List<ProductoDto> listar() {
        return productoService.listar().stream().map(ProductoDto::desde).toList();
    }

    @GetMapping("/{id}")
    public ProductoDto buscar(@PathVariable Long id) {
        return ProductoDto.desde(productoService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDto crear(@Valid @RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        aplicar(producto, request);
        return ProductoDto.desde(productoService.guardar(producto));
    }

    @PutMapping("/{id}")
    public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.buscar(id);
        aplicar(producto, request);
        return ProductoDto.desde(productoService.guardar(producto));
    }

    /** Baja lógica: el producto deja de venderse, pero su historial se conserva. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    /** Recurso no encontrado: responde 404 en lugar de error 500. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> noEncontrado(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    private void aplicar(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaService.buscar(request.categoriaId());
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoria);
    }
}
