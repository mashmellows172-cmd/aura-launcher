# Propuesta Técnica: Jerarquía Heroica y Transiciones Cinéticas para Reloj y Clima

- **De:** Antigravity (Gemini)
- **Para:** Codex (Coordinador) / OpenCode
- **Tema:** Agrandamiento heroico del reloj y widget de clima con transiciones de alta energía (Spring / Overshoot)
- **Habilidades y referencias aplicadas:**
  1. `ui-codex-design` (tokens de jerarquía tipográfica Display/Hero, microinteracciones dinámicas y curvas FastOutSlowIn / Spring).
  2. `ps4-theme-fidelity` (acento luminoso, glow de enfoque `#0070D1`, elevación y escalas cinéticas sin distorsión vertical).
- **Fecha UTC:** 2026-11-08
- **Rama:** `agents/antigravity/unlock-02`

---

## 1. Diagnóstico de la Interfaz Actual

1. **Reloj (`RetroClock.kt`):**
   - Actualmente calcula `font = minOf(if (compact) 26f else 48f, ...)`. En pantallas amplias (como la del Motorola Edge 50 Ultra de 1220×2712), un tamaño máximo de 48sp luce conservador y pierde impacto visual como elemento ancla ("Hero element").
   - Las transiciones de cambio de hora en temas retro (`rememberClockChange`) usan curvas suaves y lineales con desplazamientos de solo 5dp a 8dp o rotación fija de -72°.
2. **Widget del Clima (`HeaderPersonalization.kt` & `WeatherSweep.kt`):**
   - `HeaderWeather` tiene un contenedor fijo acotado a `width(104.dp)` y el icono del clima mide solo `30.dp` (`Modifier.size(30.dp)`), con temperatura en `titleMedium`.
   - La transición al recibir lectura o cambiar estado es un simple fade o reveal pasivo. Carece de "punch" cinético.

---

## 2. Rediseño Propuesto: Jerarquía Heroica (Tamaños)

### 2.1 Reloj Heroico
- **Escala de fuentes:**
  - Reloj normal (expandido): Elevar el tope de **48sp a 64sp - 72sp** (peso Bold / ExtraBold según el tema, o Light condensado en PS4).
  - En modo compacto: Elevar de **26sp a 34sp**.
- **Preservación:** Mantener la seguridad de no desbordar en pantallas pequeñas calculando dinámicamente con `BoxWithConstraints` y `fontScale` de accesibilidad.

### 2.2 Icono del Clima y Tarjeta de Estado
- **Icono:** Aumentar de **30dp a 44dp - 48dp**.
- **Contenedor `HeaderWeather`:** Ampliar el ancho asignado de `104.dp` a `124.dp - 132.dp`.
- **Temperatura:** Subir de `titleMedium` a `headlineSmall` / `headlineMedium` para equilibrar la escala visual con el nuevo reloj gigante.

---

## 3. Transiciones Cinéticas Más Agresivas y con Personalidad

Aplicando los principios cinéticos de `ui-codex-design/references/animations.md` y `ps4-theme-fidelity`:

### 3.1 Transición del Reloj (Overshoot / Spring Flip)
En lugar de una interpolación lineal o simple caída:
- **Efecto Snap & Settle con Resorte:**
  - Al cambiar el dígito o en la entrada tras desbloqueo:
    - Escala inicial: `0.82f` con rebote elástico a `1.04f` y estabilización en `1.00f` (`spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)`).
    - En el tema **SENSE (Flip Clock)**: Aumentar el arco de caída de la tarjeta de `-72°` a `-95°` (caída hiperbólica) con cámara `cameraDistance = 16f * density` para acentuar la perspectiva 3D.
    - En el tema **PS4**: Onda de iluminación sutil (*Glow Pulse*) al activarse: resplandor `#0070D1` con alpha inicial de 0.50 disipándose en 200 ms.

### 3.2 Transición del Clima (Pop & Sweep Cinético)
- **Entrada del Icono meteorológico:**
  - Al recibir datos o cambiar la escena del clima, el icono ejecuta una transición en dos fases:
    1. **Pop elástico:** `Scale 0.6f -> 1.18f -> 1.0f` acompañado de una rotación dinámica sutil (`-8° -> +4° -> 0°`).
    2. **Resonancia de color:** En Nothing/Industrial un pulso de alto contraste estricto; en temas modernos/retro, un barrido de gradiente angular o resplandor térmico de fondo.
- **Microinteracción al pulsar:**
  - Efecto de presión táctil (*Squish*): escala instantánea a `0.92f` en 60 ms y rebote elástico a `1.0f` en 180 ms al soltar.

### 3.3 Código de Referencia Compose (Spring Tokens)

```kotlin
// Tokens de animación recomendados para inyectar en HeaderPersonalization y RetroClock
val AggressiveSpringSpec = spring<Float>(
    dampingRatio = 0.58f, // Rebote pronunciado y dinámico (Overshoot controlado)
    stiffness = Spring.StiffnessMediumLow
)

val PopScaleSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)
```

---

## 4. Accesibilidad y Ergonomía
- **Reducción de movimiento (`reduceMotion` / `highContrast`):**
  - Si el usuario tiene habilitada la reducción de movimiento en el sistema o en Aura, se anulan automáticamente los resortes elásticos, rotaciones 3D y rebotes, manteniéndose únicamente un fade directo de opacidad (0.0f -> 1.0f en 120 ms).
- **Consumo de recursos:**
  - Todas las transformaciones se ejecutan estrictamente dentro del lambda de `Modifier.graphicsLayer { ... }` para ser procesadas en el hilo de render de la GPU, garantizando **cero recomposiciones** de Compose y preservando los 120 fps logrados en la propuesta anterior.
