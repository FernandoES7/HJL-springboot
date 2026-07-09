package com.core.hostal.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.core.hostal.repository.ClienteRepository;
import com.core.hostal.security.SecurityUtils;
import com.core.hostal.service.HotelService;
import com.core.hostal.service.TipoHabitacionService;
import com.core.hostal.session.UsuarioSesion;

@Controller
public class WebController {

    private final TipoHabitacionService tipoHabitacionService;
    private final HotelService hotelService;
    private final ClienteRepository clienteRepository;

    public WebController(
            TipoHabitacionService tipoHabitacionService,
            HotelService hotelService,
            ClienteRepository clienteRepository) {
        this.tipoHabitacionService = tipoHabitacionService;
        this.hotelService = hotelService;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("activePage", "inicio");
        model.addAttribute("habitaciones", tipoHabitacionService.listarCatalogo());
        agregarHotel(model);
        return "inicio";
    }

    @GetMapping("/habitaciones")
    public String habitaciones(
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(required = false) BigDecimal precioMax,
            Model model) {
        model.addAttribute("activePage", "habitaciones");
        model.addAttribute("tipos", tipoHabitacionService.listarCatalogo());
        model.addAttribute("habitaciones", tipoHabitacionService.listarCatalogoConFiltros(
            tipo, capacidadMin, precioMax));
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroCapacidad", capacidadMin);
        model.addAttribute("filtroPrecio", precioMax);
        agregarHotel(model);
        return "habitaciones";
    }

    @GetMapping("/reservar")
    public String reservar(Model model) {
        model.addAttribute("activePage", "reservar");
        agregarClienteLogueado(model);
        agregarHotel(model);
        return "reservar";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("activePage", "contacto");
        agregarHotel(model);
        return "contacto";
    }

    private void agregarHotel(Model model) {
        hotelService.obtenerHotelActivo().ifPresent(hotel -> model.addAttribute("hotel", hotel));
    }

    private void agregarClienteLogueado(Model model) {
        UsuarioSesion usuario = SecurityUtils.obtenerUsuarioActual();
        if (usuario != null && usuario.esCliente()) {
            clienteRepository.findById(usuario.getId())
                .ifPresent(cliente -> model.addAttribute("clienteLogueado", cliente));
        }
    }
}
