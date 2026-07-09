package com.core.hostal.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.core.hostal.exception.AuthException;
import com.core.hostal.security.SecurityUtils;
import com.core.hostal.service.AuthService;
import com.core.hostal.service.HotelService;
import com.core.hostal.session.TipoUsuario;
import com.core.hostal.session.UsuarioSesion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private final AuthService authService;
    private final HotelService hotelService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            HotelService hotelService,
            SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.hotelService = hotelService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/login")
    public String login(Model model) {
        UsuarioSesion usuario = SecurityUtils.obtenerUsuarioActual();
        if (usuario != null) {
            return redirigirSegunTipo(usuario);
        }
        model.addAttribute("activePage", "login");
        hotelService.obtenerHotelActivo().ifPresent(h -> model.addAttribute("hotel", h));
        return "login";
    }

    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String documento,
            @RequestParam String telefono,
            @RequestParam String password,
            @RequestParam String confirmacion,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        try {
            UsuarioSesion usuario = authService.registrar(
                nombre, email, documento, telefono, password, confirmacion);
            iniciarSesion(usuario, request, response);
            return "redirect:/mis-reservas";
        } catch (AuthException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("registerNombre", nombre);
            redirectAttributes.addFlashAttribute("registerEmail", email);
            redirectAttributes.addFlashAttribute("registerDocumento", documento);
            redirectAttributes.addFlashAttribute("registerTelefono", telefono);
            redirectAttributes.addFlashAttribute("showRegister", true);
            return "redirect:/login";
        }
    }

    private void iniciarSesion(
            UsuarioSesion usuario,
            HttpServletRequest request,
            HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private String redirigirSegunTipo(UsuarioSesion usuario) {
        if (usuario.getTipo() == TipoUsuario.ADMIN) {
            return "redirect:/admin";
        }
        return "redirect:/mis-reservas";
    }
}
