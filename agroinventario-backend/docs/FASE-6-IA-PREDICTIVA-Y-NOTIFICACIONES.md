# Fase 6 — IA predictiva, reposición inteligente y notificaciones al administrador

## Objetivo

Definir la siguiente fase del sistema para convertir Agro Inventario de un backend de gestión operativa en un sistema inteligente orientado a la predicción de demanda, la reposición automatizada y la notificación proactiva al administrador del sistema.

Este documento marca la base para la implementación de:

- modelos predictivos de consumo y agotamiento
- estrategias de reposición por demanda, stock mínimo y lead time
- alertas automáticas por vencimiento, stock crítico y sin movimiento
- correo electrónico al administrador del sistema
- panel administrativo con comportamiento por producto
- asistente conversacional con respuestas basadas en resultados reales y no solo en texto libre

---

## 1. Tipo de IA recomendada

La mejor opción para este proyecto no es un LLM como motor principal.

Se recomienda una arquitectura híbrida:

1. IA predictiva para demanda, agotamiento y riesgo
2. Motor de reglas para reposición
3. LLM opcional para explicar resultados en lenguaje natural

### Justificación

- La decisión de reposición no puede depender solo de texto libre.
- Debe calcularse sobre datos cuantitativos: stock, consumo, lead time, rotación y riesgo.
- El problema central es operativo y analítico, no solo conversacional.
- El sistema requiere decisiones basadas en historial, proyección y reglas definidas.

### Recomendación técnica

- Modelo principal: series de tiempo + reglas de negocio
- Modelo complementario: clasificación de riesgo por producto
- Capa de explicación: LLM para resumir la recomendación

---

## 2. Alcance funcional de la fase

### 2.1 Objetivos funcionales

- Analizar patrones históricos de consumo simulados para cada producto y categoría.
- Proyectar la fecha estimada de agotamiento.
- Detectar productos con riesgo de stock bajo, vencimiento próximo o sin movimiento prolongado.
- Generar recomendaciones automáticas de compra o reposición.
- Enviar alertas por correo al administrador del sistema.
- Mostrar un cuadro de comportamiento por producto en el dashboard administrativo.
- Brindar un asistente que explique el estado del inventario en lenguaje natural.

### 2.2 No objetivo de esta fase

No es objetivo de esta fase:

- reemplazar completamente al administrador con IA
- construir un chatbot sin lógica operativa
- decidir compras reales sin reglas de negocio
- depender únicamente de un LLM para decisiones de inventario

---

## 3. Fuentes de datos

### 3.1 Datos reales

Por el momento no existen datos reales de negocio. La solución propuesta es generar un dataset sintético realista para alimentar la IA.

### 3.2 Dataset sintético objetivo

Se propone crear 150.000 registros simulados con productos del sector agropecuario de Bucaramanga.

Cada registro debe representar una observación diaria o semanal del inventario por producto.

### 3.3 Variables mínimas del dataset

Cada registro debe incluir:

- id_registro
- fecha
- producto_id
- nombre_producto
- categoria
- perecedero (true/false)
- stock_inicial
- stock_actual
- stock_minimo
- consumo_real
- entradas
- salidas
- ventas
- precio_unitario
- fecha_vencimiento
- lead_time_dias
- rotacion_dias
- movimiento_ultimo_dia
- temporada
- zona
- estado_producto

---

## 4. Productos y categorías a considerar

Los productos actualmente planteados para la fase son:

### 4.1 Medicina veterinaria

- Oxitetraciclina
- Kaput Master
- Dexapen
- Tripen L.A
- Hierro dextran
- Streptoland
- Bonavit

Características:

- stock mínimo sugerido: 10 unidades
- lead time: 8 días
- consumo promedio: 1 unidad/día
- vencimiento típico: 1 año
- perecedero: sí

### 4.2 Semillas / horticultura

- Zanahoria
- Tomate
- Lechuga
- Cilantro
- Pepino
- Cebolla
- Arveja

Características:

