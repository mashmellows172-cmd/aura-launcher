# DeepSeek local: centro de estado y movimiento

3 de noviembre de2026. Rama `codex/beta51-adaptive-status`, base beta50.

Ollama local, `deepseek-coder:6.7b`; propuestas con JSON estructurado y alcance
por archivo, sin escritura o comandos autónomos. Runner en
`tools/local-ai/delegate.py`. Las respuestas originales permanecen fuera de
Git y no incluyen respaldos ni evidencia privada del dispositivo.

- Centro de estado: dos borradores,472 y452 tokens,96,64s y55,42s.
  Se aprovechó la composición básica; supervisión corrigió imports, parámetros
  de Text y estructura, e integró acciones reales del launcher.
- Preferencias:717 tokens,86,52s. Se conservó la propuesta de preferencias
  observables; supervisión corrigió el ámbito de DisposableEffect y la API
  del listener. Se integraron restauración y ahorro de batería.
- Fondo adaptativo: propuesta rechazada por inventar APIs y reemplazos
  ausentes. La pausa del clock se implementó directamente, conservando el
  sonido de índice en un observador independiente.
- Entradas: propuesta rechazada por código inexistente, tipos incorrectos y
  operaciones bloqueantes innecesarias. Supervisión implementó la entrada
  única por clave, limpieza al interrumpir y fundido reducido150ms.
- Intentos previos con R1: rechazados por truncamiento/repetición o ausencia
  de implementación utilizable. No se incorporaron a la aplicación.

Skills usadas: frontend-design para conservar identidad PS4 y jerarquía;
accessible-animation para reducir desplazamiento manteniendo orientación;
Impeccable animate para interrupción, presupuesto y ausencia de rebote;
ponytail para reutilización y cambios pequeños; verificación antes de
finalizar para separar evidencia de expectativas.

No se acredita ahorro de tiempo, electricidad o tokens totales en esta
sesión. El modelo local necesitó correcciones sustanciales. El runner aporta
un límite de alcance y una revisión explícita, no un desarrollador autónomo
aprobado. La validación de la aplicación se registra en BETA51.md.
