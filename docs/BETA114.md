# Beta114 · Sistema visual Sony

Versión candidata: `0.80.0-beta114` · código `114` · 22 de noviembre de 2026.

## Cambios

- Foco visual unificado: la lista PS4, el Centro rápido y sus superficies usan el mismo rim/halo de la paleta seleccionada. Clásica conserva azul, Midnight usa foco blanco frío y Portable/Vita usa cian eléctrico.
- Fondo de profundidad refinado: el lienzo PS4 combina gradiente profundo y bloom retenido; las motas responden a foco/tacto y no crean un ciclo de partículas en reposo.
- Materiales claramente diferenciados: vidrio azul lineal para Clásica, placas oscuras compactas para Midnight/PS5 y brillo cian con trama contenida para Portable/Vita.
- Tipografía Sony ajustada mediante Noto Sans libre ya incluida: PS5 y Portable separan de forma más legible HUD, etiquetas, contenido y título activo, sin reemplazar las preferencias tipográficas personales.
- La continuidad de selección sigue el color y la luz de origen hasta paneles y tarjetas. Se respetan ahorro de energía, reducir movimiento, contraste alto y animaciones del sistema.

## Revisión Antigravity

- Revisión visual externa en modo lectura aplicada a los archivos de foco, fondo, tipografía y Centro rápido.
- Confirmó no restaurar el menú horizontal de consola: Aura conserva lista vertical, buscador, A–Z y widgets.

## Validación local

- `gradlew.bat assembleBeta lintBeta`: comprobación final `BUILD SUCCESSFUL` el 22 de noviembre de 2026, 1m22s, 50 tareas (12 ejecutadas, 38 al día).
- XML de lint beta: 0 elementos `issue`.
- APK: `app/build/outputs/apk/beta/app-beta.apk`.
- SHA-256: `C210C8319A7ED0794265951E8D7D6A0228DF4CADBD51E32D3E7AEE27D2325410`.

## Límites y pendientes

- Beta114 no fue instalada ni validada visualmente en el Motorola durante esta entrega.
- No se declara medición de fluidez, FPS ni consumo; las capas nuevas se limitaron a rutas de dibujo ya existentes y sin temporizadores ambientales.
