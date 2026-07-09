package com.core.hostal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.FacturaResumenDTO;
import com.core.hostal.entity.Factura;
import com.core.hostal.enums.EstadoFactura;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    Optional<Factura> findByNumeroFactura(String numeroFactura);

    Optional<Factura> findByReservaIdReserva(Integer idReserva);

    boolean existsByNumeroFactura(String numeroFactura);

    boolean existsByReservaIdReserva(Integer idReserva);

    @Query("""
        SELECT new com.core.hostal.dto.FacturaResumenDTO(
            f.idFactura, f.numeroFactura, r.codigoReserva, c.nombre,
            f.subtotal, f.impuestos, f.total, f.fecha, f.estado
        )
        FROM Factura f
        JOIN f.reserva r
        JOIN r.cliente c
        ORDER BY f.fecha DESC, f.idFactura DESC
        """)
    List<FacturaResumenDTO> findResumenTodas();

    @Query("""
        SELECT new com.core.hostal.dto.FacturaResumenDTO(
            f.idFactura, f.numeroFactura, r.codigoReserva, c.nombre,
            f.subtotal, f.impuestos, f.total, f.fecha, f.estado
        )
        FROM Factura f
        JOIN f.reserva r
        JOIN r.cliente c
        WHERE f.estado = :estado
        ORDER BY f.fecha DESC
        """)
    List<FacturaResumenDTO> findResumenPorEstado(@Param("estado") EstadoFactura estado);

    @Query("""
        SELECT new com.core.hostal.dto.FacturaResumenDTO(
            f.idFactura, f.numeroFactura, r.codigoReserva, c.nombre,
            f.subtotal, f.impuestos, f.total, f.fecha, f.estado
        )
        FROM Factura f
        JOIN f.reserva r
        JOIN r.cliente c
        WHERE c.idCliente = :idCliente
        ORDER BY f.fecha DESC
        """)
    List<FacturaResumenDTO> findResumenPorCliente(@Param("idCliente") Integer idCliente);
}
