# Entrega UNLOCK-02 — antigravity
Fecha UTC: 2026-10-29
Rama y SHA base: agents/antigravity/unlock-02 (base SHA: 6d168e2d7db602a24d33e7b388368e144b663d44)
Commit(s) de código: (registrado en git log de la rama)
Estado: revisión

## Objetivo y alcance
Diseño de storyboards temporales y especificación de accesibilidad/interrupción para transiciones de desbloqueo (Unlock Transitions) de Aura Launcher.
Alcance acotado estrictamente al contrato UNLOCK-02:
- 6 storyboards temporales (PS4 Ribbon, Fold, Slide, CRT, Orbital, Ripple), priorizando **PS4 Ribbon** y **Fold**.
- Máximo 2 capas visuales animadas simultáneamente (`Layer Background` y `Layer Content`).
- Entrada inmediatamente interrumpible en primer toque (PointerDown) sin bloquear ni diferir la interacción.
- Cumplimiento de reducción de movimiento (*prefers-reduced-motion*) y protección contra destellos fotocognitivos (WCAG 2.2).
- Preservación íntegra de la estructura de Aura: lista vertical, buscador, índice A–Z y apertura con un toque.

---

## Storyboards Temporales (6 Variantes)

### 1. PS4 Ribbon (Prioridad 1)
- **Concepto:** Onda sutil lumínica de fondo que se asienta mientras el contenido vertical emerge suavemente.
- **Duración total:** 240 ms.
- **Capas:**
  - `Capa 1 (Fondo):` Cinta tonal PS4 (#003087 / #0070D1 con glow tenue 0.25 alpha) con desplazamiento horizontal leve (offset X: -30dp -> 0dp) y desvanecimiento a estado pasivo.
  - `Capa 2 (Contenido):` Lista vertical + barra de búsqueda. Alpha: 0.0f -> 1.0f; Offset Y: +14dp -> 0dp (CubicEaseOut).

| Tiempo (ms) | Capa 1 (Fondo Ribbon) | Capa 2 (Lista Vertical) | Estado / Interrupción |
|---|---|---|---|
| **0 ms** | Alpha 0.10, Offset X: -30dp | Alpha 0.00, Offset Y: +14dp | Desbloqueo detectado |
| **80 ms** | Alpha 0.35 (pico brillo suave, sin destello), Offset X: -12dp | Alpha 0.45, Offset Y: +7dp | Interrumpible (PointerDown -> t=240ms) |
| **160 ms** | Alpha 0.25 (asentamiento), Offset X: -3dp | Alpha 0.85, Offset Y: +2dp | Interrumpible |
| **240 ms** | Estado estable de reposo | Alpha 1.00, Offset Y: 0dp | Reposo / Idle |

### 2. Fold (Prioridad 2)
- **Concepto:** Despliegue cinemático sutil estilo pliegue de hoja/superficie (metáfora visual continua, independiente de hardware).
- **Duración total:** 220 ms.
- **Capas:**
  - `Capa 1 (Superficie):` Escala horizontal leve (ScaleX: 0.94 -> 1.0f) con sombra central disipativa (alpha sombra 0.20 -> 0.0f).
  - `Capa 2 (Contenido):` Alpha 0.0f -> 1.0f; Scale: 0.97 -> 1.0f (FastOutSlowInEasing).

| Tiempo (ms) | Capa 1 (Superficie/Pliegue) | Capa 2 (Contenido) | Estado / Interrupción |
|---|---|---|---|
| **0 ms** | ScaleX 0.94, Sombra central alpha 0.20 | Alpha 0.00, Scale 0.97 | Inicio de apertura |
| **70 ms** | ScaleX 0.97, Sombra central alpha 0.12 | Alpha 0.50, Scale 0.985 | Interrumpible |
| **140 ms** | ScaleX 0.99, Sombra central alpha 0.04 | Alpha 0.88, Scale 0.996 | Interrumpible |
| **220 ms** | ScaleX 1.00, Sombra central alpha 0.00 | Alpha 1.00, Scale 1.00 | Reposo completo |

### 3. Slide
- **Concepto:** Elevación vertical rápida y limpia.
- **Duración total:** 200 ms.
- `Capa 1 (Fondo):` Sin transformación (estática para evitar costo GPU).
- `Capa 2 (Contenido):` Offset Y: +24dp -> 0dp; Alpha: 0.0f -> 1.0f (FastOutSlowInEasing).

### 4. CRT
- **Concepto:** Apertura suave de haz catódico retro (metáfora estilizada, sin flash blanco agresivo).
- **Duración total:** 180 ms.
- `Capa 1 (Apertura):` Clip vertical de haz (ClipHeight: 8% -> 100%, curva acelerada).
- `Capa 2 (Contenido):` Alpha: 0.0f -> 1.0f con disipación de líneas de exploración (Scanlines alpha: 0.15 -> 0.0f). Luminancia máxima limitada a 105% del contenido normal (cero picos de deslumbramiento).

### 5. Orbital
- **Concepto:** Expansión radial orgánica desde el punto focal del launcher.
- **Duración total:** 220 ms.
- `Capa 1 (Vignette):` Radio de viñeta: 0.85 -> 1.00 (sin alteración de color).
- `Capa 2 (Contenido):` Scale: 0.95 -> 1.0f; Alpha: 0.0f -> 1.0f (EaseOutQuad).

### 6. Ripple
- **Concepto:** Pulso circular concéntrico que revela los elementos sin deformación.
- **Duración total:** 200 ms.
- `Capa 1 (Onda):` Anillo circular en canvas con radio 0dp -> maxDimension, alpha 0.30 -> 0.0f.
- `Capa 2 (Contenido):` Alpha: 0.0f -> 1.0f escalonado natural.

---

## Matriz de Accesibilidad y Seguridad

| Parámetro | Requisito WCAG / UX | Implementación en Aura Unlock |
|---|---|---|
| **Reducción de movimiento** | `prefers-reduced-motion` o `ANIMATOR_DURATION_SCALE == 0` | Si está activo el modo accesible del sistema: Duración 80ms, Offset 0dp, Scale 1.0f, transición únicamente con Alpha fade simple (0.0 -> 1.0). Cero desplazamientos espaciales. |
| **Prevención de destellos** | WCAG 2.2 Criterio 2.3.1 (Tres destellos / umbral crítico) | Ningún efecto produce transiciones de luminosidad > 10% en intervalos < 100ms. Frecuencia de ciclo oscilatorio = 0 Hz (curvas monótonas crecientes sin rebotes estroboscópicos). |
| **Contraste de texto** | WCAG 2.2 Criterio 1.4.3 (Contraste mínimo 4.5:1) | El fondo de las capas animadas no interfiere con el canal alfa del texto legible en ningún frame t > 100ms. |
| **Interrupción inmediata** | Criterio de usabilidad de lanzador a un solo toque | Cualquier evento `MotionEvent.ACTION_DOWN` / Compose `PointerEventType.Press` fuerza instantáneamente `progress = 1.0f` y entrega el evento al composable receptor (ej. ícono de app o input de búsqueda). Cero bloqueos de entrada. |
| **Capas concurrentes** | Rendimiento y no duplicación | Exactamente 2 capas: `Layer Background` y `Layer Content`. No hay recomposición interna de listas; uso exclusivo de propiedades `graphicsLayer` (render thread). |

---

## Validación Real
- **Contratos:** Validado cumplimiento contra `GEMINI.md`, `AGENTS.md` y `docs/delegation/unlock-2026-10-29/antigravity.md`.
- **Límites de diseño:** Se descarta explícitamente cualquier sustitución de la lista vertical por grillas o carruseles horizontales de consola (respetando la decisión del 26 de octubre).
- **Entorno git:** Rama verificada `agents/antigravity/unlock-02`.

## Recursos y Presupuesto
- **Agente:** Antigravity (Gemini 3.8 Flash).
- **Consumo:** Entrega tabular compacta <= 1200 tokens. Ejecución única sin ciclos de bucle no supervisados.

## Mensaje para Siguiente Agente
- **OpenCode (`UNLOCK-03`):** Tomar las duraciones (180ms - 240ms), capas (Layer 1 Background, Layer 2 Content), tratamiento de interrupción (snap a 1.0f en PointerDown) y fallback accesible (solo alpha fade 80ms) para la implementación en `UnlockMotionSpec.kt` y `UnlockTransition.kt`.
- **Codex (`UNLOCK-04` / Coordinador):** Handoff de especificación visual y de accesibilidad completado y listo para revisión.
