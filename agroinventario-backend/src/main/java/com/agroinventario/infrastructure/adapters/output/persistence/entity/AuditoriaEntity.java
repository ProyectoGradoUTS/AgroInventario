package com.agroinventario.infrastructure.adapters.output.persistence.entity;

import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntidadAuditoria entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAccionAuditoria accion;

    @Column(length = 1000)
    private String detalle;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_email", length = 150)
    private String usuarioEmail;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento = LocalDateTime.now();
}
