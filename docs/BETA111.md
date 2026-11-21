# Beta111 · Centro multimedia continuo

Versión objetivo: `0.77.0-beta111` · código `111` · 21 de noviembre de 2026.

## Cambios

- El Centro rápido sustituye la tarjeta multimedia fragmentada por un hub único: estado compacto de vistazo, carátula, título, progreso y control principal en una sola superficie.
- Deslizar sobre la carátula hacia arriba revela transporte, búsqueda temporal cuando la sesión la admite, abrir fuente y ficha; deslizar hacia abajo lo contrae. El asa del Centro rápido conserva su gesto.
- La carátula usa una señal visual ligada a la posición real de reproducción y se detiene cuando el contenido está en pausa. No añade un bucle decorativo en reposo.
- **Abrir vídeo** delega en YouTube/la aplicación fuente. Aura no incrusta ni reproduce una superficie de vídeo ajena; MediaSession sólo expone metadatos y controles públicos.
- Se conservan contraste alto, TalkBack, las preferencias, el acceso multimedia autorizado y las rutas de iconos/temas existentes.

## Validación

- `assembleBeta lintBeta`: correcto el 21 de noviembre de 2026; 0 errores, 59 advertencias y 4 avisos preexistentes/externos a este hub.
- APK: `app/build/outputs/apk/beta/app-beta.apk`, SHA-256 `BFB35FF506F3F5E911559717120C6BA428B737FEA27B9A5073D95601635D856D`.
- La instalación no se intentó contra el teléfono: el servidor ADB local falló al arrancar (`could not read ok from ADB Server`). Por ello no se afirman versión instalada, hash remoto, revisión física ni una mejora de fluidez medida.

## Límites conocidos

- La disponibilidad de portada, progreso, búsqueda y transporte depende de lo que publique la sesión de cada aplicación.
- El gesto de expansión no puede crear acciones (cola, calidad, subtítulos) que la aplicación origen no haya expuesto.
