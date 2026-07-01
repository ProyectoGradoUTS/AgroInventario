package com.agroinventario.domain.service;

import org.springframework.stereotype.Component;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAlerta;

import java.time.LocalDateTime;

/**
 * Generación de alertas de negocio (stock bajo, etc.).
 */
@Component
public class AlertaDomainService {

    public Alerta crearAlertaStockBajo(Producto producto) {
        String mensaje = "Stock bajo en '%s': actual=%d, mínimo=%d"
                .formatted(producto.nombre(), producto.stockActual(), producto.stockMinimo());

        return new Alerta(
                null,
                producto.id(),
                producto.nombre(),
                TipoAlerta.STOCK_BAJO,
                mensaje,
                EstadoAlerta.PENDIENTE,
                LocalDateTime.now()
        );
    }

    public boolean requiereAlertaStockBajo(Producto producto) {
        return producto.stockBajo();
    }
}
