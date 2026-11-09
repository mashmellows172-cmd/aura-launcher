# Beta63 · intensidad del reloj y escenas del clima

9 de noviembre de 2026. 0.43.0-beta63, código 63. Base beta62 `55c223f`. Rama `codex/beta63-clock-weather-scenes`.

En PS4, «Inicio y gestos» incluye el selector del reloj y una muestra de cambio de minuto. Suave usa 280 ms y 4 dp, Marcada 540 ms y 12 dp, Espectacular 900 ms y 18 dp. La cabecera compacta acorta el desplazamiento. Marcada es el valor para instalaciones existentes. La muestra cambia una hora de ejemplo. Aplicar intensidad escribe únicamente esta preferencia. El respaldo 17 incluye el ajuste y admite importación de las versiones 1 a 17.

El clima usa la descripción accesible del proveedor para elegir la escena. Las nubes se desplazan, la lluvia cae en diagonal y el sol tiene un halo cálido. La selección manual sigue disponible en los ajustes del clima. En PS4 la escena se limita al indicador y dura de 3 a 5 s en la primera lectura o al cambiar la condición. Las configuraciones antiguas de Repetición se muestran como eventos finitos en PS4. La pulsación larga abre el panel con la lectura disponible y las acciones del proveedor. Movimiento reducido, ahorro, alto contraste y vista previa suprimen la escena.

Se reutilizan el motor de escenas, el control de duración y la observación del ciclo de vida existentes. El progreso se lee al dibujar. La lluvia PS4 usa hasta 14 trazos y la nieve hasta 10 partículas. Se corrigen las tres advertencias de Compose de beta62 al medir el espacio de ventana y usar un `Modifier` neutro por defecto.

Skills aplicadas: Animate/Impeccable, Frontend Design, Accessible Animation, Ponytail y Verification Before Completion.  Sin suites automáticas.


## Validación e instalación

`assembleBeta` y `lintBeta` correctos en 1 min 31 s. Lint reportó 0 errores, 35 advertencias y 3 avisos. Se eliminaron las tres advertencias de Compose registradas en beta62. Certificado SHA-256 compatible `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`. Actualización inalámbrica desde beta62 mediante `install -r`. Versión 0.43.0-beta63 y código 63 verificados en el Motorola. SHA-256 local e instalado coinciden:

`38c34430348d61d56c971a46cb4b9dd7e5a20f25958ace79a7ae84d7434a7ecb`

El teléfono estaba mostrando otra aplicación tras la instalación. La prueba visual del selector y las escenas queda pendiente de que Aura esté visible. Sin medición de rendimiento nueva. Evidencia y ajustes privados fuera del repositorio.
