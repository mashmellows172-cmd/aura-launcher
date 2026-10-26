# Delegación a OpenCode · revisión de beta33

## Contexto

Proyecto Android nativo Kotlin/Compose Aura Launcher. Beta33 ya instalada:176 iconos PS4 y fuentes propias para siete familias. Usuario pide identidad PS4, fuentes fieles al espíritu de cada tema y ergonomía de Aura. La imitación de menú de consola de beta31 fue rechazada. NO recuperar carrusel, doble paso para abrir apps ni Biblioteca separada.

## Tarea1 · iconos

Leer tools/generate-ps4-app-icons.py, tools/render-ps4-app-icons.cjs, ui/theme/icons/Ps4AppArtwork.kt, FamilyArtwork.kt, InstalledAppArtwork.kt y ui/HomeScreen.kt. Rutas Kotlin completas bajo app/src/main/java/dev/senda/launcher. Comprobar prioridades AUTO/ORIGINAL, packs/reemplazos, apps desconocidas, contraste, pérdida de rasgos, máscaras sólidas y caché. Usar artwork/ps4/app-icons/manifest.json y SVG seleccionados como evidencia. Los176 son una captura concreta, no garantía de apps instaladas después. No son176 dibujos oficiales Sony.

## Tarea2 · tipografía

Leer ui/theme/ThemeTypography.kt, RetroDesign.kt, IndustrialDesign.kt, AuraTheme.kt y artwork/theme-fonts/README.md/manifest.json. Revisar cobertura de15 roles, pesos/síntesis, escalado Android, encabezados que fijen otra familia, faltas de caracteres y afirmaciones históricas. Distinguir un defecto probado de una preferencia estética. Las fuentes propietarias no están incluidas.

## Tarea3 · ergonomía/rendimiento

Revisar en HomeScreen.kt filas PS4, zonas de toque, foco, gestos y cálculo A–Z después del retiro de Ps4Dashboard. Inspeccionar Ps4Theme.kt para ciclo de vida/movimiento reducido. No inventar problemas sin ruta concreta de ejecución. No ejecutar pruebas ni compilar: esta delegación es de lectura.

## Entrega

Informe en español, máximo8 hallazgos priorizados: severidad, archivo y línea, evidencia, impacto, cambio mínimo recomendado y forma de verificarlo sin suites automáticas. Añadir3 tareas propuestas acotadas, con archivos que tocarían y criterios de aceptación. Señalar lo que no se pudo verificar. No editar ni publicar código; el agente principal revisará el resultado e integrará cambios autorizados.
