package com.core.hostal.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.core.hostal.session.UsuarioSesion;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UsuarioSesion obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioSesion usuario) {
            return usuario;
        }
        return null;
    }

    public static UsuarioSesion requerirCliente() {
        UsuarioSesion usuario = obtenerUsuarioActual();
        if (usuario == null || !usuario.esCliente()) {
            throw new IllegalStateException("Acceso no autorizado");
        }
        return usuario;
    }
}
