package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.ProyeccionInventario;

import java.time.LocalDate;
import java.util.List;

public interface PrediccionInventarioPersistenciaPort {

    void reemplazarPrediccionesDelDia(List<ProyeccionInventario> proyecciones);

    void registrarEjecucionModelo(
            int productos,
            int registrosHistoricos,
            LocalDate inicioDatos,
            LocalDate finDatos,
            double mae,
            double rmse);
}
