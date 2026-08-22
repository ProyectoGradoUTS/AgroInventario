package com.agroinventario.infrastructure.adapters.output.email;

import com.agroinventario.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de notificación por correo electrónico para alertas de inventario.
 * Envía correos cuando los productos están próximos a su fecha de vencimiento.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${app.alertas.email-destino:}")
    private String emailDestino;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo con la lista de productos próximos a vencer.
     *
     * @param productos Lista de productos con vencimiento próximo
     */
    public void enviarAlertaVencimiento(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return;
        }

        if (emailDestino == null || emailDestino.isBlank()) {
            log.warn("No se configuró la propiedad 'app.alertas.email-destino'. No se envió correo de alerta.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(emailDestino);
            message.setSubject("[AgroInventario] Alerta: " + productos.size() + " producto(s) próximo(s) a vencer");

            StringBuilder body = new StringBuilder();
            body.append("Se detectaron los siguientes productos próximos a su fecha de vencimiento:\n\n");
            body.append("========================================\n");

            for (Producto p : productos) {
                body.append("Producto: ").append(p.nombre()).append("\n");
                body.append("Stock actual: ").append(p.stockActual()).append("\n");
                body.append("Stock mínimo: ").append(p.stockMinimo()).append("\n");
                body.append("Fecha de vencimiento: ").append(p.fechaVencimiento()).append("\n");
                body.append("----------------------------------------\n");
            }

            body.append("\nPor favor revise el inventario y tome las acciones necesarias.\n");
            body.append("Este es un correo automático, por favor no responder.\n");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Correo de alerta de vencimiento enviado a {}", emailDestino);
        } catch (Exception e) {
            log.error("Error al enviar correo de alerta de vencimiento: {}", e.getMessage(), e);
        }
    }
}