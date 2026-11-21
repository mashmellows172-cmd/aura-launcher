# Beta109 · Centro directo y portada continua

21 de noviembre de 2026. Aura 0.75.0-beta109, código 109, paquete `dev.senda.launcher.debug`. Base beta108 `8a6777e`, rama `codex/beta109-direct-media-motion`.

## Interacción

El asa del Centro rápido sigue el arrastre y comparte un progreso con el tamaño de portada y el despliegue de controles. Arriba amplía; abajo recoge y, con recorrido largo o un lanzamiento desde compacto, cierra. La velocidad se calcula acumulando deltas en un marco estable, evitando medir un asa que se mueve con el panel. Un lanzamiento breve hacia abajo desde ampliado pasa primero por compacto. Cancelar devuelve a un ancla válida y sincroniza el estado lógico. Un nuevo arrastre interrumpe el resorte sin reiniciar la posición.

El cierre por gesto usa una sola animación hasta la altura real del panel; los botones mantienen su cierre configurado. La lista de tarjetas y el slider no reciben un detector de arrastre adicional. Movimiento reducido y efectos desactivados mantienen la interacción con cambios inmediatos. Los controles de expansión no están compuestos en progreso cero y permanecen deshabilitados para teclado, D-pad, toque y semántica hasta estar desplegados.

## Portada

Una capa de dibujo dentro de la ventana del diálogo posee la imagen mientras está activo el efecto. Los slots conservan geometría y acciones, pero no pintan una segunda portada. La imagen viaja desde la miniatura multimedia fijada, con su posición real y clave del contenido, hasta la tarjeta ampliada; al contraer, su rectángulo sigue el mismo progreso de expansión. El radio pasa de 6 a 12 dp. Vídeo conserva Fit con bandas, música conserva Crop.

Si no hay miniatura multimedia fijada, la portada aparece en su slot sin inventar un origen. Si cambia el contenido, se descarta el origen antiguo. Sin imagen válida se conserva el fallback. La imagen respeta el recorte del área de tarjetas al asentarse y en transiciones sin origen compartido. La capa decorativa no captura input ni duplica semántica.

Para ver el viaje completo desde Inicio: elegir la miniatura Música en la configuración del Centro rápido y abrir desde ella con una sesión que proporcione carátula. La ficha multimedia separada conserva su transición anterior; no se afirma que comparta esta nueva capa.

## Arquitectura y delegación

`QuickPanelDragState` concentra posición, política de anclas y resorte finito. La geometría se lee en layout y graphicsLayer; la portada lee progreso y rectángulos solo al dibujar. No hay corutina por muestra del dedo, nuevas imágenes por fotograma, dependencias o reloj decorativo en reposo.

[Registro de delegación](coordination/BETA109-DELEGATION.md): OpenCode no pudo activar complementos; Qwen local propuso código rechazado por errores; DeepSeek local no entregó una respuesta final dentro de límites acotados. Un agente interno implementó la capa de portada y otro revisó la integración. Ninguna salida de agentes se dio por válida sin revisión.

## Validación

Primer compile: faltaban imports background/Color de la miniatura; corregidos. La revisión independiente exigió corregir cancelación, marco de velocidad, clipping y foco de controles parcialmente ocultos. Revisión posterior sin P1/P2 pendientes por source. Un aviso nuevo de orden de Modifier opcional fue corregido sin suprimir lint.

`gradlew.bat --max-workers=2 assembleBeta lintBeta`: BUILD SUCCESSFUL en 1 min 7 s, 0 errores, 58 advertencias y 4 avisos. Se verificó metadata del APK: 109 / 0.75.0-beta109.

APK generado: `app/build/outputs/apk/beta/app-beta.apk`. Copia estable ignorada por Git: `beta/Aura-0.75.0-beta109.apk`. SHA-256: `889B7DFC8474D8CE9909166A04F37DFCF906E9F58AACED731E3E0C502A9E209E`.

Instalación real en Motorola Edge 50 Ultra: beta108 previa, certificado comparado con APK extraído a almacenamiento privado. Certificado compatible `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`. Hash transferido verificado, `pm install -r` devolvió Success. Después se verificaron código 109, nombre 0.75.0-beta109, hash exacto del APK instalado y rol HOME de Aura. No se desinstaló ni borró datos.

Revisión física acotada autorizada: Inicio de Aura, apertura del Centro rápido y arrastre del asa hacia arriba. UI y captura confirmaron cambio de Ampliar a Contraer y aparición de Minimizar. La sesión presente muestra estado genérico y no proporciona portada o metadatos. Se confirmó por lectura que el acceso de notificaciones de Aura está ausente; no se activó. Por tanto, el viaje con imagen real, vídeo, inversión rápida y teclado físico siguen pendientes. Capturas y XML se guardan solo en almacenamiento privado fuera de Git. No se inició contenido ni se accionó transporte.

No se ejecutaron ni añadieron suites automáticas. Se conservan temas, iconos normales, packs y recursos personales. Volumen y controles de reproducción no se modificaron. La activación multimedia posterior tuvo autorización específica; la prueba temporal de miniatura y su restauración se detallan abajo.

La compilación y la revisión source no demuestran fluidez física. La continuidad con reproducción real, vídeo, cambios de contenido y gestos rápidos debe distinguirse de una comprobación estática del panel. No se afirman FPS ni ausencia de fallos sin la evidencia correspondiente.


## Continuación de la revisión física

El usuario autorizó explícitamente activar de forma persistente «Aura · Reproducción multimedia». Se habilitó el componente existente MediaAccessService y se comprobó su aprobación y vinculación. La primera lectura inmediata de Settings todavía no reflejaba el cambio; una comprobación posterior confirmó tanto el permiso como el servicio conectado. No se cambió volumen ni se pulsaron controles de reproducción.

Después llegaron portada, título y progreso reales de YouTube. Se comprobó ampliación por el asa: slot de imagen de 1104×621 px, proporción 16:9; retorno por gesto a compacto, con retirada de los controles extra. Se probó abrir el reproductor ampliado desde su miniatura de Inicio y se inspeccionó su portada asentada. No se ha medido FPS ni registrado cada fotograma del viaje; los estados inicial/final observados no equivalen a certificar toda la interpolación.

Para probar el origen se cambió temporalmente Miniatura de «Oculta» a «Reproductor». Aura salió del primer plano durante la preparación de la restauración. No se siguió pulsando sobre la otra superficie; se solicitó volver a Aura para restaurar «Oculta». Esta restauración queda pendiente hasta confirmarla. Las evidencias privadas pueden contener avisos del sistema y no se publican en GitHub.
