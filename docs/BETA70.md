# Beta70 · controles multimedia adaptables

11 de noviembre de 2026. 0.48.1-beta70, código 70. Base beta69 `1be4460`.

La revisión física de beta69 encontró nombres partidos en la muestra multimedia de Inicio y gestos. El reproductor ahora usa el ancho real de su tarjeta y la escala de texto para decidir su distribución. En espacios estrechos muestra controles con iconos y descripciones accesibles, una carátula de 72 dp y un título compacto. En espacios amplios mantiene nombres en una sola línea y la carátula de 98 dp.

Los controles declaran explícitamente su estado desactivado. Se aumentó moderadamente la legibilidad de las cintas de entrada, que siguen siendo finitas.

## Validación

La pasada de `assembleBeta` y `lintBeta` completó en 2 min 16 s con un trabajador. Lint registró 0 errores, 40 advertencias y 3 avisos. Firma local compatible verificada. Instalación sobre beta69 mediante `install -r`. Android confirmó 0.48.1-beta70 y código 70. SHA-256 instalado coincidente con el APK local.

`9769bf096dda3ed5f19f2fdd850d89ebbc91cda5da8ce32d2e7e3275803066de`

En el Motorola se abrió la muestra dentro del panel estrecho. Se observaron la carátula compacta, el título y los tres controles con iconos. Los nombres partidos desaparecieron. Los nodos padres de los tres botones se identificaron como desactivados. Se cerraron los ajustes y se dejó Inicio visible.

La revisión del clima y la persistencia/restauración de tamaños están registradas en docs/BETA69.md. Permanecen pendientes los controles con una sesión musical real, las otras paletas, los cambios horarios de iluminación y nuevas métricas de fluidez. Las capturas privadas permanecen fuera de Git.
