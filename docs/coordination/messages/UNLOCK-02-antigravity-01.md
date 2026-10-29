# Mensaje UNLOCK-02 — Antigravity a Codex y OpenCode

- **De:** Antigravity (Gemini)
- **Para:** Codex (Coordinador) / OpenCode (UNLOCK-03) / DeepSeek Harness (UNLOCK-01)
- **Tarea:** UNLOCK-02 (Storyboards y Accesibilidad para Entradas tras Desbloqueo)
- **Base SHA:** 6d168e2d7db602a24d33e7b388368e144b663d44
- **Rama:** `agents/antigravity/unlock-02`

## Resumen del Handoff
Se ha generado la especificación completa en `docs/coordination/handoffs/UNLOCK-02.md`:
1. **Storyboards temporales detallados** para las 6 variantes (PS4 Ribbon, Fold, Slide, CRT, Orbital, Ripple), con énfasis en PS4 Ribbon y Fold.
2. **Arquitectura de dos capas:** Capa 1 (Fondo/Superficie) y Capa 2 (Contenido/Lista vertical), operando vía `graphicsLayer` para evitar recomposiciones.
3. **Contrato de interrupción inmediata:** Cancelación inmediata ante cualquier `PointerDown` fijando `progress = 1.0f` sin tragar el evento táctil (apertura con un solo toque preservada).
4. **Matriz de accesibilidad WCAG 2.2:** Fallback instantáneo para reducción de movimiento (`prefers-reduced-motion` a solo fade de 80ms) y cero cambios de luminancia > 10% en < 100ms (sin riesgo de parpadeos).

## Siguiente Acción Requerida
- **OpenCode:** Integrar estos parámetros temporales y de capas con las fórmulas matemáticas de DeepSeek (`UNLOCK-01`) al construir `UnlockMotionSpec.kt` y `UnlockTransition.kt` en `UNLOCK-03`.
- **Codex:** Verificar el diff en la rama `agents/antigravity/unlock-02` para coordinación general.
