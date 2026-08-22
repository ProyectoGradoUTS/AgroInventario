package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.producto.CrearProductoUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class CrearProductoUseCaseImpl implements CrearProductoUseCase {

        private final ProductoRepositoryPort productoRepository;
        private final CategoriaRepositoryPort categoriaRepository;
        private final ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase;
        private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

        public CrearProductoUseCaseImpl(
                        ProductoRepositoryPort productoRepository,
                        CategoriaRepositoryPort categoriaRepository,
                        ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase,
                        RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
                this.productoRepository = productoRepository;
                this.categoriaRepository = categoriaRepository;
                this.procesarAlertasProductoUseCase = procesarAlertasProductoUseCase;
                this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        }

        @Override
        public Producto ejecutar(
                        String nombre,
                        String descripcion,
                        BigDecimal precio,
                        int stockActual,
                        int stockMinimo,
                        LocalDate fechaVencimiento,
                        Long categoriaId,
                        EstadoGeneral estado) {

                var categoria = categoriaRepository.findById(categoriaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Categoría", categoriaId));

                if (nombre != null && productoRepository.findByNombre(nombre).isPresent()) {
                        throw new BusinessRuleException(
                                        "Ya existe un producto con el nombre '%s'".formatted(nombre));
                }

                EstadoGeneral estadoFinal = estado != null ? estado : EstadoGeneral.ACTIVO;

                Producto producto = new Producto(
                                null,
                                nombre,
                                descripcion,
                                precio,
                                stockActual,
                                stockMinimo,
                                fechaVencimiento,
                                categoria.id(),
                                categoria.nombre(),
                                estadoFinal,
                                LocalDateTime.now());

                Producto guardado = productoRepository.save(producto);
                procesarAlertasProductoUseCase.ejecutar(guardado);
                registrarAuditoriaUseCase.ejecutar(
                                EntidadAuditoria.PRODUCTO,
                                guardado.id(),
                                TipoAccionAuditoria.CREAR,
                                "Producto '%s' creado con stock inicial %d".formatted(guardado.nombre(),
                                                guardado.stockActual()));
                return guardado;
        }
}
