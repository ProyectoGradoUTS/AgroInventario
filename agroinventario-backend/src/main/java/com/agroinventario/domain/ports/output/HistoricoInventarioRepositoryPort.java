package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.HistoricoInventarioResumen;

import java.util.Optional;

public interface HistoricoInventarioRepositoryPort {

    Optional<HistoricoInventarioResumen> resumirProducto(Long productoId, int dias);
}
