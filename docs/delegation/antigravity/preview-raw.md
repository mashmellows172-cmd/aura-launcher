<!-- Raw AI output. Not an approved bug list. See validation.md. -->

## Revisión de Aura Launcher beta38 — Fidelidad Vista Previa / Home Real

---

### Hallazgo 1 — AlphabetRail: ancho fijo en vista previa vs. adaptativo en Home

**Severidad:** Media · **Clasificación:** Defecto de fidelidad geométrica

**Archivo:Línea:**
- [`ThemeLivePreview.kt:85`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt#L85-L85)
- [`HomeScreen.kt:508`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt#L508-L508)

**Evidencia concreta:**

En `PreviewScene` la rail se pinta con ancho fijo:
```kotlin
// ThemeLivePreview.kt:85
Modifier.width(MotionTokens.railPortrait).fillMaxHeight()
```
En Home real, el ancho es condicional según orientación **y** escala de fuente:
```kotlin
// HomeScreen.kt:508
Modifier.width(
    if (compact || LocalDensity.current.fontScale >= 1.4f)
        MotionTokens.railLandscape
    else
        MotionTokens.railPortrait
).fillMaxHeight()
```

Con texto grande (fontScale ≥ 1.4) o en landscape, Home usa `railLandscape`; la vista previa siempre usa `railPortrait`. El editor no refleja ese cambio de ancho, por lo que el espacio disponible para la lista de apps en la vista previa difiere del real.

**Propuesta:** En `PreviewScene` replicar la misma expresión condicional:
```kotlin
// ThemeLivePreview.kt:85 — propuesta
val railWidth = if (compact || LocalDensity.current.fontScale >= 1.4f)
    MotionTokens.railLandscape else MotionTokens.railPortrait
AlphabetRail(
    ...
    modifier = Modifier.width(railWidth).fillMaxHeight(),
    ...
)
```

**Criterio de aceptación:** Con fontScale=1.5 y con fontScale=1.0, el ancho de la rail en vista previa coincide visualmente con el de Home para la misma orientación.

**Esfuerzo:** S · **Incertidumbre:** Baja — los valores de `MotionTokens.railLandscape/railPortrait` son constantes conocidas; solo falta aplicar la condición.

---

### Hallazgo 2 — `DefaultHomeCard` y `ErrorBanner` ausentes en vista previa

**Severidad:** Baja · **Clasificación:** Limitación intencional documentada, con impacto menor en layout

**Archivo:Línea:**
- [`HomeScreen.kt:431–446`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt#L431-L446)
- [`ThemeLivePreview.kt:73–88`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt#L73-L88)

**Evidencia concreta:**

En Home real pueden aparecer dos bloques entre el campo de búsqueda y la lista:
```kotlin
// HomeScreen.kt:431-434 y 443-446
if (!isDefaultHome) { DefaultHomeCard(onChooseHome); Spacer(14.dp) }
if (state.errorMessage != null) { Spacer(10.dp); ErrorBanner(...) }
```
`PreviewScene` no incluye ni `DefaultHomeCard` ni `ErrorBanner`. Cuando alguno está visible en Home, desplaza hacia abajo la lista de apps y reduce el espacio visible. La vista previa no lo muestra.

**Propuesta:** Es aceptable **no** renderizar `ErrorBanner` (es transitorio). Para `DefaultHomeCard`, agregar en `PreviewScene`, después del campo de búsqueda, un placeholder de la misma altura (`~56 dp`) visible únicamente si `!isDefaultHome` fuera replicable en el estado de previsualización; o documentar explícitamente que la vista previa asume "Aura es el launcher por defecto".

**Criterio de aceptación:** El docstring de `ThemeLivePreview` o un comentario inline indica la suposición. Si se elige el placeholder, las alturas coinciden en ±4 dp con Home en el mismo estado.

**Esfuerzo:** S · **Incertidumbre:** Media — se desconoce si `HomeUiState` expone `isDefaultHome` o si es lógica local de `HomeScreen`.

---

### Hallazgo 3 — `WidgetShelf` en vista previa no es scrollable; en Home puede serlo

**Severidad:** Media · **Clasificación:** Defecto de fidelidad de contenido dinámico (no de geometría estática)

**Archivo:Línea:**
- [`WidgetShelf.kt:52–60`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/widgets/WidgetShelf.kt#L52-L60)
- [`ThemeLivePreview.kt:79`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt#L79)

**Evidencia concreta:**

En la rama de vista previa, `WidgetShelf` dibuja `Surface` de altura fija por cada widget registrado en `controller.widgets`, como `Column` sin scroll:
```kotlin
// WidgetShelf.kt:53-59
Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    controller.widgets.forEach { widget ->
        Surface(Modifier.fillMaxWidth().height(widget.heightDp.dp), ...) { ... }
    }
}
```
El `DashboardGroup` en `PreviewScene` (línea 79) está dentro de un `Column` con `verticalScroll` y `heightIn(max=…)`, por lo que el scroll sí existe en la vista previa. El problema es la **inspección por scroll sin interacción**: el bloque consume todos los toques en `PreviewScene:69` (`awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }`), impidiendo cualquier gesto dentro del preview.

Esto es coherente con el diseño ("render-only preview"), pero hace imposible inspeccionar widgets del dashboard que superen el límite de altura 28 % de pantalla.

**Propuesta — inspección por scroll sin abrir apps, sonidos ni widgets reales:**

Reemplazar el consumidor ciego de eventos por un filtro que solo permita gestos de scroll vertical dentro del área del dashboard, sin propagar tap/longPress:

```kotlin
// ThemeLivePreview.kt:67 — propuesta conceptual
Box(modifier.fillMaxSize().background(skin.wallpaper)
    .pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                // Permitir scroll, bloquear tap y longPress
                val isScrollGesture = event.changes.any { it.positionChange().y != 0f && it.pressed }
                if (!isScrollGesture) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
) { ... }
```

> [!NOTE] Esta propuesta es **hipótesis de diseño**: la distinción fiable entre scroll y tap en `PointerEventPass.Initial` requiere medir desplazamiento mínimo (`ViewConfiguration.touchSlop`). El coordinador debe validar la detección antes de integrar.

**Criterio de aceptación:** El usuario puede arrastrar verticalmente el área de dashboard en vista previa y ver los widgets ocultos debajo del límite. No se activa ningún callback de app ni widget.

**Esfuerzo:** M · **Incertidumbre:** Alta en la implementación del filtro de gestos; baja en el objetivo.

---

### Hallazgo 4 — PS4: `consoleBackground` en vista previa puede activar `Ps4Wallpaper` con animación

**Severidad:** Media · **Clasificación:** Defecto de aislamiento

**Archivo:Línea:**
- [`ThemeLivePreview.kt:66–72`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt#L66-L72)
- [`HomeScreen.kt:364–365`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt#L364-L365)

**Evidencia concreta:**

La vista previa fuerza `reduceMotion=true` en el estado (línea 42), pero en Home real `Ps4Wallpaper` recibe el valor en tiempo real de `state.visualSettings.reduceMotion`:
```kotlin
// HomeScreen.kt:365
if (consoleBackground) Ps4Wallpaper(state.visualSettings.reduceMotion, ...)
```
En la vista previa:
```kotlin
// ThemeLivePreview.kt:72
if (consoleBackground) Ps4Wallpaper(true, Modifier.matchParentSize())
```
La llamada correctamente pasa `true` (reduceMotion forzado), lo que detiene animaciones. Sin embargo, `Ps4Wallpaper` sigue instanciándose aunque el requerimiento del encargo dice **PS4 no debe recuperar el carrusel de consola rechazado**. Si `Ps4Wallpaper` internamente lanza efectos o cargas de recursos (desconocido en los fragmentos), el aislamiento puede ser insuficiente.

**Propuesta:** Añadir `LocalThemePreview` como guardia dentro de `Ps4Wallpaper` (o en el call-site de `PreviewScene`) para renderizar solo un `Box` de color sólido equivalente, sin instanciar el composable completo:
```kotlin
// ThemeLivePreview.kt:72 — propuesta
if (consoleBackground) {
    // No instanciar Ps4Wallpaper en preview para evitar efectos colaterales
    Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.surface))
}
```

**Criterio de aceptación:** Con tema PS4 y sin wallpaper personalizado, la vista previa muestra un fondo sólido (no animado, sin carga de assets de consola). El Home real sigue mostrando `Ps4Wallpaper` normalmente.

**Esfuerzo:** S · **Incertidumbre:** Media — depende de la implementación interna de `Ps4Wallpaper` (no incluida en los fragmentos). Si ya es un simple Canvas estático con `reduceMotion=true`, el riesgo real es bajo.

---

### Hallazgo 5 — Codificación UTF-8 corrupta en `WidgetShelf.kt`

**Severidad:** Baja · **Clasificación:** Defecto de codificación (no afecta comportamiento en runtime si el APK se compila correctamente, pero sí legibilidad y mantenimiento)

**Archivo:Línea:**
- [`WidgetShelf.kt:29, 31, 34, 74, 98, 99`](file:///C:/Users/LOQ/AppData/Local/Temp/aura-review-1c494h0r/app/src/main/java/dev/senda/launcher/widgets/WidgetShelf.kt#L29-L34)

**Evidencia concreta:**

Varias cadenas literales muestran secuencias de escape mal interpretadas:
```kotlin
Text("AÃ±adir widget")           // "Añadir widget"
Text("Android te pedirÃ¡ permiso") // "Android te pedirá permiso"
Text("Widget no disponible. Su aplicaciÃ³n puede estar desactivada")
Text("âˆ' Alto")                  // "− Alto"
```
Son caracteres UTF-8 interpretados como Latin-1. Sugiere que el archivo fue guardado o transferido con una codificación incorrecta.

**Propuesta:** Verificar que el archivo esté guardado en UTF-8 sin BOM. En el sistema de build, confirmar que `sourceCompatibility` y el IDE tienen la codificación de fuentes configurada en UTF-8. No requiere cambio de lógica.

**Criterio de aceptación:** Las cadenas aparecen correctamente en el APK de debug y el archivo pasa `file --mime-encoding` reportando `utf-8`.

**Esfuerzo:** S · **Incertidumbre:** Baja — es un problema de codificación de archivo, no de lógica.

---

## Orden recomendado para entrega pequeña

| Prioridad | Hallazgo | Justificación |
|-----------|----------|---------------|
| 1 | **#5** Codificación UTF-8 | Esfuerzo mínimo, riesgo cero, mejora inmediata de legibilidad |
| 2 | **#1** Ancho AlphabetRail | Un cambio de 3 líneas, impacto directo en fidelidad geométrica con texto grande |
| 3 | **#4** PS4 Wallpaper en preview | Aislamiento requerido explícitamente; change pequeño en call-site, sujeto a revisión de `Ps4Wallpaper` internals |
| 4 | **#2** DefaultHomeCard placeholder | Documentar supuesto o agregar placeholder; esfuerzo bajo, mejora fidelidad de espacio |
| 5 | **#3** Scroll en dashboard preview | Mayor esfuerzo, requiere validación de gesture filter; dejar para iteración siguiente dentro de esta fase |

---

> [!IMPORTANT] Los hallazgos 3 y 4 contienen **hipótesis** marcadas explícitamente: dependen de comportamiento interno de `Ps4Wallpaper` y del sistema de gestos de Compose que no está completamente expuesto en los fragmentos. El coordinador debe verificar antes de integrar cualquier cambio.
