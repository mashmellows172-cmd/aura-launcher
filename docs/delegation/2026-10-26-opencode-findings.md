# OpenCode review · beta33

## Verificación del agente principal

- F1: descartado como defecto actual. LauncherViewModel.icon delega a PackageManager; el catálogo PS4 retorna antes de AppIconCache. La caché de dibujos nativos no depende del tema. No se cambia su clave sin un caso reproducible.
- F2: confirmado y delegado a OpenCode para corregir4 componentes. La fuente fija de RetroClock solo estaba en SENSE, no en la rama PS4 como sugería el informe. Se verificaron las rutas reales.
- F3: pesos sintetizados presentes, pero impacto visual no demostrado. Pendiente; no es un fallo funcional probado ni se añaden fuentes por conjetura.
- F4: límite de captura ya documentado, comportamiento deliberado de fallback. Sin cambio.
- F5/F6: hipótesis ópticas, no errores confirmados. WebP se precalcula fuera del desplazamiento; no hay coste runtime de SVG.
- F7: comportamiento de foco esperado; no exige modificación.

Lo que sigue es la salida original del revisor, conservada como evidencia y no como lista automática de defectos aprobados.

# Informe de revisión delegada — Aura Launcher beta33

**Repositorio:** `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\outputs\aura-launcher` (rama actual, sin modificaciones)
**Revisor:** revisión de solo lectura (lectura de fuentes; no se compila, no se instala, no se ejecutan suites automáticas).
**Entrada:** `2026-10-26-opencode-review.md` (tareas 1 iconos, 2 tipografía, 3 ergonomía/rendimiento).

## Contexto verificado
- `CHANGELOG.md` y `docs/CONTINUITY.md` confirman beta33/code33 instalada; catálogo PS4 de 176 apps como captura, no garantía post-instalación.
- Corrección vigente (26 de octubre): PS4 conserva estructura Aura (lista vertical, búsqueda, A–Z, un toque); no carrusel, ni doble paso, ni Biblioteca separada.
- `AGENTS.md`: no añadir suites automáticas sin petición; no cambiar permisos/ajustes del teléfono; no afirmar pruebas nativas sin verificación.

---

## Hallazgos (máximo 8, por severidad)

### F1 — Alto: caché de iconos sin clave de tema (riesgo de dibujo de tema previo)
- **Archivo/líneas:** `app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt` — `iconKey` línea 1152; bloque `produceState` 1154–1167; `AppIconCache` (LruCache 6 MB) líneas 1181–1191.
- **Evidencia:** `val iconKey = "${app.component.flattenToString()}:${app.revision}:${pack?.pack?.packageName}:${pack?.pack?.revision}:$monochrome:$sourceUri"` no incluye tema/familia ni `skinName`. La caché se consulta antes de `pack?.drawable(app)` / `onLoadIcon(app)` (línea 1160).
- **Impacto:** al cambiar de tema, una app puede conservar el bitmap renderizado bajo el tema anterior hasta que la LRU lo expulse. El impacto real depende de si `LauncherViewModel.icon` / drawable del pack varían por tema **[HIPÓTESIS de impacto; defecto de clave comprobado en código]**.
- **Cambio mínimo:** añadir el tema actual a `iconKey`, p. ej. `:$skinName` o `LauncherTheme` activo, de modo que cada tema tenga su propia entrada de caché.
- **Verificación sin suites:** instalar, abrir lista con tema PS4 (placas azules), cambiar a otro tema y observar si alguna fila conserva el dibujo PS4; repetir al volver. Revisar `LauncherViewModel.icon` para confirmar si devuelve drawables distintos por tema.

