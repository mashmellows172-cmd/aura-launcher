# Beta65 · entrada Cinta PS4 más visible

10 de noviembre de 2026. 0.44.1-beta65, código 65. Base beta64 `635a0ea`.

El usuario indicó que no percibía la entrada de beta64. La revisión de código encontró un desplazamiento máximo de 10 dp multiplicado por la intensidad y una curva muy adelantada. Con el valor predeterminado de 70 %, el recorrido inicial del reloj era de 7 dp. Se encontró además una ruta posible en la que el efecto consumía la señal mientras la composición todavía esperaba `RESUMED`. La causa exacta en el teléfono queda por confirmar. Las escalas de animación del sistema estaban activas y el ahorro del sistema desactivado cuando se consultaron.

Cinta PS4 ahora usa una fase lineal común con tiempos separados. El fondo abre más su ancho y desplaza su capa decorativa. El reloj se incorpora desde el 16 % del recorrido, con hasta 28 dp de desplazamiento y mayor diferencia de opacidad. La cabecera usa un haz de mayor contraste. La vista previa de 132 dp reproduce la misma secuencia. Se mantiene la duración y la intensidad guardadas. El toque llega a Inicio durante todo el efecto. La secuencia espera a `RESUMED` antes de consumir la señal y los ajustes de reducción de movimiento siguen aplicándose.

El estilo se selecciona en Menú rápido, Entrada tras desbloquear, Cinta PS4 o Según el tema para PS4. Las cintas pertenecen al fondo PS4 generado. Con una imagen personal, se ven reloj y barrido sobre esa imagen. Los estilos alternativos conservan sus transiciones. Se mantuvo el flujo de autenticación de Android.

Systematic Debugging, Animate, Accessible Animation, Ponytail y Verification Before Completion aplicadas. Sin suites automáticas. `assembleBeta` y `lintBeta` pasaron en 1 min 2 s con 0 errores, 35 advertencias y 3 avisos. Firma SHA-256 compatible `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`.

Instalada por ADB inalámbrico mediante `install -r` sobre beta64. El Motorola confirmó nombre 0.44.1-beta65 y código 65. SHA-256 local e instalado coincidente:

`8625021ec1cfcf5edf18540741102eb966e251f0542f3cc843b119b9740a0391`

El Motorola estaba bloqueado al terminar. Queda pendiente reproducir un desbloqueo real con Aura visible y comprobar la entrada. Sin métricas nuevas de rendimiento ni cambios de preferencias del teléfono.
