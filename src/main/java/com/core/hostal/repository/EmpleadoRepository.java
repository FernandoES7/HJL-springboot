package com.core.hostal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.hostal.dto.EmpleadoListadoDTO;
import com.core.hostal.entity.Empleado;
import com.core.hostal.enums.EstadoEmpleado;
import com.core.hostal.enums.RolEmpleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    Optional<Empleado> findByEmail(String email);

    Optional<Empleado> findByResetToken(String resetToken);

    boolean existsByEmail(String email);

    @Query("""
        SELECT new com.core.hostal.dto.EmpleadoListadoDTO(
            e.idEmpleado, e.nombre, e.email, e.telefono,
            e.rol, e.estado, h.nombre
        )
        FROM Empleado e
        JOIN e.hotel h
        ORDER BY e.nombre ASC
        """)
    List<EmpleadoListadoDTO> findListado();

    @Query("""
        SELECT new com.core.hostal.dto.EmpleadoListadoDTO(
            e.idEmpleado, e.nombre, e.email, e.telefono,
            e.rol, e.estado, h.nombre
        )
        FROM Empleado e
        JOIN e.hotel h
        WHERE h.idHotel = :idHotel
        ORDER BY e.nombre ASC
        """)
    List<EmpleadoListadoDTO> findListadoPorHotel(@Param("idHotel") Integer idHotel);

    List<Empleado> findByHotelIdHotelAndEstado(Integer idHotel, EstadoEmpleado estado);

    List<Empleado> findByHotelIdHotelAndRol(Integer idHotel, RolEmpleado rol);
}
