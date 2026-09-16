package com.agroinventario.application.service.ia;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.model.TipoAlerta;

import java.util.List;
import java.util.stream.Collectors;

public final class InventarioContextoIa {

    private InventarioContextoIa() {
    }

    public static String construir(
            List<Producto> productos,
            List<Alerta> alertas,
            List<ProyeccionInventario> proyecciones) {

        List<Producto> activos = productos.stream().filter(Producto::estaActivo).toList();
        long stockBajo = activos.stream().filter(Producto::stockBajo).count();
        long vencen = activos.stream()
                .filter(p -> p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)
                .count();
        long pendientes = alertas.stream().filter(a -> a.estado() == EstadoAlerta.PENDIENTE).count();
        long agotamiento7 = proyecciones.stream().filter(p -> p.diasHastaAgotamiento() <= 7).count();

        String criticos = proyecciones.stream()
                .filter(p -> "CRITICO".equals(p.riesgo()) || "ALTO".equals(p.riesgo()))
                .limit(8)
                .map(p -> "- %s | stock %d | mínimo %d | agota en %d días | pedir %d | %s".formatted(
                        p.nombre(), p.stockActual(), p.stockMinimo(), p.diasHastaAgotamiento(),
                        p.cantidadSugerida(), p.mensaje()))
                .collect(Collectors.joining("\n"));

        String vencimientos = activos.stream()
                .filter(p -> p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)
                .sorted((a, b) -> Long.compare(a.diasHastaVencimiento(), b.diasHastaVencimiento()))
                .limit(8)
                .map(p -> "- %s vence en %d días (stock %d, precio %s)".formatted(
                        p.nombre(), p.diasHastaVencimiento(), p.stockActual(), p.precio()))
                .collect(Collectors.joining("\n"));

        String alertasTxt = alertas.stream()
                .filter(a -> a.estado() == EstadoAlerta.PENDIENTE)
                .limit(8)
                .map(a -> "- %s [%s] %s".formatted(a.productoNombre(), a.tipoAlerta(), a.mensaje()))
                .collect(Collectors.joining("\n"));

        String catalogo = activos.stream()
                .limit(40)
                .map(p -> "%s (cat %s, stock %d, mín %d)".formatted(
                        p.nombre(),
                        p.categoriaNombre() == null ? "N/A" : p.categoriaNombre(),
                        p.stockActual(),
                        p.stockMinimo()))
                .collect(Collectors.joining("; "));

        return """
                Estado en tiempo real del inventario agropecuario:
                Productos activos: %d. Stock bajo: %d. Vencen en 30 días: %d.
                Alertas pendientes: %d. Proyección de agotamiento ≤7 días: %d.

                Productos con mayor riesgo / reposición:
                %s

                Vencimientos próximos (pérdida potencial):
                %s

                Alertas pendientes:
                %s

                Catálogo activo: %s
                """.formatted(
                activos.size(),
                stockBajo,
                vencen,
                pendientes,
                agotamiento7,
                criticos.isBlank() ? "- Ninguno crítico" : criticos,
                vencimientos.isBlank() ? "- Sin vencimientos en 30 días" : vencimientos,
                alertasTxt.isBlank() ? "- Sin alertas pendientes" : alertasTxt,
                catalogo
        );
    }

    public static long contarTipo(List<Alerta> alertas, TipoAlerta tipo) {
        return alertas.stream().filter(a -> a.tipoAlerta() == tipo && a.estado() == EstadoAlerta.PENDIENTE).count();
    }

    public static List<Producto> activos(List<Producto> productos) {
        return productos.stream().filter(p -> p.estado() == EstadoGeneral.ACTIVO).toList();
    }
}
