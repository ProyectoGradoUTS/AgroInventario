/**
 * Capa de DOMINIO (núcleo hexagonal).
 * <p>
 * Contiene modelos de negocio puros, puertos (contratos) y servicios de dominio.
 * Esta capa NO depende de Spring, JPA, HTTP ni frameworks externos.
 * </p>
 *
 * <ul>
 *   <li>{@code model} — Entidades y value objects del negocio</li>
 *   <li>{@code ports.input} — Puertos de entrada (casos de uso que el dominio expone)</li>
 *   <li>{@code ports.output} — Puertos de salida (persistencia, notificaciones, etc.)</li>
 *   <li>{@code service} — Lógica de dominio que no encaja en un solo caso de uso</li>
 * </ul>
 */
package com.agroinventario.domain;
