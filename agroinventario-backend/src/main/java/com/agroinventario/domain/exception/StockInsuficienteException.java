package com.agroinventario.domain.exception;

public class StockInsuficienteException extends BusinessRuleException {

    public StockInsuficienteException(String producto, int stockActual, int cantidadSolicitada) {
        super("Stock insuficiente para '%s'. Disponible: %d, solicitado: %d"
                .formatted(producto, stockActual, cantidadSolicitada));
    }
}
