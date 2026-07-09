package com.core.hostal.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExchangeRateService {

    private static final List<String> MONEDAS_SOPORTADAS = List.of("PEN", "USD", "EUR");

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;

    private volatile Map<String, BigDecimal> tasasCacheadas;
    private volatile Instant expiraCache;

    public ExchangeRateService(
            @Value("${app.exchange-rate.api-url}") String apiUrl,
            @Value("${app.exchange-rate.api-key}") String apiKey) {
        this.apiUrl = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    public Map<String, BigDecimal> obtenerTasasDesdePen() {
        if (tasasCacheadas != null && expiraCache != null && Instant.now().isBefore(expiraCache)) {
            return tasasCacheadas;
        }

        String url = apiUrl + apiKey + "/latest/PEN";
        Map<String, Object> response = restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        if (response == null || !"success".equals(String.valueOf(response.get("result")))) {
            throw new IllegalStateException("No se pudieron obtener las tasas de cambio.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> conversionRates = (Map<String, Object>) response.get("conversion_rates");
        if (conversionRates == null) {
            throw new IllegalStateException("No se pudieron obtener las tasas de cambio.");
        }

        Map<String, BigDecimal> tasas = new LinkedHashMap<>();
        for (String moneda : MONEDAS_SOPORTADAS) {
            Object valor = conversionRates.get(moneda);
            if (valor != null) {
                tasas.put(moneda, new BigDecimal(valor.toString()));
            }
        }

        if (!tasas.containsKey("PEN")) {
            tasas.put("PEN", BigDecimal.ONE);
        }

        Object nextUpdate = response.get("time_next_update_unix");
        long nextUpdateEpoch = nextUpdate instanceof Number number ? number.longValue() : 0L;
        expiraCache = nextUpdateEpoch > 0
            ? Instant.ofEpochSecond(nextUpdateEpoch)
            : Instant.now().plusSeconds(3600);
        tasasCacheadas = Map.copyOf(tasas);
        return tasasCacheadas;
    }
}
