package com.example.caffeto.web;

import com.example.caffeto.repository.ProductoRepository;
import com.example.caffeto.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class HomeController {

    private final VentaService ventaService;
    private final ProductoRepository productoRepository;

    public HomeController(VentaService ventaService, ProductoRepository productoRepository) {
        this.ventaService = ventaService;
        this.productoRepository = productoRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        BigDecimal total = ventaService.totalVendido();
        long cantidad = ventaService.cantidadVentas();
        long productos = productoRepository.count();
        long sinStock = productoRepository.findAllConCategoria().stream()
                .filter(p -> p.getStock() <= 5)
                .count();

        model.addAttribute("totalVendido", total);
        model.addAttribute("cantidadVentas", cantidad);
        model.addAttribute("cantidadProductos", productos);
        model.addAttribute("productosStockBajo", sinStock);
        model.addAttribute("ultimasVentas", ventaService.listar().stream().limit(5).toList());
        return "index";
    }
}
