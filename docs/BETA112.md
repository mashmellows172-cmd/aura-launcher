# Beta112 · Centro rápido progresivo

Versión objetivo: `0.78.0-beta112` · código `112` · 21 de noviembre de 2026.

## Cambios

- El Centro rápido abre en una única fila horizontal de cápsulas: Música, Temporizadores, Alarma, Agenda, Nota cuando corresponde y Accesos.
- Un toque selecciona una cápsula; deslizar hacia arriba sobre ella revela sólo su detalle. Un deslizamiento hacia abajo la contrae. El asa superior continúa controlando el panel entero.
- Las herramientas (linterna, calculadora y Gemini) pasan a **Accesos**: ya no compiten con el contexto ni ocupan una segunda fila al abrir.
- El reproductor no se monta ni tapa el panel hasta expandir **Música**. Su estado ampliado conserva carátula reactiva, transporte, búsqueda temporal cuando MediaSession la permite, ficha y apertura de la fuente.
- YouTube usa saltos simétricos de −10/+10 segundos al estar disponibles; el resto de sesiones mantiene −10/+30. No se incrusta vídeo ni se fingen acciones que la sesión no expone.
- Revisión de fuente independiente aplicada: Música sin sesión mantiene la acción de abrir, la portada se desmonta con su tarjeta, fijados conservan prioridad, la nota guardada reaparece y la fila lleva la cápsula seleccionada a la vista. Con texto grande la cápsula puede crecer verticalmente.

## Validación

- `assembleBeta lintBeta`: correcto el 21 de noviembre de 2026; 0 errores, 59 advertencias y 4 avisos existentes.
- APK: `app/build/outputs/apk/beta/app-beta.apk`, SHA-256 `AB352DF56FB661F011215828F92E8A405C0EEA3298F733ACEB0E713D36916E2A`.
- ADB detectó `motorola edge 50 ultra`; certificado local e instalado coincide (`729385fc…202a67c`). Se actualizó con `pm install -r` sin desinstalar. Android confirma `0.78.0-beta112`/código 112, hash instalado `AB352DF56FB661F011215828F92E8A405C0EEA3298F733ACEB0E713D36916E2A` y Aura como Inicio.
- El primer comando usó por error una ruta del teléfono con `adb install`, que sólo acepta una ruta local; devolvió `failed to stat` y no modificó la aplicación. Se corrigió con `adb shell pm install -r` tras volver a comparar hash. No se cambió ningún permiso ni ajuste.
- No se realizó revisión visual/táctil del rail ni se afirma mejora de fluidez medida.

## Límites

- La fila puede desplazarse horizontalmente si el tamaño de fuente o la pantalla no permite mostrar todas las cápsulas.
- La disponibilidad de carátula, progreso, controles y búsqueda sigue dependiendo de la sesión multimedia pública de cada aplicación.
