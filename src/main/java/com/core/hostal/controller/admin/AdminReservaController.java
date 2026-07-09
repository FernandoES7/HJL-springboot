package com.core.hostal.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.dto.ReservaDetalleDTO;
import com.core.hostal.dto.ReservaResumenDTO;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.service.AdminReservaService;
import com.core.hostal.service.ClienteService;
import com.core.hostal.service.ReservaService;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;
    private final AdminReservaService adminReservaService;
    private final ClienteService clienteService;

    public AdminReservaController(
            ReservaService reservaService,
            AdminReservaService adminReservaService,
            ClienteService clienteService) {
        this.reservaService = reservaService;
        this.adminReservaService = adminReservaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estadoFiltro,
            @RequestParam(required = false) Integer idCliente,
            Model model) {
        List<ReservaResumenDTO> reservas;
        if (idCliente != null) {
            reservas = reservaService.listarPorCliente(idCliente);
        } else if (estadoFiltro != null && !estadoFiltro.isBlank() && !"TODAS".equalsIgnoreCase(estadoFiltro)) {
            EstadoReserva estado = EstadoReserva.valueOf(estadoFiltro.toLowerCase());
            reservas = reservaService.listarPorEstado(estado);
        } else {
            reservas = reservaService.buscar(busqueda);
        }

        if (busqueda != null && !busqueda.isBlank() && (idCliente != null || estadoFiltro != null)) {
            String t = busqueda.trim().toLowerCase();
            reservas = reservas.stream()
                .filter(r -> r.getCodigoReserva().toLowerCase().contains(t)
                    || r.getNombreCliente().toLowerCase().contains(t))
                .toList();
        }

        model.addAttribute("reservas", reservas);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoFiltro", estadoFiltro);
        model.addAttribute("idClienteFiltro", idCliente);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("totalReservas", reservaService.contarTotal());
        model.addAttribute("reservasConfirmadas", reservaService.contarConfirmadas());
        model.addAttribute("seccionActiva", "reservas");
        return "admin/reservas";
    }

    @GetMapping("/ver/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        ReservaDetalleDTO reserva = reservaService.obtenerDetalle(id)
            .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        model.addAttribute("seccionActiva", "reservas");
        return "admin/reserva-detalle";
    }

    @PostMapping("/cambiarEstado")
    public String cambiarEstado(
            @RequestParam Integer id,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes) {
        try {
            adminReservaService.cambiarEstado(id, EstadoReserva.valueOf(nuevoEstado.toLowerCase()));
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado de reserva actualizado.");
        } catch (ReservaException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/reservas/ver/" + id;
    }
}
