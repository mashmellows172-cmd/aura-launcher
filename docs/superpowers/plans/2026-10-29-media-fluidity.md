# Media fluidity Implementation Plan

Goal: multimedia visible y distintivo por tema, expansión al reproducir y compactación breve al parar. Sin prometer desbloqueos aún no implementados.
Architecture: UI conserva último contenido solo2.5s para transición; controles reales únicamente en reproducción; MediaSession intacto. Visualizador decorativo visible con carátula, no análisis real de audio. Reloj RESUMED y ahorro/reduceMotion.

- [x] Inspeccionar estado y código existentes.
- [x] Estado activo/compacto/salida, una transición de tamaño y siete visualizadores existentes visibles.
- [x] Reutilizar Path de Xperia/PS4; detener reloj fuera RESUMED.
- [ ] Revisión fuente, assembleBeta/lintBeta.
- [ ] Documentar candidato beta44 y límites de validación. Firma instalada incompatible: no instalar ni borrar datos.

Skills: Using Efficient Effects y Deferring State Reads. Sin suites automáticas. Recursos originales y preferencias intactos. Duraciones son propuestas, no evidencia de FPS. Desbloqueos siguen siendo trabajo separado del plan unlock.
