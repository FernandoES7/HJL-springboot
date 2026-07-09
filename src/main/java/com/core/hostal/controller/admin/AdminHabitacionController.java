package com.core.hostal.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.entity.Hotel;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.service.HabitacionService;
import com.core.hostal.service.HotelService;
import com.core.hostal.service.TipoHabitacionService;

@Controller
@RequestMapping("/admin/habitaciones")
public class AdminHabitacionController {

    private final HabitacionService habitacionService;
    private final HotelService hotelService;
    private final TipoHabitacionService tipoHabitacionService;

    public AdminHabitacionController(
            HabitacionService habitacionService,
            HotelService hotelService,
            TipoHabitacionService tipoHabitacionService) {
        this.habitacionService = habitacionService;
        this.hotelService = hotelService;
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Integer idTipo,
            Model model) {
        Hotel hotel = hotelService.obtenerHotelActivo()
            .orElseThrow(() -> new IllegalStateException("No hay hotel activo"));

        model.addAttribute("habitaciones",
            habitacionService.buscarPorHotel(hotel.getIdHotel(), busqueda, idTipo));
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("idTipoFiltro", idTipo);
        model.addAttribute("totalHabitaciones",
            habitacionService.contarPorHotel(hotel.getIdHotel()));
        model.addAttribute("habitacionesDisponibles",
            habitacionService.contarDisponiblesPorHotel(hotel.getIdHotel()));
        model.addAttribute("habitacionesOcupadas",
            habitacionService.contarOcupadasPorHotel(hotel.getIdHotel()));
        model.addAttribute("tipos", tipoHabitacionService.listarAdmin());
        model.addAttribute("seccionActiva", "habitaciones");
        return "admin/habitaciones";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String numero,
            @RequestParam Integer piso,
            @RequestParam Integer idTipo,
            @RequestParam EstadoHabitacion estado,
            RedirectAttributes redirectAttributes) {
        Hotel hotel = hotelService.obtenerHotelActivo()
            .orElseThrow(() -> new IllegalStateException("No hay hotel activo"));
        try {
            habitacionService.crear(hotel.getIdHotel(), numero, piso, idTipo, estado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Habitación creada correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/habitaciones";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Integer idHabitacion,
            @RequestParam String numero,
            @RequestParam Integer piso,
            @RequestParam Integer idTipo,
            @RequestParam EstadoHabitacion estado,
            RedirectAttributes redirectAttributes) {
        try {
            habitacionService.actualizar(idHabitacion, numero, piso, idTipo, estado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Habitación actualizada correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/habitaciones";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idHabitacion,
            RedirectAttributes redirectAttributes) {
        try {
            habitacionService.eliminar(idHabitacion);
            redirectAttributes.addFlashAttribute("mensajeExito", "Habitación eliminada correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/habitaciones";
    }
}
