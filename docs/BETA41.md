# Aura0.30.0-beta41 · PS4 con fondo vivo reactivo

28 de octubre de2026. Code41, paquete dev.senda.launcher.debug. Rama de validación; no promover efectos a main sin las mediciones físicas pendientes.

## Cambio

El fondo integrado de PS4 sustituye las seis franjas paralelas por tres cintas con curvas independientes, volumen translúcido, bordes cian, luz ambiental y pequeñas partículas. Geometría y sombreado originales de Aura, inspirados en la captura aportada; no se afirma equivalencia100% con el renderer de Sony.

LazyListState alimenta un parallax amortiguado. El scroll aumenta suavemente la energía del fondo; el cambio de app cercana al centro produce un pulso de luz breve. No consume gestos ni modifica el desplazamiento de la lista. Conserva preferencias, imágenes personales, widgets, iconos y navegación vertical. Es un fondo vivo dentro de Aura; no instala un WallpaperService global ni cambia el bloqueo.

Un reloj ligado a VSYNC actualiza la escena solo en RESUMED. Ahorro de batería observado mediante BroadcastReceiver con cleanup; movimiento reducido, alto contraste, vista previa y animadores desactivados detienen la animación. Posición, energía y fase se leen en draw; buffers, paths y brushes se reutilizan. Tres curvas de48 segmentos con ocho capas translúcidas; sin blur ni sensores ni trabajos por partícula. Esto limita trabajo por frame, pero no sustituye mediciones reales de consumo/FPS.

## Skills y revisión

PS4 Fidelity: paleta azul y lenguaje de ondas. UI/UX Pro Max: consulta `parallax motion readability`, respetando legibilidad y sensibilidad al movimiento. Deferring State Reads: estado de alta frecuencia fuera de composición. Using Efficient Effects: efecto ligado al lifecycle y limpieza del receptor.

Revisor local independiente examinó clock, prioridad de reducción/ahorro, lecturas Compose y geometría; no encontró fallos concretos. Revisión estática, sin servicios externos ni suites automáticas.

## Validación

Compilación/lint local: BUILD SUCCESSFUL en2m24s,50 tareas (23 ejecutadas/27 al día). Lint0 errores,30 advertencias,3 avisos. Sin suites automáticas.

APK beta/Aura-0.30.0-beta41.apk, SHA256422bca8ef58acf3963ef9ab7d4641d2a4ee7a15124a7f39b91d68fe28f890351. Instalación mediante adb install -r en el Motorola autorizado: certificado original compatible, versión0.30.0-beta41/code41, certificado y hash del APK posterior verificados. Datos conservados; no se cambiaron permisos ni ajustes.

Revisión visual acotada: dos capturas separadas por3s muestran movimiento de las curvas en reposo y una captura tras desplazamiento muestra el fondo y foco durante la interacción. No son mediciones de FPS/energía ni prueban exhaustivamente ahorro de batería/lifecycle. Una captura intermedia omitió el encabezado, que aparece en la siguiente; observación no diagnosticada, pendiente de reproducción. El PiP permaneció intacto. Al volver el teléfono a estar en uso terminó la revisión; las capturas quedan privadas, fuera de Git.

Pendientes: mediciones físicas, reproducción de la anomalía de captura y aceptación visual del usuario. No promover a main sin PERFORMANCE.md.
