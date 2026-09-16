package com.agroinventario.domain.ports.input.asistente;

import com.agroinventario.application.dto.response.AsistenteConsultaResponse;

public interface ConsultarAsistenteUseCase {

    AsistenteConsultaResponse ejecutar(String pregunta);
}
