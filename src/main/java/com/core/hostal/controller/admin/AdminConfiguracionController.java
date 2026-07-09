package com.core.hostal.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.entity.Hotel;
import com.core.hostal.service.HotelService;

@Controller
@RequestMapping("/admin/configuracion")
public class AdminConfiguracionController {

    private final HotelService hotelService;

    public AdminConfiguracionController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public String mostrar(Model model) {
        Hotel hotel = hotelService.obtenerHotelActivo()
            .orElseThrow(() -> new IllegalStateException("No hay hotel activo"));

        model.addAttribute("nombreHostal", hotel.getNombre());
        model.addAttribute("direccion", hotel.getDireccion());
        model.addAttribute("emailContacto", hotel.getEmail());
        model.addAttribute("telefono", hotel.getTelefono());
        model.addAttribute("categoria", hotel.getCategoria());
        model.addAttribute("seccionActiva", "configuracion");
        return "admin/configuracion";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombreHostal,
            @RequestParam String direccion,
            @RequestParam String emailContacto,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes) {
        Hotel hotel = hotelService.obtenerHotelActivo()
            .orElseThrow(() -> new IllegalStateException("No hay hotel activo"));

        hotelService.actualizarDatosGenerales(
            hotel.getIdHotel(), nombreHostal, direccion, emailContacto, telefono);
        redirectAttributes.addFlashAttribute("mensajeExito", "Configuración guardada correctamente.");
        return "redirect:/admin/configuracion";
    }
}
