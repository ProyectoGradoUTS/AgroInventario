package com.agroinventario.application.service.ia;

import com.agroinventario.application.service.ia.ClasificadorIntencionInventario.Intencion;
import com.agroinventario.application.service.ia.ClasificadorIntencionInventario.Resultado;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GeneradorConversacionInventario {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d 'de' MMMM", ES);

    private GeneradorConversacionInventario() {
    }

    public static String responder(
            Resultado clasificacion,
            List<Producto> activos,
            List<Alerta> alertas,
            List<ProyeccionInventario> proyecciones,
            long registrosHistoricos) {

        return switch (clasificacion.principal()) {
            case SALUDO -> saludo(activos, proyecciones);
            case AYUDA -> ayuda();
            case GRACIAS -> "Con gusto. Cuando quiera revisamos otra línea del inventario.";
            case DATOS -> respuestaDatos(registrosHistoricos, activos, proyecciones);
            case PRODUCTO -> clasificacion.producto() == null
                    ? resumenGeneral(activos, alertas, proyecciones, registrosHistoricos)
                    : respuestaProducto(clasificacion.producto(), proyecciones);
            case REPOSICION, COSTOS -> respuestaReposicion(
                    proyecciones, clasificacion.intenciones().contains(Intencion.COSTOS));
            case AGOTAMIENTO -> respuestaAgotamiento(proyecciones);
            case STOCK_BAJO -> respuestaStockBajo(activos, proyecciones);
            case VENCIMIENTO -> respuestaVencimiento(activos);
            case ALERTAS -> respuestaAlertas(alertas);
            case CATEGORIA -> clasificacion.categoria() == null
                    ? resumenGeneral(activos, alertas, proyecciones, registrosHistoricos)
                    : respuestaCategoria(activos, proyecciones, clasificacion.categoria());
            case RESUMEN, GENERAL -> resumenGeneral(activos, alertas, proyecciones, registrosHistoricos);
        };
    }

    private static String saludo(List<Producto> activos, List<ProyeccionInventario> proyecciones) {
        long stockBajo = activos.stream().filter(Producto::stockBajo).count();
        long agota7 = proyecciones.stream().filter(p -> p.diasHastaAgotamiento() <= 7).count();
        return """
                Hola. Estoy al tanto del inventario: hay %s, %s con stock bajo y %s que podrían agotarse esta semana.
                Pregúnteme cómo va todo, qué conviene pedir, qué vence pronto o el nombre de un producto.
                """.formatted(
                cuantos(activos.size(), "producto activo", "productos activos"),
                entero(stockBajo),
                cuantos(agota7, "producto", "productos")
        ).trim();
    }

    private static String ayuda() {
        return """
                Puede hablarme con naturalidad. Por ejemplo:
                • ¿Cómo está el inventario ahora?
                • ¿Qué se va a agotar esta semana?
                • ¿Qué debo pedir para no desabastecerme?
                • ¿Qué vence pronto?
                • O el nombre de un producto, y le cuento el stock y cuántos días le quedan.
                """.trim();
    }

    private static String respuestaDatos(
            long registros,
            List<Producto> activos,
            List<ProyeccionInventario> proyecciones) {
        long pedir = proyecciones.stream().filter(p -> p.cantidadSugerida() > 0).count();
        return """
                Estoy trabajando con %s de consumo diario sobre %s.
                Con esa historia calculo cuándo se agota cada línea y qué conviene pedir. Ahora mismo hay %s con reposición sugerida.
                """.formatted(
                cuantos(registros, "registro", "registros"),
                cuantos(activos.size(), "producto activo", "productos activos"),
                cuantos(pedir, "producto", "productos")
        ).trim();
    }

    private static String resumenGeneral(
            List<Producto> activos,
            List<Alerta> alertas,
            List<ProyeccionInventario> proyecciones,
            long registrosHistoricos) {

        long stockBajo = activos.stream().filter(Producto::stockBajo).count();
        long vencen = activos.stream().filter(p -> p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30).count();
        long pendientes = alertas.stream().filter(a -> a.estado() == EstadoAlerta.PENDIENTE).count();
        long agota7 = proyecciones.stream().filter(p -> p.diasHastaAgotamiento() <= 7).count();
        long pedir = proyecciones.stream().filter(p -> p.cantidadSugerida() > 0).count();

        StringBuilder sb = new StringBuilder();
        sb.append("El inventario tiene ")
                .append(cuantos(activos.size(), "producto activo", "productos activos"))
                .append(", con ")
                .append(cuantos(registrosHistoricos, "registro de consumo", "registros de consumo"))
                .append(". ");

        if (stockBajo == 0 && agota7 == 0 && vencen == 0) {
            sb.append("En este momento no veo quiebres urgentes ni vencimientos cercanos. ");
        } else {
            sb.append("Hay ")
                    .append(cuantos(stockBajo, "producto", "productos"))
                    .append(" por debajo del mínimo, ")
                    .append(cuantos(agota7, "línea", "líneas"))
                    .append(" que se agotarían en 7 días o menos, y ")
                    .append(cuantos(vencen, "producto", "productos"))
                    .append(" que vencen en el próximo mes. ");
        }

        if (pedir > 0) {
            String detalle = listar(proyecciones.stream()
                    .filter(p -> p.cantidadSugerida() > 0)
                    .sorted(Comparator.comparingInt(ProyeccionInventario::cantidadSugerida).reversed())
                    .limit(3)
                    .map(p -> p.nombre() + " (" + entero(p.cantidadSugerida()) + " ud)")
                    .toList());
            sb.append("Conviene pedir primero: ").append(detalle).append(". ");
        } else {
            sb.append("No hay pedidos urgentes. ");
        }

        if (pendientes > 0) {
            sb.append("También hay ")
                    .append(cuantos(pendientes, "alerta pendiente", "alertas pendientes"))
                    .append(" para revisar.");
        } else {
            sb.append("No hay alertas pendientes.");
        }
        return sb.toString().trim();
    }

    private static String respuestaProducto(Producto producto, List<ProyeccionInventario> proyecciones) {
        ProyeccionInventario p = proyecciones.stream()
                .filter(x -> Objects.equals(x.productoId(), producto.id()))
                .findFirst()
                .orElse(null);
        if (p == null) {
            return "%s tiene hoy %s (el mínimo es %s)."
                    .formatted(producto.nombre(), unidades(producto.stockActual()), entero(producto.stockMinimo()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(p.nombre())
                .append(" tiene hoy ")
                .append(unidades(p.stockActual()))
                .append(", y el mínimo que manejamos es ")
                .append(entero(p.stockMinimo()))
                .append(". ");

        if (p.consumoPromedio() > 0) {
            sb.append("Se está usando alrededor de ")
                    .append(decimal(p.consumoPromedio()))
                    .append(" unidades al día. ");
        }

        if (p.stockActual() <= 0) {
            sb.append("En este momento está sin stock. ");
        } else if (p.diasHastaAgotamiento() <= 0) {
            sb.append("Podría agotarse hoy mismo. ");
        } else {
            sb.append("Con ese ritmo se agotaría en ")
                    .append(cuantos(p.diasHastaAgotamiento(), "día", "días"));
            if (p.fechaProyectadaAgotamiento() != null) {
                sb.append(", más o menos el ").append(p.fechaProyectadaAgotamiento().format(FECHA));
            }
            sb.append(". ");
        }

        if (producto.tieneFechaVencimiento() && producto.diasHastaVencimiento() <= 60) {
            long dias = producto.diasHastaVencimiento();
            if (dias < 0) {
                sb.append("Ojo: ya venció. ");
            } else {
                sb.append("Vence en ")
                        .append(cuantos(dias, "día", "días"))
                        .append(". ");
            }
        }

        if (p.cantidadSugerida() > 0) {
            sb.append("Le conviene pedir ")
                    .append(unidades(p.cantidadSugerida()))
                    .append(" para no quedarse corto");
            if ("ROTACION_VENCIMIENTO".equals(p.estrategia())) {
                sb.append(", sin comprar de más porque está cerca del vencimiento");
            }
            sb.append(".");
        } else {
            sb.append("Por ahora no hace falta reponer.");
        }
        return sb.toString();
    }

    private static String respuestaReposicion(List<ProyeccionInventario> proyecciones, boolean costos) {
        List<ProyeccionInventario> pedidos = proyecciones.stream()
                .filter(p -> p.cantidadSugerida() > 0)
                .sorted(Comparator.comparingInt(ProyeccionInventario::cantidadSugerida).reversed())
                .toList();
        if (pedidos.isEmpty()) {
            return "Hoy no veo compras urgentes. El stock cubre el mínimo y el tiempo de reposición; pedir de más solo inmovilizaría dinero.";
        }
        String detalle = pedidos.stream()
                .limit(5)
                .map(p -> "%s: pedir %s; se agotaría en %s".formatted(
                        p.nombre(),
                        unidades(p.cantidadSugerida()),
                        cuantos(p.diasHastaAgotamiento(), "día", "días")))
                .collect(Collectors.joining("; "));
        String extra = costos
                ? " Eso cubre lo que se va a acabar y evita comprar de más en lo que está por vencer."
                : " Con esas cantidades se baja el riesgo de quedarse sin producto.";
        return "Le conviene reponer %s. Lo más importante: %s.%s"
                .formatted(cuantos(pedidos.size(), "producto", "productos"), detalle, extra);
    }

    private static String respuestaAgotamiento(List<ProyeccionInventario> proyecciones) {
        List<ProyeccionInventario> cerca = proyecciones.stream()
                .sorted(Comparator.comparingLong(ProyeccionInventario::diasHastaAgotamiento))
                .limit(5)
                .toList();
        if (cerca.isEmpty()) {
            return "No tengo productos activos para proyectar agotamiento.";
        }
        String detalle = cerca.stream()
                .map(p -> {
                    if (p.diasHastaAgotamiento() <= 0) {
                        return p.nombre() + " ya está en cero";
                    }
                    String fecha = p.fechaProyectadaAgotamiento() == null
                            ? ""
                            : " (el " + p.fechaProyectadaAgotamiento().format(FECHA) + ")";
                    return "%s se agotaría en %s%s".formatted(
                            p.nombre(),
                            cuantos(p.diasHastaAgotamiento(), "día", "días"),
                            fecha);
                })
                .collect(Collectors.joining("; "));
        return "Según el consumo reciente, esto es lo que se acaba primero: %s.".formatted(detalle);
    }

    private static String respuestaStockBajo(List<Producto> activos, List<ProyeccionInventario> proyecciones) {
        List<Producto> stockBajo = activos.stream()
                .filter(Producto::stockBajo)
                .sorted(Comparator.comparingInt(Producto::stockActual))
                .toList();
        if (stockBajo.isEmpty()) {
            return "Ningún producto está por debajo del mínimo. El cuidado ahora está en lo que se puede agotar en los próximos días, no en el stock de hoy.";
        }
        String detalle = stockBajo.stream().limit(5)
                .map(p -> {
                    int sugerido = proyecciones.stream()
                            .filter(x -> Objects.equals(x.productoId(), p.id()))
                            .mapToInt(ProyeccionInventario::cantidadSugerida)
                            .findFirst()
                            .orElse(Math.max(p.stockMinimo() - p.stockActual(), 1));
                    return "%s tiene %s (mínimo %s; pedir %s)".formatted(
                            p.nombre(), unidades(p.stockActual()), entero(p.stockMinimo()), unidades(sugerido));
                })
                .collect(Collectors.joining("; "));
        return "Hay %s por debajo del mínimo. Los más justos: %s."
                .formatted(cuantos(stockBajo.size(), "producto", "productos"), detalle);
    }

    private static String respuestaVencimiento(List<Producto> activos) {
        List<Producto> vencen = activos.stream()
                .filter(p -> p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)
                .sorted(Comparator.comparingLong(Producto::diasHastaVencimiento))
                .toList();
        if (vencen.isEmpty()) {
            return "En los próximos 30 días no hay vencimientos. No hace falta comprar de más en las líneas que caducan.";
        }
        String detalle = vencen.stream().limit(5)
                .map(p -> {
                    long dias = p.diasHastaVencimiento();
                    if (dias < 0) {
                        return "%s ya venció y aún tiene %s".formatted(p.nombre(), unidades(p.stockActual()));
                    }
                    return "%s vence en %s (queda %s)".formatted(
                            p.nombre(), cuantos(dias, "día", "días"), unidades(p.stockActual()));
                })
                .collect(Collectors.joining("; "));
        return "Hay %s por vencer. Conviene rotar o vender primero: %s. Reponer solo lo que no esté a punto de caducar."
                .formatted(cuantos(vencen.size(), "producto", "productos"), detalle);
    }

    private static String respuestaAlertas(List<Alerta> alertas) {
        List<Alerta> pendientes = alertas.stream().filter(a -> a.estado() == EstadoAlerta.PENDIENTE).toList();
        if (pendientes.isEmpty()) {
            return "No hay alertas pendientes de stock ni de vencimiento. El inventario está quieto en ese frente.";
        }
        String detalle = pendientes.stream().limit(5)
                .map(a -> a.productoNombre() + " (" + nombreAlerta(a.tipoAlerta() == null ? "" : a.tipoAlerta().name()) + ")")
                .collect(Collectors.joining("; "));
        return "Hay %s pendientes: %s."
                .formatted(cuantos(pendientes.size(), "alerta", "alertas"), detalle);
    }

    private static String respuestaCategoria(
            List<Producto> activos,
            List<ProyeccionInventario> proyecciones,
            String categoria) {
        List<Producto> filtrados = activos.stream()
                .filter(p -> categoria.equalsIgnoreCase(p.categoriaNombre()))
                .toList();
        long bajo = filtrados.stream().filter(Producto::stockBajo).count();
        List<ProyeccionInventario> deCategoria = proyecciones.stream()
                .filter(p -> categoria.equalsIgnoreCase(p.categoria()))
                .toList();
        long pedir = deCategoria.stream().filter(p -> p.cantidadSugerida() > 0).count();
        StringBuilder sb = new StringBuilder();
        sb.append("En ").append(categoria.toLowerCase(ES)).append(" hay ")
                .append(cuantos(filtrados.size(), "producto activo", "productos activos"))
                .append(". ");
        if (filtrados.isEmpty()) {
            sb.append("No encuentro líneas activas con ese nombre.");
            return sb.toString();
        }
        if (bajo == 0) {
            sb.append("Ninguno está bajo el mínimo");
        } else {
            sb.append(cuantos(bajo, "está", "están")).append(" bajo el mínimo");
        }
        sb.append(" y ");
        if (pedir == 0) {
            sb.append("no hay pedidos sugeridos.");
        } else {
            sb.append(cuantos(pedir, "tiene", "tienen")).append(" pedido sugerido.");
        }
        String nombres = deCategoria.stream()
                .filter(p -> p.cantidadSugerida() > 0 || p.diasHastaAgotamiento() <= 7)
                .limit(3)
                .map(ProyeccionInventario::nombre)
                .collect(Collectors.joining(", "));
        if (!nombres.isBlank()) {
            sb.append(" Vale la pena mirar: ").append(nombres).append(".");
        }
        return sb.toString();
    }

    private static String nombreAlerta(String tipo) {
        return switch (tipo) {
            case "STOCK_BAJO" -> "stock bajo";
            case "VENCIMIENTO" -> "vencimiento";
            default -> tipo.isBlank() ? "alerta" : tipo.toLowerCase(ES).replace('_', ' ');
        };
    }

    private static String listar(List<String> partes) {
        if (partes.isEmpty()) {
            return "";
        }
        if (partes.size() == 1) {
            return partes.get(0);
        }
        if (partes.size() == 2) {
            return partes.get(0) + " y " + partes.get(1);
        }
        return String.join(", ", partes.subList(0, partes.size() - 1)) + " y " + partes.get(partes.size() - 1);
    }

    private static String cuantos(long n, String singular, String plural) {
        return entero(n) + " " + (n == 1 ? singular : plural);
    }

    private static String unidades(long n) {
        return cuantos(n, "unidad", "unidades");
    }

    private static String entero(long n) {
        return NumberFormat.getIntegerInstance(ES).format(n);
    }

    private static String decimal(double n) {
        NumberFormat nf = NumberFormat.getNumberInstance(ES);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(n == Math.rint(n) ? 0 : 1);
        return nf.format(n);
    }
}
