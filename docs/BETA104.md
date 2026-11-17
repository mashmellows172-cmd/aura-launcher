# Beta104 · Movimiento de lista y cabecera PS4

17 de noviembre de 2026. 0.70.0-beta104, código 104. Base beta103 `46714b1`.

## Cambios visibles

- **Animaciones de lista PS4** abre el panel Movimiento y efectos con diez opciones y una muestra que se puede desplazar. Se añadieron Cristal en movimiento, Carril de luz, Cintas de seda, Relieve de placas y Estela de mosaicos. Funcionan en Inicio y en los resultados de búsqueda. El perfil PS4 se guarda por separado y se incluye en el respaldo. Hasta elegir un perfil, se conserva el efecto compatible de beta103.
- **Reloj y clima** se recogen en una transición de altura, opacidad y desplazamiento leve. La cabecera comienza a plegarse tras 120 ms de reposo y usa una curva gradual de hasta 240 ms al cerrar y hasta 280 ms al abrir. Un nuevo gesto interrumpe la transición en su altura actual. Los umbrales de 64 y 16 dp conservan la histéresis. El encabezado compacto reaparece por encima de la tarjeta conforme se recoge.

## Rendimiento y accesibilidad

Los nuevos perfiles leen una velocidad suavizada y dibujan bajo el contenido en una capa por lista. Los dos perfiles con detalle por aplicación recorren solo filas visibles. La adaptación de calidad puede suspender los adornos. El plegado solo modifica la altura en reposo y utiliza una progresión para tamaño y contenido. Reducir movimiento, ahorro de batería, alto contraste y las animaciones desactivadas en Android eliminan los adornos nuevos y llevan la cabecera a su estado final sin desplazamiento animado.

Se aplicaron `animate`, `accessible-animation` y `ponytail`.

## Compilación e instalación

`assembleBeta lintBeta` terminó en 1 min 24 s con 0 errores, 54 advertencias y 4 avisos. `git diff --check` pasó. No se ejecutaron suites automáticas.

Se actualizó beta103 por ADB inalámbrico con `install -r`. AAPT y apksigner verificaron paquete, versión y firma. Android confirmó 0.70.0-beta104, código 104, y a Aura como Inicio predeterminado. El SHA-256 del APK instalado coincidió con el local `95ae8987940de838726e6da385892ea3bf32b3f2ead09b5191a6c45bc9a0a855`.

## Validación en el Motorola

El 17 de noviembre de 2026 se comprobó PS4 en Inicio. Se descartaron dos muestras tomadas mientras «Personalizar Aura» cubría la lista. Tras cerrar ese panel con Atrás, un deslizamiento hacia abajo en la lista mostró la versión compacta de reloj y clima, y al volver a la parte superior reapareció la tarjeta amplia. Una captura de vídeo separada mostró posiciones intermedias del buscador durante el plegado y la reapertura. En algunos cuadros del intercambio coincidieron el reloj compacto y parte del reloj grande recortado por el borde de la tarjeta. El acabado de ese intercambio sigue pendiente.

Seis swipes de 450 ms, tres por dirección, con las mismas coordenadas de beta103 y `gfxinfo` reiniciado antes de la medición. Se registraron 647 cuadros, 2 con tirones (0,31 %), P50 de 11 ms y P95 de 15 ms. El contador legacy marcó 57 (8,81 %). En beta103 se habían medido P95 de 19 y 20 ms. La muestra es descriptiva. El estado del dispositivo, el fondo, la cabecera y la lista no están aislados, y `gfxinfo` no mide la presentación del compositor. Las capturas y el vídeo se guardaron fuera de Git. Falta recorrer los cinco perfiles nuevos en pantalla.
