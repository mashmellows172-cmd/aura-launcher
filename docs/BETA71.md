# Beta71 · fondo PS4 reactivo

11 de noviembre de 2026. 0.49.0-beta71, código 71. Base beta70 `44c542e` y plan `9b5afea`. Compilación/lint correctos e instalada por ADB inalámbrico. Revisión visual y aceptación de rendimiento pendientes.

## Cambios implementados

- Las cintas retenidas acompañan el arrastre con un desplazamiento vertical acotado a 6 dp al 100 %; intensidad inicial 50 %. El gesto conserva todo el desplazamiento nativo.
- Foco asentado y pulsación iluminan suavemente el fondo. La pulsación mantiene su señal mientras se sostiene; las confirmaciones duran 400 ms. Se evita generar una señal por cada fila atravesada durante el fling o por cada carácter escrito.
- La búsqueda cambia la profundidad al ganar/perder foco. No se solicitan foco ni teclado desde este sistema.
- Ficha, menú rápido y panel meteorológico comparten su progreso existente con el fondo. No se crean otros animadores para sus transiciones.
- Movimiento y efectos ofrece Fondo PS4: Estático/Reactivo e intensidad 0–100 %. Preferencias persistidas y admitidas por el respaldo, con validación de valores. Se conservan los datos anteriores.
- Las imágenes personales mantienen el recurso y reciben una transformación y un sombreado moderados. No se añaden permisos, dependencias, vídeos ni bitmaps de animación.
- El desbloqueo conserva el progreso y las preferencias de Cinta PS4 existentes. Sin efecto sigue siendo válido.

## Diseño y coste

Un coordinador recibe señales confladas; reemplaza objetivos y no acumula una cola de efectos. Usa `withFrameNanos` sin el antiguo descarte hasta 16 ms. El suavizado exponencial usa `-ln(.05)/.25 = 11.98293 s⁻¹`, con delta máximo 50 ms. Los estados se leen en dibujo o graphicsLayer, sin animar el layout de las filas. Al asentarse deja de pedir cuadros; al perder RESUMED cancela su trabajo y borra transitorios. Movimiento reducido, alto contraste y ahorro de energía desactivan esta reacción.

La luz está en una capa de dibujo separada; no reconstruye las cintas ni sus degradados. La geometría permanece fija en caché. El modo Ambiental y la deformación de una cinta quedan aplazados hasta superar las mediciones de presentación del plan.

## Delegación local y supervisión

Se ejecutaron dos consultas locales a Ollama con `qwen3.5:4b`: matemática de cadencia y revisión del coordinador. Ambas respuestas se rechazaron. La primera dio coeficiente y frecuencias erróneos. La segunda confundió `tryReceive` con una operación bloqueante y negó la cancelación estructurada de LaunchedEffect/repeatOnLifecycle. No se integró código del modelo. Contrato y evaluación: docs/coordination/BETA71-OLLAMA.md. El modelo se descargó de RAM antes de compilar.

Skills aplicadas: executing-plans, ponytail, animate/impeccable, accessible-animation y verification-before-completion, adaptadas a Android y a la prohibición de suites de AGENTS.md.

## Validación y pendientes

Revisión de fuente y `git diff --check` ejecutadas. Primera pasada de assembleBeta/lintBeta correcta en 3 min 26 s. Tras añadir la reevaluación de las animaciones del sistema al volver a RESUMED y cancelar la confirmación anterior al pulsar, la pasada final terminó correctamente en 2 min 5 s, con un trabajador. Lint: 0 errores, 41 advertencias y 3 avisos. Una nueva advertencia recomienda LocalWindowInfo en lugar de Configuration.screenHeightDp para normalizar la altura de la luz; queda como mejora para ventanas parciales. No se añadieron ni ejecutaron suites automáticas.

APKs verificados con aapt y apksigner. Firma compatible `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`. Instalación `install -r` correcta sobre beta70, sin desinstalar. Android confirmó versión 0.49.0-beta71 y código 71. Hash del APK instalado coincide con el local:

`9b02038cc2abad7a4157c09263c5460123c195fcbff30ac0f9cef085120256db`

Tamaño del APK: 15 694 148 bytes; el APK de beta70 instalado medía 15 694 144 bytes. Diferencia real de archivo: +4 bytes, condicionada por el empaquetado/alineación; no equivale al tamaño del código añadido ni acredita consumo de memoria. El APK queda fuera de Git.

No se inició Aura sobre la aplicación que el usuario estaba utilizando. No hay revisión visual de beta71 ni comparación acreditada de presentación.

La captura de referencia de beta70 se interrumpió antes de grabar porque Aura dejó de estar en primer plano. El Motorola quedó primero cubierto por el panel de notificaciones y después mostró otra aplicación. Tras la confirmación del usuario se intentó medir: Perfetto rechazó la configuración en /data/local/tmp por SELinux; se corrigió su ubicación a /data/misc/perfetto-configs. Antes de capturar cuadros el teléfono perdió primer plano y después Keyguard indicó showing=true/inputRestricted=true. Las capturas se cancelaron; no hay muestra válida de esta beta. Se solicitó desbloquear sin cambiar ajustes ni permisos. No se afirma una mejora de fluidez ni el cumplimiento de los presupuestos de CPU, memoria o presentación.

Pendientes: tres recorridos comparables de FrameTimeline en Estático/Reactivo; inversión rápida, fling y listas cortas; pulsación y apertura inmediata; búsqueda/teclado; abrir/cerrar paneles; bloqueo/retorno; paletas; imágenes personales; confirmación de favoritas. Las trazas y capturas del teléfono permanecen fuera de Git.
