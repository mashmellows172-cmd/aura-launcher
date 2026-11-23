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
- `git diff --check` no informó errores de espacios. No hay resultado completo de `assembleBeta lintBeta`: la sesión Windows interrumpió Gradle antes del empaquetado/lint final y un `R.jar` intermedio estuvo bloqueado al intentar la limpieza. El archivo intermedio se regeneró de forma recuperable; no se borró código ni se cambió el teléfono.
- Quedan pendientes compilación/lint completos, APK e instalación física en Motorola.
