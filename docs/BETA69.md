# Beta69 · clima expandido, iluminación y multimedia PS4

11 de noviembre de 2026. 0.48.0-beta69, código 69. Base beta68 `84f5bd1`.

## Cambios visibles

- El toque en el clima PS4 abre un panel desde la cabecera. Permite buscar y elegir una localidad, ver hasta 24 horas, cambiar la escena tocando una hora y consultar amanecer y atardecer. La lectura actual mantiene su identificación como dato del widget. El pronóstico se identifica como dato de Open-Meteo, en °C y con la zona horaria de la localidad.
- La iluminación ambiental modifica el fondo PS4 generado y sus superficies según la hora del dispositivo. Usa día de 06 a 16, atardecer de 17 a 19 y noche de 20 a 05. Se puede desactivar en Inicio y gestos. Las paletas Azul clásico, Medianoche y Blanco conservan sus colores de texto.
- Inicio y gestos incluye tamaño del buscador de 56 a 88 dp, reloj PS4 de 32 a 64 sp y altura de cabecera de 120 a 216 dp, con restablecimiento. El reloj respeta el ancho disponible y la cabecera compacta. Los ajustes se guardan y participan en exportación e importación.
- El reproductor PS4 tiene carátula ampliada, jerarquía de título y artista, controles plateados y una entrada de cintas y reflejo de 2.4 s. Se detiene en reposo y responde a cambios de pista. Inicio y gestos permite reproducir una muestra visual con controles desactivados.

## Datos y permisos

El usuario autorizó explícitamente Internet y las consultas a Open-Meteo el 11 de noviembre. La localidad se elige en el panel. Las peticiones tienen tiempos límite y tamaño de respuesta acotado. La caché se escribe de forma atómica y puede servir una copia de hasta seis horas ante un fallo de red. Movimiento reducido, alto contraste y ahorro de energía limitan los efectos.

[API de pronóstico](https://open-meteo.com/en/docs) y [búsqueda de localidades](https://open-meteo.com/en/docs/geocoding-api).

## Delegación y validación

[Encargo y supervisión de Ollama](coordination/BETA69-OLLAMA.md). Una consulta manual a Open-Meteo para Berlín devolvió 48 horas, temperatura, códigos meteorológicos y dos pares de amanecer/atardecer. La pasada final de `assembleBeta` y `lintBeta` completó en 8 min 48 s con un trabajador. Lint registró 0 errores, 41 advertencias y 3 avisos. Una pasada anterior fue cancelada tras cambios de fuente realizados durante el análisis.

El Motorola se actualizó mediante `install -r`. Android confirmó 0.48.0-beta69, código 69, y el permiso Internet autorizado como concedido. Firma del APK local verificada y SHA-256 instalado coincidente con el local.

`4de81059ced2f48086b09ed64b7386ed06039cf9657f70f217a76cc9aa101ee4`

## Comprobación física del 11 de noviembre

El panel cargó el pronóstico para una localidad coincidente con el widget. Se observaron las horas, la zona America/Santiago, las condiciones, la temperatura y los eventos de amanecer y atardecer. Tocar una hora mostró el pronóstico en la cabecera del panel y Volver al clima actual recuperó la lectura del widget. Se eliminó la localidad temporal al terminar, comprobando el regreso al buscador de localidades.

Se probaron buscador de 88 dp, reloj de 32 sp y cabecera de 208 dp. Los valores persistieron al cerrar y reabrir Inicio y gestos. Restablecer tamaños recuperó los tres valores iniciales, 56 dp, 48 sp y 120 dp. La cabecera y el buscador se observaron con los tamaños modificados.

La muestra multimedia apareció y Repetir animación multimedia produjo dos estados visuales distintos. Se detectó que los nombres de transporte se partían en el panel estrecho. Esto motiva la corrección beta70. La revisión del árbol accesible confirmó que los controles padres estaban desactivados en ambas versiones. La muestra fue cerrada y se dejó Inicio visible.

Quedan pendientes reproducción real con metadatos, las otras paletas, los cambios horarios de iluminación y una nueva medición de fluidez. La evidencia privada permanece fuera de Git.
