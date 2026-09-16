package com.agroinventario.domain.service;

import com.agroinventario.domain.model.Producto;

public final class PoliticaLeadTime {

    private PoliticaLeadTime() {
    }

    public static int diasPara(Producto producto) {
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
