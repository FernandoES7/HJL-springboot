package com.core.hostal.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.enums.EstadoCliente;
import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.service.ClienteService;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClienteController {

    private final ClienteService clienteService;

    public AdminClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String busqueda,
            Model model) {
        model.addAttribute("clientes", clienteService.buscar(busqueda));
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("totalClientes", clienteService.contarTotal());
        model.addAttribute("seccionActiva", "clientes");
        return "admin/clientes";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Integer idCliente,
            @RequestParam String nombre,
            @RequestParam(required = false) String telefono,
            @RequestParam EstadoCliente estado,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.actualizar(idCliente, nombre, telefono, estado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente actualizado correctamente.");
        } catch (OperacionInvalidaException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}
