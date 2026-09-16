package com.agroinventario.application.service.ia;

import com.agroinventario.domain.model.Producto;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ClasificadorIntencionInventario {

    public enum Intencion {
        SALUDO,
        AYUDA,
        GRACIAS,
        DATOS,
        RESUMEN,
        STOCK_BAJO,
        VENCIMIENTO,
        REPOSICION,
        ALERTAS,
        AGOTAMIENTO,
        COSTOS,
        CATEGORIA,
        PRODUCTO,
        GENERAL
    }

    private ClasificadorIntencionInventario() {
    }

    public static Resultado clasificar(String pregunta, List<Producto> productos) {
        String texto = normalizar(pregunta);
        List<Intencion> intenciones = new ArrayList<>();

        boolean saludo = contiene(texto, "hola", "buenos dias", "buen dia", "buenas tardes",
                "buenas noches", "que tal", "hey", "saludos");
        boolean ayuda = contiene(texto, "ayuda", "ayudame", "que puedes", "que sabes", "como te uso",
                "que haces", "en que me puedes", "como funciona", "que te puedo preguntar");
        boolean gracias = contiene(texto, "gracias", "muchas gracias", "te agradezco", "perfecto gracias");

        if (contiene(texto, "resumen", "estado general", "como va", "como esta", "situacion",
                "panorama", "inventario general", "como andamos", "que hay de nuevo")) {
            intenciones.add(Intencion.RESUMEN);
        }
        if (contiene(texto, "repon", "compra", "pedir", "pedido", "ordenar", "suger",
                "cuanto pedir", "que pido", "que debo pedir", "que conviene pedir")) {
            intenciones.add(Intencion.REPOSICION);
        }
        if (contiene(texto, "agota", "cuanto dura", "cuando se acaba", "dias de stock",
                "proyecc", "demanda", "consumo", "se va a acabar", "quedan dias")) {
            intenciones.add(Intencion.AGOTAMIENTO);
        }
        if (contiene(texto, "stock bajo", "bajo stock", "sin stock", "critico", "agotado",
                "quiebre", "desabaste", "faltante", "no hay suficiente")) {
            intenciones.add(Intencion.STOCK_BAJO);
        }
        if (contiene(texto, "venc", "caduc", "caduca", "por vencer", "fecha de vencimiento",
                "perder", "merma", "vence pronto")) {
            intenciones.add(Intencion.VENCIMIENTO);
        }
        if (contiene(texto, "alerta", "pendiente", "notific", "aviso")) {
            intenciones.add(Intencion.ALERTAS);
        }
        if (contiene(texto, "costo", "costos", "plata", "dinero", "perdida", "ahorro", "innecesar")) {
            intenciones.add(Intencion.COSTOS);
        }
        if (contiene(texto, "cuantos datos", "cuantos registros", "historico", "observaciones",
                "con que datos", "base de datos", "serie historica")) {
            intenciones.add(Intencion.DATOS);
        }

        Producto producto = encontrarProducto(texto, productos);
        String categoria = encontrarCategoria(texto, productos);

        if (producto != null) {
            intenciones.add(Intencion.PRODUCTO);
        }
        if (categoria != null) {
            intenciones.add(Intencion.CATEGORIA);
        }

        if (intenciones.isEmpty()) {
            if (gracias) {
                intenciones.add(Intencion.GRACIAS);
            } else if (ayuda) {
                intenciones.add(Intencion.AYUDA);
            } else if (saludo) {
                intenciones.add(Intencion.SALUDO);
            } else if (texto.isBlank()) {
                intenciones.add(Intencion.AYUDA);
            } else {
                intenciones.add(Intencion.RESUMEN);
            }
        }

        return new Resultado(List.copyOf(intenciones), producto, categoria, texto);
    }

    private static Producto encontrarProducto(String texto, List<Producto> productos) {
        Producto mejor = null;
        int mejorScore = 0;
        for (Producto producto : productos) {
            if (producto.nombre() == null) {
                continue;
            }
            String nombre = normalizar(producto.nombre());
            if (nombre.length() < 3) {
                continue;
            }
            if (texto.contains(nombre)) {
                if (nombre.length() > mejorScore) {
                    mejor = producto;
                    mejorScore = nombre.length();
                }
                continue;
            }
            String[] tokens = nombre.split("\\s+");
            int hits = 0;
            for (String token : tokens) {
                if (token.length() >= 4 && texto.contains(token)) {
                    hits++;
                }
            }
            if (hits > 0 && hits == tokens.length && nombre.length() > mejorScore) {
                mejor = producto;
                mejorScore = nombre.length();
            }
        }
        return mejor;
    }

    private static String encontrarCategoria(String texto, List<Producto> productos) {
        Set<String> categorias = productos.stream()
                .map(Producto::categoriaNombre)
                .filter(c -> c != null && !c.isBlank())
                .collect(java.util.stream.Collectors.toSet());
        for (String categoria : categorias) {
            String n = normalizar(categoria);
            if (n.length() >= 4 && texto.contains(n)) {
                return categoria;
            }
        }
        if (texto.contains("medicina")) {
            return "Medicina";
        }
        if (texto.contains("semilla")) {
            return "Semillas";
        }
        if (texto.contains("herbic")) {
            return "Herbicidas";
        }
        if (texto.contains("insect")) {
            return "Insecticidas";
        }
        if (texto.contains("fungi")) {
            return "Fungicidas";
        }
        if (texto.contains("alimento")) {
            return "Alimentos";
        }
        return null;
    }

    private static boolean contiene(String texto, String... claves) {
        for (String clave : claves) {
            if (texto.contains(normalizar(clave))) {
                return true;
            }
        }
        return false;
    }

    public static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String nfd = Normalizer.normalize(valor.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}+", "").replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    public record Resultado(
            List<Intencion> intenciones,
            Producto producto,
            String categoria,
            String textoNormalizado
    ) {
        public Intencion principal() {
            return intenciones.isEmpty() ? Intencion.GENERAL : intenciones.get(0);
        }

        public boolean esSocial() {
            Intencion principal = principal();
            return principal == Intencion.SALUDO
                    || principal == Intencion.AYUDA
                    || principal == Intencion.GRACIAS;
        }
    }
}
