package com.core.hostal.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.core.hostal.entity.Factura;
import com.core.hostal.entity.Reserva;
import com.core.hostal.entity.ReservaHabitacion;
import com.core.hostal.exception.AuthException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;
    private final String baseUrl;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail,
            @Value("${app.mail.from-name}") String fromName,
            @Value("${app.base-url}") String baseUrl) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public void enviarEnlaceRecuperacion(String destinatario, String nombre, String enlace) {
        enviarObligatorio(
            destinatario,
            "Recuperar contraseña - " + fromName,
            "Hola " + nombre + ",\n\n"
            + "Recibimos una solicitud para restablecer tu contraseña en " + fromName + ".\n\n"
            + "Haz clic en el siguiente enlace (válido por 1 hora):\n"
            + enlace + "\n\n"
            + "Si no solicitaste este cambio, puedes ignorar este mensaje.\n\n"
            + "Saludos,\n" + fromName
        );
    }

    public void enviarBienvenida(String destinatario, String nombre) {
        enviarNotificacion(
            destinatario,
            "Bienvenido a " + fromName,
            "Hola " + nombre + ",\n\n"
            + "¡Gracias por registrarte en " + fromName + "!\n\n"
            + "Tu cuenta ha sido creada correctamente. Ya puedes iniciar sesión y reservar "
            + "tus habitaciones desde nuestra plataforma.\n\n"
            + "Ingresa aquí: " + baseUrl + "/login\n\n"
            + "Te esperamos pronto.\n\n"
            + "Saludos,\n" + fromName
        );
    }

    public void enviarBoletaReserva(Reserva reserva, Factura factura, String nombreHotel) {
        long noches = ChronoUnit.DAYS.between(reserva.getFechaCheckin(), reserva.getFechaCheckout());
        String habitaciones = reserva.getHabitaciones().stream()
            .map(detalle -> formatearLineaHabitacion(detalle, noches))
            .collect(Collectors.joining("\n"));

        String cuerpo = """
            Hola %s,

            Tu reserva en %s ha sido confirmada. Adjuntamos el detalle de tu boleta:

            ─────────────────────────────────────
            BOLETA DE RESERVA
            ─────────────────────────────────────
            N° Factura: %s
            Código de reserva: %s
            Fecha de emisión: %s

            Cliente: %s
            Documento: %s

            Check-in: %s
            Check-out: %s
            Huéspedes: %d
            Noches: %d

            Habitaciones:
            %s

            Subtotal: %s
            Impuestos: %s
            TOTAL: %s
            ─────────────────────────────────────

            Puedes ver tus reservas en: %s/mis-reservas

            Gracias por elegirnos.

            Saludos,
            %s
            """.formatted(
            reserva.getCliente().getNombre(),
            nombreHotel,
            factura.getNumeroFactura(),
            reserva.getCodigoReserva(),
            factura.getFecha().format(FECHA),
            reserva.getCliente().getNombre(),
            reserva.getCliente().getDocumento(),
            reserva.getFechaCheckin().format(FECHA),
            reserva.getFechaCheckout().format(FECHA),
            reserva.getNumHuespedes(),
            noches,
            habitaciones,
            formatearMonto(factura.getSubtotal()),
            formatearMonto(factura.getImpuestos()),
            formatearMonto(factura.getTotal()),
            baseUrl,
            fromName
        );

        enviarNotificacion(
            reserva.getCliente().getEmail(),
            "Boleta de reserva " + reserva.getCodigoReserva() + " - " + fromName,
            cuerpo
        );
    }

    private String formatearLineaHabitacion(ReservaHabitacion detalle, long noches) {
        String tipo = detalle.getHabitacion().getTipo().getNombre();
        String numero = detalle.getHabitacion().getNumero();
        BigDecimal subtotal = detalle.getPrecioNoche().multiply(BigDecimal.valueOf(noches));
        return String.format(
            "- %s (N° %s): %s x %d noche(s) = %s",
            tipo,
            numero,
            formatearMonto(detalle.getPrecioNoche()) + "/noche",
            noches,
            formatearMonto(subtotal)
        );
    }

    private String formatearMonto(BigDecimal monto) {
        return "S/ " + monto.stripTrailingZeros().toPlainString();
    }

    private void enviarObligatorio(String destinatario, String asunto, String texto) {
        try {
            enviar(destinatario, asunto, texto);
        } catch (MailException ex) {
            throw new AuthException(
                "No se pudo enviar el correo. Verifica que la dirección sea válida e intenta nuevamente.");
        }
    }

    private void enviarNotificacion(String destinatario, String asunto, String texto) {
        try {
            enviar(destinatario, asunto, texto);
        } catch (MailException ex) {
            log.warn("No se pudo enviar el correo '{}' a {}: {}", asunto, destinatario, ex.getMessage());
        }
    }

    private void enviar(String destinatario, String asunto, String texto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        mailSender.send(mensaje);
    }
}
