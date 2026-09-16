package com.agroinventario.domain.service;

import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.model.SerieConsumoDiario;

import java.time.LocalDate;
import java.util.List;

/**
 * Motor estadístico de demanda: media móvil, tendencia lineal y stock de seguridad.
 * No depende de Spring ni de un LLM; opera sobre la serie histórica del producto.
 */
public class MotorPredictivoInventario {

    private static final double Z_SERVICIO_90 = 1.28;

    public ProyeccionInventario proyectar(
            Producto producto,
            List<SerieConsumoDiario> serie,
            String fuenteDatos) {

        int leadTime = PoliticaLeadTime.diasPara(producto);
        EstadisticaConsumo estadistica = analizar(serie, producto);

        double consumo = estadistica.consumoProyectado();
        long diasHastaAgotamiento = producto.stockActual() <= 0
                ? 0
                : Math.max(0, Math.round(producto.stockActual() / Math.max(consumo, 0.2)));
        LocalDate fechaAgotamiento = LocalDate.now().plusDays(diasHastaAgotamiento);

        double demanda7d = consumo * 7;
        double demanda30d = consumo * 30;
        double stockSeguridad = consumo * leadTime + Z_SERVICIO_90 * estadistica.desviacion() * Math.sqrt(leadTime);
        int porMinimo = Math.max(0, producto.stockMinimo() - producto.stockActual());
        int porLeadTime = (int) Math.ceil(Math.max(0, stockSeguridad - producto.stockActual()));
        int cantidadSugerida = Math.max(porMinimo, porLeadTime);

        long diasVencimiento = producto.tieneFechaVencimiento()
                ? producto.diasHastaVencimiento()
                : -1;

        String estrategia = seleccionarEstrategia(producto, consumo, leadTime, diasHastaAgotamiento, diasVencimiento, cantidadSugerida);
        String riesgo = seleccionarRiesgo(producto, diasHastaAgotamiento, consumo, leadTime, diasVencimiento);
        String mensaje = construirMensaje(producto, diasHastaAgotamiento, cantidadSugerida, estrategia, riesgo, diasVencimiento);

        return new ProyeccionInventario(
                producto.id(),
                producto.nombre(),
                producto.categoriaNombre() == null ? "Sin categoría" : producto.categoriaNombre(),
                producto.stockActual(),
                producto.stockMinimo(),
                redondear(estadistica.promedio()),
                redondear(estadistica.pendiente()),
                redondear(estadistica.desviacion()),
                leadTime,
                diasHastaAgotamiento,
                fechaAgotamiento,
                redondear(demanda7d),
                redondear(demanda30d),
                cantidadSugerida,
                estrategia,
                mapearEstrategiaPersistida(estrategia),
                riesgo,
                mensaje,
                redondear(estadistica.confianza()),
                fuenteDatos,
                estadistica.observaciones(),
                diasVencimiento
        );
    }

    private EstadisticaConsumo analizar(List<SerieConsumoDiario> serie, Producto producto) {
        if (serie == null || serie.isEmpty()) {
            double fallback = Math.max(0.3, producto.stockMinimo() / 7.0);
            return new EstadisticaConsumo(0, fallback, 0, fallback * 0.4, fallback, 0.4);
        }

        double[] valores = serie.stream().mapToDouble(SerieConsumoDiario::salidas).toArray();
        int n = valores.length;
        double suma = 0;
        for (double valor : valores) {
            suma += valor;
        }
        double promedio = suma / n;

        double sumaCuadrados = 0;
        for (double valor : valores) {
            double d = valor - promedio;
            sumaCuadrados += d * d;
        }
        double desviacion = Math.sqrt(sumaCuadrados / n);

        double pendiente = pendienteLineal(valores);
        double consumoProyectado = Math.max(0.2, promedio + pendiente * 3);

        double cv = promedio > 0 ? Math.min(desviacion / promedio, 1.5) : 1.0;
        double cobertura = Math.min(1.0, n / 90.0);
        double confianza = clamp(0.35, 0.95, 0.45 * cobertura + 0.55 * (1 - cv / 1.5));

        return new EstadisticaConsumo(n, promedio, pendiente, desviacion, consumoProyectado, confianza);
    }

