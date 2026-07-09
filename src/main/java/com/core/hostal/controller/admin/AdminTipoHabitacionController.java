package com.core.hostal.controller.admin;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.service.TipoHabitacionService;

@Controller
@RequestMapping("/admin/tipos-habitacion")
public class AdminTipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;

    public AdminTipoHabitacionController(TipoHabitacionService tipoHabitacionService) {
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tipos", tipoHabitacionService.listarAdmin());
        model.addAttribute("seccionActiva", "tiposHabitacion");
        return "admin/tipos-habitacion";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Integer capacidad,
            @RequestParam BigDecimal precioBase,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam Integer cantidadTotal,
            @RequestParam(required = false, defaultValue = "false") Boolean activo,
            RedirectAttributes redirectAttributes) {
        try {
            tipoHabitacionService.crear(nombre, descripcion, capacidad, precioBase, imagenUrl, cantidadTotal, activo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de habitación creado correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/tipos-habitacion";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Integer idTipo,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Integer capacidad,
            @RequestParam BigDecimal precioBase,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam Integer cantidadTotal,
            @RequestParam(required = false, defaultValue = "false") Boolean activo,
            RedirectAttributes redirectAttributes) {
        try {
            tipoHabitacionService.actualizar(idTipo, nombre, descripcion, capacidad, precioBase, imagenUrl, cantidadTotal, activo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de habitación actualizado correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/tipos-habitacion";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idTipo,
            RedirectAttributes redirectAttributes) {
        try {
            tipoHabitacionService.eliminar(idTipo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de habitación eliminado correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/tipos-habitacion";
    }
}
