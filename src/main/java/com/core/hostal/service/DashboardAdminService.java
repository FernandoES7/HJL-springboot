package com.core.hostal.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.EstadisticaMensualDTO;
import com.core.hostal.dto.HabitacionPorTipoDTO;
import com.core.hostal.dto.ResumenDashboardDTO;
import com.core.hostal.repository.DashboardJdbcRepository;
import com.core.hostal.repository.ReservaRepository;

@Service
@Transactional
public class DashboardAdminService {

    private final DashboardJdbcRepository dashboardJdbcRepository;
    private final ReservaRepository reservaRepository;

    public DashboardAdminService(
            DashboardJdbcRepository dashboardJdbcRepository,
            ReservaRepository reservaRepository) {
        this.dashboardJdbcRepository = dashboardJdbcRepository;
        this.reservaRepository = reservaRepository;
    }

    public ResumenDashboardDTO obtenerResumen(int anio) {
        return dashboardJdbcRepository.obtenerResumenDashboard(anio);
    }

    public List<EstadisticaMensualDTO> obtenerEstadisticasMensuales(int anio) {
        List<Object[]> filas = reservaRepository.ejecutarEstadisticasMensuales(anio);
        List<EstadisticaMensualDTO> resultado = new ArrayList<>();
        for (Object[] fila : filas) {
            resultado.add(new EstadisticaMensualDTO(
                ((Number) fila[0]).intValue(),
                (String) fila[1],
                fila[2] != null ? new BigDecimal(fila[2].toString()) : BigDecimal.ZERO,
                ((Number) fila[3]).intValue(),
                ((Number) fila[4]).intValue()
            ));
        }
        return resultado;
    }

    public List<HabitacionPorTipoDTO> obtenerDistribucionPorTipo() {
        return dashboardJdbcRepository.listarHabitacionesPorTipo();
    }

    public List<Integer> obtenerAniosDisponibles() {
        List<Integer> anios = reservaRepository.findAniosDisponibles();
        if (anios.isEmpty()) {
            anios = List.of(LocalDate.now().getYear());
        }
        return anios;
    }

    public String generarLabelsJson(List<EstadisticaMensualDTO> stats) {
        return aJsonArray(stats.stream().map(EstadisticaMensualDTO::getMesNombre).toList());
    }

    public String generarIngresosJson(List<EstadisticaMensualDTO> stats) {
        List<String> valores = stats.stream()
            .map(s -> s.getIngresos().setScale(2, RoundingMode.HALF_UP).toPlainString())
            .toList();
        return "[" + String.join(",", valores) + "]";
    }

    public String generarOcupacionJson(List<EstadisticaMensualDTO> stats) {
        List<String> valores = stats.stream()
            .map(s -> String.valueOf(s.getOcupacionPct()))
            .toList();
        return "[" + String.join(",", valores) + "]";
    }

    public String generarReservasJson(List<EstadisticaMensualDTO> stats) {
        List<String> valores = stats.stream()
            .map(s -> String.valueOf(s.getNumReservas()))
            .toList();
        return "[" + String.join(",", valores) + "]";
    }

    public String generarTiposJson(List<HabitacionPorTipoDTO> tipos) {
        return aJsonArray(tipos.stream().map(HabitacionPorTipoDTO::getNombreTipo).toList());
    }

    public String generarCantidadPorTipoJson(List<HabitacionPorTipoDTO> tipos) {
        List<String> valores = tipos.stream()
            .map(t -> String.valueOf(t.getCantidad()))
            .toList();
        return "[" + String.join(",", valores) + "]";
    }

    private String aJsonArray(List<String> textos) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < textos.size(); i++) {
            sb.append("\"").append(escaparJson(textos.get(i))).append("\"");
            if (i < textos.size() - 1) {
                sb.append(",");
            }
        }
        return sb.append("]").toString();
    }

    private String escaparJson(String texto) {
        return texto == null ? "" : texto.replace("\"", "\\\"");
    }
}
