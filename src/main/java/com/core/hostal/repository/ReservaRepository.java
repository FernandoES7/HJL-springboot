package com.core.hostal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.ReservaResumenDTO;
import com.core.hostal.entity.Reserva;
import com.core.hostal.enums.EstadoReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    boolean existsByCodigoReserva(String codigoReserva);

    @Query("""
        SELECT new com.core.hostal.dto.ReservaResumenDTO(
            r.idReserva, r.codigoReserva, c.nombre,
            r.fechaCheckin, r.fechaCheckout, r.numHuespedes, r.total, r.estado
        )
        FROM Reserva r
        JOIN r.cliente c
        ORDER BY r.fechaReserva DESC, r.idReserva DESC
        """)
    List<ReservaResumenDTO> findResumenTodas();

    @Query("""
        SELECT new com.core.hostal.dto.ReservaResumenDTO(
            r.idReserva, r.codigoReserva, c.nombre,
            r.fechaCheckin, r.fechaCheckout, r.numHuespedes, r.total, r.estado
        )
        FROM Reserva r
        JOIN r.cliente c
        WHERE c.idCliente = :idCliente
        ORDER BY r.fechaReserva DESC, r.idReserva DESC
        """)
    List<ReservaResumenDTO> findResumenPorCliente(@Param("idCliente") Integer idCliente);

    @Query("""
        SELECT new com.core.hostal.dto.ReservaResumenDTO(
            r.idReserva, r.codigoReserva, c.nombre,
            r.fechaCheckin, r.fechaCheckout, r.numHuespedes, r.total, r.estado
        )
        FROM Reserva r
        JOIN r.cliente c
        WHERE r.estado = :estado
        ORDER BY r.fechaCheckin ASC
        """)
    List<ReservaResumenDTO> findResumenPorEstado(@Param("estado") EstadoReserva estado);

    @Query("""
        SELECT r
        FROM Reserva r
        JOIN FETCH r.cliente c
        LEFT JOIN FETCH r.empleado e
        WHERE r.idReserva = :idReserva
        """)
    Optional<Reserva> findByIdConCliente(@Param("idReserva") Integer idReserva);

    @Query("""
        SELECT r
        FROM Reserva r
        JOIN FETCH r.cliente c
        LEFT JOIN FETCH r.empleado e
        WHERE r.codigoReserva = :codigoReserva
        """)
    Optional<Reserva> findByCodigoConCliente(@Param("codigoReserva") String codigoReserva);

    Optional<Reserva> findByIdReservaAndClienteIdCliente(Integer idReserva, Integer idCliente);

    @Query("SELECT DISTINCT YEAR(r.fechaCheckin) FROM Reserva r ORDER BY YEAR(r.fechaCheckin) DESC")
    List<Integer> findAniosDisponibles();

    long countByEstado(EstadoReserva estado);

    long count();

    @Procedure(procedureName = "sp_estadisticas_mensuales")
    List<Object[]> ejecutarEstadisticasMensuales(@Param("p_anio") Integer anio);
}
