package com.core.hostal.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.entity.Hotel;
import com.core.hostal.repository.HotelRepository;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Optional<Hotel> obtenerHotelActivo() {
        return hotelRepository.findFirstByActivoTrue();
    }

    public Optional<Hotel> buscarPorId(Integer idHotel) {
        return hotelRepository.findById(idHotel);
    }

    @Transactional
    public Hotel actualizarDatosGenerales(
            Integer idHotel,
            String nombre,
            String direccion,
            String email,
            String telefono) {
        Hotel hotel = hotelRepository.findById(idHotel)
            .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado"));
        hotel.setNombre(nombre);
        hotel.setDireccion(direccion);
        hotel.setEmail(email);
        hotel.setTelefono(telefono);
        return hotelRepository.save(hotel);
    }
}
