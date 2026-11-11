# Beta72 · materiales, controles y ambiente multimedia

11 de noviembre de 2026. Candidata 0.50.0-beta72, código 72. Base beta71 439c3df. Implementa las propuestas elegidas por el usuario: 1, 2, 6 y 4. Compilación/lint correctos y APK con firma compatible. No instalada: el Motorola perdió conexión ADB.

## Cambios visibles

- PS4 comparte cristal azul, reflejo superior, faceta diagonal y borde plateado en cabecera, búsqueda, tarjetas y menú rápido. Se conservan las paletas y los textos legibles.
- Nuevo ambiente multimedia PS4: el color de una carátula local tiñe moderadamente el reproductor y el fondo Reactivo. Transición de color de 650 ms y presencia de 240 ms; al pausar se asienta y desaparece. Las imágenes personales conservan su recurso. El fondo Estático no recibe esta reacción.
- La carátula se cruza con la siguiente mediante una transición de 320 ms. La muestra multimedia incluye dos carátulas originales pequeñas y Alternar carátula y ambiente, sin manejar una sesión musical real.
- Familia original de 21 glifos de sistema: búsqueda, ajustes, cerrar, más, reproducción, pausa, anterior/siguiente, calendario, reloj, alarma, volumen, favorita, aplicaciones, estado, añadir, editar, volver, inicio, perfil y clima. Comparte geometría y acabado; PS4, Razr y Sense tienen tono metálico. Nothing usa lentes/relojes angulares. Los iconos de apps, packs y reemplazos personales se conservan.
- Integrados en buscador/borrar, ajustes de cabecera, calendario, tarjetas de información, menú rápido, filas del Centro de estado, favoritas y multimedia. Se conservan las etiquetas accesibles, áreas de toque y estado desactivado del control padre.
- Nothing: recortes rectangulares, marcas cuadradas y presión corta. Razr: cepillado, línea de bisagra y ligera inclinación al pulsar. SIII/TouchWiz: brillo acuoso y compresión suave. Sense, Optimus y Xperia conservan sus materiales con una respuesta contenida propia. Son animaciones finitas; no se añaden relojes de fondo ni sensores.
- Fondo PS4 e intensidad pasan al principio de Movimiento y efectos para facilitar el acceso sin atravesar la vista previa gestual. La normalización de la luz usa el tamaño real de ventana.

## Límites de implementación

El ambiente se calcula desde la carátula existente con una muestra máxima de 144 píxeles fuera del hilo principal. No copia el bitmap ni consulta Internet. Carátulas hardware, recicladas, neutras o ausentes recuperan el color base. El color extraído solo afecta decoración; no modifica los roles de texto. Genérico, ahorro de energía y alto contraste no activan el ambiente. Movimiento reducido evita los desplazamientos; transiciones de color/entrada se resuelven sin duración cuando corresponde. La reproducción real requiere los metadatos que la integración multimedia existente ya pueda obtener.

No se añaden permisos, dependencias, vídeos, fuentes ni sonidos. Se conservan la familia sonora AUTO por tema y las elecciones del usuario. El trabajo profundiza superficies, controles y respuesta táctil; no reconstruye todas las aplicaciones del pack histórico ni declara una nueva réplica documental completa de SIII. La lista vertical, búsqueda, A–Z y apertura con un toque permanecen.

## Delegación y revisión

Ollama qwen3.5:4b propuso motivos visuales. Codex aceptó los materiales y rechazó cambios de layout/gestos fuera de alcance. Ver docs/coordination/BETA72-OLLAMA.md. Revisión independiente de fuente completada sin hallazgos concretos materiales; no editó archivos ni tocó el teléfono. Ver docs/coordination/BETA72-REVIEW.md. Se aplicaron writing-plans/executing-plans, frontend-design, animate/impeccable, accessible-animation, ponytail y verification-before-completion, con las restricciones de AGENTS.md.

## Validación

La primera compilación encontró un tipo nullable de tema en InformationCard; corregido con el perfil SYSTEM para el caso sin tema. Después de corregir el nullable, assembleBeta/lintBeta completaron correctamente en 1 min 20 s. La pasada final, que incluye el Centro de estado y su tema activo, terminó correctamente en 56 s con un trabajador. Lint: 0 errores, 42 advertencias y 3 avisos. Dos recomendaciones nuevas UseKtx proponen funciones de extensión para leer/crear bitmaps; no son fallos de ejecución. La recomendación previa sobre altura de ventana de AppRow desaparece al usar LocalWindowInfo. Revisión de diff y git diff --check correctos. No se añadieron ni ejecutaron suites automáticas.

Aapt confirmó versión 0.50.0-beta72 y código 72. Apksigner verificó firma compatible `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`.

SHA-256 del APK local:

`e659d8a44396cfcee5e1a41892e714f0c218b13675e96a4a3f03bfd8986a0a17`

Tamaño del archivo: 15 694 144 bytes. No hay aumento de archivo respecto a beta71, sujeto al empaquetado/alineación; esto no mide memoria ni CPU. APK privado: C:/Users/victus/Documents/Aura-private-beta72/aura-beta72.apk, fuera del repositorio.

No instalada. ADB pasó a offline y después no detectó dispositivos; mDNS no anunció un destino y la reconexión al último puerto agotó el tiempo. Se solicitó la dirección actual. No se verificaron versión/hash instalados de beta72, revisión física, una sesión multimedia real ni métricas de esta versión. La comparación Estático/Reactivo de beta71 sigue sin referencia confirmada; Ambiental continúa aplazado. Evidencia del teléfono, salidas locales y APK fuera de Git.
