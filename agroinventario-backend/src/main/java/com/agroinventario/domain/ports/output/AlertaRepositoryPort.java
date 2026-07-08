package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.TipoAlerta;

import java.util.List;
import java.util.Optional;

public interface AlertaRepositoryPort {

    Alerta save(Alerta alerta);

    List<Alerta> findAll();

    List<Alerta> findByEstado(EstadoAlerta estado);

    Optional<Alerta> findById(Long id);

    Optional<Alerta> findPendienteByProductoAndTipo(Long productoId, TipoAlerta tipoAlerta);
}
