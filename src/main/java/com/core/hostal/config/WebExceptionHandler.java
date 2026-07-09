package com.core.hostal.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String manejarAccesoNoAutorizado(IllegalStateException ex, HttpServletRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains("Acceso no autorizado")) {
            return "redirect:/login";
        }
        throw ex;
    }
}