    private double pendienteLineal(double[] y) {
        int n = y.length;
        if (n < 2) {
            return 0;
        }
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += y[i];
            sumXY += i * y[i];
            sumX2 += (double) i * i;
        }
        double denominador = n * sumX2 - sumX * sumX;
        if (Math.abs(denominador) < 1e-9) {
            return 0;
        }
        return (n * sumXY - sumX * sumY) / denominador;
    }

    private String seleccionarEstrategia(
            Producto producto,
            double consumoPromedio,
            int leadTimeDias,
            long diasHastaAgotamiento,
            long diasHastaVencimiento,
            int cantidadSugerida) {

        if (producto.stockActual() <= 0 || diasHastaAgotamiento <= 3) {
            return "STOCK_BAJO_DEMANDA";
        }
        if (producto.stockActual() <= producto.stockMinimo()) {
            return "REPOSICION_MINIMO";
        }
        if (diasHastaVencimiento >= 0 && diasHastaVencimiento <= 30 && producto.stockActual() > producto.stockMinimo()) {
            return "ROTACION_VENCIMIENTO";
        }
        if (diasHastaAgotamiento <= leadTimeDias || cantidadSugerida > 0) {
            return "TOP_OFF_LEAD_TIME";
        }
        return "MONITOREO";
    }

    private String mapearEstrategiaPersistida(String estrategia) {
        return switch (estrategia) {
            case "STOCK_BAJO_DEMANDA" -> "URGENTE";
            case "REPOSICION_MINIMO" -> "STOCK_MINIMO";
            case "ROTACION_VENCIMIENTO" -> "NIVELACION";
            case "TOP_OFF_LEAD_TIME" -> "DEMANDA_LEAD_TIME";
            default -> "NIVELACION";
        };
    }

    private String seleccionarRiesgo(
            Producto producto,
            long diasHastaAgotamiento,
            double consumoPromedio,
            int leadTimeDias,
            long diasHastaVencimiento) {

        if (producto.stockActual() <= 0 || diasHastaAgotamiento <= 3) {
            return "CRITICO";
        }
        if (producto.stockActual() <= producto.stockMinimo() || diasHastaAgotamiento <= 7) {
            return "ALTO";
        }
        if (diasHastaVencimiento >= 0 && diasHastaVencimiento <= 30) {
            return "ALTO";
        }
        if (diasHastaAgotamiento <= leadTimeDias || producto.stockActual() <= consumoPromedio * leadTimeDias + producto.stockMinimo()) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String construirMensaje(
            Producto producto,
            long diasHastaAgotamiento,
            int cantidadSugerida,
            String estrategia,
            String riesgo,
            long diasHastaVencimiento) {

        if ("ROTACION_VENCIMIENTO".equals(estrategia)) {
            return "Priorizar rotación: vence en %d día(s). Pedir de más elevaría pérdidas por caducidad."
                    .formatted(diasHastaVencimiento);
        }
        if ("STOCK_BAJO_DEMANDA".equals(estrategia) || "CRITICO".equals(riesgo)) {
            return "Riesgo de desabastecimiento en %d día(s). Reponer %d unidad(es) para cubrir demanda y lead time."
                    .formatted(diasHastaAgotamiento, cantidadSugerida);
        }
        if ("REPOSICION_MINIMO".equals(estrategia)) {
            return "Stock bajo el mínimo. Reponer %d unidad(es) evita ruptura operativa.".formatted(cantidadSugerida);
        }
        if (cantidadSugerida > 0) {
            return "Con el consumo proyectado se agota en %d día(s). Pedido sugerido: %d unidad(es)."
                    .formatted(diasHastaAgotamiento, cantidadSugerida);
        }
        return "Tendencia estable para %s. Monitorear consumo; no hay pedido urgente.".formatted(producto.nombre());
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private double clamp(double min, double max, double valor) {
        return Math.max(min, Math.min(max, valor));
    }

    private record EstadisticaConsumo(
            long observaciones,
            double promedio,
            double pendiente,
            double desviacion,
            double consumoProyectado,
            double confianza
    ) {
    }
}
