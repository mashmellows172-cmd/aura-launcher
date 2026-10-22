# Rendimiento Compose — 2026-10-22 — app

## Alcance

Revisión dirigida con Compose Performance Skills. No se presenta como una auditoría Macrobenchmark completa: todavía no hay módulo de Macrobenchmark, perfil base propio ni diez arranques controlados. Las mediciones pendientes se mantienen explícitas.

## Entorno y referencia

Kotlin/Compose Compiler 2.2.10; Compose BOM 2025.05.00; AGP9.2.1; JDK17; Motorola Edge 50 Ultra API37. Mediciones de una ejecución en variante beta derivada de release, R8 activo, ART gestionado por Android, ocho desplazamientos mediante ADB. Las condiciones cambiaron entre muestras y no se usa el resultado como comparación causal.

## Hallazgos y cambios beta21

| Hallazgo | Cambio comprobable |
|---|---|
| Regla amplia `-keep class androidx.compose.**` | Se eliminó para permitir las reglas consumidoras y optimizaciones de Compose. |
| Estado de la actividad recogido sin ciclo de vida | Se usa `collectAsStateWithLifecycle`. |
| Lista heterogénea sin tipos de contenido | Se distinguen aplicaciones, encabezados, letras y bloques. |
| Caché limitada por cantidad de imágenes | Presupuesto aproximado de 6 MiB según dimensiones ARGB. |
| Clima solicita animación continuamente | Un ciclo al aparecer/cambiar datos, con lectura en capa gráfica y respeto de movimiento reducido. |
| Iconos importados vuelven a decodificarse | Caché por componente, revisión, origen y tratamiento. |

APK firmado antes de los últimos cambios: 10.215.636 bytes y 66.747 referencias a métodos dex. APK final: 2.996.226 bytes. El tamaño de archivo no equivale a una reducción de tiempo de arranque, memoria o batería.

## Diagnóstico del compilador

Se generaron informes para variantes no debug; métricas específicas bajo `app/build/compose_compiler/beta` y `release`. La salida beta final declara StrongSkipping activo, 224 composables skippable, 316 restartable y 17 clases inferidas inestables. Estos conteos son diagnósticos, no objetivos de calidad.

`AppRow`, `AppIcon`, `AlphabetRail` y la galería permiten saltarse recomposiciones. `LaunchableApp` contiene `ComponentName`; `HomeUiState` y `RetroThemeAssets` contienen colecciones; `WidgetController` contiene objetos Android mutables. No se añadieron anotaciones de estabilidad que prometan falsamente inmutabilidad.

## Pendientes para una auditoría completa

- Recorrido físico estable y repetible, con tema/configuración sin cambios.
- Macrobenchmark con arranque frío y percentiles de desplazamiento, compilación documentada y varias iteraciones.
- Perfil base propio y comprobación de su inclusión en el APK.
- Confirmación de hotspots con instrumentación antes de cambios de estabilidad o prefetch.
- Verificación en segundo plano y bajo presión de memoria. No hay medición de ahorro de batería.
