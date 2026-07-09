package com.core.hostal.controller.api;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.hostal.dto.TasasCambioResponse;
import com.core.hostal.service.ExchangeRateService;

@RestController
@RequestMapping("/api/tipos-cambio")
public class ExchangeRateApiController {

    private static final Map<String, BigDecimal> TASAS_RESPALDO = Map.of(
        "PEN", BigDecimal.ONE,
        "USD", new BigDecimal("0.2940"),
        "EUR", new BigDecimal("0.2573")
    );

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateApiController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public TasasCambioResponse obtenerTasas() {
        try {
            return new TasasCambioResponse(exchangeRateService.obtenerTasasDesdePen());
        } catch (Exception ex) {
            return new TasasCambioResponse(TASAS_RESPALDO);
        }
    }
}
