package com.core.hostal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.HabitacionListadoDTO;
import com.core.hostal.entity.Habitacion;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.enums.EstadoReserva;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    @Query("""
        SELECT new com.core.hostal.dto.HabitacionListadoDTO(
            h.idHabitacion, h.numero, h.piso, h.estado,
            t.nombre, t.capacidad, t.imagenUrl
        )
        FROM Habitacion h
        JOIN h.tipo t
        WHERE h.hotel.idHotel = :idHotel
        ORDER BY h.piso ASC, h.numero ASC
        """)
    List<HabitacionListadoDTO> findListadoPorHotel(@Param("idHotel") Integer idHotel);

    @Query("""
        SELECT new com.core.hostal.dto.HabitacionListadoDTO(
            h.idHabitacion, h.numero, h.piso, h.estado,
            t.nombre, t.capacidad, t.imagenUrl
        )
        FROM Habitacion h
        JOIN h.tipo t
        WHERE h.hotel.idHotel = :idHotel
          AND t.idTipo = :idTipo
        ORDER BY h.piso ASC, h.numero ASC
        """)
    List<HabitacionListadoDTO> findListadoPorHotelYTipo(
        @Param("idHotel") Integer idHotel, @Param("idTipo") Integer idTipo);

    boolean existsByTipoIdTipo(Integer idTipo);

    @Query("""
        SELECT h
        FROM Habitacion h
        JOIN FETCH h.tipo t
        WHERE h.tipo.idTipo = :idTipo
          AND h.hotel.idHotel = :idHotel
          AND h.estado = :estadoDisponible
          AND h.idHabitacion NOT IN (
              SELECT rh.habitacion.idHabitacion
              FROM ReservaHabitacion rh
              JOIN rh.reserva r
              WHERE r.estado NOT IN (:estadoCancelada, :estadoCompletada)
                AND r.fechaCheckin < :fechaCheckout
                AND r.fechaCheckout > :fechaCheckin
          )
        ORDER BY h.numero ASC
        """)
    List<Habitacion> findDisponiblesPorTipoYFechas(
        @Param("idHotel") Integer idHotel,
        @Param("idTipo") Integer idTipo,
        @Param("fechaCheckin") LocalDate fechaCheckin,
        @Param("fechaCheckout") LocalDate fechaCheckout,
        @Param("estadoDisponible") EstadoHabitacion estadoDisponible,
        @Param("estadoCancelada") EstadoReserva estadoCancelada,
        @Param("estadoCompletada") EstadoReserva estadoCompletada
    );

    boolean existsByHotelIdHotelAndNumero(Integer idHotel, String numero);
}