### F2 — Medio: encabezados, reloj, audio y cards saltan la tipografía del tema
- **Archivo/líneas:** `ui/theme/ThemeTypography.kt` (sistema correcto: 15 roles por familia); `ui/ThemedHeaderCaption.kt:29`, `ui/RetroClock.kt:66`, `ui/ThemedAudioWidget.kt:83`, `ui/InformationCard.kt:36` — `fontFamily = FontFamily.SansSerif` (o `Monospace` solo para NOTHING/RAZR).
- **Evidencia:** esos cuatro composables fijan `FontFamily.SansSerif` del sistema en lugar de usar `MaterialTheme.typography` (que resuelve la familia del tema). En tema PS4 se usa SansSerif sistema, no Noto Sans Regular/Light.
- **Impacto:** ruptura de coherencia tipográfica en PS4 (y otros temas) en encabezado del header, caption, reloj del header, widget de audio y tarjetas. No es preferencia: el código ignora la familia asignada al tema en esos puntos.
- **Cambio mínimo:** quitar las fijaciones hardcodeadas y dejar que los `Text` hereden `MaterialTheme.typography` (o aplicar `retroTypography(theme)` para los temas retro), manteniendo Monospace solo si el diseño de RAZR/NOTHING lo exige.
- **Verificación:** inspeccionar visualmente el header (reloj, caption "AURA / ORBIT"), widget de audio e InformationCard con tema PS4 activo; comparar contra `ThemeTypography.kt`.

### F3 — Medio: pesos de fuente incompletos en PS4 y RAZR → síntesis de negritas
- **Archivo/líneas:** `ui/theme/ThemeTypography.kt:11` (Droid Sans regular+bold), `:15` `razrFont = FontFamily(Font(R.font.share_tech_mono))` solo regular, `:16` `ps4Font = FontFamily(Font(R.font.noto_sans_regular), Font(R.font.noto_sans_light, FontWeight.Light))`; `app/src/main/res/font/` contiene solo `noto_sans_regular.ttf`, `noto_sans_light.ttf`, `share_tech_mono.ttf`. Uso de negrita: `ui/HomeScreen.kt:959` (`fontWeight = FontWeight.Bold` en letras de sección A–Z con `labelLarge`).
- **Evidencia:** `res/font/` no tiene `noto_sans_bold.ttf` ni `share_tech_mono` con pesos intermedios. `historicalTypography` asigna la familia a los 15 roles, pero `FontWeight.Bold/Medium` no tiene glifo exacto en PS4 ni RAZR.
- **Impacto:** en tema PS4 las letras de sección (A–Z, peso Bold) y cualquier texto en negrita se dibujan con síntesis de negrita o caída a Regular; se pierde el trazo diseñado. Comprobado en código; el efecto visual es **[HIPÓTESIS]**.
- **Cambio mínimo:** agregar `noto_sans_bold.ttf` (y opcional Medium) al `FontFamily` de PS4 en `ThemeTypography.kt`; evitar `FontWeight.Bold` en secciones PS4 o usar el peso del tema; evaluar idéntico para RAZR.
- **Verificación:** confirmar `res/font/` (hecho); observar las letras de sección A–Z en tema PS4 en dispositivo y comparar contra una familia con bold real.

### F4 — Medio: catálogo PS4 cerrado a 176 capturas; apps nuevas sin placa PS4
- **Archivo/líneas:** `ui/theme/icons/Ps4AppArtwork.kt:6–183` (`else -> null`); `ui/theme/icons/InstalledAppArtwork.kt:187–196` (PS4 delega a `ps4AppArtwork`); `artwork/ps4/app-icons/manifest.json:1–2` (`count: 176`, "Aura PS4-style interpretations, not official Sony Android app icons").
- **Evidencia:** `ps4AppArtwork` solo resuelve los 176 paquetes del manifiesto; cualquier paquete no catalogado devuelve `null`. `AppIcon` (HomeScreen.kt:1136–1160) entonces continúa hacia el pack de iconos o `onLoadIcon` (icono real de la app). El manifiesto aclara que es una captura, no garantía.
- **Impacto:** no es defecto funcional (se respeta una toque y la identidad real de la app), pero tras instalar nuevas aplicaciones la mayoría no tendrá la placa PS4, rompiendo la expectativa de identidad visual uniforme en el tema.
- **Cambio mínimo:** documentar en `docs/BETA33.md`/`CHANGELOG.md` que el catálogo es de captura y no se amplía automáticamente; opcionalmente generar una placa genérica PS4 para apps desconocidas en lugar del icono original (solo si el usuario lo aprueba, porque altera la identidad esperada).
- **Verificación:** instalar una app no catalogada, tema PS4, confirmar que muestra su icono original y se abre con un toque; contar entradas en `manifest.json` (176, hecho).

