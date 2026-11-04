# 001 — Liberar refresco durante reposo

- Status: IMPLEMENTED, validación física pendiente
- Commit base: f9794f0
- Severity: HIGH
- Category: Performance
- Scope: MainActivity.kt y ui/RefreshRatePolicy.kt

## Problema
MainActivity no informa preferencia de refresco. Fijarla permanentemente tampoco satisface reposo. La consulta se origina en el contrato BETA56-TASKS.md; el lag continúa sin causa única demostrada.

## Objetivo exacto
API30+:120f durante interacción con ventana enfocada y RESUMED; liberar a0f tras1500ms. API29: mayor tasa con la misma resolución del modo actual; liberar modeId0. Cancelar al pausar, perder foco, cerrar o activar light/powerSaving. Sin polling ni nuevas animaciones, dependencias o permisos.

## Pasos y convenciones
Usar Handler principal con un único Runnable cancelable. Leer valores previos antes de mutar LayoutParams. Integrar onUserInteraction sin consumir eventos y usar preferencias observables de beta55. No tocar respaldo, widgets, iconos, selección de efectos ni firma. Lint rechazó dispatchKeyEvent por RestrictedApi; el callback nativo evita esa restricción.

## Verificación
assembleBeta lintBeta. En Motorola comparar preferencia de ventana durante gesto y2s después, ahorro/modo ligero y cambio de foco. Preservar ajustes y no afirmar120FPS niLTPO1Hz. Revisión de fluidez con mismo recorrido y condiciones. La rasterización del fondo requiere otra entrega acotada.
