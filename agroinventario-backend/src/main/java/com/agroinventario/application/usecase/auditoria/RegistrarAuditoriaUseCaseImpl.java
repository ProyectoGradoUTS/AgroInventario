package com.agroinventario.application.usecase.auditoria;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.output.AuditoriaRepositoryPort;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarAuditoriaUseCaseImpl implements RegistrarAuditoriaUseCase {

    private final AuditoriaRepositoryPort auditoriaRepository;
    private final CurrentUserPort currentUserPort;

    public RegistrarAuditoriaUseCaseImpl(
            AuditoriaRepositoryPort auditoriaRepository,
            CurrentUserPort currentUserPort) {
        this.auditoriaRepository = auditoriaRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void ejecutar(EntidadAuditoria entidad, Long entidadId, TipoAccionAuditoria accion, String detalle) {
        Long usuarioId = null;
        String usuarioEmail = null;

        try {
            usuarioId = currentUserPort.getUserId();
            usuarioEmail = currentUserPort.getEmail();
        } catch (Exception ignored) {
            // Procesos automáticos sin contexto de seguridad
        }

        Auditoria registro = new Auditoria(
                null,
                entidad,
                entidadId,
                accion,
                detalle,
                usuarioId,
                usuarioEmail,
                LocalDateTime.now()
        );

        auditoriaRepository.save(registro);
    }
}
