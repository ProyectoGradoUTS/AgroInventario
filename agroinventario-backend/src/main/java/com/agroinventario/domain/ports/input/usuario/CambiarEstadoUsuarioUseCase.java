package com.agroinventario.domain.ports.input.usuario;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Usuario;

public interface CambiarEstadoUsuarioUseCase {

    Usuario ejecutar(Long id, EstadoGeneral estado);
}
