# Beta46 — revisión de movimiento y sonidos PS4
29oct2026,0.33.1/code46, rama de validación.

Causa sonora: SoundAssets enviaba PS4 a Nothing y SoundFamily no permitía PS4. Añadida séptima familia completa con cinco señales PCM originales sintetizadas (abrir, menú, seleccionar, favoritas, índice); no son samples oficiales Sony. Selector, AUTO y validador de respaldo admiten PS4. Respeta políticas existentes de silencio/DND/volumen/medios, sin cambiar ajustes del teléfono.
Movimiento: curva de desaceleración continua sin rebote y menor amplitud de slide/giro/zoom/compresión; intensidad y movimiento reducido siguen aplicando. Fondo PS4 pasa de8 a4 superficies por cinta y48 a32 segmentos, conservando tres cintas y rutas reutilizadas: menos dibujo por frame según fuente, no prueba de FPS.
Diagnóstico acumulado beta45:29334 frames,54.07% janky,p50 42ms,p90 77ms. Sesión no controlada, no permite atribuir coste al desbloqueo ni demostrar mejora. Teléfono volvió a NotificationShade al preparar revisión; no se forzó navegación ni cambió timeout.
Revisor independiente confirmó mapping/generador y controles de desbloqueo. Compilación final y revisión física pendientes. Sin suites automáticas ni nueva dependencia.

## Entrega y revisión
Compilación final assembleBeta/lintBeta correcta3m14s,50 tareas (11 ejecutadas/39 cacheadas),0 errores/31 advertencias/3 avisos. WAV verificados mono22050Hz,45–175ms,pico0.22 y cinco hashes distintos. Firma original y versión46 instaladas mediante actualización -r, conservando datos. SHA256 extraído posterior:e0bc66154967ce67a481f94dd4d648b1a1ab331513269b668e879345e4005137.
Dos swipes manuales por versión tras reset diagnóstico gfxinfo: beta45 67frames/45janky(67.16%),p50 48ms,p90 77ms; beta46 89frames/34janky(38.20%),p50 42ms,p90 61ms. Muestras pequeñas, sin control térmico/warmup y distinta cantidad de frames: NO son benchmark ni evidencia causal definitiva. Hay tirones pendientes. No se midió el desbloqueo autenticado.
Panel de sonidos visto con familia AUTO y volumen relativo100% existente. Pulsada una muestra; no confirmación auditiva por parte del agente. No se cambió volumen/silencio/NoMolestar ni permisos. Sonidos PS4 propios, no grabaciones Sony.
