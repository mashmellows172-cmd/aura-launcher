# OpenCode H5 raw report — conclusions require validation

Tengo el código completo de los cuatro archivos objetivo. He verificado cada área del enfoque (identidad/contraste/prioridad del cache de iconos, recorte con font scale, transformes en estado, ciclo de vida/movilidad/accesibilidad, consistencia del holder). A continuación el reporte.

# Auditoría de código read-only — Aura Launcher (H5)

**Archivos inspeccionados:**
- `app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt` (AppRow 1048–1132, AppIcon 1135–1188, AppIconCache 1190–1200)
- `app/src/main/java/dev/senda/launcher/ui/AuraMotion.kt` (AuraMotionController 35–56, applyAuraMotion 119–166, AuraAlertDialog 182–233, rememberAuraMotion 88–116)
- `app/src/main/java/dev/senda/launcher/ui/theme/HistoricalDesign.kt` (HistoricalProfile 35–54, readableAccent 50–53, HistoricalPanel 97–167)
- `app/src/main/java/dev/senda/launcher/ui/theme/icons/FamilyArtwork.kt` (FamilyArtwork 26–28, ArtworkResource 31–44, renderFamilyAppIcon 48–56, renderInstalledAppArtwork 60–63)

**Hallazgos concretos (orden por severidad):**

---

### F1 — Holder inconsistency entre artwork de familia y drawable nativo (Media)
**Evidencia:** `HomeScreen.kt` 1144–1152 define `holderRadius` y `holderShape` (`SQUIRCLE 15.dp`, `CIRCLE 21.dp`, `ROUNDED 12.dp`, `BEVELED 5.dp`, `PILL 21.dp`), anima el radio con `animateDpAsState` (1151) y aplica `holderShape` como fondo del icono (1177–1180). En cambio `FamilyArtwork.kt` 33–39 define una tabla distinta (`SQUIRCLE 16.dp`, `PILL RoundedCornerShape(50)`, `ROUNDED 10.dp`, `BEVELED CutCornerShape(5.dp)`), **sin animación**, y `ArtworkResource` solo pinta fondo cuando `customHolder` (40).

Cuando `renderFamilyAppIcon` devuelve `true`, `AppIcon` retorna temprano en `HomeScreen.kt` 1143 y nunca dibuja su holder; el dibujo usa la forma de `FamilyArtwork`. Mismo ajuste `IconHolder` produce silueta distinta según sea icono de familia (Nothing/generados sin plate) o drawable nativo, rompiendo la consistencia del holder.

**Fix mínimo:** extraer una `holderShape(style)` compartida (misma tabla de radios y misma animación `animateDpAsState`) y reutilizarla en `AppIcon` y en `ArtworkResource`, o al menos igualar los radios en ambos sitios.

---

### F2 — Frame de foco del teclado nunca se activa (Media — accesibilidad)
**Evidencia:** `HomeScreen.kt` 1070 declara `rowFocused`; 1082 lo actualiza solo con `.onFocusChanged { rowFocused = it.hasFocus }`; 1080–1081 pinta el `border` de foco condicionado a `rowFocused` (solo consola PS4). Pero el `Row` interior (1078–1085) usa `combinedClickable` **sin `Modifier.focusable()`** (ni `focusRequester`). `combinedClickable` no hace el nodo focusable, así que la navegación por teclado/D-pad nunca enfoca la fila: `rowFocused` queda siempre `false` y el frame de foco nunca se pinta.

**Fix mínimo:** añadir `.focusable()` (y opcionalmente un `FocusRequester`) al `Row` interior de `AppRow` para que `onFocusChanged` dispare con navegación de teclado y se muestren background/border de foco.

---

### F3 — `iconKey` incluye `monoInk` incluso cuando `monochrome` es falso (Baja — fragmentación de cache)
**Evidencia:** `HomeScreen.kt` 1157 calcula `monoInk` siempre; 1161 lo incorpora incondicionalmente al `iconKey` (`...:$monochrome:$sourceUri:$monoInk`). Sin embargo, 1170–1171 solo aplica `setTint(monoInk)` cuando `monochrome` es true, y 1185 solo aplica saturación cero cuando `monochrome` es true. Cuando `!monochrome`, `monoInk` **no afecta el bitmap**, pero sí fuerza claves de cache distintas por tema (distinto `onSurface` → distinto `monoInk`).

