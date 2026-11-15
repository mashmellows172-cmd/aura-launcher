# Beta95 · Accesos fijos y vista previa interactiva

14 de noviembre de 2026. Aura Local 0.66.0-beta95, código 95.

- El Centro rápido separa las tarjetas desplazables de los accesos inferiores. En una pantalla baja, con texto grande o con el teclado abierto, la fila se puede plegar. Con muy poca altura, el botón abre los accesos en un menú.
- En el editor, «Probar Inicio» amplía la vista con las apps, favoritas y el fondo del tema en edición. Se puede desplazar la lista y usar A–Z. En PS4 también se puede seleccionar una app para probar su color y abrir el Centro rápido de muestra.
- La reproducción de la vista previa es simulada y los widgets externos se representan mediante marcadores pasivos. La muestra del Centro rápido no cambia preferencias ni inicia acciones del sistema.
- El editor permite ajustar la intensidad del acento PS4 entre 0 y 100 y la protección de lectura del fondo. El valor se incorpora a variantes y respaldos, con 100 como valor predeterminado para datos anteriores.

## Verificación

- `assembleBeta lintBeta` para la variante Local completados. Lint informó 0 errores, 56 advertencias y 3 avisos.
- Actualización inalámbrica con `adb install -r` sobre beta94 en el Motorola Edge 50 Ultra. Versión, certificado y hash del APK instalado verificados. SHA-256 `21D0E073655368EA7D452DD9713ED1CAFC327E6E3BD2CC7F0697FE3502971499`.
- La revisión interactiva queda pendiente. Android no devolvió el árbol de la interfaz durante la comprobación por ADB.
