# Fondo reactivo PS4 · Plan de implementación

> Para ejecutar este plan por tareas, usar subagent-driven-development o executing-plans. Aplicar las restricciones de AGENTS.md a la validación.

**Estado** Propuesto. 11 de noviembre de 2026. Base beta70, commit `44c542e`. Este documento especifica trabajo futuro.

**Objetivo** Hacer que las cintas PS4 respondan al uso de Aura con movimiento suave, profundidad y continuidad, manteniendo la cadencia de la lista y la legibilidad.

**Arquitectura** Un coordinador de movimiento observa acciones existentes y produce un estado acotado para el fondo. El renderizador reutiliza geometría y degradados y consume el estado durante dibujo o transformación. Las animaciones de paneles y desbloqueo conservan una única fuente de progreso.

**Tecnología** Kotlin y Jetpack Compose existentes, graphicsLayer, drawWithCache, Animatable, ciclo de vida y observación de scroll.

**Especificación** Las decisiones visuales, prioridades y límites de este documento constituyen la especificación inicial.

## Punto de partida comprobado

- `Ps4LiveWallpaper.kt` contiene `staticWallpaper = true`. Su reloj decorativo está detenido.
- `focusLight` recibe eventos, pero el dibujo utiliza `light = 0f`. Esa reacción no aparece en pantalla.
- El limitador anterior omite actualizaciones inferiores a 16 ms. Si se reactivase, a 90 Hz podría actualizar aproximadamente cada dos cuadros, con una cadencia próxima a 45 actualizaciones/s. Es una inferencia del código, pendiente de medición.
- La geometría ya se prepara en caché y la capa usa transformaciones retenidas. Conviene conservar esa base.
- Existen señales de foco, scroll, desbloqueo, búsqueda y apertura de paneles. Deben reutilizarse.
- La medición previa de beta68 produjo contadores y frecuencias discrepantes. Se necesita medir presentación efectiva antes de aprobar una frecuencia objetivo.

## Dirección visual

Las cintas son el elemento protagonista. Se desplazan con inercia corta y cambian su relieve al interactuar. La luz tiene entradas y salidas progresivas, con intensidad limitada y contraste protegido. Los textos y objetivos táctiles mantienen sus posiciones.

Los siguientes valores son puntos iniciales de ajuste.

| Acción | Reacción propuesta | Duración o límite inicial |
| --- | --- | --- |
| Arrastre de lista | Desplazamiento suave de cintas ligado a la dirección y velocidad del gesto | Hasta 6 dp, con asentamiento de 220–320 ms |
| Foco por teclado o mando | Una cresta se orienta hacia la fila enfocada | 220–350 ms |
| Pulsación de aplicación | Ligera compresión y desplazamiento de luz hacia la zona pulsada | 100–180 ms; apertura inmediata |
| Foco del buscador | Luz tranquila detrás de la búsqueda y menor actividad del resto del fondo | 250 ms al entrar, 180 ms al salir |
| Apertura de menú, ficha o clima | Fondo y panel comparten una transición de profundidad | Progreso del panel existente, 220/160 ms |
| Favorita confirmada | Una cinta se eleva ligeramente y vuelve a su posición | Un recorrido de 350–450 ms |
| Desbloqueo con Cinta PS4 | Apertura del fondo coordinada con el reloj | Progreso de UnlockEntry y preferencias guardadas |

Durante un arrastre, los cambios de la fila más cercana no disparan confirmaciones sucesivas. El foco por scroll puede reaccionar al asentarse durante 150 ms. Cada letra escrita mantiene la escena de búsqueda vigente.

## Perfiles y controles

- **Estático** conserva la referencia para comparar rendimiento.
- **Reactivo** añade respuestas finitas y vuelve al reposo. Es el perfil recomendado para la primera entrega.
- **Ambiental** añade una deriva lenta de 24–40 s mientras Inicio está visible. Se habilita después de superar la comparación de rendimiento.

En Movimiento y efectos se proponen perfil e intensidad. La intensidad ajusta las amplitudes dentro de los límites de la tabla. Movimiento reducido, alto contraste y ahorro de energía mantienen una composición estable y señales locales compatibles con sus ajustes. La elección Sin efecto para desbloquear sigue siendo válida.

Las imágenes personales conservan su recurso. En ellas, la respuesta se limita a transformación y sombreado moderados del recurso existente.

## Prioridades e interrupciones

1. La entrada táctil y el scroll conservan prioridad.
2. Una transición modal suspende el movimiento ambiental y reutiliza el progreso del panel.
3. Una nueva pulsación sustituye la reacción transitoria desde su valor actual.
4. El desbloqueo usa el progreso ya disponible y se cancela al perder visibilidad.
5. Al ocultarse Inicio se detiene el coordinador y se descartan reacciones transitorias.

El estado describe profundidad, desplazamiento, luz y energía. No se crea una cola creciente de efectos. Los objetivos se combinan con prioridades explícitas y amplitudes máximas. Las transformaciones de reposo deben cubrir los bordes del área visible.

## Archivos e interfaces

Las rutas siguientes son relativas a `app/src/main/java/dev/senda/launcher/`.

- Crear `ui/Ps4WallpaperMotion.kt` para el coordinador, en el paquete de UI existente.
- Modificar `ui/Ps4LiveWallpaper.kt` para conectar el estado al dibujo y reemplazar el interruptor fijo por el perfil.
- Modificar `ui/HomeScreen.kt` para enlazar scroll, foco, búsqueda y pulsación sin consumir sus gestos.
- Reutilizar `ui/Ps4PanelMotion.kt` y `ui/UnlockEntry.kt` como fuentes de progreso.
- Añadir preferencias acotadas en `data/HomeSettings.kt`, `FavoritesStore.kt` y `SettingsBackup.kt`, y controles en `VisualSettingsDialog.kt`.

