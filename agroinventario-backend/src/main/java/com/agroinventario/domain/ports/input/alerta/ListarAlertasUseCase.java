package com.agroinventario.domain.ports.input.alerta;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;

import java.util.List;

public interface ListarAlertasUseCase {

    List<Alerta> ejecutar(EstadoAlerta estado);
}
