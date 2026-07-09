package com.core.hostal.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.entity.Cliente;
import com.core.hostal.entity.Empleado;
import com.core.hostal.enums.EstadoCliente;
import com.core.hostal.enums.EstadoEmpleado;
import com.core.hostal.enums.RolEmpleado;
import com.core.hostal.exception.AuthException;
import com.core.hostal.repository.ClienteRepository;
import com.core.hostal.repository.EmpleadoRepository;

@Service
public class PasswordResetService {

    private static final int HORAS_VALIDEZ_TOKEN = 1;

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final String baseUrl;

    public PasswordResetService(
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            @Value("${app.base-url}") String baseUrl) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Transactional
    public void solicitarRecuperacion(String email) {
        String emailNormalizado = normalizarEmail(email);

        Optional<Empleado> empleadoOpt = empleadoRepository.findByEmail(emailNormalizado);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            if (empleado.getEstado() != EstadoEmpleado.activo) {
                throw new AuthException("La cuenta no está activa.");
            }
            if (empleado.getRol() != RolEmpleado.admin) {
                throw new AuthException("No encontramos una cuenta registrada con ese correo.");
            }
            enviarTokenEmpleado(empleado);
            return;
        }

        Cliente cliente = clienteRepository.findByEmail(emailNormalizado)
            .orElseThrow(() -> new AuthException("No encontramos una cuenta registrada con ese correo."));

        if (cliente.getEstado() != EstadoCliente.activo) {
            throw new AuthException("La cuenta no está activa.");
        }

        enviarTokenCliente(cliente);
    }

    @Transactional
    public void restablecerContrasena(String token, String password, String confirmacion) {
        if (token == null || token.isBlank()) {
            throw new AuthException("El enlace de recuperación no es válido.");
        }
        if (password == null || password.length() < 6) {
            throw new AuthException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (!password.equals(confirmacion)) {
            throw new AuthException("Las contraseñas no coinciden.");
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findByResetToken(token);
        if (empleadoOpt.isPresent()) {
            actualizarContrasenaEmpleado(empleadoOpt.get(), token, password);
            return;
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByResetToken(token);
        if (clienteOpt.isPresent()) {
            actualizarContrasenaCliente(clienteOpt.get(), token, password);
            return;
        }

        throw new AuthException("El enlace de recuperación no es válido o ha expirado.");
    }

    public boolean esTokenValido(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return empleadoRepository.findByResetToken(token)
            .filter(this::tokenVigente)
            .isPresent()
            || clienteRepository.findByResetToken(token)
                .filter(this::tokenVigente)
                .isPresent();
    }

    private void enviarTokenEmpleado(Empleado empleado) {
        String token = generarToken();
        empleado.setResetToken(token);
        empleado.setResetTokenExpires(LocalDateTime.now().plusHours(HORAS_VALIDEZ_TOKEN));
        empleadoRepository.save(empleado);
        emailService.enviarEnlaceRecuperacion(
            empleado.getEmail(),
            empleado.getNombre(),
            construirEnlace(token)
        );
    }

    private void enviarTokenCliente(Cliente cliente) {
        String token = generarToken();
        cliente.setResetToken(token);
        cliente.setResetTokenExpires(LocalDateTime.now().plusHours(HORAS_VALIDEZ_TOKEN));
        clienteRepository.save(cliente);
        emailService.enviarEnlaceRecuperacion(
            cliente.getEmail(),
            cliente.getNombre(),
            construirEnlace(token)
        );
    }

    private void actualizarContrasenaEmpleado(Empleado empleado, String token, String password) {
        validarToken(empleado.getResetToken(), empleado.getResetTokenExpires(), token);
        empleado.setPasswordHash(passwordEncoder.encode(password));
        empleado.setResetToken(null);
        empleado.setResetTokenExpires(null);
        empleadoRepository.save(empleado);
    }

    private void actualizarContrasenaCliente(Cliente cliente, String token, String password) {
        validarToken(cliente.getResetToken(), cliente.getResetTokenExpires(), token);
        cliente.setPasswordHash(passwordEncoder.encode(password));
        cliente.setResetToken(null);
        cliente.setResetTokenExpires(null);
        clienteRepository.save(cliente);
    }

    private void validarToken(String tokenGuardado, LocalDateTime expira, String tokenRecibido) {
        if (tokenGuardado == null || !tokenGuardado.equals(tokenRecibido)) {
            throw new AuthException("El enlace de recuperación no es válido o ha expirado.");
        }
        if (expira == null || expira.isBefore(LocalDateTime.now())) {
            throw new AuthException("El enlace de recuperación ha expirado. Solicita uno nuevo.");
        }
    }

    private boolean tokenVigente(Empleado empleado) {
        return empleado.getResetTokenExpires() != null
            && !empleado.getResetTokenExpires().isBefore(LocalDateTime.now());
    }

    private boolean tokenVigente(Cliente cliente) {
        return cliente.getResetTokenExpires() != null
            && !cliente.getResetTokenExpires().isBefore(LocalDateTime.now());
    }

    private String generarToken() {
        return UUID.randomUUID().toString();
    }

    private String construirEnlace(String token) {
        return baseUrl + "/restablecer-contrasena?token=" + token;
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