Interfaces propuestas para el coordinador

```kotlin
enum class Ps4WallpaperMode { STATIC, REACTIVE, AMBIENT }
data class Ps4WallpaperSettings(
    val mode: Ps4WallpaperMode = Ps4WallpaperMode.REACTIVE,
    val intensity: Int = 50,
)
```

El estado mutable del coordinador contiene desplazamiento en píxeles, profundidad y posición de la luz normalizada. Su observación queda en las lambdas de dibujo o graphicsLayer. Las señales de panel y desbloqueo se leen como State<Float> existentes. Antes de persistir, la intensidad se acota a 0..100 y los perfiles desconocidos recuperan REACTIVE.

Las primeras reacciones usan transformaciones y luz sobre la geometría retenida. Para conseguir una ondulación orgánica se evalúa después deformar una sola cinta activa con los buffers FloatArray y Path de ConsoleRibbon. Las demás cintas conservan su geometría. Esta variante se compara por separado con la transformación y solo se conserva si cumple el presupuesto.

## Etapas de entrega

- [ ] **1. Establecer la referencia.** Comparar scroll y paneles en beta70 con captura de presentación. Correlacionar frecuencia efectiva, carga y temperatura, y repetir tres veces el mismo recorrido. Entregar un informe con límites.
- [ ] **2. Entregar Reactivo.** Conectar arrastre, foco y asentamiento con un coordinador. Revisar inversión rápida, fling, listas cortas y retorno desde otra aplicación. Comparar con Estático antes de integrar más reacciones.
- [ ] **3. Integrar búsqueda y paneles.** Reutilizar sus estados y progresos. Revisar abrir/cerrar rápidamente, teclado visible, búsqueda vacía, bloqueo durante la transición y opciones de accesibilidad.
- [ ] **4. Añadir respuestas de confirmación y desbloqueo.** Coordinar favoritas y Cinta PS4 con sus preferencias. Comprobar que la aplicación responde al primer toque y que no se retrasa el lanzamiento.
- [ ] **5. Evaluar Ambiental.** Añadir deriva de baja amplitud solo tras aprobar Reactivo. Comprobar pausa al cambiar de app y retorno continuo al iniciar otro gesto.
- [ ] **6. Cerrar la entrega.** Compilar y ejecutar lint. Revisar el Motorola, registrar evidencia y límites, actualizar CHANGELOG, BETA, MASTER y CONTINUITY en el commit de entrega y verificar instalación y hash.

Cada etapa debe poder aceptarse o rechazarse por separado. La primera entrega pública puede terminar en la etapa 3 si las siguientes necesitan más validación.

## Presupuesto y aceptación

Son objetivos de ingeniería que requieren medición.

- Un coordinador y una única fuente de tiempo para el fondo.
- El perfil Reactivo deja de producir cuadros adicionales cuando termina el asentamiento.
- La frecuencia de actualización sigue el reloj de pantalla durante una reacción activa. La calidad se ajusta reduciendo trabajo, capas o amplitud.
- Geometría y degradados reutilizados. Las transformaciones del fondo no deben causar recomposición o relayout de las filas.
- Objetivo inicial de coste añadido del fondo inferior a 0.8 ms por cuadro activo, memoria Java adicional inferior a 4 MiB y aumento del APK inferior a 250 KiB.
- En condiciones comparables, P95 de duración de cuadro con un incremento máximo de 1 ms frente a Estático y cuadros presentados tarde con un incremento máximo de un punto porcentual. Si la referencia ya incumple el presupuesto de la frecuencia efectiva, se corrige esa cadencia antes de aprobar Ambiental.
- Pulsaciones, apertura con un toque, A–Z, teclado y desplazamiento conservan su respuesta.

A 90 Hz el intervalo es aproximadamente 11.11 ms y a 120 Hz, 8.33 ms. Los percentiles se interpretan junto con las fechas de presentación y la frecuencia efectiva. Se registran tanto los contadores actuales como los legacy de gfxinfo cuando discrepen.

La captura propuesta usa FrameTimeline, que distingue tiempos esperados y reales de presentación. La lectura diferida de estado se apoya en las fases de Compose. [FrameTimeline](https://perfetto.dev/docs/data-sources/frametimeline), [fases y rendimiento de Compose](https://developer.android.com/develop/ui/compose/performance/phases).

## Revisión y delegación

Ollama puede proponer curvas, rangos y pseudocódigo acotado para una reacción cada vez. El coordinador revisa las APIs y compara los candidatos con el código real antes de integrar. Los modelos locales anteriores produjeron sintaxis inválida, por lo que cada candidato debe pasar esa revisión.

AGENTS.md establece compilación, lint y revisión visual/source. No se añaden ni ejecutan suites automáticas sin nueva petición expresa. Las trazas y capturas privadas del Motorola permanecen fuera de Git.

## Ejecución beta71 · 11 de noviembre de 2026

La fuente implementa las reacciones finitas de las etapas 2–4: arrastre, foco asentado, pulsación, búsqueda, paneles y confirmación, conservando el desbloqueo existente. Preferencias añadidas a VisualSettings, que ya alimenta Movimiento y efectos y el respaldo; no se creó una segunda estructura de ajustes. Se ofrecen solo Estático y Reactivo. Ambiental no se habilita ni se simula como disponible.

Las casillas anteriores incluyen aceptación física: permanecen pendientes mientras no exista la comparación y revisión del Motorola. La referencia de beta70 se abortó antes de capturar al perder primer plano. El estado verificable de compilación, instalación y pendientes se registra en docs/BETA71.md. El primer release no implica que el presupuesto de rendimiento esté acreditado.
