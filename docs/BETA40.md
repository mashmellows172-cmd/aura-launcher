# Aura0.29.0-beta40 · PS4 vertical

28 de octubre de2026, code40, paquete diario dev.senda.launcher.debug. Rama de validación codex/phase1-theme-editor; no promover efectos a main sin las mediciones físicas exigidas.

## Dirección visual

Implementación propia basada en la captura aportada por el usuario: lista vertical de Aura, títulos ligeros, placas cuadradas, identidades a color, foco luminoso central y cintas azules. No es la variante cloud beta38 con Ps4Carousel.kt, que sigue ausente. No se restauró el carrusel horizontal rechazado.

La documentación pública de Sony describe el sistema de consola: https://manuals.playstation.net/document/gb/ps4/ . La adaptación vertical no es una reproducción100% del Dynamic Menu. Se usa Noto Sans Light incluida como alternativa libre a SST, sin afirmar fuente propietaria idéntica ni tiempos oficiales medidos.

## Cambios

- Un ítem de app cercano al42% del viewport recibe foco al desplazarse. Encabezados/widgets no se consideran apps. El toque sigue abriendo inmediatamente; pulsación larga y acciones TalkBack permanecen.
- Foco320ms FastOutSlowIn: doble contorno blanco/cian, halo dibujado con trazos y relleno azul. Escalas de fila, placa y título leídas en graphicsLayer. No se anima el tamaño del layout por frame. El cálculo del objetivo usa derivedStateOf; la composición cambia al pasar de ítem.
- AUTO PS4 muestra los drawables originales a color cuando no existen packs, reemplazos ni importaciones explícitos. Normales/Monocromo/packs y reemplazos conservan prioridad. No se regeneró ni borró el catálogo histórico de iconos PS4.
- Placas cuadradas con borde fino, títulos de sección32sp Light, tipografía Noto y reloj22sp. Se respetan tamaño de texto del sistema y valores personales; nombres largos mantienen elipsis.
- Fondo propio con seis cintas curvas translúcidas, líneas cian y luz radial sobre azul profundo. Mismo reloj de20s ligado al lifecycle, sin nuevos timers, bitmaps capturados ni blur. Se conservan fondo personal, alto contraste y movimiento reducido.
- Búsqueda, A–Z, favoritas, widgets, reproductor, accesos al clima/calendario/alarmas, gestos y preferencias permanecen en la estructura compartida.

## Validación

Verificación final local: `assembleBeta lintBeta`, BUILD SUCCESSFUL en2m12s;50 tareas,12 ejecutadas/38 al día. Lint0 errores/30 advertencias/3 avisos. Log privado work/aura-beta40-verified-build.log. Incidencias anteriores y revisión independiente en superpowers/reports/2026-10-28-ps4-vertical.md. No suites automáticas.

APK beta/Aura-0.29.0-beta40.apk; SHA2563f99aae5aabcccc84fd4859a9f8c604a240efc469afee56839467c4a1066b4cc. Certificado original SHA256729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c, verificado con apksigner y comparado con el APK instalado antes de actualizar. Instalada mediante `adb install -r` en el Edge50Ultra autorizado; APK posterior extraído para comprobar versión0.29.0-beta40/code40, certificado y mismo hash. Sin borrar datos, cambiar permisos ni ajustes. Claves/APK/evidencia privada fuera de Git.

Revisión visual acotada realizada en el Motorola tras autorización de dejar Aura PS4 libre: identidad a color, placas, encabezados y foco observados. Un desplazamiento vertical confirmó cambio de app resaltada y sección A–Z visible; después se desplazó hacia el inicio. Capturas privadas de dos estados permiten observar variación del fondo, pero no miden FPS ni consumo. PiP de vídeo ya activo cubría parte del reproductor; no se cerró, pausó ni cambió esa otra app. No se revisaron exhaustivamente menús, apertura de apps, todos los modos de accesibilidad ni todos los temas.

Sin métricas físicas de rendimiento/batería ni certificación de equivalencia100%. No promover a main hasta completar PERFORMANCE.md.
