# Beta93 · Centro rápido y acentos PS4

14 de noviembre de 2026. Aura Local 0.65.1-beta93, código 93.

- Actividades sale de Inicio y del menú Compartir de Android. Los enlaces guardados por beta92 se conservan en sus preferencias privadas.
- El color dominante se calcula desde el icono de la app tras estabilizarse la selección. El marco, la cabecera y el botón del Centro rápido comparten la transición. El panel usa ese acento en el borde, el asa y el progreso multimedia. En alto contraste usa los colores del tema y en movimiento reducido cambia el acento sin transición.
- El reproductor del Centro rápido ofrece un control visible para ampliar y contraer. En pantallas bajas o con texto grande, los accesos entran en el desplazamiento de las tarjetas y dejan más espacio al contenido.
- Compilación `assembleBeta` y `lintBeta` correctos para la variante Local. Lint informó 0 errores, 56 advertencias y 3 avisos.
- Actualización inalámbrica con `adb install -r` desde beta92 en el Motorola Edge 50 Ultra. Firma, versión, hash y ausencia del filtro de Compartir verificados. SHA-256 `553DCA0F8E2F97166F4A5F117FE54904CA25FE2E634756775CD6E6988F56533F`.
- Pendiente de revisión visual en el Motorola con iconos de varios colores, texto grande y orientación horizontal.
