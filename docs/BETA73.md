# Beta73 · progreso multimedia y calidad adaptativa

11 de noviembre de 2026. 0.51.0-beta73, código 73. Base beta72 5e84283. Implementa las propuestas 2 y 1 elegidas por el usuario.

La ficha PS4 se abre desde la tarjeta multimedia. La carátula usa una capa superpuesta de ventana completa, con origen y destino medidos, y comparte el progreso de apertura/cierre del panel. Muestra título, artista, tiempo transcurrido/duración y permite buscar cuando la sesión publica ACTION_SEEK_TO y duración válida. Pausa/reanudación y apertura del reproductor conservan la sesión elegida. La información genérica conserva el acceso a configuración. No inventa una duración cuando falta.

AudioPlaybackMonitor publica posición, velocidad, tiempo monotónico, duración y capacidades reales. La ficha extrapola desde esa posición cada 500 ms únicamente mientras está abierta, RESUMED y reproduciendo. Pausa conserva la sesión actual si no hay otro audio activo. Cambiar o perder sesión descarta la ficha; los comandos comprueban sesión, estado, acciones y límites de duración de nuevo al ejecutarse. No se añaden permisos ni consultas de red.

La calidad adaptativa observa FrameMetrics de la ventana Activity mientras Inicio está RESUMED y el modo adaptativo activo. Excluye primer dibujo y valores inválidos; usa DEADLINE en API31+ o intervalo de pantalla como alternativa. Ventanas de 60 cuadros: seis lentos reducen decoración; tres ventanas con como máximo uno lento, tras dos segundos, recuperan calidad. Al pausar se elimina el listener y se borra su estado. Solo hace trabajo cuando Android entrega métricas; no crea un reloj de render ni cambia el refresco solicitado.

Se reducen resplandores y motas de cintas, la luz superpuesta, curvas multimedia y detalles ornamentales de paneles. Se conservan fondo base, cintas, texto, controles, gestos y desplazamiento nativo. No se persisten cambios ni se alteran imágenes, packs, sonidos o permisos. Es una heurística de la ventana Activity, no una medida de presentación de SurfaceFlinger ni prueba de menor latencia.

## Validación y límites

Primera compilación/lint correctos en 1 min 30 s. La mejora posterior de la capa de carátula requirió corregir el montaje del diálogo; pasada final correcta en 57 s, con 0 errores, 42 advertencias y 3 avisos. Se conservan también sesiones en buffering/conexión para evitar cerrar la ficha durante una búsqueda; los comandos siguen validando estados reproducible/pausado. Se corrigió una conversión de codificación durante la edición y se revisó el diff para conservar los textos. Revisión de fuente y diff. No se añadieron ni ejecutaron suites automáticas, conforme AGENTS.md. No instalada. Se mantienen pendientes prueba física con reproducción/pausa/seek soportado y no soportado, cambio de sesión, carátulas ausentes, rotación/ventana pequeña, accesibilidad, recuperación de calidad y comparación de fluidez.

Skills: writing-plans/executing-plans, ponytail, animate y accessible-animation. No se delegó otra revisión de nube para conservar el margen de cuota solicitado; esta entrega no acredita revisión independiente ni rendimiento físico. Archivos de compilación y APK fuera de Git.

APK local verificado con aapt/apksigner: 0.51.0-beta73/código73, firma compatible 2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514. Tamaño 15710532 bytes. SHA-256 `de4f0fa94d2356623289155b60c882b194b02f7845ced10b76bc28454bf7670f`. APK privado C:/Users/victus/Documents/Aura-private-beta73/aura-beta73.apk. Sin instalación ni hash del teléfono de esta versión.
