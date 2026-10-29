# Beta45 — entradas tras desbloqueo
29 octubre2026,0.33.0-beta45/code45, rama de validación.

Seis renderizadores propios: Slide, CRT suave, Fold, Orbital, Ripple y cinta PS4. Modo automático por tema y Sin efecto; selector desde menú del encabezado, duración200–650ms, intensidad y preview. Preferencia local SharedPreferences: todavía fuera del respaldo JSON existente.
Solo entrada de Aura después del desbloqueo Android; no sustituye Keyguard, PIN ni biometría. Evento USER_PRESENT observado tras pantalla apagada, guardas Keyguard/RESUMED/foco, vencimiento2s. Inicio frío sin evento observado no anima. No se infieren coordenadas del sensor ni postura plegable. Touch cancela sin consumir el evento. Sin movimiento con ahorro/alto contraste/reduceMotion/animadores deshabilitados. Un Home compuesto, paths reutilizados y estado leído en draw/layer.
Incluye fuente beta44 multimedia activo/compacto. Duraciones/diferenciación son diseño propio, no fidelidad propietaria100%. Sin métricas FPS o batería. Instalación sigue bloqueada por certificado incompatible del paquete existente: no desinstalar ni borrar datos.

Validación final: assembleBeta/lintBeta correctos en3m14s,50 tareas (11 ejecutadas/39 cacheadas). Lint0 errores/31 advertencias/3 avisos; advertencia nueva UseKtx recomienda una extensión de SharedPreferences, no fallo funcional. Revisión estática independiente completada y correcciones confirmadas; sin suites automáticas. No se ejecutaron DeepSeek/Antigravity/OpenCode para estos cambios; son implementación local Codex.
Usuario retiró también el límite interno90% de Codex antes de reanudar; límites del proveedor siguen vigentes. No pagos.

## Referencias consultadas y decisiones
https://github.com/android/compose-samples
https://github.com/android/skills/blob/main/jetpack-compose/jetpack-compose-performance/references/animation.md
https://github.com/android/performance-samples
https://developer.android.com/develop/ui/compose/animation/quick-guide
No se instalaron paquetes nuevos: las APIs Compose/lifecycle existentes cubren animación finita y draw/layer. No se copiaron recursos propietarios ni se ejecutó Macrobenchmark sin preparación física.

Revisión independiente final: corregidos encoding UTF-8, intensidad0%, vista previa automática y repetición por reanudación. Confirmación por fuente, no validación visual. Autonomía29oct hasta13:00 Santiago: terminar bloque actual antes del plazo, sin tareas de relleno.

APK local: beta/Aura-0.33.0-beta45.apk. SHA256:250ad04b47b4d228c1f0d03253f404f81fbd4ee6fbe97691cdf7d99ca679c51c. Firma original verificada:729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c. NO instalado; revisión física pendiente. Cierre del bloque antes13:00, sin promover a main.
