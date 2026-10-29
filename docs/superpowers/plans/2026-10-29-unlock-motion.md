# Propuesta y arquitectura de entradas de desbloqueo — 29 octubre2026

Estado: investigación/diseño y contratos preparados. Sin implementación de app ni delegación de inferencia ejecutada. Beta42 sin cambios. Restricción nueva del usuario: consumo nunca superior90% diario.

## Alcance
Entrada de Aura después de autenticación Android, no pantalla de bloqueo alternativa. No exigir segundo slide. No pedir overlays/accesibilidad nueva. No mostrar datos antes de isDeviceLocked=false e isKeyguardLocked=false. No fingir acceso a la huella ni sincronización perfecta con animaciones propietarias del sistema.

## Seis opciones (duraciones propuestas, no medidas)
1. Slide clásico: barrido lateral suave, contenido asentándose,360ms. Referencia iPhone clásico; no atribuir CRT a Apple. Sin gesto adicional obligatorio.
2. CRT: línea tenue central que se expande verticalmente,300ms; sin flash blanco de pantalla completa. Referencia histórica ElectronBeam de Android. Opcional.
3. Fold: revelado desde eje central y profundidad suave,420ms; sin duplicar widgets ni simular sensor real en Motorola no plegable.
4. Orbital: acercamiento del fondo con capas y llegada de contenido,520ms. Inspirado en continuidad de Super Wallpapers MIUI; fondos originales. No prometer transición real desde AOD/Keyguard.
5. Ripple: máscara radial desde centro inferior configurable,360ms, siguiendo revelado Material; no inferir coordenadas del sensor biométrico.
6. PS4 Ribbon: cinta azul cruza fondo, luz se abre y lista aparece con movimiento leve,420ms. Identidad propia para tema PS4; no supuesto desbloqueo original de consola.

## Arquitectura propuesta
UnlockEventCoordinator integrado por director en MainActivity: receiver de SCREEN_OFF/USER_PRESENT mientras proceso existe; verificar Keyguard+RESUMED y ventana visible. Máquina Idle/LockedObserved/Pending/Animating; consumir evento una vez, expirar pendiente tras2s para no animar al regresar de otra app mucho después. Arranque frío sin evidencia de bloqueo no dispara por defecto. No polling ni servicio permanente.
UnlockMotionSpec: enum y parámetros validados, duración200–650ms, intensidad, automático por tema, Sin efecto. Persistencia según almacenamiento existente con valores predeterminados compatibles y cobertura de respaldo.
UnlockTransition: único Home compuesto; separar fondo y contenido, aplicar transformaciones en draw/layer. Animatable finito cancelable; no transformaciones de layout por fotograma. Interacción cancela efecto sin consumir toque. Al parar actividad, descartar evento y estabilizar. Reducir movimiento/animadores desactivados/alto contraste/ahorro: estado final inmediato.
Selector: vista previa con datos ficticios, duración/intensidad y restablecer. Sin audio de desbloqueo por defecto. Efectos independientes de navegación A-Z.

## Asignaciones
DeepSeek Harness: físicas/interpolación, contrato docs/delegation/unlock-2026-10-29/deepseek-harness.md.
Antigravity: storyboards, accesibilidad y auditoría visual, contrato antigravity.md.
OpenCode: renderizadores Compose aislados, contrato opencode.md.
Codex: eventos Android, persistencia, integración, revisión de diffs, compilación/lint y validación física. Ningún cambio entra main sin métricas reales.

## Validación prevista
Revisión estática del ciclo de vida y eventos; assembleBeta/lintBeta. Revisión física: PIN/huella, pantalla apagada/encendida, volver desde app, rotación, teclado, widgets, TalkBack, reducir movimiento, cancelación y ahorro. Medir frames/tiempo con efecto y Sin efecto, misma build/configuración; no afirmar FPS. No modificar ajustes del móvil sin permiso específico. Firma incompatible de app instalada sigue bloqueando instalación de actualización.

## Cuotas y bloqueos verificados
Codex al inicio22%/5h y59%/semana; no contador diario. Antigravity CLI disponible; ayuda no expone contador diario ni max_tokens. OpenCode disponible; stats expone uso local, no denominador contractual global. dsh no encontrado en PATH ni en herramientas revisadas. No concluir ausencia de toda instalación. No se enviaron prompts a estos tres servicios.
Política: no despachar cuota desconocida. Requiere límite diario asignado por proveedor, consumo actual y reserva máxima de ejecución (incluyendo razonamiento). Una tarea a la vez, alarma80%, parada85%, absoluto90%; sin reintentos ni pagos. No prometer control global de consumo de otras sesiones.

## Referencias
https://www.mi.com/sg/support/article/KA-06655/
https://android.googlesource.com/platform/frameworks/base/+/6078b50/services/core/java/com/android/server/display/ElectronBeam.java
https://developer.android.com/reference/android/app/KeyguardManager
https://developer.android.com/develop/ui/views/animations/reveal-or-hide-view
https://github.com/deepseek-ai/deepseek-harness

No hay evidencia comparativa suficiente para llamar a estas opciones las mejor valoradas de toda la industria. Son referencias reconocibles y propuestas cuya UX debe validarse. Skills usadas: brainstorming/Superpowers y UI/UX Pro Max (movimiento reducido, máximo1–2 focos, evitar mareo).

## Corrección de detección local
DeepSeek Harness Desktop confirmado instalado y ejecutándose en C:/Users/LOQ/AppData/Local/Programs/DeepSeek Harness/DeepSeek Harness.exe. La detección inicial estaba limitada por sandbox/PATH; no requiere reinstalación. Accesos directos en Desktop y Start Menu. No se ejecutó inferencia; cuota diaria todavía no verificada.

## Cambio de presupuesto vigente
Usuario retiró el límite interno90% SOLO para DeepSeek. Lo anterior sobre bloquear DeepSeek por falta de cuota diaria queda supersedido; límites del proveedor siguen aplicando. Los otros agentes conservan la restricción. Ver docs/coordination/STATE.json y contratos.
