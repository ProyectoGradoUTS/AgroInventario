package com.agroinventario.application.usecase.alerta;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.alerta.ActualizarEstadoAlertaUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarEstadoAlertaUseCaseImpl implements ActualizarEstadoAlertaUseCase {

    private final AlertaRepositoryPort alertaRepository;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public ActualizarEstadoAlertaUseCaseImpl(
            AlertaRepositoryPort alertaRepository,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.alertaRepository = alertaRepository;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @Override
    public Alerta ejecutar(Long alertaId, EstadoAlerta nuevoEstado) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", alertaId));

        if (!alerta.puedeTransicionarA(nuevoEstado)) {
            throw new BusinessRuleException(
                    "No se puede cambiar la alerta de %s a %s".formatted(alerta.estado(), nuevoEstado));
        }

        // Si el estado es el mismo, retornar sin hacer nada pero sin error
        if (alerta.estado() == nuevoEstado) {
            return alerta;
        }

        // Guardar el cambio de estado
        Alerta actualizada = alerta.conEstado(nuevoEstado);
        Alerta guardada = alertaRepository.save(actualizada);

        // Registrar en auditoría
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.ALERTA,
                guardada.id(),
                TipoAccionAuditoria.ALERTA_ACTUALIZADA,
                "Estado de alerta cambiado a %s".formatted(nuevoEstado));

        return guardada;
    }
}