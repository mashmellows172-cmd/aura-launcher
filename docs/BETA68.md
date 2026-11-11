# Beta68 · acabado de la interfaz PS4

11 de noviembre de 2026. 0.47.0-beta68, código 68. Base beta67 `4accab7`.

La cabecera agrupa reloj, fecha y clima sobre una superficie común. El reloj principal pasa a 48 sp, el calendario tiene un símbolo de 18 dp y un separador organiza las dos lecturas. Se conserva una distribución compacta para ventanas pequeñas y texto ampliado.

Las tarjetas de información y reproducción, la búsqueda y las placas de favoritas usan un acabado común con reflejo superior y borde inferior. En las superficies interactivas la luz responde al toque y al foco. Los iconos de ajustes, búsqueda, calendario, alarmas, detalles y transporte multimedia comparten un sombreado plateado adaptable a las paletas clásicas, Medianoche y Blanco.

Las fichas de aplicaciones conservan la expansión desde la placa y usan una curva de salida coherente con su entrada. La respuesta al pulsar usa la curva común. Fichas, menú y placas respetan el ahorro de energía. La geometría y los degradados de los controles se preparan en caché, con lectura de estado durante el dibujo.

## Delegación y revisión

[Encargo y resultado de Ollama](coordination/BETA68-OLLAMA.md). La integración incluye correcciones de los candidatos locales y una revisión de APIs. El primer intento de compilación detectó controles ausentes del paquete de iconos disponible. Se reemplazaron por trazados concretos.

## Validación y pendientes

Compilación y lint finales completados en 1 min con 0 errores, 37 advertencias y 3 avisos. El APK local superó `apksigner verify` y su versión se comprobó con `aapt`.

Se actualizó el Motorola por ADB inalámbrico con `install -r`. Android confirmó 0.47.0-beta68, código 68. El SHA-256 instalado coincide con el local.

`f63a97125986a598557dca4c99ff9862e5fc8f7818bfa6bf8358ae8cf2151274`

## Revisión física del 11 de noviembre

En el Motorola se comprobó la cabecera completa, incluidos reloj, fecha, temperatura, ciudad e icono de nubes parciales. Se observó el acabado plateado de ajustes y calendario. Una ficha se abrió mediante su botón de detalles, mostró Accesos directos y volvió a Inicio al pulsar Cerrar.

Se midieron seis arrastres ADB de 450 ms, tres en cada sentido, con 250 ms entre gestos y reinicio previo de los contadores de gfxinfo. La muestra incluyó 644 cuadros. Android informó 1 cuadro janky, 0.16 %, y su contador legacy informó 515, 79.97 %. Los percentiles de tiempo de cuadro fueron P50 16 ms, P90 18 ms, P95 18 ms y P99 22 ms. Las consultas posteriores de pantalla mostraron 90 Hz en DisplayManager y un período de 16.67 ms en SurfaceFlinger. Se mantienen ambos resultados por su discrepancia. Esta muestra requiere una captura de tiempos de presentación correlacionada con la frecuencia efectiva para evaluar la cadencia.

Quedan pendientes las demás paletas, texto ampliado, controles multimedia con una sesión compatible y la evaluación de cadencia. La evidencia privada del teléfono permanece fuera de Git.
