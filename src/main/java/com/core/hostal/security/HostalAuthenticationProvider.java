package com.core.hostal.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.core.hostal.exception.AuthException;
import com.core.hostal.service.AuthService;
import com.core.hostal.session.UsuarioSesion;

@Component
public class HostalAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;

    public HostalAuthenticationProvider(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UsuarioSesion usuario = authService.iniciarSesion(email, password);
            return new UsernamePasswordAuthenticationToken(
                usuario, null, usuario.getAuthorities());
        } catch (AuthException ex) {
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
