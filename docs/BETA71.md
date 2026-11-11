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

La instalación no abrió Aura sobre otra aplicación. Después del desbloqueo autorizado se revisaron una captura estática de Inicio y los controles nuevos. No se ha acreditado aún la suavidad de las transiciones ni una comparación Estático/Reactivo.

La captura de referencia de beta70 se interrumpió antes de grabar porque Aura dejó de estar en primer plano. El Motorola quedó primero cubierto por el panel de notificaciones y después mostró otra aplicación. Tras la confirmación del usuario se intentó medir: Perfetto rechazó la configuración en /data/local/tmp por SELinux; se corrigió su ubicación a /data/misc/perfetto-configs. Antes de capturar cuadros el teléfono perdió primer plano y después Keyguard indicó showing=true/inputRestricted=true. Esos intentos iniciales se cancelaron; posteriormente se obtuvieron las muestras Reactivas de la tabla siguiente. Se solicitó desbloquear sin cambiar ajustes ni permisos. No se afirma una mejora de fluidez ni el cumplimiento de los presupuestos de CPU, memoria o presentación.

### Capturas posteriores al desbloqueo

Tres muestras válidas de Reactivo, de 9 s cada una, con seis swipes ADB de 450 ms (tres por dirección), comprobando Aura en primer plano. Se aplicó el mismo recorrido previsto para Estático. La captura se obtuvo con android.surfaceflinger.frametimeline y linux.process_stats. Procesamiento local con trace_processor oficial de Perfetto. Los valores siguientes filtran actual_frame_timeline_slice por el proceso de Aura.

| Muestra Reactiva | Cuadros | Late Present | Porcentaje tarde | P95 duración de slice real |
| --- | ---: | ---: | ---: | ---: |
| 1 | 660 | 4 | 0.606 % | 19.412 ms |
| 2 | 670 | 2 | 0.299 % | 19.164 ms |
| 3 | 819 | 3 | 0.366 % | 19.101 ms |

Total: 2149 cuadros y 9 presentaciones tarde (0.419 %). La duración del slice real comprende el intervalo de FrameTimeline, no solo tiempo de CPU de dibujo. En la primera muestra SurfaceFlinger presentó habitualmente a intervalos de 8.35 ms, aproximadamente 120 Hz efectivos durante la actividad. No se deduce ese refresco del P95 de gfxinfo.

Gfxinfo, con una ventana algo mayor que la traza, registró P95 19/20/19 ms y jank moderno 0.45/0.96/0.60 %, frente a legacy 43.18/93.84/67.03 %. La discrepancia impide usar un único porcentaje como prueba de suavidad. FrameTimeline también etiqueta muchos slices como Buffer Stuffing; no se interpreta automáticamente como retraso de presentación ni se declara resuelta la latencia.

Se descartaron dos tandas etiquetadas provisionalmente Estático: la primera no confirmó el selector; en la segunda el usuario confirmó su elección, pero la comprobación posterior mostró Reactivo. Sin confirmación de persistencia no se consideran una referencia Estática. El último control observado en Movimiento y efectos mostró **Reactivo e intensidad 50 %**, y se cerró el panel dejando Inicio visible. No se modificaron permisos ni ajustes del sistema.

No hay comparación causal válida ni validación de CPU adicional, memoria, temperatura o cadencia en otras cargas. Ambiental sigue aplazado. Evidencia privada fuera de Git.

Pendientes: capturas de Estático con selección y persistencia comprobadas antes/después de cada tanda; comparación con Reactivo; inversión rápida, fling y listas cortas; pulsación y apertura inmediata; búsqueda/teclado; abrir/cerrar paneles; bloqueo/retorno; paletas; imágenes personales; confirmación de favoritas. Las trazas y capturas del teléfono permanecen fuera de Git.
