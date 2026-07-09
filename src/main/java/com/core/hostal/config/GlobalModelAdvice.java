package com.core.hostal.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.core.hostal.security.SecurityUtils;
import com.core.hostal.session.UsuarioSesion;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("usuarioSesion")
    public UsuarioSesion usuarioSesion() {
        return SecurityUtils.obtenerUsuarioActual();
    }
}
