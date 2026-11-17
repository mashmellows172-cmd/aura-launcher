# Beta102 · Cabecera y Centro rápido PS4

17 de noviembre de 2026. 0.69.0-beta102, código 102. Base beta101 `2e11f8e`.

- La cabecera PS4 muestra reloj y clima amplios cerca del inicio de la lista. Al pasar de la primera fila o de 64 dp de desplazamiento se contrae. Dentro de la primera fila, vuelve al formato amplio al alcanzar 16 dp o menos. La franja compacta conserva el reloj, la temperatura disponible, sus accesos y el engranaje. La búsqueda sigue visible debajo. Movimiento reducido y las opciones del sistema hacen el cambio de tamaño instantáneo.
- El Centro rápido mantiene música, temporizadores y la próxima alarma al alcance. Conserva agenda cuando corresponde, orden y tarjetas fijadas. Música ofrece la ficha expandida de la sesión real, con un origen medido en la carátula. El engranaje abre Ajustes de Aura. Su opción Accesos del dispositivo contiene la linterna, la búsqueda y los ajustes rápidos de Android.
- Las ondas PS4 responden a toques breves, scroll, foco de búsqueda y apertura o cierre de paneles. Los toques observados no consumen gestos. La reacción desplaza y estira la geometría retenida con la señal del controlador existente. El perfil Estático, la intensidad cero, el movimiento reducido, el ahorro o el alto contraste desactivan el efecto. La calidad adaptativa suspende la ondulación adicional.

## Revisión local

Se aplicaron `animate` (Impeccable para Android) y `ponytail`. Ollama `qwen3.5:4b` completó una revisión acotada de 213 tokens. Se aprovecharon la histéresis, la amplitud acotada y la reutilización de las cintas. Los umbrales se fijaron en dp y se conservó la política de calidad existente. Se descartaron un umbral de FPS nuevo y la respuesta háptica propuesta.

## Compilación e instalación

`assembleBeta lintBeta` terminó en 2 min 51 s con 0 errores, 54 advertencias y 3 avisos. No se ejecutaron suites automáticas. AAPT y apksigner verificaron la versión y la firma compatible. Se actualizó beta101 por ADB inalámbrico con `install -r`. Android confirmó 0.69.0-beta102, código 102, como la misma aplicación de Inicio. El SHA-256 instalado coincide con el APK local `973771b74ccf8bd9acfbe7b1d6f0c69dd024ee321f404c68328e02833aa2a765`.

## Revisión física

En el Motorola se observaron ambos tamaños durante desplazamientos de la lista. La franja compacta mostró la hora, 22 °C, el engranaje y la búsqueda. La versión amplia volvió a mostrar el reloj y la tarjeta de clima al llegar al principio de la lista. Se abrió el Centro rápido y se verificaron las tarjetas Música, Temporizadores y Próxima alarma. El engranaje mostró Ajustes de Aura y su entrada Accesos del dispositivo.

Quedan pendientes una medición aislada de la reacción de las ondas, la comparación de fluidez y revalidar la ficha con una sesión musical real. Las capturas privadas se mantienen fuera de Git.
