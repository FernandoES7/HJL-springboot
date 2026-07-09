package com.core.hostal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.hostal.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Optional<Hotel> findFirstByActivoTrue();
}