**Consecuencia (hipótesis de impacto bajo):** un bitmap nativo idéntico se almacena bajo múltiples claves según el tema, fragmentando el `LruCache` de 6 MB y provocando recargas innecesarias. No es identidad incorrecta, es falta de minimalidad del clave.

**Fix mínimo:** incluir `monoInk` en el clave solo cuando `monochrome` es true, p. ej. `:${if (monochrome) monoInk else 0}:$sourceUri`.

---

### Áreas revisadas sin defecto probado

| Área | Resultado |
|------|-----------|
| **Stale transforms** | `applyAuraMotion` (AuraMotion.kt 120–122) y el `graphicsLayer` de `AuraAlertDialog` (210–211) resetean **todos** los transformes al entrar. `cameraDistance` solo se asigna dentro de la rama `SPRING` (223) y solo importa con rotación activa; al salir de esa rama los transformes se restablecen a identidad. No se confirmó stale transform con impacto visual. |
| **Clipping con font scale** | No hay `clipToBounds`/`clip` en `HomeScreen.kt`. El label usa `maxLines = 1` + `TextOverflow.Ellipsis` (1094–1095), y `fontScale` solo ajusta el ancho del `AlphabetRail` (506: `LocalDensity.current.fontScale >= 1.4f`). No hay recorte de texto probado con font scale (máximo posible desbordamiento visual sin clip, no verificado). **Hipótesis no probada.** |
| **Lifecycle / motion / accesibilidad** | `rememberAuraMotion` (88–116) respeta `reduceMotion`, `motion == "NONE"`, `ValueAnimator.areAnimatorsEnabled()`, usa `repeatOnLifecycle(STARTED)` y ejecuta `reset()` en `finally`. `AuraAlertDialog` (194–201) y `MotionPreview` (241–249) respetan `noMotion`. No se encontró defecto. |
| **Identidad del icon cache / theme-in-key** | `iconKey` (1161) ya incluye `pack?.pack?.packageName` y `pack?.pack?.revision`. El artwork themed se resuelve **antes del cache** mediante `renderFamilyAppIcon` (1143, retorno temprano sin tocar `AppIconCache`), tal como especifica el encargo. No se repite la afirmación previamente desestimada de que el cache nativo de PackageManager necesite theme en la clave. |

---

### Preservación de entregas recientes (verificada por lectura, sin afirmaciones de FPS/QA física)
- Flexible app row minimum height vía `appearance.rowHeight.dp` (1073) y `heightIn(min = 48.dp)` interior.
- `monoInk` semántica nativa en clave (1161, ver F3 para su refinamiento).
- Siete estilos A–Z ya entregados (no tocados en esta auditoría).
- Nothing generado sin plates (bypassa holder de `AppIcon` vía 1143 — ver F1).
- Card keyboard-focus frame implementado pero inactivo (ver F2).
- PS4 Noto weight files: no se modifican recursos ni assets.
- Estructura/ergonomía de Aura preservada (lista vertical, búsqueda, A–Z, apertura un toque); PS4 sin imitación de menú de consola beta31.

---

## Handoff actionable

1. **F1 (prioridad alta):** Unificar `holderShape` entre `HomeScreen.kt` (1144–1152) y `FamilyArtwork.kt` (33–40): misma tabla de radios (`SQUIRCLE 15`, `PILL 21`, `ROUNDED 12`, `BEVELED 5`, `CIRCLE`) y misma animación `animateDpAsState`; aplicar en ambos caminos de dibujo.
2. **F2 (prioridad alta):** Añadir `.focusable()` al `Row` interior de `AppRow` (`HomeScreen.kt` 1078–1085) para que el frame de foco de teclado se active y se cumpla accesibilidad.
3. **F3 (prioridad baja):** Condicionar `monoInk` en el `iconKey` solo si `monochrome` (`HomeScreen.kt` 1161).
4. Regresión: comprobar que Nothing sin plates, 7 estilos A–Z y la estructura vertical de PS4 sigan renderizando igual tras los cambios F1/F2 (revisión de source + build/lint; sin reclamar verificación física ni FPS).

No se realizaron cambios, ni shell, ni acceso a assets privados, ni pruebas en dispositivo.