- stock mínimo sugerido: 15 unidades
- lead time: 15 días
- consumo promedio: 1 unidad/día
- vencimiento: no aplica o largo plazo según almacenamiento
- perecedero: no

### 4.3 Toxicológicos

- Curagan NL 375ml
- Raid cucarachas 5
- Negasunt 3

Características:

- stock mínimo sugerido: 5-8 unidades
- lead time: 10-15 días
- consumo promedio: 1 unidad/día
- vencimiento típico: 2 años
- perecedero: sí

### 4.4 Herbicidas

- Panzer litro
- Roundup litro
- Destierro litro
- Gramafin litro

Características:

- stock mínimo sugerido: 5-15 unidades
- lead time: 8 días
- consumo promedio: 2 unidades/día
- vencimiento típico: 4 años
- perecedero: sí

### 4.5 Insecticidas

- Invetrina 250ml
- Alfa point ML
- Candonga 250ml
- Fulminator 500ml
- Lannate sobre

Características:

- stock mínimo sugerido: 2-6 unidades
- lead time: 8-15 días
- consumo promedio: 0.2 a 1 unidad/día
- vencimiento típico: 3 años
- perecedero: sí

### 4.6 Fungicidas

- Mertec 100ml
- Evito-T litro

Características:

- stock mínimo sugerido: 2-4 unidades
- lead time: 8 días
- consumo promedio: 0.3 unidades/día
- vencimiento típico: 2 años
- perecedero: sí

### 4.7 Alimentos

- PONEDORA
- CERDOS
- GANADERIA
- POLLOS

Características:

- stock mínimo sugerido: 10-50 bultos
- lead time: 8 días
- consumo promedio: 1.25 a 6.25 bultos/día
- vencimiento típico: 2 meses máximo
- perecedero: sí

---

## 5. Normalización de los datos

Para que la IA funcione bien, los datos deben quedar transformados en formato numérico y consistente.

### Reglas de normalización

- stock mínimo: número entero
- lead time: número entero de días
- consumo promedio: número decimal por día
- vencimiento: número de días o meses
- perecedero: booleano true/false
- unidades: unificar unidad base por producto o categoría
- cantidades tipo “50 bultos” deben convertirse a 50 y mantener la unidad = bultos

### Ejemplo de normalización

| Producto | Categoria | Stock mínimo | Lead time | Consumo diario | Vencimiento | Perecedero |
|---------|-----------|--------------|-----------|---------------|-------------|------------|
| Oxitetraciclina | medicina | 10 | 8 | 1 | 365 | true |
| Zanahoria | semillas | 15 | 15 | 1 | 3650 | false |
| Panzer litro | herbicidas | 15 | 8 | 2 | 1460 | true |
| PONEDORA | alimentos | 50 | 8 | 6.25 | 60 | true |

---

## 6. Modelos predictivos sugeridos

### 6.1 Modelo de demanda

Se recomienda modelar la demanda por producto con series de tiempo.

Objetivo:

- predecir demanda futura por día o por semana
- estimar cuántos unidades se consumirán en los próximos 7, 14 y 30 días

Modelos sugeridos:

- SARIMAX
- Prophet
- XGBoost con features de tiempo y categoría
- regresión lineal simple como baseline

### 6.2 Modelo de agotamiento

Objetivo:

- calcular la fecha estimada de agotamiento del producto

Fórmula base:

- fecha_agotamiento_estimado = fecha_hoy + (stock_actual / consumo_promedio_diario)

Luego se puede ajustar con:

- estacionalidad
- lead time
- stock mínimo
- tendencia histórica

### 6.3 Modelo de riesgo de vencimiento

Objetivo:

- detectar si un producto está próximo a vencerse
- asignar riesgo alto/medio/bajo

Regla de riesgo:

- riesgo alto: vencimiento en 0-30 días
- riesgo medio: 31-90 días
- riesgo bajo: >90 días

### 6.4 Modelo de riesgo de stock crítico

- riesgo alto: stock_actual <= stock_minimo
- riesgo medio: stock_actual <= stock_minimo + buffer
- riesgo bajo: stock_actual > stock_minimo + buffer

---

## 7. Estrategias de reposición

