package com.agroinventario.application.usecase.alerta;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.ports.input.alerta.ListarAlertasUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarAlertasUseCaseImpl implements ListarAlertasUseCase {

    private final AlertaRepositoryPort alertaRepository;

    public ListarAlertasUseCaseImpl(AlertaRepositoryPort alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Override
    public List<Alerta> ejecutar(EstadoAlerta estado) {
        if (estado == null) {
            return alertaRepository.findAll();
        }
        return alertaRepository.findByEstado(estado);
    }
}
