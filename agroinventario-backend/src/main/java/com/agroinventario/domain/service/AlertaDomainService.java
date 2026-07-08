package com.agroinventario.domain.service;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAlerta;

import java.time.LocalDateTime;

/**
 * Generación de alertas de negocio (stock bajo, vencimiento próximo, etc.).
 */
public class AlertaDomainService {

    public Alerta crearAlertaStockBajo(Producto producto) {
        String mensaje = "Stock bajo en '%s': actual=%d, mínimo=%d"
                .formatted(producto.nombre(), producto.stockActual(), producto.stockMinimo());

        return nuevaAlerta(producto, TipoAlerta.STOCK_BAJO, mensaje);
    }

    public Alerta crearAlertaVencimientoProximo(Producto producto) {
        long dias = producto.diasHastaVencimiento();
        String mensaje = switch ((int) dias) {
            case 0 -> "El producto '%s' vence hoy (%s)".formatted(
                    producto.nombre(), producto.fechaVencimiento());
            default -> {
                if (dias < 0) {
                    yield "El producto '%s' está vencido desde hace %d día(s) (%s)".formatted(
                            producto.nombre(), Math.abs(dias), producto.fechaVencimiento());
                }
                yield "El producto '%s' vence en %d día(s) (%s)".formatted(
                        producto.nombre(), dias, producto.fechaVencimiento());
            }
        };

        return nuevaAlerta(producto, TipoAlerta.VENCIMIENTO_PROXIMO, mensaje);
    }

    public boolean requiereAlertaStockBajo(Producto producto) {
        return producto.estaActivo() && producto.stockBajo();
    }

    public boolean requiereAlertaVencimiento(Producto producto, int diasAnticipacion) {
        return producto.estaActivo() && producto.vencimientoProximo(diasAnticipacion);
    }

    private Alerta nuevaAlerta(Producto producto, TipoAlerta tipo, String mensaje) {
        return new Alerta(
                null,
                producto.id(),
                producto.nombre(),
                tipo,
                mensaje,
                EstadoAlerta.PENDIENTE,
                LocalDateTime.now()
        );
    }
}
