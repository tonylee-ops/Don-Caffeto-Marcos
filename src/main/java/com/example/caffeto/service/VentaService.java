package com.example.caffeto.service;

import com.example.caffeto.model.DetalleVenta;
import com.example.caffeto.model.Producto;
import com.example.caffeto.model.Venta;
import com.example.caffeto.repository.ProductoRepository;
import com.example.caffeto.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Venta> listar() {
        return ventaRepository.findAllConDetalles();
    }

    @Transactional(readOnly = true)
    public Venta buscar(Long id) {
        return ventaRepository.findByIdConDetalles(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + id));
    }

    /**
     * Registra una venta. {@code cantidades} asocia el id del producto con la cantidad pedida.
     * Valida que haya stock suficiente, descuenta el stock y calcula el total.
     */
    public Venta registrar(String cliente, Map<Long, Integer> cantidades) {
        Venta venta = new Venta();
        venta.setCliente(cliente == null || cliente.isBlank() ? "Cliente general" : cliente.trim());

        for (Map.Entry<Long, Integer> item : cantidades.entrySet()) {
            int cantidad = item.getValue() == null ? 0 : item.getValue();
            if (cantidad <= 0) {
                continue;
            }
            Producto producto = productoRepository.findByIdConCategoria(item.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getKey()));

            if (!producto.isActivo()) {
                throw new IllegalStateException(producto.getNombre() + " no está disponible");
            }
            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente de " + producto.getNombre()
                        + " (disponible: " + producto.getStock() + ")");
            }

            producto.setStock(producto.getStock() - cantidad);
            venta.agregarDetalle(new DetalleVenta(producto, cantidad));
        }

        if (venta.getDetalles().isEmpty()) {
            throw new IllegalStateException("Agregue al menos un producto a la venta");
        }
        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalVendido() {
        return ventaRepository.sumarVentas();
    }

    @Transactional(readOnly = true)
    public long cantidadVentas() {
        return ventaRepository.contarVentas();
    }
}
