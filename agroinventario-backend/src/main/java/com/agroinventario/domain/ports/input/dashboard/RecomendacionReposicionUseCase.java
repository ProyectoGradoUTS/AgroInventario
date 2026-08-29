package com.agroinventario.domain.ports.input.dashboard;

import com.agroinventario.application.dto.response.RecomendacionProductoResponse;

import java.util.List;

public interface RecomendacionReposicionUseCase {

    List<RecomendacionProductoResponse> ejecutar();
}
