# Beta82 · movimiento coordinado y reproductor

13 de noviembre de 2026. 0.59.0-beta82, código 82.

- PS4: pulso de minuto hacia el clima y recorrido meteorológico inverso con prioridad. En cabecera compacta se mantienen los efectos individuales.
- PS4 y Xperia: el borde de búsqueda se abre desde el centro al enfocar y se recoge al salir. Reutiliza el brillo y la posición independiente de los resultados.
- PS4: la fila multimedia sigue disponible con una sesión pausada compatible. El panel se minimiza con botón o arrastre dedicado y vuelve al centro si el arrastre es corto. La carátula real conecta ambos tamaños en modo Expansión. Si falta, Aura intenta mostrar el icono de la aplicación.
- La barra multimedia muestra la posición solicitada y distingue la confirmación del reproductor. Los controles dependen de las capacidades de la sesión. La portada y el tinte siguen la pista activa.
- Movimiento y efectos permite desactivar por separado el pulso de cabecera y el movimiento multimedia. Los ajustes se incluyen en el respaldo.

## Referencias

- [LiveArea de PS Vita](https://manuals.playstation.net/document/en/psvita/basic/basicoperation.html) para continuidad entre superficies.
- [Cosmic Flow del Xperia NX](https://www.sony.com/ja/SonyInfo/News/somc_pdf/2012/0110_xperianx.pdf) para el recorrido de luz.
- [Tarjeta de reproducción de PS5](https://www.playstation.com/en-us/support/subscriptions/how-to-stream-music-and-podcasts-on-ps5-consoles/) para controles contextuales. Se conserva el tratamiento azul del tema PS4 de Aura.

## Verificación

- assembleBeta y lintBeta correctos en 2m54s con 0 errores, 40 advertencias y 3 avisos.
- Aura Local código 82 y certificado compatible con la copia verificada de beta80. SHA-256 del APK 988DE390FD17F37DE8B9E8CB60BBAC9C25A306F91626F9F464E18D18620FED69.
- ADB no detectó el Motorola por USB ni por descubrimiento inalámbrico. Instalación y validación visual con una sesión real pendientes.
