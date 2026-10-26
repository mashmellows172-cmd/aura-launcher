# Continuidad del proyecto

Última entrega instalada y verificada:0.26.0-beta35/code35. Consultar BETA35.md; revisión visual física todavía pendiente. Ver [historial](../CHANGELOG.md), [validación](BETA35.md) y [configuración](DEVELOPMENT.md).

## Retomar

1. Leer CHANGELOG.md y su apartado Pendiente antes de cambiar código.
2. Revisar git status y actualizar sin sobrescribir cambios locales. Crear una rama por mejora.
3. Corrección vigente del usuario: PS4 debe conservar la esencia y ergonomía de Aura. En beta32 se retiró Ps4Dashboard.kt; no restaurar el carrusel ni Biblioteca separada. Revisar en el Motorola. Nothing sin círculos y Razr siguen pendientes.
4. Mantener iconos normales, reemplazos personales, favoritos, widgets, datos y firma de actualizaciones.
5. Para SIII, usar artwork/touchwiz-s3/coverage.json:20/176,9 IA y11 equivalencias históricas;156 pendientes. No contar los antiguos círculos genéricos como restauración aprobada. Referencias y certeza en sus manifests.
6. No conceder permisos del teléfono ni cambiar sus ajustes para validar. La ausencia de ADB no bloquea revisión de código.
7. No agregar ni ejecutar suites automáticas sin nueva petición explícita del usuario. Compilación y lint mediante wrapper; revisión visual con límites expresos.
8. Actualizar CHANGELOG.md y la ficha BETA correspondiente antes de cada entrega; registrar lo pendiente con honestidad.

## Código clave

- UI PS4: app/src/main/java/dev/senda/launcher/ui/Ps4Theme.kt.
- Recursos SIII: app/src/main/java/dev/senda/launcher/ui/theme/icons/S3Artwork.kt y S3AiArtwork.kt.
- Catálogo y generación: artwork/touchwiz-s3 y tools/prepare-s3-ai.cjs; revisión tools/review-s3-assets.py.
- Plan previo: superpowers/plans/2026-10-25-s3-ps4.md; registro: superpowers/reports/2026-10-24-visual-sound.md.

No se afirma120fps, equivalencia total con PS4 ni fidelidad histórica de todos los recursos. Verificar esas afirmaciones antes de documentarlas como logradas.

Beta33: iconos PS4 en artwork/ps4/app-icons y Ps4AppArtwork.kt, tipografías en ThemeTypography.kt; procedencia en artwork/theme-fonts. No volver a fuentes genéricas compartidas ni a la pantalla de consola retirada. Catálogo176/176 de la captura, no garantía de cobertura de apps instaladas después. Se conservan Normales y reemplazos.

Beta34: OpenCode auditó iconos/tipografías/ergonomía e implementó4 correcciones de herencia tipográfica, verificadas y compiladas por Codex. Prompts, agentes y validación en docs/delegation. No aplicar automáticamente las hipótesis del informe; F1/caché fue descartada.
