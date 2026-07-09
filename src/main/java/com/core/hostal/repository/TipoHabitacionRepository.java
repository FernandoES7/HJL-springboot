package com.core.hostal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.DisponibilidadTipoDTO;
import com.core.hostal.dto.TipoHabitacionAdminDTO;
import com.core.hostal.dto.TipoHabitacionCatalogoDTO;
import com.core.hostal.entity.TipoHabitacion;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.enums.EstadoReserva;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {

    @Query("""
        SELECT new com.core.hostal.dto.TipoHabitacionAdminDTO(
            t.idTipo, t.nombre, t.descripcion, t.capacidad,
            t.precioBase, t.imagenUrl, t.cantidadTotal, t.activo,
            COUNT(h.idHabitacion)
        )
        FROM TipoHabitacion t
        LEFT JOIN Habitacion h ON h.tipo = t
        GROUP BY t.idTipo, t.nombre, t.descripcion, t.capacidad,
            t.precioBase, t.imagenUrl, t.cantidadTotal, t.activo
        ORDER BY t.nombre ASC
        """)
    List<TipoHabitacionAdminDTO> findListadoAdmin();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdTipoNot(String nombre, Integer idTipo);

    @Query("""
        SELECT new com.core.hostal.dto.TipoHabitacionCatalogoDTO(
            t.idTipo, t.nombre, t.descripcion, t.capacidad,
            t.precioBase, t.imagenUrl, t.cantidadTotal
        )
        FROM TipoHabitacion t
        WHERE t.activo = true
        ORDER BY t.precioBase ASC
        """)
    List<TipoHabitacionCatalogoDTO> findCatalogoActivos();

    @Query("""
        SELECT new com.core.hostal.dto.TipoHabitacionCatalogoDTO(
            t.idTipo, t.nombre, t.descripcion, t.capacidad,
            t.precioBase, t.imagenUrl, t.cantidadTotal
        )
        FROM TipoHabitacion t
        WHERE t.activo = true
          AND (:idTipo IS NULL OR t.idTipo = :idTipo)
          AND (:capacidadMin IS NULL OR t.capacidad >= :capacidadMin)
          AND (:precioMax IS NULL OR t.precioBase <= :precioMax)
        ORDER BY t.precioBase ASC
        """)
    List<TipoHabitacionCatalogoDTO> findCatalogoConFiltros(
        @Param("idTipo") Integer idTipo,
        @Param("capacidadMin") Integer capacidadMin,
        @Param("precioMax") BigDecimal precioMax
    );

    @Query("""
        SELECT new com.core.hostal.dto.TipoHabitacionCatalogoDTO(
            t.idTipo, t.nombre, t.descripcion, t.capacidad,
            t.precioBase, t.imagenUrl, t.cantidadTotal
        )
        FROM TipoHabitacion t
        WHERE t.idTipo = :idTipo AND t.activo = true
        """)
    Optional<TipoHabitacionCatalogoDTO> findCatalogoById(@Param("idTipo") Integer idTipo);

    @Query("""
        SELECT new com.core.hostal.dto.DisponibilidadTipoDTO(
            t.idTipo, t.nombre, t.descripcion, t.capacidad, t.precioBase, t.imagenUrl,
            COUNT(DISTINCT h.idHabitacion)
        )
        FROM TipoHabitacion t
        JOIN Habitacion h ON h.tipo = t
        WHERE t.activo = true
          AND h.estado = :estadoDisponible
          AND (:capacidadMin IS NULL OR t.capacidad >= :capacidadMin)
          AND h.idHabitacion NOT IN (
              SELECT rh.habitacion.idHabitacion
              FROM ReservaHabitacion rh
              JOIN rh.reserva r
              WHERE r.estado NOT IN (:estadoCancelada, :estadoCompletada)
                AND r.fechaCheckin < :fechaCheckout
                AND r.fechaCheckout > :fechaCheckin
          )
        GROUP BY t.idTipo, t.nombre, t.descripcion, t.capacidad, t.precioBase, t.imagenUrl
        HAVING COUNT(DISTINCT h.idHabitacion) > 0
        ORDER BY t.precioBase ASC
        """)
    List<DisponibilidadTipoDTO> findDisponibilidadPorFechas(
        @Param("fechaCheckin") LocalDate fechaCheckin,
        @Param("fechaCheckout") LocalDate fechaCheckout,
        @Param("capacidadMin") Integer capacidadMin,
        @Param("estadoDisponible") EstadoHabitacion estadoDisponible,
        @Param("estadoCancelada") EstadoReserva estadoCancelada,
        @Param("estadoCompletada") EstadoReserva estadoCompletada
    );
}
