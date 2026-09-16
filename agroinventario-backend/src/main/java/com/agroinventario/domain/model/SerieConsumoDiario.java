package com.agroinventario.domain.model;

import java.time.LocalDate;

public record SerieConsumoDiario(
        LocalDate fecha,
        int entradas,
        int salidas,
        int stockFinal
) {
}
