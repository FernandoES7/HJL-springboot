package com.core.hostal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.HabitacionReservadaDTO;
import com.core.hostal.entity.ReservaHabitacion;

public interface ReservaHabitacionRepository extends JpaRepository<ReservaHabitacion, Integer> {

    @Query("""
        SELECT new com.core.hostal.dto.HabitacionReservadaDTO(
            h.idHabitacion, h.numero, h.piso, t.nombre, rh.precioNoche
        )
        FROM ReservaHabitacion rh
        JOIN rh.habitacion h
        JOIN h.tipo t
        WHERE rh.reserva.idReserva = :idReserva
        ORDER BY h.piso ASC, h.numero ASC
        """)
    List<HabitacionReservadaDTO> findHabitacionesPorReserva(@Param("idReserva") Integer idReserva);

    List<ReservaHabitacion> findByReservaIdReserva(Integer idReserva);

    boolean existsByHabitacionIdHabitacion(Integer idHabitacion);
}
