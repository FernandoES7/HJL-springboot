package com.core.hostal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.core.hostal.dto.ClienteListadoDTO;
import com.core.hostal.entity.Cliente;
import com.core.hostal.enums.EstadoCliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByResetToken(String resetToken);

    Optional<Cliente> findByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByDocumento(String documento);

    @Query("""
        SELECT new com.core.hostal.dto.ClienteListadoDTO(
            c.idCliente, c.nombre, c.email, c.telefono,
            c.documento, c.fechaRegistro, c.estado
        )
        FROM Cliente c
        ORDER BY c.nombre ASC
        """)
    List<ClienteListadoDTO> findListado();

    @Query("""
        SELECT new com.core.hostal.dto.ClienteListadoDTO(
            c.idCliente, c.nombre, c.email, c.telefono,
            c.documento, c.fechaRegistro, c.estado
        )
        FROM Cliente c
        WHERE c.estado = :estado
        ORDER BY c.nombre ASC
        """)
    List<ClienteListadoDTO> findListadoPorEstado(EstadoCliente estado);
}
