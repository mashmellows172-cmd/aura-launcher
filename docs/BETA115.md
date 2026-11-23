# Beta115 · Cromo estético PS4

Versión candidata: `0.81.0-beta115` · código `115` · 23 de noviembre de 2026.

## Cambios

- HUD PS4 superior con reflejo fino ligado a la paleta, sin alterar avatar, mensajes, hora, accesos ni gestos existentes.
- Fondo Clásico PS4 con tres curvas retenidas de profundidad; no se añade reloj de animación ni reconstrucción de geometría en reposo.
- Centro rápido y sus controles reciben cristal más definido: reflexión superior, borde de foco y contraste diferenciado para Clásica, Midnight y Vita.
- El foco de app y panel comparte rim, halo y una línea de reflejo; el tratamiento se apaga en alto contraste, intensidad cero y modos de movimiento reducidos.

## Límites y pendientes

- No hay cambio de permisos, dependencias, acciones del sistema ni disposición horizontal de consola.
- La revisión de fuente independiente detectó un reflejo fijo de foco; se retiró para no simular una animación y para no contaminar la vista previa. No quedaron hallazgos P1/P2.
- `assembleBeta lintBeta` terminó tras regenerar de forma recuperable un `R.jar` intermedio bloqueado; no se borró código ni se cambió el teléfono. El APK está en `app/build/outputs/apk/beta/app-beta.apk`; SHA-256 `635323E9F927949C3C63740B121D3320CCD1D109E30653DEF287DDEE420F9420`. El XML de lint contiene 0 elementos `issue`.
- Queda pendiente la comparación de firma con la app instalada, la actualización física y revisión visual/táctil en Motorola.
