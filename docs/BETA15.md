# Aura Launcher 0.10.0-beta15

## Nuevo menú
⋮ → **Personalización y respaldo**.

- Texto de apps: 14–24 sp, además del tamaño de fuente de Android.
- Contenedor del icono: 32–60 dp; separación adicional: 0–20 dp. La fila crece para alojarlo.
- **Aplicar tamaños** guarda los controles; **Valores iniciales** prepara los valores predeterminados para aplicar.
- Alto contraste: fondo uniforme negro o blanco, texto de máximo contraste y acento legible; oculta el wallpaper sin borrar su selección.
- Iconos por tema: elegir un paquete instalado. Admite correspondencias appfilter.xml en res/xml o assets y drawables Android, incluidos vectores. Los PNG individuales tienen prioridad y las apps no cubiertas conservan su icono.
- No incluye máscaras generativas, calendarios dinámicos, relojes animados ni instalación de paquetes desde Internet.

## Respaldo abierto
**Exportar respaldo** abre el selector de archivos de Android. JSON formato `aura-settings`, versión 1. Incluye favoritas ordenadas, ocultas, tema, app de clima, parámetros visuales y de tamaño, paquetes de iconos elegidos y wallpapers/PNG personalizados legibles. Las imágenes se incluyen dentro del archivo; no dependen de permisos URI de otro dispositivo. No incluye las aplicaciones de los paquetes de iconos.

Se normalizan las imágenes a resolución acotada para pantalla/icono antes de exportar. Imágenes no accesibles se omiten con un aviso. Máximo 24 MiB de archivo y 16 MiB de imágenes. Sin contraseña ni cifrado propio; el archivo contiene tu configuración y tus imágenes.

**Importar respaldo** valida formato, versión, tipos, rangos e imágenes antes de mostrar un resumen. **Restaurar ajustes** reemplaza la configuración en una sola transacción DataStore. Cancelar o un archivo inválido no modifica los ajustes. Los widgets existentes permanecen: Android exige vincular y configurar los widgets en cada dispositivo y sus IDs no se exportan.

## Instalación
Actualizar con `adb install -r` usando el APK firmado de beta15. Mismo paquete y certificado de beta14; no desinstalar para actualizar. Las animaciones conservan beta12 y siguen en pausa.

## Fuentes de interoperabilidad
- Android Storage Access Framework: https://developer.android.com/training/data-storage/shared/documents-files
- Mapeos appfilter documentados por Lawnchair: https://github.com/LawnchairLauncher/docs/blob/main/developers/concepts/icon-packs.md

## Validación
Compilación optimizada (`assembleBeta`) y análisis estático (`lintBeta`) completados: 0 errores, 5 advertencias no bloqueantes (versión Gradle, resolución dinámica de recursos necesaria para icon packs, shrink de recursos y sugerencia KTX). Revisión en Motorola completada: exportación/restauración con imágenes, paquetes de iconos/fallback, controles persistentes, entradas inválidas e Inicio. La exportación final coincide exactamente con el respaldo original. Consulta `QA-beta15.md`. La batería durante una jornada sigue pendiente. No se afirma compatibilidad con todos los paquetes de iconos.

Tamaño del APK: 9.805.713 bytes. SHA-256: `7E2B1627943A10F4917E5FF9684B00D4C98397C410E0858BAE62199C33B9E8A4`.
