# Beta110 · Reproductor interior y herramientas temáticas

Base beta109 `2510853`, rama `codex/beta110-quick-tools`. Reutilizar controles existentes; Kotlin/Compose, sin dependencias ni suites automáticas.

## Diseño

El reproductor permanece dentro del Centro rápido, con expansión por gesto y portada continua entre compacto/ampliado. Retirar solo MEDIA de las miniaturas de Inicio; conservar notas/temporizadores y convertir la selección MEDIA previa a Oculta, como solicita ahora el usuario. Ese cambio sustituye la restauración pendiente de la prueba anterior.

Fila visible de tres herramientas dentro del panel: Linterna, Calculadora, Gemini. Material, fuente, silueta e iconos siguen la familia activa. Pulsación con compresión breve y respuesta luminosa finita; la linterna solo se ilumina tras confirmación del sistema. Los accesos a apps cierran el panel con su transición configurada antes de abrirlas.

Linterna reutiliza CameraManager/TorchCallback y petición runtime existente: no conceder CAMERA por ADB. Calculadora usa el selector estándar Android. Gemini abre su aplicación instalada; no se integra un chat con claves/API ni se envían consultas automáticamente. Si falta una app, mostrar mensaje útil sin abrir páginas o instalar nada.

## Ejecución

- [x] Retirar miniatura multimedia y migrar selección anterior sin tocar otros ajustes.
- [x] Reutilizar linterna y añadir calculadora/Gemini dentro del Centro rápido; iconos propios y objetivos táctiles accesibles.
- [x] Transición de presión y confirmación finitas, con movimiento reducido/ahorro/alto contraste.
- [x] Revisión source independiente; assembleBeta lintBeta; actualizar BETA110, historial y continuidad.
- [x] Instalación compatible y revisión física disponible, sin cambiar permisos no autorizados.

Referencias: Android Intent.CATEGORY_APP_CALCULATOR y CameraManager.setTorchMode, documentación oficial consultada. UI/UX Pro Max/frontend-design aplicadas a jerarquía táctil, materiales por tema y movimiento motivado por acciones, sin decoración permanente.


## Ampliación autorizada · selección de scroll

El usuario detectó que cambiar el perfil de lista no cambiaba su aspecto. Investigación source independiente: persistencia y propagación correctas; PS4 excluía transformaciones de fila y varios perfiles eran solo decoración, eliminada en calidad reducida. El modo ligero también podía forzar reduceMotion mientras el diálogo mostraba preferencias sin esa máscara.

Añadir transformaciones acotadas a seis perfiles PS4 existentes (profundidad, perspectiva, desplazamiento lateral, onda, relieve, estela), sin traslación vertical ni duplicar filas. Un solo controlador por lista; valores leídos en graphicsLayer. Preview y lista deben usar el mismo helper. Con calidad reducida se conserva una versión atenuada; movimiento reducido, ahorro y alto contraste continúan desactivando efectos. Mostrar la causa cuando están bloqueados y ofrecer desactivar solo el modo ligero de Aura mediante acción explícita del usuario. No forzar modos de Android ni sobrescribir perfiles guardados.

La revisión detectó distancia de cámara insuficiente para filas anchas; se deriva de las dimensiones de la capa antes de validar. El caso concreto de supresión en el teléfono no se da por confirmado sin comprobar su estado.

## Resultado
Beta110 compilada/lint y revisiones source correctas, instalada conservando datos. Se observaron herramientas y ausencia de miniatura multimedia; calculadora y Gemini abiertos. Prueba física de linterna y comparación de perfiles pendientes; no se dan por realizadas por tener el código revisado.
