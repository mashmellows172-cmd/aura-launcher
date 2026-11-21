# Centro multimedia compacto — Plan de implementación

## Intención

Reemplazar la tarjeta multimedia extensa del Centro rápido por un único hub de reproducción claro en estado compacto y útil al expandirse. La prioridad es control real de la sesión Android, no una simulación de reproductor.

## Decisión de diseño

Se compararon tres alternativas:

1. Insertar un reproductor WebView de YouTube. Se descarta: duplicaría la reproducción, exige una integración/consentimiento distinto y MediaSession no entrega una superficie de vídeo reutilizable al launcher.
2. Conservar varias tarjetas pequeñas (carátula, controles y ficha). Se descarta: es el origen de la fragmentación y ocupa demasiado alto.
3. Una superficie continua, compacta y expandible por arrastre. Elegida: ofrece un estado de vistazo de ~96 dp, carátula protagonista, controles de transporte reales y alternativas sólo al pedirlas.

Al pulsar **Abrir vídeo** o **Abrir app**, Aura delega en la aplicación fuente. La carátula puede ser reactiva al progreso/reproducción, pero no se presentará como vídeo embebido.

## Alcance

- Rehacer `QuickMediaCard` como una única escena de carátula, título y control principal.
- Un gesto vertical sobre la tarjeta amplía/contrae el hub; el arrastre del asa del Centro rápido continúa siendo válido.
- Mostrar al expandir: barra de progreso accesible, salto temporal sólo cuando la sesión lo declara compatible, pista anterior/siguiente y abrir fuente.
- Animar de forma acotada la carátula con el progreso real de la sesión; sin reloj de animación en reposo.
- Mantener temas, contraste alto, objetivos de toque de 48 dp, TalkBack y la notificación de acceso multimedia ya autorizada.

## Fuera de alcance

- No incrustar ni grabar vídeo de YouTube/otra app.
- No solicitar nuevos permisos ni cambiar ajustes del teléfono.
- No fabricar acciones como cola, letras o calidad si MediaSession no las expone.

## Archivos

- `ui/QuickCenter.kt`: sustituir el diseño legado por el hub compacto/expandido, gesto y controles.
- `app/build.gradle.kts`: versión beta111.
- `CHANGELOG.md`, `docs/CONTINUITY.md`, `docs/BETA111.md`, `docs/coordination/{README.md,STATE.json}`: checkpoint verificable.

## Tareas

1. Simplificar la tarjeta a una superficie con imagen, gradiente, identidad de fuente y progreso.
2. Añadir expansión por gesto y preservar los callbacks de `MediaController` existentes.
3. Revisar contraste, semántica, estado sin portada y disponibilidad de controles.
4. Compilar `assembleBeta lintBeta`; revisar el diff/fuente. No ejecutar suites automáticas.
5. Si ADB sigue disponible, comparar certificados, actualizar con `install -r`, verificar versión/hash y hacer una revisión visual acotada sin iniciar contenido del usuario.
