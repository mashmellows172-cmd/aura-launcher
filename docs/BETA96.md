# Beta96 · Rendimiento de animaciones PS4

14 de noviembre de 2026. Aura Local 0.66.1-beta96, código 96.

- Inicio PS4 usa el motor del fondo y su propio marco de selección. El controlador genérico de scroll deja de ejecutarse en el tema PS4. Los otros temas lo activan según sus ajustes.
- La base del fondo se pinta una vez, bajo la capa de ondas. El halo reutiliza las máscaras de sus degradados al cambiar de color.
- El reproductor conserva el tamaño ampliado de la portada. La transición de altura afecta al bloque de controles desplegables. El progreso compacto se dibuja en su propia superficie y el contador se suspende cuando Aura pierde el estado activo.

## Verificación

- `assembleBeta lintBeta` para la variante Local completados. Lint informó 0 errores, 56 advertencias y 3 avisos.
- Actualización inalámbrica con `adb install -r` sobre beta95. Versión, certificado y hash verificados en el Motorola Edge 50 Ultra. SHA-256 `120B34132C3C4BC2B4CB1CD904C7849B47E6E2E3C1EDE92C74CED66B59535ED5`.
- Muestra de referencia de beta95 con seis desplazamientos en Inicio y contadores `gfxinfo` reiniciados: 700 fotogramas, 5 marcados con tirones, P50/P95/P99 de 12/17/20 ms. Muestra acotada, sin suites automáticas.
- En beta96 se comprobó visualmente el render de Inicio y el acento. Android no devolvió la jerarquía en el intento comparativo y había un video flotante visible. Quedan pendientes una muestra equivalente de beta96 y la medición del reproductor.
