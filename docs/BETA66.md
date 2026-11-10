# Beta66 · iconos dinámicos del clima PS4

10 de noviembre de 2026. 0.45.0-beta66, código 66. Base beta65 `d42cd0a`.

La cabecera PS4 muestra ocho iconos de relieve plateado, contorno frío y halo azul basados en la referencia visual entregada. Los dibuja Compose con trazos, formas y degradados. La imagen de referencia permanece fuera del repositorio.

| Evento | Animación |
| --- | --- |
| Soleado | El halo se expande y los rayos giran suavemente. |
| Nublado | La nube se eleva y se desliza antes de asentarse. |
| Lluvia | Los trazos caen en diagonal y se desvanecen. |
| Tormenta | Aumenta gradualmente el brillo local del rayo. |
| Nieve | Dos copos descienden y giran a ritmos distintos. |
| Viento | Tres ráfagas curvas se desplazan con desfase. |
| Niebla | Cinco bandas onduladas derivan en secuencia. |
| Noche despejada | La luna se eleva y las estrellas cambian suavemente de brillo. |

La cabecera usa la condición descrita por el proveedor y conserva su símbolo si el texto falta o no se reconoce. Viento y noche requieren una descripción explícita. La temperatura y la ciudad siguen siendo las del widget. Los ajustes manuales de escena conservan su efecto decorativo independiente.

En **Menú rápido → Clima en el reloj → Ver iconos y animaciones PS4** se pueden reproducir los ocho eventos con datos de ejemplo. En Inicio, el movimiento dura entre 3 y 5 segundos según los ajustes y termina en una silueta estática. El efecto automático se dibuja dentro del icono. El ahorro de energía, el alto contraste, el movimiento reducido y la desactivación de animaciones del sistema mantienen el icono estático. No se añadieron dependencias ni imágenes al APK.

## Validación

Revisión de código con Frontend Design, Animate/Impeccable, Accessible Animation, Ponytail y Verification Before Completion. `assembleBeta` y `lintBeta` completados en 56 s. Lint registró 0 errores, 35 advertencias y 3 avisos. `aapt` confirmó el código 66 y `apksigner verify` confirmó la firma del APK local. La instalación, la inspección visual y la medición de fluidez en el Motorola quedan pendientes.