### F5 — Medio-bajo: posible pérdida de rasgos en el trazado PS4 (rasterizado a celdas)
- **Archivo/líneas:** `tools/generate-ps4-app-icons.py:16–36` (función `scene`); especialmente línea 33: bloques `h{w*.625} v{h*.625}` sobre rejilla 6×6; `manifest.json` anotaciones `"(solid monochrome rejected)"` para Cámara, YouTube, Notas, Doggis, aCuenta.
- **Evidencia:** el generador reduce la obra a rectángulos de celda sobre un degradado plate; obras complejas (líneas finas, curvas) se simplifican. Ya existe el rechazo de monocromáticos sólidos >78 % relleno (líneas 51–57), que evita placas sin rasgos.
- **Impacto:** identidades simplificadas; posible pérdida de trazos finos en algunas de las 176. **[HIPÓTESIS]** — requiere comparación óptica SVG master vs render WebP.
- **Cambio mínimo:** conservar el rechazo de sólidos; para las entradas anotadas, evaluar regenerar desde `source.foreground` (captura de identidad) en lugar de bloques de celda.
- **Verificación:** revisar los SVG maestros en `artwork/ps4/app-icons/` y comparar ópticamente contra los renders `.webp` de `drawable-nodpi` en una app compleja (p. ej. Chrome, YouTube).

### F6 — Medio-bajo: contraste de las placas PS4 (tinta blanca sobre degradado plate)
- **Archivo/líneas:** `tools/generate-ps4-app-icons.py:29–30` (`gradient #1464B9 → #00265E`, rect plate `#A8D8FF` con `.6` alpha, tinta `#FFFFFF`); `artwork/ps4/app-icons/manifest.json` (origen de cada entrada).
- **Evidencia:** las pinturas de identidad son `#FFFFFF` / `#83BAF0` / `#15365C` sobre un degradado plate cuyo extremo superior es bastante claro (`#1464B9`). Los detalles blancos sobre zonas claras del degradado pueden tener poco contraste. **[HIPÓTESIS]** — no se midió contraste en renders ni en dispositivo.
- **Impacto:** posible pérdida de legibilidad de detalles de identidad en algunas placas.
- **Cambio mínimo:** revisar las entradas con `captured-app-identity` y tinta blanca sobre plate claro; ajustar pintura o añadir contorno fino solo si la revisión óptica lo confirma.
- **Verificación:** medir contraste de los WebP renderizados o revisar en el Motorola con buena luz; no se afirma sin medición.

### F7 — Bajo: indicador de foco en filas PS4 solo con navegación no táctil
- **Archivo/líneas:** `ui/HomeScreen.kt:1061–1078` (`rowFocused` vía `onFocusChanged` línea 1075; fondo/borde de consola líneas 1072–1074 cuando `pressed || rowFocused`; `combinedClickable` `onClick = onLaunch` línea 1077).
- **Evidencia:** la fila PS4 ocupa ancho completo con altura `appearance.rowHeight.dp` y área mínima 48 dp; `combinedClickable` la hace focusable y el borde de enfoque se dibuja solo si recibe foco (teclado/dpad). En pantalla táctil pura, `rowFocused` nunca se activa, pero eso no impide la apertura con un toque.
- **Impacto:** en modo consola/TV con dpad o TalkBack el foco es visible; en táctil no hay estado de enfoque (aceptable). No rompe la ergonomía de Aura (un toque, objetivo amplio).
- **Cambio mínimo:** ninguno obligatorio; si se quiere consistencia de consola, añadir estado `pressed` más distinguible en el fondo PS4.
- **Verificación:** conectar teclado/dpad o TalkBack, navegar filas PS4, confirmar borde de enfoque; en táctil confirmar que un toque abre la app (una sola acción).

---

