# Aura0.28.0-beta39 — favoritas, fondos y movimiento por tema

Revalidación local del 28 de octubre de 2026: wrapper assembleBeta/lintBeta correcto en43s, lint0 errores/30 advertencias/3 avisos. JDK/SDK existentes y clave original del usuario. Certificado coincide con la entrega original; APK recompilado con hash distinto, conservado aparte en beta/Aura-0.28.0-beta39-local-20261028.apk: eba7aba0d765ca6415b3047eb33abb6cac61a2c27eba5cc711549fd24e33d832. No sustituye el artefacto del27 ni valida el teléfono. Instalación pendiente de conexión ADB. Ver LOCAL-DEVELOPMENT.md para comandos y recuperación de la variante cloud beta38 ausente.

27 de octubre de2026. Código39. Rama de validación codex/phase1-theme-editor. Instalación local verificada el28 de octubre; validación visual y de rendimiento físicas pendientes.

Instalación del28 de octubre: vinculación ADB completada con código temporal; identidad del Edge50Ultra autorizado comprobada. Certificados del APK instalado previamente y del candidato coincidentes con la clave original. `adb install -r` correcto; APK posterior extraído y verificado: versión0.28.0-beta39/code39, certificado original y SHA256 eba7aba0d765ca6415b3047eb33abb6cac61a2c27eba5cc711549fd24e33d832. No se desinstaló, limpiaron datos, concedieron permisos ni cambiaron ajustes. Las copias de comprobación permanecen privadas fuera del repositorio. Los apartados anteriores y posteriores que indican instalación pendiente describen el estado previo a esta comprobación.

## Cambios visibles

- Sin estrellas/botones repetidos en todas las filas. Solo las favoritas llevan un punto discreto; sección, orden, pulsación larga y acción de accesibilidad conservados. Fila completa abre la app.
- Siete efectos de lista más Sin efecto: Profundidad, Pliegue3D, Onda, Órbita, Deslizamiento, Cascada y Abanico. Intensidad/duración/vista previa actuales conservadas; respaldo13 acepta1–13.
- Siete despliegues finitos según tema, aplicados al reloj, clima, reproductor y widgets: Nothing/puntos; Sense/pliegue; SIII/gota; Optimus/capas; Xperia/onda; RAZR/bisagra horizontal; PS4/foco. No ciclos infinitos añadidos; fase leída en capa/dibujo y trabajo de entrada ligado a STARTED. Movimiento reducido, alto contraste, vista previa y animaciones del sistema desactivadas muestran el estado final.
- Nothing ilumina cifras al cambiar minuto sin volver a recortar todo el reloj. Sense conserva flip por campo cambiado. PS4 tiene visualizador propio y clima CONSOLE_WAVE, distinto de AURORA de Xperia. Las condiciones meteorológicas siguen viniendo del proveedor; la decoración no genera pronósticos.
- Tipografía: pesos disponibles realmente incluidos, sin solicitar Light/ExtraLight inexistentes en ICS/Condensed o peso grueso sintético en RAZR. Se conserva Roboto ICS, Droid Sans, Lato, Roboto Condensed, Share Tech Mono, Noto y Geist existentes. Son alternativas/reconstrucciones de Aura; no se certifican fuentes OEM originales.
- Fondos: galería de miniaturas con búsqueda por nombre/tema/colección, filtros, contador, ampliación y Usar explícito. Conserva imagen personal y selector del sistema. Carga en IO y resolución acotada. La elección permanece en borrador hasta guardar. Colecciones ZIP ahora admiten las siete familias, conservando límites, validación de rutas y límites de imágenes. No se añadieron imágenes OEM ni fondos generados.
- Vista previa A–Z usa la misma condición de ancho que Inicio para horizontal/texto grande. Cambiar tema base restablece su fuente propia en vez de heredar la fuente de otra variante.

## Conservación e integración

Se conserva el cambio local previo de S3ReviewDialog; se corrigen imports, parámetro Image, compatibilidad con tamaño del editor y acceso a detalles. No se atribuye esa ampliación previa a esta implementación ni se considera arte nuevo. SIII sigue20 candidatos/156 pendientes.

Las variantes Room siguen fuera del respaldo JSON existente. Esta entrega mejora el catálogo de fondos y el respaldo de nuevos efectos, no implementa exportación de variantes. Firma original requerida para instalar sin borrar datos. No cambiar permisos ni ajustes del teléfono.

## Delegación y evidencia

Ver docs/delegation/2026-10-27-seven-themes.md. Siete encargos Antigravity rechazados por cuota; siete revisiones temáticas locales completadas como respaldo. Tres constructores locales modificaron archivos separados. Codex revisa e integra; ningún revisor ejecutó suites, compilación ni teléfono.

Validación final: assembleBeta/lintBeta BUILD SUCCESSFUL en3m34s;50 tareas (12 ejecutadas/38 al día). Lint:0 errores,30 advertencias,3 avisos. Revisión de fuente/diffs y siete revisiones temáticas locales completadas. APK beta/Aura-0.28.0-beta39.apk code39, versión0.28.0-beta39, firmado con certificado SHA256729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c y verificado con apksigner. SHA256 APK: 278e5eb62cfd7ce7b0b1134a19ede1ff47fb3fd6e681aeb7c70da19aabb58fa3. Motorola ausente de ADB/mDNS; APK preparado, no instalado. No se ejecutaron suites automáticas ajenas. Sin FPS, energía, fidelidad100% ni aceptación física afirmados. No fusionar efectos a main sin las mediciones exigidas en PERFORMANCE.md.
