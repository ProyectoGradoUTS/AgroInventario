package com.agroinventario.application.service.predictive;

import com.agroinventario.domain.model.Producto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetSinteticoPrediccionService {

    public List<HistoricoConsumoSintetico> generarParaProducto(Producto producto) {
        double base = Math.max(0.3, producto.stockMinimo() / 7.0);
        List<HistoricoConsumoSintetico> historico = new ArrayList<>();

        for (int i = 89; i >= 0; i--) {
            LocalDate fecha = LocalDate.now().minusDays(i);
            double variacion = Math.sin((fecha.toEpochDay() / 9.0) + producto.id()) * 0.35;
            double estacional = switch (producto.categoriaNombre() == null ? "" : producto.categoriaNombre().toLowerCase()) {
                case "medicina" -> 0.25;
                case "semillas" -> 0.15;
                case "toxicológica", "toxicologica" -> 0.2;
                case "herbicidas" -> 0.3;
                case "insecticidas" -> 0.2;
                case "fungicidas" -> 0.1;
                case "alimentos" -> 0.4;
                default -> 0.18;
            };

            double consumo = Math.max(0.1, base + variacion + estacional);
            historico.add(new HistoricoConsumoSintetico(fecha, consumo));
        }

        return historico;
    }

    public List<RegistroPrediccionSintetico> generarDatasetCompleto(List<Producto> productos) {
        List<RegistroPrediccionSintetico> registros = new ArrayList<>();

        int totalObjetivo = 150_000;
        int productosCount = Math.max(1, productos.size());
        int registrosPorProducto = Math.max(1, totalObjetivo / productosCount);

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            double base = Math.max(0.3, producto.stockMinimo() / 7.0);

            for (int dia = 0; dia < registrosPorProducto; dia++) {
                LocalDate fecha = LocalDate.now().minusDays(registrosPorProducto - dia);
                double tendencia = Math.sin((fecha.toEpochDay() / 11.0) + producto.id() + i) * 0.4;
                double estacional = switch (producto.categoriaNombre() == null ? "" : producto.categoriaNombre().toLowerCase()) {
                    case "medicina" -> 0.32;
                    case "semillas" -> 0.18;
                    case "toxicológica", "toxicologica" -> 0.24;
                    case "herbicidas" -> 0.28;
                    case "insecticidas" -> 0.21;
                    case "fungicidas" -> 0.12;
                    case "alimentos" -> 0.38;
                    default -> 0.2;
                };

                double consumo = Math.max(0.1, base + tendencia + estacional);
                double stockActual = Math.max(0, producto.stockActual() - (dia % 10) + (consumo * 2.5));
                registros.add(new RegistroPrediccionSintetico(
                        (long) (i * registrosPorProducto + dia + 1),
                        fecha,
                        producto.id(),
                        producto.nombre(),
                        producto.categoriaNombre() == null ? "Sin categoría" : producto.categoriaNombre(),
                        producto.stockActual(),
                        stockActual,
                        producto.stockMinimo(),
                        consumo,
                        producto.fechaVencimiento(),
                        leadTime(producto),
                        producto.estaActivo()
                ));
            }
        }

        return registros;
    }

    public long totalRegistrosEstimados() {
        return 150_000L;
    }

    public record HistoricoConsumoSintetico(LocalDate fecha, double consumoReal) {
    }

    public record RegistroPrediccionSintetico(
            Long idRegistro,
            LocalDate fecha,
            Long productoId,
            String nombreProducto,
            String categoria,
            int stockInicial,
            double stockActual,
            int stockMinimo,
            double consumoReal,
            java.time.LocalDate fechaVencimiento,
            int leadTimeDias,
            boolean activo
    ) {
    }

    private int leadTime(Producto producto) {
        String categoria = producto.categoriaNombre() == null ? "" : producto.categoriaNombre().trim().toLowerCase();
        return switch (categoria) {
            case "medicina" -> 8;
            case "semillas" -> 15;
            case "toxicológica", "toxicologica" -> 12;
            case "herbicidas" -> 8;
            case "insecticidas" -> 10;
            case "fungicidas" -> 8;
            case "alimentos" -> 8;
            default -> 10;
        };
    }
}
