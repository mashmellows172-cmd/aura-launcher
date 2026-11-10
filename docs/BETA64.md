# Beta64 · lenguaje de movimiento PS4

10 de noviembre de 2026. 0.44.0-beta64, código 64. Base beta63 `dfce7a7`. Rama `codex/beta64-ps4-motion`.

- Marco de dos contornos dibujado una sola vez por lista. Prefiere la fila con foco real cuando está visible. Se desplaza entre filas visibles en reposo y sigue la posición del scroll al arrastrar. La presión contrae la placa y su contenido levemente.
- Entrada Cinta PS4 tras autenticación. El fondo abre sus cintas, entra el reloj y un barrido recorre la cabecera mientras Inicio ya acepta toques. Conserva duración e intensidad guardadas y usa 600 ms cuando falta una preferencia PS4. La vista previa usa el mismo progreso. Un toque asienta el efecto.
- Fichas de apps. Al abrir se toma la posición de la placa en pantalla. La ficha crece desde ese origen y vuelve hacia él al cerrar. Si la posición no está disponible, se usa una expansión centrada. Se conservan apertura directa y atajos del sistema.
- Menú rápido. El borde luminoso acompaña el panel lateral. Entrada 220 ms y salida 160 ms. Las acciones de menú esperan el cierre breve antes de abrir su destino. «Barra A–Z» vuelve a mostrarse correctamente.
- Confirmación de acciones. Pulso de la fila al cambiar favorita y sonido tras guardar la preferencia. Pulso de la cabecera tras guardar una edición PS4 y confirmación sonora de la ficha. Usa la política de audio existente, con sus preferencias de volumen, silencio y reproducción.

Movimiento reducido y alto contraste muestran estados estables. Los progresos decorativos se leen en dibujo o capas. El gesto de desbloqueo usa la observación del sistema existente. El marco sigue la lista sin iniciar un recorrido independiente durante el swipe. Sin dependencias nuevas.

Primer compile detectó una importación faltante, corregida. `assembleBeta` y `lintBeta` finales en curso. Prueba física, grabación de las transiciones, foco con mando y medición de fluidez pendientes. Se conserva beta63 como última instalación registrada. Sin suites automáticas ni datos privados del teléfono en Git.

Skills aplicadas: Animate, Frontend Design, Accessible Animation, Ponytail y Verification Before Completion. La cuota se consulta en las ventanas de uso de Codex. Las consultas son lecturas puntuales.
