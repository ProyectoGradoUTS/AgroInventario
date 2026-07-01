package com.agroinventario.domain.model;

import java.util.List;

/**
 * Resultado paginado agnóstico de framework.
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
