# Verificación beta26

## Alcance

Implementación directa por Codex, como pidió el usuario. No ejecutada en OpenCode y sin agentes implementadores. Se conserva el encargo anterior como documento histórico, no como prueba de delegación.

- 192 variantes SVG y VectorDrawable;32 funciones por6 familias. Catálogo de recursos estático, sin getIdentifier para el arte integrado.
- Inspección de muestra comparativa de48 dibujos y hojas completas de las seis familias. Durante revisión se detectaron dos dibujos repetidos entre Ajustes/Automatización; diferenciados. También se corrigieron lente Sense, contraste de nube LG y diferenciación cromática Xperia con música naranja/álbum ámbar.
- Inspección de precedencias: Normales no usa dibujos ni reemplazos; AUTO acepta familia después de reemplazos/pack/colección; MONOCHROME conserva el tratamiento existente. La categorización antigua por subcadenas permanece solo para una colección personal activada expresamente.
- Galería de32 funciones, tamaños32/42/60dp, fondos claro/oscuro y selección manual por aplicación. Navegar no escribe preferencias; seleccionar una variante para una app sí es una acción explícita y guarda el reemplazo para el tema de destino.
- Guardado manual PNG128px en almacenamiento privado, fuera del hilo principal y con archivo temporal; compatible con imágenes embebidas del respaldo9. Sin permisos nuevos.
- No se agregaron ni ejecutaron suites automáticas. Revisión de fuentes, inventario de arte, renderizado SVG y compilación/lint no prueban FPS ni la experiencia completa del dispositivo.

## Gates de entrega

- `assembleBeta lintBeta`: **BUILD SUCCESSFUL in3m5s**, 49 tareas (16 ejecutadas,33 actualizadas), R8 y reducción de recursos activos.
- Lint: **0 errores,11 advertencias,3 sugerencias**; no se agregaron suites de pruebas.
- APK de3609477 bytes, versión0.20.0-beta26/código26, paquete `dev.senda.launcher.debug`.
- Firma verificada, mismo certificado SHA256 `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`.
- APK SHA256 `8c93b93548ed21479b39ce91c524fcc6e9773bfaeb10afc4e35568c85aa2d99d`.
- Arte y fuentes distribuidos con manifiesto y generador; sin recursos privados ni claves.

## Entrega al dispositivo

**Instalación de beta26 no confirmada.** La última versión leída mediante PackageManager fue beta25. ADB identificó el Motorola correcto, pero la instalación por streaming y las copias SYNC terminaron en EOF; se comprobó que el temporal quedó vacío. No se desinstaló Aura ni se concedieron permisos nuevos.

Se inició una transferencia alternativa por `exec-in dd` con verificación SHA256 antes de permitir `pm install -r`. Su proceso ya no está disponible y no se recuperó una salida final. La comprobación posterior no encontró un dispositivo conectado, por lo que no se afirma éxito ni se reinicia a ciegas esa operación. Confirmar versión y hash cuando el Motorola vuelva a estar disponible. La copia a Descargas tampoco está confirmada.

El puerto43529 era el último anunciado y puede cambiar. Las comprobaciones de beta25 no se usan como prueba de entrega de beta26.

## Revisión física pendiente

Comparar la carga vectorial real frente al maestro SVG; grandes tamaños de letra y orientación horizontal; elegir/cancelar/restablecer una variante; Normales/Monocromáticos y precedencias con un pack externo/colección; exportar/importar elección manual sin reemplazar datos del usuario; contraste sobre fondos personales y fluidez del catálogo en el panel real. Estas comprobaciones no están aprobadas por una compilación.
