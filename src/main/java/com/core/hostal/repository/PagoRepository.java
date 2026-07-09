package com.core.hostal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.hostal.entity.Pago;
import com.core.hostal.enums.EstadoPago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByFacturaIdFactura(Integer idFactura);

    List<Pago> findByFacturaIdFacturaAndEstado(Integer idFactura, EstadoPago estado);
}
