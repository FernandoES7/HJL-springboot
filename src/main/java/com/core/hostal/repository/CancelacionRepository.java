package com.core.hostal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.hostal.entity.Cancelacion;

public interface CancelacionRepository extends JpaRepository<Cancelacion, Integer> {

    Optional<Cancelacion> findByReservaIdReserva(Integer idReserva);

    boolean existsByReservaIdReserva(Integer idReserva);
}
