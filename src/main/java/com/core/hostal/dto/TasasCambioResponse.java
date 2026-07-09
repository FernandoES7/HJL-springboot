package com.core.hostal.dto;

import java.math.BigDecimal;
import java.util.Map;

public record TasasCambioResponse(Map<String, BigDecimal> rates) {
}
