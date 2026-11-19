# Beta106 · YouTube en el Centro multimedia

18 de noviembre de 2026. Aura 0.72.0-beta106, código 106, paquete `dev.senda.launcher.debug`. Base beta105 `d46e4e7`, rama `codex/beta106-youtube-center`.

## Integración

- YouTube tiene una cabecera propia en Centro rápido y ficha multimedia. Miniatura completa con ajuste Fit, formato panorámico ampliable en 240 ms, título, subtítulo o canal comunicado por el proveedor, estado confirmado y acción Ver vídeo.
- YouTube Music y los otros reproductores conservan la presentación musical. La identificación usa el paquete exacto de YouTube.
- Saltos −10/+10 segundos disponibles en tarjeta compacta, ampliada y ficha cuando la sesión expone duración, posición y SEEK_TO. Los límites, solicitud pendiente y confirmación del proveedor se comparten con el slider. Durante carga se deshabilita el transporte que el monitor no acepta.
- Se recuperan sesiones pausadas con metadatos y capacidad de reanudar al volver a Aura si no hay otro audio musical activo. Se mantiene preferencia por la sesión actual válida. Anterior/siguiente admite pausa si el proveedor declara la acción.
- Estados Buffering/Connecting se muestran como Cargando. No se deduce una emisión en directo por carecer de duración.
- Metadatos prefieren subtítulo de presentación y el arte del contenido. La imagen conserva su proporción y se limita a 768 px en YouTube, 384 px en otros reproductores. Si solo llega una URI content local, se decodifica con tamaño acotado y asignación software. No se descargan miniaturas por HTTP.
- Un cambio de metadatos del mismo contenido conserva la miniatura mientras se carga su reemplazo. El ID del contenido reinicia los estados de búsqueda al cambiar de vídeo aunque el título coincida.
- Ver vídeo usa primero la actividad de la sesión. Si falta, admite una URI HTTPS de contenido con host YouTube conocido y restringe el intent al paquete de YouTube; finalmente abre la aplicación.

La imagen de Aura es una miniatura. La reproducción de vídeo continúa en YouTube. Las funciones disponibles dependen de los metadatos y acciones de su sesión. No se implementaron capítulos, cola, calidad ni permisos adicionales. Los cambios de selección pausada y lectura de metadatos también se aplican a otras sesiones.

## Arquitectura y referencias

Se reutilizan MediaController/PlaybackState, transporte, confirmación de seek y tintes de portada existentes. Sin nuevas dependencias ni tráfico de vídeo desde Aura. UI/UX Pro Max se aplicó a objetivos táctiles de 48 dp y estructura adaptable. Systematic Debugging guio la selección pausada y la revisión independiente contrastó controles y contraste.

Referencias oficiales consultadas el 18 de noviembre de 2026. [MediaController](https://developer.android.com/reference/kotlin/android/media/session/MediaController.html) documenta la actividad y transporte de la sesión. [MediaMetadata](https://developer.android.com/reference/android/media/MediaMetadata) documenta subtítulo, arte y URI del contenido.

## Verificación

- Compilación y lint finales `assembleBeta lintBeta` correctos en 1 min 13 s. Lint con 0 errores, 54 advertencias y 4 avisos. `git diff --check` correcto.
- Revisión independiente de fuente encontró dos P2, saltos activos durante carga y contraste de canal/apertura con paleta PS4 blanca. Ambos se corrigieron. Segunda revisión sin otros defectos importantes en los ajustes revisados.
- No se añadieron ni ejecutaron suites automáticas.
- APK en `app/build/outputs/apk/beta/app-beta.apk`, copia estable local `beta/Aura-0.72.0-beta106.apk`. SHA-256 `f949bcc03fbf7930fa53f01ac92e4e7177934fac1f72b5d17d7a6838ccb8d383`. Firma verificada `729385fc…`.
- Candidata inicial bloqueada por firma incompatible con beta104. La reinstalación autorizada posterior y la restauración verificadas están registradas al final de esta ficha.

## Pendientes

Probar sesión real YouTube, vídeos verticales, pausa al volver a Aura, carga, múltiples reproductores, seek confirmado, URI de miniatura, contraste y fuentes grandes en el Motorola. Compilación y revisión de fuente no certifican funcionamiento físico ni mejora de FPS. Se necesita la clave compatible para actualizar conservando datos.

## Comprobación de instalación solicitada

El 18 de noviembre de 2026 se reconectó el Motorola Edge 50 Ultra autorizado por ADB inalámbrico y se extrajo el APK instalado para comparar certificados. Android confirmó beta104, 0.70.0-beta104, código 104. El certificado instalado sigue siendo 2eb417c5… y beta106 está firmada con 729385fc…. La comparación canceló la actualización antes de ejecutar install -r. No se desinstaló ni se modificaron datos, permisos o ajustes. La evidencia y el APK anterior quedan fuera de Git. Instalación pendiente de firma compatible o de una migración con respaldo verificado y autorización específica.

## Migración autorizada e instalación completada

El 18 de noviembre de 2026, el usuario indicó «resintala» y desbloqueó el Motorola para preparar y restaurar el respaldo. Se exportó la configuración actual desde beta104 antes de desinstalar. La exportación contenía 111 ajustes, 9 favoritas y 5 imágenes, sin omisiones. Se comprobó el JSON y las imágenes PNG y se conservaron fuera de Git el respaldo actual, la biblioteca retro y el APK original de recuperación.

Se desinstaló beta104 y se instaló beta106 con el mismo paquete dev.senda.launcher.debug. Android confirmó 0.72.0-beta106, código106. El APK instalado se extrajo y su SHA-256 coincidió con el candidato f949bcc03fbf7930fa53f01ac92e4e7177934fac1f72b5d17d7a6838ccb8d383. La firma vigente empieza por 729385fc; una copia de la clave local quedó en la carpeta privada, fuera de Git. Las próximas entregas deben conservar esa firma.

Aura validó el respaldo y confirmó «Ajustes restaurados». Una segunda exportación, guardada con nombre diferente, coincidió en todos los 111 ajustes y en las 5 imágenes byte a byte, con 9 favoritas y cero omisiones. Se restauró el rol HOME de Aura según la preferencia autorizada del usuario y Android lo confirmó.

La biblioteca retro ZIP está preservada en Descargas y en el respaldo privado; falta importarla en la instalación nueva. Los widgets y permisos especiales requieren reconexión. El JSON no incluía variantes del editor guardadas en Room, notas del Centro rápido ni temporizadores; no se verificó si existían antes de desinstalar y no se declaran recuperados. La biblioteca no se importó mientras el usuario estaba usando WhatsApp. No se concedieron permisos especiales.

Quedan pendientes la prueba de YouTube real y la revisión visual de las mejoras. La comprobación física realizada aquí cubre instalación, importación, equivalencia del respaldo y rol HOME.
