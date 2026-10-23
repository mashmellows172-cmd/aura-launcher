# Beta24 — escenas meteorológicas

Solicitud: sustituir el barrido tosco por clima vistoso y diferenciado detrás del reloj, con puntos luminosos para Nothing. Continúa la autorización previa de trabajo autónomo, sin nuevas rondas de aprobación.

Enfoque elegido: Canvas nativo, una fase temporal leída solo en dibujo, partículas deterministas y formas originales; sin vídeo, shaders dependientes de GPU ni animación del bitmap del proveedor. Se consideraron sprites (más recursos y escalado limitado) y shaders (más complejidad y compatibilidad). La escena ocupa el fondo del reloj y clima, sin tocar disposición o acciones.

Condición: texto y descripción accesible del widget, nunca temperatura ni análisis del icono. Si el proveedor no informa, atmósfera neutra. Selector manual explícito para elegir condición cuando no se comunica. Persistir y respaldar la selección. No implica que la elección manual sea un pronóstico.

Nothing: matriz luminosa; Sense: volumen de nubes y rayos cálidos; TouchWiz: cristal y agua; LG: cintas y partículas de color; Xperia: auroras finas; RAZR: segmentos LCD. Sol, nubes, lluvia, nieve, tormenta y niebla comparten significado con dibujo específico por tema. Sin destellos bruscos. Repetición continua opcional; OFF y movimiento reducido conservados.

Plan:
- [ ] Extraer condición y guardar selección manual compatible con respaldos antiguos.
- [ ] Reemplazar barrido por escena atmosférica de área completa, fluida y diferenciada.
- [ ] Compilar beta optimizada; revisar firma y entregar actualización conservando datos.
- [ ] Registrar límites de validación. No afirmar estética impecable ni rendimiento medido sin evidencia nativa.

UI/UX Pro Max: resultados pertinentes de reduced motion, excessive motion y animación UI de Jetpack Compose. Se aplica una sola escena, controles de pausa y accesibilidad; la repetición decorativa es una excepción solicitada por el usuario y sigue siendo opcional. No se añaden pruebas nuevas ni se ejecutan suites en este cambio; se compila el artefacto. Límite de cuota previo 95% continúa vigente.
