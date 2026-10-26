# Aura0.27.0-beta38 — Fase1

26 de octubre de2026. Código38, paquete diario dev.senda.launcher.debug. APK preparado; no instalado. Rama codex/phase1-theme-editor, base0714c7c. No fusionar a main hasta validar rendimiento en Motorola.

## Implementado

Editor con estado elevado al ViewModel, snapshot inmutable, historial Memento de50 pasos, Undo/Redo por gesto, variantes/borrador Room y copia privada de fondos al guardar. Ajustes de iconos, contenedores, fuentes, separación y fondos; guardar copia o activar. Los temas base y los reemplazos personales se conservan.

Vista previa con Header/AppSections/DashboardGroup reales y fondos estáticos del tema, incluidas PS4 y texturas. Movimiento congelado, audio silenciado y acciones táctiles bloqueadas; widgets externos son placeholders para no duplicar su host. No se garantiza recomposición cero: los ajustes que cambian layout deben recomponerlo.

SIII: metadata revisable para176 apps del inventario existente.20 recursos candidatos/156 pendientes, con procedencia, certeza y hash. Grid a32–60dp, claves por paquete y recursos existentes; no hay generación automática ni nuevo arte aprobado. Un graphicsLayer no aumenta la resolución de un bitmap; la nitidez depende del recurso fuente y revisión óptica.

Medición: módulos macrobenchmark/baselineprofile, variante performance aislada dev.senda.launcher.benchmark, cold startup TTID/TTFD, scroll/A–Z y generador de perfiles. Herramienta de comparación y captura de reposo de solo lectura. Ver PERFORMANCE.md.

## Evidencia

Build final: app assembleBeta/lintBeta/assemblePerformance, macrobenchmark assembleBenchmark, baselineprofile assembleNonMinifiedPerformance y listado de tareas: BUILD SUCCESSFUL3m9s,162 tareas. Existe generatePerformanceBaselineProfile. Lint:0 errores/30 advertencias/3 avisos. Scripts Python compilan; diff revisado. No suites ajenas ejecutadas.

APK beta/Aura-0.27.0-beta38.apk SHA256: adf9a8a3ff6a0b2d90c7b5aa7c4c50089bb5608f4c64cc7d0de6e84839175301.
Certificado SHA256:729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c, verificado con apksigner. Se volvió a firmar el APK de entrega con la clave original; el APK crudo de build usa la clave del entorno y no debe instalarse como actualización diaria.

## Pendiente

Generar perfiles propios y medir referencia/candidato en Motorola disponible; revisión física del editor/rotación/reinicio/Undo/Redo, alto contraste e iconos personales. No FPS, energía ni mejora cuantificada. Sin instalar ni tocar ajustes del teléfono durante esta entrega.

Variantes locales no incluidas aún en el respaldo JSON anterior (avisado en UI). Borrador persiste; historial Undo/Redo solo vive durante la sesión. Catálogo156 pendiente no significa packs completos. Fase2 requiere confirmación.

OpenCode fue bloqueado por revisión automática por posible transferencia de código privado; implementación completada localmente sin reintentar esa transferencia.
