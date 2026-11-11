# Encargo local para beta68

11 de noviembre de 2026. Base beta67 `4accab7`. Rama `codex/beta68-ps4-polish`.

Ollama local con `deepseek-coder:6.7b` Q4_0, seleccionado entre los modelos instalados por su especialización en código. Tres encargos de generación acotada. Codex revisa e integra cada candidato y ejecuta compilación y lint. El acceso del modelo se limita al texto enviado por la API local.

1. Generar `Ps4Chrome.kt` con controles dibujados en Compose y acabado plateado adaptable a las tres paletas.
2. Generar `Ps4Material.kt` con luz y profundidad discretas, estado de foco o presión leído al dibujar y ausencia de trabajo animado en reposo.
3. Proponer una revisión completa de `Ps4PanelMotion.kt` para entrada y salida finitas e interrumpibles conservando su API.

Integración prevista en la cabecera, tarjetas, búsqueda y controles. Mantener los objetivos táctiles de 48 dp, las descripciones accesibles y el gesto de lista vertical. Usar las APIs y preferencias existentes. Comprobar accesibilidad y ahorro de energía. Registrar resultados reales y límites en docs/BETA68.md.
