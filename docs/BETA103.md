# Beta103 · Cabecera estable y Centro rápido contextual

17 de noviembre de 2026. 0.69.1-beta103, código 103. Base beta102 `ecec186`.

- La cabecera PS4 permanece en el mismo tamaño durante el arrastre, la inercia y los desplazamientos programados. Conserva sus umbrales de 64 y 16 dp, y cambia después de 120 ms en reposo. Si comienza otro gesto, se cancela el cambio pendiente. La cabecera amplia reaparece con un fundido de 140 ms sobre su tamaño final. El plegado cambia el tamaño en un solo paso. Se respetan movimiento reducido y la configuración de animaciones de Android.
- El Centro rápido toma una instantánea al abrir. Un acceso directo solicitado tiene prioridad; después van los temporizadores en marcha a 60 segundos o menos de vencer y la música en reproducción. Las tarjetas fijadas y el orden del usuario resuelven el resto. Reproducir, pausar, iniciar una cuenta regresiva o expandir una tarjeta dentro del panel actualiza su contenido sin reordenar la lista ni forzar un salto al inicio. La tarjeta de agenda utiliza la disponibilidad observada al entrar.

La revisión de código encontró que beta102 cambiaba el tamaño durante `isScrollInProgress` y animaba las medidas del contenido de la cabecera. Ambas operaciones pueden recolocar el inicio de la lista durante el gesto. El efecto de ondas conserva su comportamiento.

## Compilación e instalación

`assembleBeta lintBeta` terminó en 2 min 10 s con 0 errores, 54 advertencias y 3 avisos. Se aplicaron `animate` y `ponytail`. No se ejecutaron suites automáticas.

Se actualizó beta102 por ADB inalámbrico con `install -r`. AAPT, apksigner y Android confirmaron el único paquete de Aura, 0.69.1-beta103, código 103 y firma compatible. El SHA-256 del APK instalado coincidió con el local `5e100a196cd6f8e5bd694e99afac458d4799b1ced35068aa370feaf646b8826c`.

## Comparación física

Se reinició `gfxinfo` antes de seis swipes ADB de 450 ms, tres por dirección, con Aura visible. En beta102 se registraron 951 fotogramas, 4 marcados con tirones (0,42 %), P50 de 12 ms y P95 de 17 ms. El contador legacy marcó 40 (4,21 %). Una sola muestra incluye cuadros de diseño y fondo y no aísla la causa del tirón ni mide presentación del compositor.

El primer intento posterior se interrumpió al perder Aura el primer plano. Tras recuperar Inicio, se repitió el mismo recorrido dos veces.

| Versión | Cuadros | Tirones modernos | Tirones legacy | P50 | P95 |
| --- | ---: | ---: | ---: | ---: | ---: |
| beta102 | 951 | 4 (0,42 %) | 40 (4,21 %) | 12 ms | 17 ms |
| beta103, primera muestra | 989 | 4 (0,40 %) | 667 (67,44 %) | 15 ms | 19 ms |
| beta103, segunda muestra | 1147 | 14 (1,22 %) | 517 (45,07 %) | 14 ms | 20 ms |

Las dos muestras posteriores registraron P95 más alto. Los contadores modernos y legacy difieren, y la muestra no controla la cadencia ni aísla cabecera, fondo y lista. El cambio del momento en que se contrae la cabecera se verificó por revisión de fuente. La fluidez no queda acreditada como mejorada. Falta comprobar en pantalla el orden contextual con música y temporizadores reales. Evidencia del teléfono fuera de Git.
