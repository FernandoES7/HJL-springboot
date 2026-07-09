package com.core.hostal.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.core.hostal.dto.HabitacionPorTipoDTO;
import com.core.hostal.dto.ResumenDashboardDTO;

@Repository
public class DashboardJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResumenDashboardDTO obtenerResumenDashboard(int anio) {
        List<ResumenDashboardDTO> resultados = jdbcTemplate.query(
            "CALL sp_resumen_dashboard(?)",
            (rs, rowNum) -> mapearResumen(rs),
            anio
        );
        return resultados.isEmpty() ? new ResumenDashboardDTO() : resultados.get(0);
    }

    public List<HabitacionPorTipoDTO> listarHabitacionesPorTipo() {
        return jdbcTemplate.query(
            "CALL sp_habitaciones_por_tipo()",
            (rs, rowNum) -> new HabitacionPorTipoDTO(
                rs.getString("nombre_tipo"),
                rs.getInt("cantidad")
            )
        );
    }

    private ResumenDashboardDTO mapearResumen(ResultSet rs) throws SQLException {
        ResumenDashboardDTO dto = new ResumenDashboardDTO();
        dto.setTotalHabitaciones(rs.getInt("total_habitaciones"));
        dto.setHabitacionesOcupadas(rs.getInt("habitaciones_ocupadas"));
        dto.setHabitacionesDisponibles(rs.getInt("habitaciones_disponibles"));
        dto.setReservasActivas(rs.getInt("reservas_activas"));
        dto.setIngresosAnio(rs.getBigDecimal("ingresos_anio"));
        dto.setTotalClientes(rs.getInt("total_clientes"));
        dto.setTotalReservasAnio(rs.getInt("total_reservas_anio"));
        dto.setReservasConfirmadas(rs.getInt("reservas_confirmadas"));
        dto.setTasaOcupacion(rs.getInt("tasa_ocupacion"));
        return dto;
    }
}
