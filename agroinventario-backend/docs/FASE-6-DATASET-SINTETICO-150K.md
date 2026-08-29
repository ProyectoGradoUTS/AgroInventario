# Dataset sintético de 150.000 registros para IA predictiva

## Objetivo

Generar un dataset realista para entrenar y validar modelos de predicción de demanda, agotamiento, vencimiento y reposición en una empresa agropecuaria de Bucaramanga.

---

## 1. Alcance

Se generarán 150.000 observaciones sintéticas, distribuidas por categoría, producto y fecha.

La idea es simular comportamiento realista de inventario para:

- consumo variable por producto
- stock crítico y stock mínimo
- vencimiento por categoría
- lead time por producto
- estacionalidad por temporada
- comportamiento dependiente de la categoría agropecuaria

---

## 2. Estructura propuesta del dataset

Cada fila debe contener:

```text
id_registro,
fecha,
producto_id,
nombre_producto,
categoria,
perecedero,
stock_inicial,
stock_actual,
stock_minimo,
consumo_real,
entradas,
salidas,
ventas,
precio_unitario,
fecha_vencimiento,
lead_time_dias,
rotacion_dias,
ultimo_movimiento,
temporada,
zona,
estado_producto
```

---

## 3. Distribución esperada por categoría

Se busca una distribución equilibrada para reflejar un inventario realista:

- medicina veterinaria: 25%
- semillas: 18%
- toxicológicos: 12%
- herbicidas: 14%
- insecticidas: 15%
- fungicidas: 8%
- alimentos: 8%

---

## 4. Producto base y parámetros por categoría

| Categoria | Stock mínimo | Lead time | Consumo diario base | Vencimiento | Perecedero |
|----------|--------------|-----------|--------------------|-------------|------------|
| medicina | 10 | 8 | 1.0 | 365 días | true |
| semillas | 15 | 15 | 1.0 | 3650 días | false |
| toxicológicos | 5-8 | 10-15 | 1.0 | 730 días | true |
| herbicidas | 5-15 | 8 | 2.0 | 1460 días | true |
| insecticidas | 2-6 | 8-15 | 0.2-1.0 | 1095 días | true |
| fungicidas | 2-4 | 8 | 0.3 | 730 días | true |
| alimentos | 10-50 | 8 | 1.25-6.25 | 60 días | true |

---

## 5. Regla de generación sintética

### 5.1 Consumo diario

El consumo real debe generar variaciones por:

- temporada
- producto
- categoría
- comportamiento histórico
- efecto de ventas o movimiento previo

Fórmula base sugerida:

```text
consumo_real = consumo_base * factor_temporada * factor_categoria * ruido
```

### 5.2 Stock actual

```text
stock_actual = max(0, stock_inicial + entradas - salidas - ventas)
```

### 5.3 Stock mínimo

Se debe asignar por producto o por categoría, con posibles variaciones del 10% al 20%.

### 5.4 Lead time

Se recomienda guardar el lead time fijo por producto y con un pequeño ruido del ±10% para simular variabilidad realista.

### 5.5 Fecha de vencimiento

Para productos perecederos:

- fecha de vencimiento debe estar en el futuro o próximo
- se puede generar con base en días útiles o calendario real

Para productos no perecederos:

- se usa vencimiento largo o se marca como sin vencimiento

---

## 6. Patrones de comportamiento esperados

### 6.1 Productos críticos

- stock bajo
- consumo irregular
- vencimiento próximo
- sin movimiento prolongado

### 6.2 Productos estables

- stock por encima del mínimo
- consumo regular
- sin riesgo de vencimiento

### 6.3 Productos estacionales

- repunte de consumo en ciertos meses
- cierre de inventario en temporadas específicas

---

## 7. Segmentación de la base

Se recomienda dividir el dataset en:

- entrenamiento: 70%
- validación: 15%
- prueba: 15%

---

## 8. Reglas de calidad del dataset

- no valores nulos en producto, categoría, stock, consumo y lead time
- stock actual no negativo
- consumo relevante y no exagerado
- fechas válidas
- consistencia entre stock y movimientos
- no se deben generar productos sin categoría

---

## 9. Ejemplo de un registro sintético

```json
{
  "id_registro": 154320,
  "fecha": "2025-03-12",
  "producto_id": 15,
  "nombre_producto": "Oxitetraciclina",
  "categoria": "medicina",
  "perecedero": true,
  "stock_inicial": 18,
  "stock_actual": 9,
  "stock_minimo": 10,
  "consumo_real": 1.2,
  "entradas": 0,
  "salidas": 1,
  "ventas": 1,
  "precio_unitario": 22000,
  "fecha_vencimiento": "2026-03-12",
  "lead_time_dias": 8,
  "rotacion_dias": 9,
  "ultimo_movimiento": "2025-03-11",
  "temporada": "seca",
  "zona": "Bucaramanga",
  "estado_producto": "RIESGO"
}
```

---

## 10. Resultado esperado

Con este dataset, la IA podrá:

- proyectar demanda por producto
- detectar agotamiento inminente
- estimar stock crítico
- establecer prioridad de compra
- sugerir una reposición racional y automatizada
- disparar alertas por correo
- alimentar el dashboard de comportamiento de cada producto
