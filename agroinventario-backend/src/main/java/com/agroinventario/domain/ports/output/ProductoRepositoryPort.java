package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    Producto save(Producto producto);

    Optional<Producto> findById(Long id);

    List<Producto> findAll();

    List<Producto> findByEstado(EstadoGeneral estado);

    PageResult<Producto> findPaginado(
            EstadoGeneral estado,
            Long categoriaId,
            String nombre,
            int page,
            int size
    );

    long countByCategoriaId(Long categoriaId);

    void deleteById(Long id);

    boolean existsById(Long id);
}
