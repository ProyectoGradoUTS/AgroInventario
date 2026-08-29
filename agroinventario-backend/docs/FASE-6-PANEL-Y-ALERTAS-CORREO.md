# Panel administrativo y alertas por correo

## Objetivo

Definir la capa analítica y de alertas que permitirá al administrador del sistema conocer, en tiempo real o por resumen programado, el estado de cada producto y decidir sobre reposición, vencimiento y riesgo de agotamiento.

---

## 1. Requisitos del administrador

El administrador necesita ver el estado de cada producto con claridad, rapidez y prioridad operativa.

Se deben mostrar:

- producto
- categoría
- stock actual
- stock mínimo
- consumo promedio
- días hasta agotarse
- días hasta vencimiento
- estado del producto
- última fecha de movimiento
- recomendación de reposición
- nivel de riesgo

---

## 2. Cuadro de comportamiento por producto

Cada producto debe quedar representado en una fila o tarjeta con estos campos:

| Campo | Descripción |
|------|-------------|
| Nombre | Nombre del producto |
| Categoría | Medicina, semilla, herbicida, etc. |
| Stock actual | Cantidad disponible en inventario |
| Stock mínimo | Mínimo configurado |
| Consumo promedio | Promedio por día o por semana |
| Días hasta agotarse | Estimación calculada por IA |
| Días hasta vencimiento | Para productos perecederos |
| Último movimiento | Fecha de última entrada/salida |
| Estado | Normal, bajo, crítico, vencido, inactivo |
| Riesgo | Bajo, medio, alto |
| Recomendación | Comprar, reponer, no aplicar |
| Prioridad | Alta, media, baja |

---

## 3. Estados esperados por producto

- NORMAL: stock suficiente y sin riesgo
- BAJO: stock por debajo del mínimo
- CRITICO: stock muy bajo o riesgo de agotamiento
- VENCIMIENTO_PROXIMO: vencimiento cercano
- INACTIVO: sin movimiento prolongado
- SIN_MOVIMIENTO: no se registran ventas ni salidas durante tiempo definido

---

## 4. Prioridad de atención

La prioridad se calcula según:

- agotamiento inminente
- vencimiento próximo
- stock crítico
- sin movimiento prolongado
- lead time largo

### Prioridad recomendada

- Alta: riesgo crítico o genera compra inmediata
- Media: riesgo moderado o requiere seguimiento
- Baja: stock estable

---

## 5. Reglas para la alerta por correo

### 5.1 Envío de correo

El administrador del sistema debe recibir un correo cuando:

- existe al menos un producto en riesgo alto
- existe vencimiento próximo
- existe stock crítico
- existe recomendación de compra no atendida

### 5.2 Frecuencia

- inmediato si hay riesgo crítico
- resumen diario programado
- resumen semanal opcional

### 5.3 Formato sugerido del correo

#### Asunto

```text
[AgroInventario] Resumen diario de inventario - X alertas críticas
```

#### Cuerpo

- resumen ejecutivo
- cantidad de productos por riesgo
- lista de productos críticos
- productos próximos a vencer
- recomendaciones de compra
- enlaces o referencias al sistema

### 5.4 Ejemplo de resumen

```text
Resumen diario Agro Inventario

Productos críticos: 6
Próximos a vencer: 4
Sin movimiento: 3
Recomendaciones de compra: 9

Productos con mayor riesgo:
- Oxitetraciclina | medicina | stock 6 / mínimo 10 | Riesgo alto | Comprar 15 unidades
- Panzer litro | herbicidas | stock 12 / mínimo 15 | Riesgo medio | Comprar 8 unidades
- PONEDORA | alimentos | stock 20 / mínimo 50 | Riesgo alto | Comprar 40 bultos
```

---

## 6. Consolidación de alertas

Un producto puede activarse por varias causas simultáneamente. El sistema debe:

- consolidar esas causas en una sola alerta por producto
- priorizar la causa más severa
- enviar un único resumen ejecutivo al administrador

### Ejemplo

Producto X puede estar:

- con stock bajo
- próximo a vencer
- sin movimiento reciente

Entonces la alarma debe reflejar:

- prioridad alta
- mensaje consolidado
- recomendación combinada

---

## 7. Requisitos del servicio de email

El servicio de notificación debe:

- enviar resumen ejecutivo al administrador
- apoyar correo inmediato y correo programado
- validar configuración SMTP
- evitar enviar correos vacíos
- registrar cada correo enviado en auditoría

### Requisitos mínimos

- `spring.mail` configurado
- `app.alertas.email-destino` definido
- `MAIL_USERNAME` y `MAIL_PASSWORD` configurados
- soporte para envío HTML o texto plano

---

## 8. Recomendación para la UI del dashboard

### 8.1 Vista principal

- tarjetas de KPI
- tabla de inventario por producto
- filtro por categoría
- últimos productos críticos

### 8.2 Vista detalle de producto

- historial de consumo
- stock mínimo vs actual
- fecha estimada de agotamiento
- fecha de vencimiento
- riesgo y recomendación

---

## 9. Definición de éxito

La fase será considerada exitosa cuando:

- el administrador puede ver el estado real de cada producto
- el sistema identifica productos críticos antes de que fallen
- el sistema recomienda reposición de forma cuantitativa
- el administrador recibe correos justificables y útiles
- la interfaz refleja comportamiento, riesgo y prioridad

---

## 10. Resumen final

El panel administrativo y la alerta por correo deben convertir la IA y la lógica de negocio en acción operativa. La infraestructura no debe ser solo descriptiva: debe generar decisiones claras, priorizadas y enviadas al responsable del sistema para que pueda actuar de forma oportuna.
