# Centro rápido progresivo — Plan de implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task by task.

## Objetivo

Condensar el Centro rápido a una única fila horizontal de cápsulas contextuales y revelar una sola función a la vez mediante gestos locales. La lista de Inicio no debe quedar tapada al abrir el panel.

## Decisiones

- La fila contiene Música, Temporizadores, Alarma, Agenda y Accesos; la nota fijada aparece sólo cuando existe. Respeta el orden persistente de las tarjetas.
- Tocar selecciona una cápsula; deslizar hacia arriba sobre ella revela su detalle. Deslizar hacia abajo lo contrae. El asa superior sigue controlando el panel general.
- Música no muestra un reproductor grande hasta que se expande. Entonces reaprovecha los controles reales de `MediaController`: transporte, progreso, búsqueda cuando la sesión la permite, ficha y apertura segura de la fuente.
- Vídeo no se incrusta. La carátula es una visualización reactiva de metadatos/progreso; **Abrir vídeo** entrega el control a YouTube. No se simulan cola, calidad, subtítulos ni una superficie de vídeo que Android no expone.
- La animación usa un solo foco, 200–260 ms/spring de baja amplitud, sin bucles de reposo. El movimiento se deriva de gesto/progreso de sesión.

## Archivos

- `ui/QuickCenter.kt`: estado de cápsula activa, fila, revelado localizado y detalle único.
- `ui/QuickCenterRail.kt` (nuevo): cápsulas compactas, gestos y semántica independiente del panel.
- `app/build.gradle.kts`: beta112.
- `README.md`, `CHANGELOG.md`, `docs/CONTINUITY.md`, `docs/BETA112.md`, coordinación: evidencia de entrega.

## Tareas

1. Crear cápsulas con objetivo táctil de 48 dp, selección visible y alternativa por toque/semántica al gesto.
2. Sustituir la columna de todas las tarjetas por rail + un detalle bajo demanda; conservar notas, orden/fijado, alarmas, agenda y accesos.
3. Hacer que la tarjeta multimedia se monte durante la expansión y usar saltos de ±10 s específicamente para vídeo YouTube.
4. Revisar contraste/semántica, gesto reducido y tamaños de texto en fuente; compilar `assembleBeta lintBeta` sin suites automáticas.
5. Comparar firma e instalar mediante `adb install -r` sólo si ADB identifica al Motorola y es compatible; registrar hash/resultado real.
