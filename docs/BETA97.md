# Beta97 · Tres perfiles de desplazamiento

14 de noviembre de 2026. Aura Local 0.67.0-beta97, código 97.

- En Movimiento y efectos → Animaciones de lista, elige un perfil, prueba el desplazamiento en la vista previa y ajusta su intensidad. El perfil previo se conserva al actualizar.
- Inercia elástica usa un frenado exponencial ajustable y entrega la velocidad restante al efecto de borde de Android.
- Foco al aterrizar atenúa un único marco durante el scroll y realza la fila al detenerse. Respeta el foco de teclado o mando.
- Pulso de puntos dibuja una estela junto a los iconos, guiada por el sentido y velocidad del gesto. Se apaga al volver al reposo.
- Los perfiles de lista se aplican a Inicio y búsqueda. Movimiento reducido suspende estos efectos. La opción Sin efecto mantiene el indicador de selección estático del tema PS4.

## Verificación

- `assembleBeta lintBeta` para la variante Local completados. Lint informó 0 errores, 56 advertencias y 3 avisos.
- Actualización inalámbrica con `adb install -r` sobre beta96. Versión, certificado y hash verificados en el Motorola Edge 50 Ultra. SHA-256 `F90D1C86D6C51E9F4FB46E4B964463A28CC8F3E369306A2F9F269483DE61DAFD`.
- Revisión de código realizada. Quedan pendientes la prueba táctil de los tres perfiles y la medición comparativa de fluidez.

## Prueba física del 16 de noviembre

El hash instalado coincide con el de beta97 indicado arriba. En el tema PS4, el Centro rápido reconoció música real y mostró los controles. La ficha expandida mostró la barra de posición. Al asentarse la transición, la carátula cubría parte de la barra y el contador. Se reprodujo con dos pistas. No se confirmó un salto solicitado ni se obtuvo una medición válida de deslizamiento. La corrección está en [beta99](BETA99.md).
