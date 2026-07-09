package com.core.hostal.service;

import java.time.LocalDate;
import java.util.Optional;

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
import com.core.hostal.security.PasswordConstants;
import com.core.hostal.session.UsuarioSesion;

@Service
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UsuarioSesion iniciarSesion(String email, String password) {
        String emailNormalizado = normalizarEmail(email);

        Optional<Empleado> empleadoOpt = empleadoRepository.findByEmail(emailNormalizado);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            if (empleado.getEstado() != EstadoEmpleado.activo) {
                throw new AuthException("La cuenta de empleado no está activa.");
            }
            if (!passwordEncoder.matches(password, empleado.getPasswordHash())) {
                throw new AuthException("Correo o contraseña incorrectos.");
            }
            if (empleado.getRol() == RolEmpleado.admin) {
                return UsuarioSesion.admin(
                    empleado.getIdEmpleado(),
                    empleado.getNombre(),
                    empleado.getEmail()
                );
            }
            throw new AuthException("Acceso no permitido para este tipo de usuario.");
        }

        Cliente cliente = clienteRepository.findByEmail(emailNormalizado)
            .orElseThrow(() -> new AuthException("Correo o contraseña incorrectos."));

        if (cliente.getEstado() != EstadoCliente.activo) {
            throw new AuthException("La cuenta de cliente no está activa.");
        }
        if (!passwordEncoder.matches(password, cliente.getPasswordHash())) {
            throw new AuthException("Correo o contraseña incorrectos.");
        }

        return UsuarioSesion.cliente(
            cliente.getIdCliente(),
            cliente.getNombre(),
            cliente.getEmail()
        );
    }

    @Transactional
    public UsuarioSesion registrar(
            String nombre,
            String email,
            String documento,
            String telefono,
            String password,
            String confirmacion) {
        validarRegistro(nombre, email, documento, telefono, password, confirmacion);
        String emailNormalizado = normalizarEmail(email);
        String documentoNormalizado = documento.trim();

        if (empleadoRepository.existsByEmail(emailNormalizado)) {
            throw new AuthException("El correo ya está registrado como empleado.");
        }

        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(emailNormalizado);
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            if (!esCuentaInvitado(cliente)) {
                throw new AuthException("El correo ya está registrado. Inicia sesión con tu contraseña.");
            }
            validarDocumentoDisponible(documentoNormalizado, cliente.getIdCliente());
            cliente.setNombre(nombre.trim());
            cliente.setDocumento(documentoNormalizado);
            cliente.setTelefono(telefono.trim());
            cliente.setPasswordHash(passwordEncoder.encode(password));
            clienteRepository.save(cliente);
            emailService.enviarBienvenida(cliente.getEmail(), cliente.getNombre());
            return UsuarioSesion.cliente(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getEmail()
            );
        }

        if (clienteRepository.existsByDocumento(documentoNormalizado)) {
            throw new AuthException("El documento ya está registrado.");
        }

        Cliente nuevo = new Cliente();
        nuevo.setNombre(nombre.trim());
        nuevo.setEmail(emailNormalizado);
        nuevo.setDocumento(documentoNormalizado);
        nuevo.setTelefono(telefono.trim());
        nuevo.setPasswordHash(passwordEncoder.encode(password));
        nuevo.setFechaRegistro(LocalDate.now());
        nuevo.setEstado(EstadoCliente.activo);
        nuevo = clienteRepository.save(nuevo);
        emailService.enviarBienvenida(nuevo.getEmail(), nuevo.getNombre());

        return UsuarioSesion.cliente(
            nuevo.getIdCliente(),
            nuevo.getNombre(),
            nuevo.getEmail()
        );
    }

    public boolean esCuentaInvitado(Cliente cliente) {
        return passwordEncoder.matches(
            PasswordConstants.GUEST_PLAIN_PASSWORD,
            cliente.getPasswordHash()
        );
    }

    private void validarRegistro(
            String nombre,
            String email,
            String documento,
            String telefono,
            String password,
            String confirmacion) {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new AuthException("Ingresa tu nombre completo.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new AuthException("Ingresa un correo electrónico válido.");
        }
        if (documento == null || documento.trim().length() < 6) {
            throw new AuthException("Ingresa un documento de identidad válido.");
        }
        if (telefono == null || telefono.trim().length() < 6) {
            throw new AuthException("Ingresa un número de teléfono válido.");
        }
        if (password == null || password.length() < 6) {
            throw new AuthException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (!password.equals(confirmacion)) {
            throw new AuthException("Las contraseñas no coinciden.");
        }
    }

    private void validarDocumentoDisponible(String documento, Integer idClienteActual) {
        clienteRepository.findByDocumento(documento).ifPresent(existente -> {
            if (!existente.getIdCliente().equals(idClienteActual)) {
                throw new AuthException("El documento ya está registrado.");
            }
        });
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
