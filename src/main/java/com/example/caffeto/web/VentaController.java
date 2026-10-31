package com.example.caffeto.web;

import com.example.caffeto.service.ProductoService;
import com.example.caffeto.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "ventas/nueva";
    }

    /**
     * Recibe un campo "cantidad_<idProducto>" por cada producto del formulario.
     * Solo se registran los productos con cantidad mayor que cero.
     */
    @PostMapping("/guardar")
    public String guardar(@RequestParam(defaultValue = "") String cliente,
                          @RequestParam Map<String, String> parametros,
                          RedirectAttributes redirect) {
        Map<Long, Integer> cantidades = new HashMap<>();
        for (Map.Entry<String, String> p : parametros.entrySet()) {
            if (!p.getKey().startsWith("cantidad_")) {
                continue;
            }
            try {
                Long idProducto = Long.valueOf(p.getKey().substring("cantidad_".length()));
                int cantidad = p.getValue() == null || p.getValue().isBlank() ? 0 : Integer.parseInt(p.getValue());
                cantidades.put(idProducto, cantidad);
            } catch (NumberFormatException e) {
                redirect.addFlashAttribute("error", "Cantidad no válida");
                return "redirect:/ventas/nueva";
            }
        }

        try {
            var venta = ventaService.registrar(cliente, cantidades);
            redirect.addFlashAttribute("mensaje", "Venta registrada: #" + venta.getId());
            return "redirect:/ventas/" + venta.getId();
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("venta", ventaService.buscar(id));
        return "ventas/detalle";
    }
}
