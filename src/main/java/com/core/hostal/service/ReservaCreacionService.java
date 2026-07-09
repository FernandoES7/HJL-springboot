package com.core.hostal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.CrearReservaRequestDTO;
import com.core.hostal.dto.HabitacionSeleccionRequestDTO;
import com.core.hostal.dto.ReservaCreadaResponseDTO;
import com.core.hostal.entity.Cliente;
import com.core.hostal.entity.Factura;
import com.core.hostal.entity.Habitacion;
import com.core.hostal.entity.Hotel;
import com.core.hostal.entity.Pago;
import com.core.hostal.entity.Reserva;
import com.core.hostal.entity.ReservaHabitacion;
import com.core.hostal.entity.TipoHabitacion;
import com.core.hostal.enums.EstadoCliente;
import com.core.hostal.enums.EstadoFactura;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.enums.EstadoPago;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.security.PasswordConstants;
import com.core.hostal.repository.ClienteRepository;
import com.core.hostal.repository.FacturaRepository;
import com.core.hostal.repository.HabitacionRepository;
import com.core.hostal.repository.HotelRepository;
import com.core.hostal.repository.PagoRepository;
import com.core.hostal.repository.ReservaRepository;
import com.core.hostal.repository.TipoHabitacionRepository;

@Service
public class ReservaCreacionService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final HotelRepository hotelRepository;
    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ReservaCreacionService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            HotelRepository hotelRepository,
            HabitacionRepository habitacionRepository,
            TipoHabitacionRepository tipoHabitacionRepository,
            FacturaRepository facturaRepository,
            PagoRepository pagoRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.hotelRepository = hotelRepository;
        this.habitacionRepository = habitacionRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public ReservaCreadaResponseDTO crearReserva(CrearReservaRequestDTO request, Integer idClienteSesion) {
        validarRequest(request);

        Hotel hotel = hotelRepository.findFirstByActivoTrue()
            .orElseThrow(() -> new ReservaException("No hay un hotel activo configurado."));

        Cliente cliente = resolverCliente(request, idClienteSesion);
        long noches = ChronoUnit.DAYS.between(request.getFechaCheckin(), request.getFechaCheckout());

        List<ReservaHabitacion> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (HabitacionSeleccionRequestDTO seleccion : request.getHabitaciones()) {
            TipoHabitacion tipo = tipoHabitacionRepository.findById(seleccion.getIdTipo())
                .orElseThrow(() -> new ReservaException("Tipo de habitación no encontrado."));

            List<Habitacion> disponibles = habitacionRepository.findDisponiblesPorTipoYFechas(
                hotel.getIdHotel(),
                seleccion.getIdTipo(),
                request.getFechaCheckin(),
                request.getFechaCheckout(),
                EstadoHabitacion.disponible,
                EstadoReserva.cancelada,
                EstadoReserva.completada
            );

            if (disponibles.size() < seleccion.getCantidad()) {
                throw new ReservaException(
                    "No hay suficientes habitaciones disponibles de tipo: " + tipo.getNombre());
            }

            for (int i = 0; i < seleccion.getCantidad(); i++) {
                Habitacion habitacion = disponibles.get(i);
                ReservaHabitacion detalle = new ReservaHabitacion();
                detalle.setHabitacion(habitacion);
                detalle.setPrecioNoche(tipo.getPrecioBase());
                detalles.add(detalle);
                total = total.add(tipo.getPrecioBase().multiply(BigDecimal.valueOf(noches)));
            }
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setEmpleado(null);
        reserva.setCodigoReserva(generarCodigoUnico());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaCheckin(request.getFechaCheckin());
        reserva.setFechaCheckout(request.getFechaCheckout());
        reserva.setNumHuespedes(request.getNumHuespedes());
        reserva.setTotal(total);
        reserva.setEstado(EstadoReserva.confirmada);

        for (ReservaHabitacion detalle : detalles) {
            reserva.addHabitacion(detalle);
        }

        reserva = reservaRepository.save(reserva);

        Factura factura = crearFacturaYPago(reserva, request.getTarjetaReferencia());
        emailService.enviarBoletaReserva(reserva, factura, hotel.getNombre());

        return new ReservaCreadaResponseDTO(
            reserva.getIdReserva(),
            reserva.getCodigoReserva(),
            reserva.getTotal()
        );
    }

    private void validarRequest(CrearReservaRequestDTO request) {
        if (request.getFechaCheckin() == null || request.getFechaCheckout() == null) {
            throw new ReservaException("Las fechas de check-in y check-out son obligatorias.");
        }
        if (!request.getFechaCheckout().isAfter(request.getFechaCheckin())) {
            throw new ReservaException("La fecha de check-out debe ser posterior al check-in.");
        }
        if (request.getNumHuespedes() == null || request.getNumHuespedes() < 1) {
            throw new ReservaException("Debe indicar al menos un huésped.");
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ReservaException("El nombre del cliente es obligatorio.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ReservaException("El correo electrónico es obligatorio.");
        }
        if (request.getDocumento() == null || request.getDocumento().isBlank()) {
            throw new ReservaException("El documento de identidad es obligatorio.");
        }
        if (request.getHabitaciones() == null || request.getHabitaciones().isEmpty()) {
            throw new ReservaException("Debe seleccionar al menos una habitación.");
        }
        for (HabitacionSeleccionRequestDTO h : request.getHabitaciones()) {
            if (h.getIdTipo() == null || h.getCantidad() == null || h.getCantidad() < 1) {
                throw new ReservaException("La selección de habitaciones no es válida.");
            }
        }
    }

    private Cliente resolverCliente(CrearReservaRequestDTO request, Integer idClienteSesion) {
        if (idClienteSesion != null) {
            Cliente cliente = clienteRepository.findById(idClienteSesion)
                .orElseThrow(() -> new ReservaException("Cliente no encontrado."));
            if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
                cliente.setTelefono(request.getTelefono().trim());
            }
            return clienteRepository.save(cliente);
        }

        Optional<Cliente> porEmail = clienteRepository.findByEmail(request.getEmail().trim());
        Optional<Cliente> porDocumento = clienteRepository.findByDocumento(request.getDocumento().trim());

        if (porEmail.isPresent()) {
            Cliente cliente = porEmail.get();
            if (!cliente.getDocumento().equals(request.getDocumento().trim())) {
                throw new ReservaException("El correo ya está registrado con otro documento.");
            }
            actualizarDatosCliente(cliente, request);
            return clienteRepository.save(cliente);
        }

        if (porDocumento.isPresent()) {
            throw new ReservaException("El documento ya está registrado con otro correo.");
        }

        Cliente nuevo = new Cliente();
        nuevo.setNombre(request.getNombre().trim());
        nuevo.setEmail(request.getEmail().trim());
        nuevo.setDocumento(request.getDocumento().trim());
        nuevo.setTelefono(request.getTelefono() != null ? request.getTelefono().trim() : null);
        nuevo.setPasswordHash(passwordEncoder.encode(PasswordConstants.GUEST_PLAIN_PASSWORD));
        nuevo.setFechaRegistro(LocalDate.now());
        nuevo.setEstado(EstadoCliente.activo);
        return clienteRepository.save(nuevo);
    }

    private void actualizarDatosCliente(Cliente cliente, CrearReservaRequestDTO request) {
        cliente.setNombre(request.getNombre().trim());
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            cliente.setTelefono(request.getTelefono().trim());
        }
    }

    private Factura crearFacturaYPago(Reserva reserva, String tarjetaReferencia) {
        Factura factura = new Factura();
        factura.setReserva(reserva);
        factura.setNumeroFactura(generarNumeroFacturaUnico());
        factura.setSubtotal(reserva.getTotal());
        factura.setImpuestos(BigDecimal.ZERO);
        factura.setTotal(reserva.getTotal());
        factura.setFecha(LocalDate.now());
        factura.setEstado(EstadoFactura.emitida);
        factura = facturaRepository.save(factura);

        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMonto(reserva.getTotal());
        pago.setFecha(LocalDateTime.now());
        pago.setEstado(EstadoPago.completado);
        pago.setReferencia(tarjetaReferencia != null ? "TARJ-" + tarjetaReferencia : "TARJ-ONLINE");
        pagoRepository.save(pago);
        return factura;
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            int aleatorio = ThreadLocalRandom.current().nextInt(1000, 9999);
            codigo = "HB-" + fecha + "-" + aleatorio;
        } while (reservaRepository.existsByCodigoReserva(codigo));
        return codigo;
    }

    private String generarNumeroFacturaUnico() {
        String numero;
        do {
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int aleatorio = ThreadLocalRandom.current().nextInt(1000, 9999);
            numero = "FAC-" + fecha + "-" + aleatorio;
        } while (facturaRepository.existsByNumeroFactura(numero));
        return numero;
    }
}
