package com.example.caffeto.web;

import com.example.caffeto.model.Categoria;
import com.example.caffeto.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categorias/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscar(id));
        return "categorias/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult result,
                          RedirectAttributes redirect) {
        if (!result.hasFieldErrors("nombre")
                && !categoriaService.nombreDisponible(categoria.getNombre(), categoria.getId())) {
            result.rejectValue("nombre", "duplicado", "Ya existe una categoría con ese nombre");
        }
        if (result.hasErrors()) {
            return "categorias/form";
        }
        categoriaService.guardar(categoria);
        redirect.addFlashAttribute("mensaje", "Categoría guardada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoriaService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Categoría eliminada");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
}
