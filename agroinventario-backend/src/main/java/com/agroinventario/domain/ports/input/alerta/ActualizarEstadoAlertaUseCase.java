package com.agroinventario.domain.ports.input.alerta;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;

public interface ActualizarEstadoAlertaUseCase {

    Alerta ejecutar(Long alertaId, EstadoAlerta nuevoEstado);
}
