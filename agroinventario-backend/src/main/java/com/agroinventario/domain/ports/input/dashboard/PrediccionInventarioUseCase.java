package com.agroinventario.domain.ports.input.dashboard;

import com.agroinventario.application.dto.response.PrediccionProductoResponse;

import java.util.List;

public interface PrediccionInventarioUseCase {

    List<PrediccionProductoResponse> ejecutar();
}
