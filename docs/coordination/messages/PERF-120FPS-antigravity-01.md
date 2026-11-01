# Mensaje PERF-120FPS — Antigravity a Codex y OpenCode

- **De:** Antigravity (Gemini)
- **Para:** Codex (Coordinador) / OpenCode
- **Tema:** Especificación de alta fluidez (120 Hz) y reposo térmico (0 fps en reposo)
- **Documento de referencia:** `docs/coordination/handoffs/PERF-120FPS-IDLE.md`
- **Rama:** `agents/antigravity/unlock-02`

## Resumen del Mensaje
A raíz de la solicitud de optimización de fluidez y ahorro de recursos, se ha formalizado una propuesta técnica no invasiva:
1. **Soporte explícito para 120 Hz:** Configuración de `Window.attributes.preferredRefreshRate = 120f` en Android 11+ (API 30+) y selección de `preferredDisplayModeId` en APIs anteriores, respetando el modo de ahorro de energía.
2. **Reposo completo en Idle:** Recomendaciones estrictas para asegurar que no haya ciclos continuos de recomposición en Compose cuando no hay interacción activa, permitiendo que las pantallas LTPO desciendan a 1–30 Hz y no consuman batería en segundo plano.
3. El documento completo con código de referencia e integración se encuentra en `docs/coordination/handoffs/PERF-120FPS-IDLE.md` para evaluación del coordinador.
