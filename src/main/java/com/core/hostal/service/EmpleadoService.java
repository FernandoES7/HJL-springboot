package com.core.hostal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.EmpleadoListadoDTO;
import com.core.hostal.entity.Empleado;
import com.core.hostal.repository.EmpleadoRepository;

@Service
@Transactional(readOnly = true)
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<EmpleadoListadoDTO> listarTodos() {
        return empleadoRepository.findListado();
    }

    public List<EmpleadoListadoDTO> listarPorHotel(Integer idHotel) {
        return empleadoRepository.findListadoPorHotel(idHotel);
    }

    public Optional<Empleado> buscarPorEmail(String email) {
        return empleadoRepository.findByEmail(email);
    }
}
