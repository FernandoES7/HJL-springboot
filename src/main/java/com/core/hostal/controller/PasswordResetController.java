package com.core.hostal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.exception.AuthException;
import com.core.hostal.security.SecurityUtils;
import com.core.hostal.service.HotelService;
import com.core.hostal.service.PasswordResetService;
import com.core.hostal.session.UsuarioSesion;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final HotelService hotelService;

    public PasswordResetController(
            PasswordResetService passwordResetService,
            HotelService hotelService) {
        this.passwordResetService = passwordResetService;
        this.hotelService = hotelService;
    }

    @GetMapping("/recuperar-contrasena")
    public String mostrarRecuperacion(Model model) {
        if (usuarioYaAutenticado()) {
            return "redirect:/login";
        }
        model.addAttribute("activePage", "login");
        agregarHotel(model);
        return "recuperar-contrasena";
    }

    @PostMapping("/recuperar-contrasena")
    public String procesarRecuperacion(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.solicitarRecuperacion(email);
            redirectAttributes.addFlashAttribute(
                "mensaje",
                "Te enviamos un correo con instrucciones para restablecer tu contraseña.");
            return "redirect:/login";
        } catch (AuthException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("recuperarEmail", email);
            return "redirect:/recuperar-contrasena";
        }
    }

    @GetMapping("/restablecer-contrasena")
    public String mostrarRestablecimiento(
            @RequestParam(required = false) String token,
            Model model) {
        if (usuarioYaAutenticado()) {
            return "redirect:/login";
        }
        if (!passwordResetService.esTokenValido(token)) {
            model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
            model.addAttribute("tokenValido", false);
        } else {
            model.addAttribute("tokenValido", true);
        }
        model.addAttribute("token", token);
        model.addAttribute("activePage", "login");
        agregarHotel(model);
        return "restablecer-contrasena";
    }

    @PostMapping("/restablecer-contrasena")
    public String procesarRestablecimiento(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmacion,
            RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.restablecerContrasena(token, password, confirmacion);
            redirectAttributes.addFlashAttribute(
                "mensaje",
                "Tu contraseña fue actualizada correctamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (AuthException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/restablecer-contrasena?token=" + token;
        }
    }

    private boolean usuarioYaAutenticado() {
        UsuarioSesion usuario = SecurityUtils.obtenerUsuarioActual();
        return usuario != null;
    }

    private void agregarHotel(Model model) {
        hotelService.obtenerHotelActivo().ifPresent(h -> model.addAttribute("hotel", h));
    }
}