La IA debe recomendar reposición basada en tres estrategias.

### 7.1 Estrategia de stock bajo demanda

Se usa cuando la demanda proyectada supera el stock disponible.

Fórmula sugerida:

- demanda_proyectada = consumo_promedio * horizonte_dias
- cantidad_recomendada = max(0, demanda_proyectada + buffer_seguridad - stock_actual)

### 7.2 Reposición por rotura del stock mínimo configurado

Se usa para evitar quedarnos por debajo del mínimo permitido.

Fórmula sugerida:

- si stock_actual <= stock_minimo:
  - cantidad_recomendada = stock_minimo + lead_time * consumo_promedio - stock_actual

### 7.3 Estrategia top-off o basada en lead time

Se usa para proteger la operación cuando el lead time es alto o el producto es crítico.

Fórmula sugerida:

- cantidad_recomendada = lead_time * consumo_promedio + buffer_seguridad
- luego se ajusta comparando con stock_actual

### 7.4 Prioridad operativa

Cada recomendación debe obtener prioridad:

- alta: riesgo de agotamiento, vencimiento o stock crítico
- media: faltante probable en 7-15 días
- baja: producto estable con stock suficiente

---

## 8. Reglas de negocio para alertas

### 8.1 Alertas por stock crítico

Se activa cuando:

- stock_actual <= stock_minimo
- o stock_actual <= stock_minimo + 10%

### 8.2 Alertas por vencimiento próximo

Se activa cuando:

- producto perecedero y días_hasta_vencimiento <= 30

### 8.3 Alertas por agotamiento inminente

Se activa cuando:

- fecha estimada de agotamiento <= 7 días

### 8.4 Alertas por sin movimiento prolongado

Se activa cuando:

- no se registra movimiento del producto en más de 30 días
- o en categorías sensibles como medicina o alimentos, más de 14 días sin movimiento

### 8.5 Alertas por reposición recomendada

Se activa cuando:

- el motor de reposición genera una sugerencia de compra
- y la cantidad es > 0

---

## 9. Reglas de correlación y deduplicación

Un producto puede cumplir varias condiciones al mismo tiempo.

### Regla de consolidación

- una alerta por producto debe agrupar todas las causas relevantes
- el correo debe agrupar productos con riesgo similar
- el sistema no debe enviar un correo duplicado por cada condición

### Nivel de severidad

Se debe calcular el máximo riesgo por producto:

- alto si tiene stock crítico o agotamiento inminente
- medio si tiene vencimiento próximo o sin movimiento prolongado
- bajo si solo tiene una tendencia leve

---

## 10. Requisitos para la notificación por correo al administrador

### 10.1 Destinatario

El administrador del sistema es el destinatario principal de las notificaciones.

Configuración esperada:

- `app.alertas.email-destino`
- valor configurable por entorno
- idealmente se toma de variable de entorno o `.env`

### 10.2 Frecuencia

- envío inmediato cuando hay riesgo crítico
- envío programado diario a las 7:00 AM o según horario definido
- envío opcional manual por ejecución administrativa

### 10.3 Contenido del correo

El correo debe incluir:

- resumen ejecutivo del día
- total de productos en riesgo
- productos críticos
- productos por vencimiento cercano
- recomendación de reposición
- detalle por producto
- prioridad de acción

### 10.4 Estructura del correo

#### Resumen ejecutivo

- total productos en riesgo: X
- total próximas a vencer: Y
- total stock crítico: Z
- total sugerencias de compra: W

#### Tabla por producto

| Producto | Categoría | Stock actual | Stock mínimo | Riesgo | Días hasta agotarse | Recomendación |
|---------|-----------|--------------|--------------|--------|--------------------|---------------|
| Oxitetraciclina | medicina | 6 | 10 | Alto | 3 | Comprar 15 unidades |
| Panzer litro | herbicidas | 12 | 15 | Medio | 10 | Comprar 8 unidades |

### 10.5 Reglas de envío

- no enviar correo si no hay alertas
- if hay riesgo crítico, enviar correo inmediato
- si hay varias alertas, consolidarlas en un resumen ejecutivo

