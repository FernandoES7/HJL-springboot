package com.core.hostal.controller.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.hostal.dto.CrearReservaRequestDTO;
import com.core.hostal.dto.ReservaCreadaResponseDTO;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.security.SecurityUtils;
import com.core.hostal.service.ReservaCreacionService;
import com.core.hostal.session.UsuarioSesion;

@RestController
@RequestMapping("/api/reservas")
public class ReservaApiController {

    private final ReservaCreacionService reservaCreacionService;

    public ReservaApiController(ReservaCreacionService reservaCreacionService) {
        this.reservaCreacionService = reservaCreacionService;
    }

    @PostMapping
    public ResponseEntity<ReservaCreadaResponseDTO> crear(@RequestBody CrearReservaRequestDTO request) {
        UsuarioSesion usuario = SecurityUtils.requerirCliente();
        ReservaCreadaResponseDTO respuesta =
            reservaCreacionService.crearReserva(request, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @ExceptionHandler(ReservaException.class)
    public ResponseEntity<Map<String, String>> manejarReservaException(ReservaException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
