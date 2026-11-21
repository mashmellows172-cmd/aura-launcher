# Beta110 · Herramientas interiores y selección de scroll

21 de noviembre de 2026. Aura 0.76.0-beta110, código 110. Paquete `dev.senda.launcher.debug`, rama `codex/beta110-quick-tools`, base beta109 `2510853`.

## Centro rápido

El reproductor permanece dentro del Centro rápido. Se retira Reproductor del selector de miniaturas de Inicio y se migra esa elección previa a Oculta. Notas y temporizadores conservan su opción de miniatura. Esto cumple la nueva petición del usuario y sustituye la restauración pendiente de la prueba beta109. La expansión compacta/ampliada y la portada continua dentro del panel se conservan.

Fila de tres herramientas siempre visible sobre las tarjetas: Linterna, Calculadora y Gemini. Reutiliza las superficies, tipografía e iconografía temática; los glifos de calculadora y asistente son dibujos propios. La presión produce una compresión breve y, en Sense/RAZR, una inclinación contenida. La linterna emite un pulso finito solo al recibir confirmación real de encendido. No se añaden bucles decorativos. Movimiento reducido, ahorro, alto contraste y modo ligero desactivan ese movimiento.

Linterna reutiliza CameraManager, su callback y solicitud runtime de CAMERA. No se activa ni concede el permiso por ADB. Calculadora usa makeMainSelectorActivity con CATEGORY_APP_CALCULATOR. Gemini abre su aplicación instalada, declarada en queries para visibilidad de paquetes. Ambos accesos cierran el panel mediante la transición configurada antes de abrir la aplicación; no se envían prompts ni se integran claves/API. Si falta una app o falla el inicio, se muestra un mensaje, sin abrir páginas o instalar nada.

## Fallo de selección de animaciones

La investigación source independiente no encontró un fallo de persistencia: selector, DataStore y elección PS4 llegaban correctamente a Inicio y búsqueda. Sí encontró dos causas de aparente igualdad:

- PS4 excluía las transformaciones de filas; cinco perfiles solo pintaban decoración que desaparecía por completo bajo la protección de calidad adaptativa.
- El modo ligero podía forzar reduceMotion en ejecución, mientras el diálogo mostraba la preferencia guardada sin explicar esa máscara.

Se añade un helper retenido para seis perfiles existentes: profundidad con escala; cristal con perspectiva lateral; carril con desplazamiento lateral; cintas con onda lateral; relieve con perspectiva vertical; estela con desfase lateral escalonado. No se trasladan filas verticalmente ni se dibujan clones. Los valores se leen en graphicsLayer y comparten un controlador por lista. Con calidad reducida permanecen atenuados; las decoraciones más caras pueden seguir ocultándose. NONE, inercia, foco al aterrizar y puntos mantienen sus comportamientos específicos.

La vista previa usa el mismo helper y claves que sus filas. La intensidad se aplica también al perfil clásico. Modo ligero, ahorro, alto contraste, movimiento reducido y animadores Android siguen teniendo prioridad; el diálogo explica la causa de bloqueo y ofrece desactivar solo el modo ligero de Aura mediante una pulsación explícita. No se cambian silenciosamente preferencias ni modos del sistema.

La revisión detectó una distancia de cámara fija demasiado corta para filas anchas; se sustituyó por el doble de la dimensión mayor de la capa. Revisión source posterior sin hallazgos accionables pendientes. No se da por identificado cuál de las máscaras estaba activa en el teléfono del usuario sin comprobarlo.

## Validación y entrega

`gradlew.bat --max-workers=2 assembleBeta lintBeta`: BUILD SUCCESSFUL en 1 min 22 s. 0 errores, 59 advertencias y 4 avisos. Una advertencia adicional UseKtx corresponde a la nueva migración de miniatura; no se afirma lint sin advertencias.

APK: `app/build/outputs/apk/beta/app-beta.apk`, copia estable ignorada por Git `beta/Aura-0.76.0-beta110.apk`. SHA-256 `C9B405B97CDD57D614B031A25DBA5D75EC0324C2843473CA5C9217C55843770B`.

Motorola Edge 50 Ultra: se extrajo beta109 a almacenamiento privado, se comparó su certificado y se confirmó compatibilidad con `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`. Transferencia verificada y `pm install -r`: Success. Después se comprobaron 110 / 0.76.0-beta110, hash exacto del APK instalado y Aura como HOME. Sin desinstalar ni borrar datos.

Revisión física: Inicio sin fila de reproducción; tres accesos legibles dentro del Centro rápido y reproducción real de YouTube conservada. Se abrió Calculadora desde el botón y se verificó su actividad; Gemini/host Google también se abrió desde su acceso sin introducir prompts ni aceptar pantallas de cuenta. Al principio una consulta inmediata tras pulsar Calculadora fue prematura y una pulsación tras volver a Inicio llegó antes de asentarse la ventana; se repitieron desde UI confirmada y esperando la transición. No se presentan esos intentos como fallos de apertura de la app. CAMERA seguía denegado: no se tocó Linterna ni se concedió permiso. Capturas/XML solo en almacenamiento privado.

Los cambios de perfiles se siguieron hasta el helper real y su vista previa por source, con revisión independiente. No se completó la comparación física de varios perfiles en movimiento ni se cambiaron las elecciones guardadas para esa prueba. Por tanto, no se afirma una diferencia perceptiva certificada o una mejora medida de FPS.

Comprobación de disponibilidad en Motorola: calculadora estándar resoluble y paquete Gemini instalado. CAMERA estaba denegado; no se concedió ni se probó el encendido físico. No se han añadido dependencias o suites automáticas. Revisión independiente de accesos y migración de miniatura sin P1/P2; revisión del scroll con la corrección de cámara confirmada.

Los cambios no acreditan FPS ni fluidez física por sí solos. Apertura real de apps, presentación de herramientas y diferencia perceptible entre perfiles deben registrarse separadamente de la revisión source. Permisos, datos, iconos y recursos personales se conservan; el cambio MEDIA→Oculta es el único ajuste de preferencia intencional pedido aquí.

## Referencias

- [Selector de calculadora Android](https://developer.android.com/reference/android/content/Intent#CATEGORY_APP_CALCULATOR).
- [Control y callback de linterna](https://developer.android.com/reference/android/hardware/camera2/CameraManager#setTorchMode(java.lang.String,boolean)).
