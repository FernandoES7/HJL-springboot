package com.core.hostal.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.dto.MisReservaClienteDTO;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.security.SecurityUtils;
import com.core.hostal.service.CancelacionService;
import com.core.hostal.service.HotelService;
import com.core.hostal.service.MisReservasService;
import com.core.hostal.session.UsuarioSesion;

@Controller
public class MisReservasController {

    private final MisReservasService misReservasService;
    private final CancelacionService cancelacionService;
    private final HotelService hotelService;

    public MisReservasController(
            MisReservasService misReservasService,
            CancelacionService cancelacionService,
            HotelService hotelService) {
        this.misReservasService = misReservasService;
        this.cancelacionService = cancelacionService;
        this.hotelService = hotelService;
    }

    @GetMapping("/mis-reservas")
    public String misReservas(Model model) {
        UsuarioSesion usuario = SecurityUtils.requerirCliente();
        List<MisReservaClienteDTO> reservas =
            misReservasService.listarPorCliente(usuario.getId());

        model.addAttribute("activePage", "mis-reservas");
        model.addAttribute("reservas", reservas);
        hotelService.obtenerHotelActivo().ifPresent(h -> model.addAttribute("hotel", h));
        return "mis-reservas";
    }

    @PostMapping("/mis-reservas/{idReserva}/cancelar")
    public String cancelarReserva(
            @PathVariable Integer idReserva,
            @RequestParam(required = false) String motivo,
            RedirectAttributes redirectAttributes) {
        UsuarioSesion usuario = SecurityUtils.requerirCliente();
        try {
            cancelacionService.cancelarPorCliente(idReserva, usuario.getId(), motivo);
            redirectAttributes.addFlashAttribute("mensaje", "Reserva cancelada correctamente.");
        } catch (ReservaException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/mis-reservas";
    }
}
