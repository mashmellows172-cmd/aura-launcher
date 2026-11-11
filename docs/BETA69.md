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

[Encargo y supervisión de Ollama](coordination/BETA69-OLLAMA.md). Una consulta manual a Open-Meteo para Berlín devolvió 48 horas, temperatura, códigos meteorológicos y dos pares de amanecer/atardecer. La pasada final completó `assembleBeta`. Lint continúa en ejecución. Una pasada anterior fue cancelada tras cambios de fuente realizados durante el análisis.

El Motorola se actualizó mediante `install -r`. Android confirmó 0.48.0-beta69, código 69, y el permiso Internet autorizado como concedido. Firma del APK local verificada y SHA-256 instalado coincidente con el local.

`4de81059ced2f48086b09ed64b7386ed06039cf9657f70f217a76cc9aa101ee4`

Pruebas visuales, flujo de pronóstico en el teléfono y medición de fluidez pendientes. La evidencia privada permanece fuera de Git.
