# Beta47 — volumen ampliado y trabajo de scroll reducido
29 de octubre de 2026. 0.33.2-beta47/code47. Rama de validación.

## Alcance
Control de volumen relativo ampliado conservando niveles previos y reglas de silencio, No molestar y reproducción multimedia. Auditoría de trabajo repetido en filas durante desplazamiento; no añadir más efectos antes de resolver coste.

Dos agentes locales trabajan en audio y movimiento; integración y revisión por Codex. No se afirma ejecución en DeepSeek, Antigravity u OpenCode.

## Evidencia previa
Beta46, comprobación manual de dos swipes tras reset de gfxinfo:51 frames,46 janky(90.20%),p50 69ms,p90 89ms. Sesión sin control térmico ni benchmark; no permite atribuir una causa única. Evidencia privada fuera de Git.

## Validación
Pendiente de cambios finales, compilación/lint e instalación compatible. Sin suites automáticas ni nuevas dependencias.

## Cinco propuestas para la siguiente etapa (no implementadas)
1. Escena PS4 coordinada: una única transición dirige foco, luz de fondo y respuesta sonora, sin animaciones concurrentes contradictorias.
2. Mezclador por evento: volumen separado para apertura, selección, menú, favoritas e índice, con muestras y perfiles discretos.
3. Entrada continua: desbloqueo que revela el estado real del launcher y se interrumpe inmediatamente con el gesto del usuario.
4. Reproductor protagonista: carátula y acento coordinados; expansión solo al reproducir, controles accesibles y desplazamiento estable.
5. Estudio de temas: comparación antes/después sobre el Inicio real, historial reversible y variantes sin alterar el original.

Referencias: guía oficial Android de fases de Compose https://developer.android.com/develop/ui/compose/performance/phases ; revisión local UI/UX Pro Max sobre continuidad, duración contextual y movimiento reducido.

## Implementación revisada
Volumen relativo0–300%, presets35/100/250 y muestra. PCM a pico0.66 con SoundPool dividido por300 conserva nivel previo0–100; una voz evita sumar cues. Persistencia/backup ampliados: una versión anterior puede rechazar un respaldo con volumen superior a100. No se cambia el volumen del teléfono.
Foco PS4 calculado una vez por lista (antes por fila), elimina Map de posiciones por frame; omite búsquedas de layout en reposo y pausa controlador fuera de RESUMED. Resorte amortiguado conserva velocidad al cambiar el objetivo. No demuestra eliminación de todos los tirones.

## Entrega verificada
assembleBeta/lintBeta correctos en3m31s,50 tareas(27 ejecutadas/23 al día),0 errores/31 advertencias/3 avisos. Revisión cruzada local sin regresiones concretas. WAV máximo0.659993, compatibilidad de niveles previos verificada por agente en muestras PCM; no escucha física.
Instalada mediante actualización -r en Motorola Edge50Ultra autorizado, firma original compatible y versión/code47/hash posterior verificados. SHA256:84c539046d23e4ddcc67e9cf2158621955ff8333e70537bae42b551177dc22db.
Después de instalar, foco NotificationShade: comparación física posterior pendiente. No se manipuló cortina, volumen, timeout ni permisos. No afirmar fluidez conseguida. Preferencia sonora previa conservada: usuario elige amplificación desde Sonidos y respuesta.
APK local ignorado:beta/Aura-0.33.2-beta47.apk. Sin nueva dependencia y sin suites automáticas.