---

## 11. Especificación del panel administrativo

El panel administrativo debe mostrar un cuadro de comportamiento por producto.

### 11.1 Informacion por producto

Cada tarjeta o fila debe mostrar:

- nombre del producto
- categoría
- stock actual
- stock mínimo
- consumo promedio
- stock disponible vs mínimo
- días hasta agotamiento
- días hasta vencimiento
- última fecha de movimiento
- estado: normal / bajo / crítico / vencimiento / inactivo
- recomendación de compra
- prioridad

### 11.2 KPI del dashboard

- productos con stock crítico
- productos con vencimiento próximo
- productos sin movimiento reciente
- productos con mayor riesgo de agotamiento
- rotación promedio por categoría
- valor del inventario total
- valor de inventario crítico

### 11.3 Requisitos de visualización

- color por nivel de riesgo: rojo, amarillo, verde
- filtro por categoría
- filtro por estado
- orden por prioridad
- vista de detalle por producto

---

## 12. Requerimientos para el asistente conversacional

El asistente no será el motor de decisión, sino la capa de explicación del sistema.

### 12.1 Preguntas que debe responder

- ¿Qué productos tienen stock bajo?
- ¿Qué está por vencer?
- ¿Qué productos necesitan reposición?
- ¿Cuáles están por agotarse?
- ¿Qué riesgos hay en medicina o alimentos?
- ¿Cuánto se debería pedir de X producto?
- ¿Cuál es la categoría con más riesgo?

### 12.2 Estructura de respuesta

La IA debe responder con:

1. resumen ejecutivo
2. lista de productos relevantes
3. recomendación cuantitativa
4. justificación
5. nivel de riesgo

### 12.3 Reglas de respuesta

- responder con base en predicción y datos reales del sistema
- si no existen datos suficientes, usar recomendaciones por regla de negocio
- nunca inventar un dato que no existe en la base

---

## 13. Requisitos técnicos de implementación

### 13.1 Backend

Se deben crear o adaptar estos componentes:

- `ForecastService`
- `RecomendacionReposicionService`
- `AlertaInventarioService`
- `EmailNotificationService` (ya existente, reforzado)
- `DashboardInventarioService`
- `AsistenteIaService` (explicación final, no motor central)

### 13.2 Persistencia

Se recomienda guardar:

- historial de movimientos
- alertas por producto
- recomendaciones generadas
- notificaciones enviadas
- predicciones por fecha

### 13.3 Scheduling

Se recomienda un cron diario para:

- calcular predicciones
- evaluar alertas
- enviar correo resumen al administrador

### 13.4 API sugerida

- `GET /api/v1/ia/dashboard`
- `GET /api/v1/ia/productos-riesgo`
- `GET /api/v1/ia/recomendaciones`
- `POST /api/v1/ia/consultar`
- `GET /api/v1/alertas` (ya existente)
- `POST /api/v1/alertas/email/resumen`

---

## 14. Definición de éxito de la fase

La fase será exitosa si el sistema:

- detecta riesgos de inventario antes de que ocurran
- recomienda reposición basada en demanda y lead time
- envía correos útiles al administrador del sistema
- presenta el comportamiento real de cada producto
- reduce pérdidas por vencimiento, rotura de stock y desabastecimiento

---

## 15. Conclusión

La siguiente fase debe centrarse en convertir el sistema de inventario en un sistema inteligente de toma de decisiones. La IA no debe vivir aislada ni reemplazar al negocio, sino apoyar decisiones operativas con predicción, reglas y alertas.

La combinación recomendada es:

- predicción para entender demanda y riesgo
- reglas para decidir reposición
- notificaciones por correo para informar al administrador
- dashboard para visualizar comportamiento por producto
- asistente conversacional para explicar la situación en lenguaje natural

Esta es la base técnica adecuada para que Agro Inventario avance hacia el objetivo general del proyecto: un asistente inteligente para gestión automatizada de inventarios agropecuarios, con análisis predictivo y apoyo a la toma de decisiones estratégicas.
