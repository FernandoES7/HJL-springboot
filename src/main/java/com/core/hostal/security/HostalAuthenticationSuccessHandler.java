package com.core.hostal.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.core.hostal.session.UsuarioSesion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HostalAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public HostalAuthenticationSuccessHandler() {
        setDefaultTargetUrl("/mis-reservas");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UsuarioSesion usuario = (UsuarioSesion) authentication.getPrincipal();
        if (usuario.esAdmin()) {
            setDefaultTargetUrl("/admin");
        } else {
            setDefaultTargetUrl("/mis-reservas");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
