package com.core.hostal.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.core.hostal.dto.DisponibilidadTipoDTO;
import com.core.hostal.service.TipoHabitacionService;

@RestController
@RequestMapping("/api/disponibilidad")
public class DisponibilidadApiController {

    private final TipoHabitacionService tipoHabitacionService;

    public DisponibilidadApiController(TipoHabitacionService tipoHabitacionService) {
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadTipoDTO>> consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
            @RequestParam(defaultValue = "1") int huespedes) {

        if (!checkout.isAfter(checkin)) {
            return ResponseEntity.badRequest().build();
        }

        List<DisponibilidadTipoDTO> disponibles =
            tipoHabitacionService.consultarDisponibilidad(checkin, checkout);

        List<DisponibilidadTipoDTO> filtradas = disponibles.stream()
            .filter(d -> d.getHabitacionesDisponibles() > 0)
            .filter(d -> puedeAlojarHuespedes(d, huespedes))
            .toList();

        return ResponseEntity.ok(filtradas);
    }

    private boolean puedeAlojarHuespedes(DisponibilidadTipoDTO tipo, int huespedes) {
        long capacidadMaxima = tipo.getCapacidad() * tipo.getHabitacionesDisponibles();
        return capacidadMaxima >= huespedes;
    }
}
