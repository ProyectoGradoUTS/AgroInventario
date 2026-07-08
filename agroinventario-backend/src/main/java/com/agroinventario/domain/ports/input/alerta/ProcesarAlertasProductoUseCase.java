package com.agroinventario.domain.ports.input.alerta;

import com.agroinventario.domain.model.Producto;

public interface ProcesarAlertasProductoUseCase {

    void ejecutar(Producto producto);
}