### Verificado sin hallazgo (positivos, para constar)
- **Ergonomía PS4:** `HomeScreen.kt` no referencia `Ps4Dashboard` (retirado en beta32); `AppRow` usa `combinedClickable` con `onClick = onLaunch` (una toque), lista vertical, búsqueda permanente y rail A–Z (`HomeScreen.kt:300–346, 937–984`). Coherente con la corrección del 26 de octubre.
- **Movimiento reducido:** `rememberHistoricalPhase` detiene la animación (`snapTo`) cuando `reduced == true` (`ThemeMotion.kt:29`), y `Ps4Wallpaper` recibe ese parámetro (`Ps4Theme.kt:43–44`); filas anulan specs de animación si `reduceMotion` (`HomeScreen.kt:947, 973, 1027`); brillo de búsqueda a 100 ms (`HomeScreen.kt:244`). Cumple el requisito de lectura.
- **Cobertura de roles:** `themedTypography` aplica la familia a los 15 roles Material (`ThemeTypography.kt:20–25`); `historicalTypography` los resuelve por tema en `AuraTheme.kt:118`. No hay encabezados con familia distinta salvo F2.
- **Despacho PS4 correcto:** `installedAppArtwork` para `LauncherTheme.PS4` delega a `ps4AppArtwork` (`InstalledAppArtwork.kt:188`), por lo que las 176 apps catalogadas sí reciben su placa PS4.

---

## No se pudo verificar (lectura únicamente)
- Revisión visual nativa y FPS en el Motorola (pendiente según `CHANGELOG.md`/`CONTINUITY.md`; AGENTS.md prohíbe cambiar ajustes del teléfono y no hay ADB asumido).
- Medición real de contraste de placas PS4 y comparación óptica SVG vs WebP (F5, F6 son hipótesis).
- Confirmación de que `LauncherViewModel.icon` / drawable del pack varían por tema (confirma o descarta el impacto real de F1).
- Cobertura de apps instaladas después de beta33 (por definición, los 176 son una captura).
- Instalación/hash de un nuevo APK (no se compila ni instala).

---

## Tareas propuestas (acotadas)

### T1 — Clave de tema en el caché de iconos
- **Archivos:** `app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt` (línea 1152 `iconKey`; bloque caché 1181–1191).
- **Criterios de aceptación:** `iconKey` incorpora el tema activo; tras cambiar tema (PS4 → otro → PS4) ninguna fila conserva el dibujo de un tema previo; compilación y lint sin errores nuevos; verificación manual en dispositivo.

### T2 — Tipografía de tema en encabezados y pesos PS4/RAZR
- **Archivos:** `ui/theme/ThemeTypography.kt` (añadir `noto_sans_bold` al `ps4Font`); `ui/ThemedHeaderCaption.kt:29`, `ui/RetroClock.kt:66`, `ui/ThemedAudioWidget.kt:83`, `ui/InformationCard.kt:36` (quitar `fontFamily` hardcodeado y heredar `MaterialTheme.typography`).
- **Criterios de aceptación:** en tema PS4 el caption, reloj, widget de audio y cards usan Noto Sans; las letras de sección A–Z no muestran negrita sintetizada (o se usa el peso disponible); lint limpio; verificación visual en dispositivo.

### T3 — Documentación y revisión óptica del catálogo PS4
- **Archivos:** `docs/BETA33.md`, `CHANGELOG.md`; `artwork/ps4/app-icons/manifest.json` (entradas con `(solid monochrome rejected)`).
- **Criterios de aceptación:** se documenta que el catálogo es una captura de 176 y no garantía para apps instaladas después; las cinco entradas rechazadas como sólidos se revisan ópticamente contra la captura de identidad; contraste de placas revisado en dispositivo; nada se presenta como terminado sin evidencia.

---

## Handoff accionable

1. El agente principal apruebe o descarte T1–T3 (especialmente el cambio de `iconKey`, que altera comportamiento de caché).
2. Si se aprueban, aplicar cambios en los archivos indicados, ejecutar compilación + lint del wrapper (no suites automáticas) y actualizar `CHANGELOG.md` + `docs/BETA33.md` en el mismo commit, conforme a `AGENTS.md`, sin afirmar validación nativa hasta revisión en el Motorola.
3. Pendientes abiertos para la siguiente sesión: revisión física de temas/FPS, medición de contraste PS4 y confirmación de variación de drawables por tema para cerrar F1/F5/F6.

No se ha modificado ni publicado código; todo queda a criterio del agente principal.
