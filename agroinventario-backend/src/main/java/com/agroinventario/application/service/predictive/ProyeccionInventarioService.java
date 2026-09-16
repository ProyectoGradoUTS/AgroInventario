package com.agroinventario.application.service.predictive;

import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.model.SerieConsumoDiario;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import com.agroinventario.domain.service.MotorPredictivoInventario;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProyeccionInventarioService {

    private static final int VENTANA_DIAS = 90;
    private static final int MINIMO_OBSERVACIONES = 14;
    private static final long TTL_MS = 15_000;

    private final HistoricoInventarioRepositoryPort historicoRepository;
    private final DatasetSinteticoPrediccionService datasetSintetico;
    private final MotorPredictivoInventario motor = new MotorPredictivoInventario();
    private List<ProyeccionInventario> cache = List.of();
    private long cacheMillis = 0;

    public ProyeccionInventarioService(
            HistoricoInventarioRepositoryPort historicoRepository,
            DatasetSinteticoPrediccionService datasetSintetico) {
        this.historicoRepository = historicoRepository;
        this.datasetSintetico = datasetSintetico;
    }

    public ProyeccionInventario proyectar(Producto producto) {
        List<SerieConsumoDiario> serie = historicoRepository.listarSerie(producto.id(), VENTANA_DIAS);
        String fuente = "HISTORICO_REAL";

        if (serie.size() < MINIMO_OBSERVACIONES) {
            List<SerieConsumoDiario> sintetico = desdeSintetico(producto);
            historicoRepository.insertarSerieSiAusente(producto.id(), sintetico);
            List<SerieConsumoDiario> recargada = historicoRepository.listarSerie(producto.id(), VENTANA_DIAS);
            if (recargada.size() >= MINIMO_OBSERVACIONES) {
                serie = recargada;
                fuente = "HISTORICO_REAL";
            } else {
                serie = sintetico;
                fuente = "SINTETICO";
            }
        }

        return motor.proyectar(producto, serie, fuente);
    }

    public List<ProyeccionInventario> proyectarTodos(List<Producto> productos) {
        long ahora = System.currentTimeMillis();
        synchronized (this) {
            if (!cache.isEmpty()
                    && ahora - cacheMillis < TTL_MS
                    && cache.size() == productos.size()) {
                return cache;
            }
            List<ProyeccionInventario> resultado = new ArrayList<>();
            for (Producto producto : productos) {
                resultado.add(proyectar(producto));
            }
            cache = List.copyOf(resultado);
            cacheMillis = ahora;
            return cache;
        }
    }

    public void asegurarHistorico(List<Producto> productos) {
        for (Producto producto : productos) {
            if (historicoRepository.contarRegistrosProducto(producto.id()) >= MINIMO_OBSERVACIONES) {
                continue;
            }
            historicoRepository.insertarSerieSiAusente(producto.id(), desdeSintetico(producto));
        }
    }

    public void invalidarCache() {
        synchronized (this) {
            cache = List.of();
            cacheMillis = 0;
        }
    }

    private List<SerieConsumoDiario> desdeSintetico(Producto producto) {
        return datasetSintetico.generarParaProducto(producto).stream()
                .map(punto -> new SerieConsumoDiario(
                        punto.fecha(),
                        0,
                        Math.max(0, (int) Math.round(punto.consumoReal())),
                        Math.max(0, producto.stockActual())
                ))
                .toList();
    }

    public LocalDate inicioVentana() {
        return LocalDate.now().minusDays(VENTANA_DIAS);
    }
}
