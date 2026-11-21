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
- APK: `app/build/outputs/apk/beta/app-beta.apk`, SHA-256 `07170BABE1E89507931F7F3C924FDFF3F3A0052E79CAADE8887BE0D7772AD966`.
- No se intentó instalar ni abrir el panel en el Motorola: ADB local seguía sin iniciar su servidor, por lo que no se afirman versión remota, revisión visual ni mejora de fluidez medida.

## Límites

- La fila puede desplazarse horizontalmente si el tamaño de fuente o la pantalla no permite mostrar todas las cápsulas.
- La disponibilidad de carátula, progreso, controles y búsqueda sigue dependiendo de la sesión multimedia pública de cada aplicación.
