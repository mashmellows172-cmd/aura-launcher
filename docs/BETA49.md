# Beta49 — diagnóstico local de fluidez
29 de octubre de2026,0.35.0-beta49/code49.

## Implementación
Dos agentes locales entregaron medidor y panel; integración/revisión por Codex. Acceso desde menú rápido PS4 y menú de otros temas: Diagnóstico de fluidez.
Muestra opt-in60s, solo ventana Activity de Aura. FrameMetrics en HandlerThread, agregación constante y publicación1/s. Informa frames, porcentaje que excede plazo, media, máximo, Hz de referencia y callbacks descartados. DEADLINE API31+, intervaloHz para29/30 o dato no disponible. No es FPS, benchmark, consumo ni métrica oficial de jank. Detiene al pausar, al cerrar Activity o a60s. No se guardan muestras ni se envían datos. Resultado vive mientras la Activity exista.
Modo ligero persistido aparte en aura_performance: aplica reduceMotion y desactiva glow como overlay, no sobrescribe preferencias visuales originales. Ajustes de movimiento/índice editan el estado almacenado original. Desactivarlo restaura sus elecciones. No cambia ajustes del sistema. Esta preferencia independiente aún no está incluida en el respaldo JSON.
No se agregaron dependencias ni suites automáticas. Referencia oficial: https://developer.android.com/reference/android/view/FrameMetrics

## Validación
Compilación/lint e instalación pendientes. No se afirma mejora medida por incorporar diagnóstico. Comparar recorridos equivalentes y temperatura/carga; las muestras no son comparables automáticamente si se cambia modo durante captura.

## Entrega verificada
Compilación final assembleBeta/lintBeta correcta3m20s,50 tareas(17 ejecutadas/33 al día),0 errores/32 advertencias/3 avisos. Se corrigió inferencia recursiva de Kotlin con retornos Unit explícitos. Revisión cruzada confirmó preservación de preferencias y limpieza; se bloqueó cambio de modo durante muestra para no mezclar resultados.
Instalada actualización -r conservando datos en Motorola autorizado; certificado original, versión49 y hash posterior verificados. SHA256:184c8b78845bd4579d8ecedb52fdfe6a1e68d3439198f14626ceab6399d4c1f2.
No se inició medición ni se cambió modo ligero en el teléfono: revisión física pendiente, estaba en otra app al preparar. Sin afirmaciones de FPS, batería o mejora cuantificada. Modo ligero sigue desactivado por defecto.
