package com.example.caffeto.web;

import com.example.caffeto.model.Producto;
import com.example.caffeto.service.CategoriaService;
import com.example.caffeto.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    /** Convierte el id de categoría que llega del select en la entidad Categoria. */
    @InitBinder("producto")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(com.example.caffeto.model.Categoria.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String texto) {
                if (texto == null || texto.isBlank()) {
                    setValue(null);
                } else {
                    setValue(categoriaService.buscar(Long.valueOf(texto)));
                }
            }
        });
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listar());
        return "productos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscar(id));
        model.addAttribute("categorias", categoriaService.listar());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listar());
            return "productos/form";
        }
        productoService.guardar(producto);
        redirect.addFlashAttribute("mensaje", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirect) {
        productoService.desactivar(id);
        redirect.addFlashAttribute("mensaje", "Producto desactivado: ya no aparece en ventas");
        return "redirect:/productos";
    }
}
