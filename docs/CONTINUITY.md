# Continuidad del proyecto

Última entrega instalada:0.24.0-beta31/code31. Ver [historial](../CHANGELOG.md), [validación](BETA31.md) y [configuración](DEVELOPMENT.md).

## Retomar

1. Leer CHANGELOG.md y su apartado Pendiente antes de cambiar código.
2. Revisar git status y actualizar sin sobrescribir cambios locales. Crear una rama por mejora.
3. PS4 ya tiene pantalla propia en Ps4Dashboard.kt: revisar visualmente beta31 en el Motorola y completar fidelidad de recursos. Nothing sin círculos y Razr siguen pendientes.
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
