# Beta56 — solicitud de refresco ligada a interacción

3 de noviembre de2026. 0.37.0-beta56/code56. Base beta55 e8b5419.

## Cambio
RefreshRatePolicy solicita120Hz en API30+ mientras Aura tiene foco, está RESUMED y hay interacción táctil/teclado reciente. Tras1500ms sin eventos libera preferredRefreshRate0f y preferredDisplayModeId0. API29 selecciona modo de mayor tasa con resolución actual. Ahorro o modo ligero bloquean solicitud y cancelan callback; pausa/pérdida de foco/destrucción liberan inmediatamente. Sin cambios de ajustes del sistema, polling ni dependencias nuevas. Conserva fondo estático deliberado y efectos guardados.

## Skills y delegación
improve-animations instalada mediante comando solicitado; auditoría traducida a Compose en plans/001-adaptive-refresh.md. Su alcance de planificación se separó de la implementación autorizada. Caveman aplica a comunicación; Ponytail reduce alcance a una política nativa; Frontend Design conserva identidad existente sin añadir efectos durante diagnóstico.
Ollama local qwen2.5-coder:7b recibió únicamente el controlador en revisión acotada (240tokens máximo). Sus hipótesis de NPE y error de índice fueron descartadas: ya existen safe-calls y fallback nullable. Respuesta truncada, sin evidencia adicional ni reintento. Implementación y revisión finales por Codex. No atribuir código aceptado a Ollama ni ahorro cuantificado.

## Validación
Compilación/lint en curso. Sin suites automáticas. Motorola ausente en adb devices; no instalación ni validación física. No se afirma120FPS ni corrección del lag. El coste de curvas/transparencias del fondo sigue pendiente de aislar/rasterizar en una entrega separada.
Firma del teléfono beta55 documentada2eb417c5…; no instalar candidato firmado con otra clave ni desinstalar. Esta compilación local permite validar código, no acredita compatibilidad con aquella línea.

## Resultado final
assembleBeta/lintBeta final correcto2m39s,50 tareas12 ejecutadas/38 al día;0 errores/35 advertencias/3 avisos. Primer lint detectó RestrictedApi en dispatchKeyEvent; sustituido por onUserInteraction nativo y verificación completa repetida. Sin suites automáticas.
SHA256 candidato:F78B1E9544D61097379BFECE678FDDA78110CC491548C4D1236D2FA8FB15CF4C. Certificado729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c.
MotorolaZY22K88ZFL reconectado con dirección facilitada por usuario: beta55 confirmada. APK instalado extraído y certificado2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514 comprobado. Incompatibilidad de firma: instalación detenida antes de install. No desinstalación, cambios de preferencias ni permisos. Se solicitó ubicación de la clave de beta55; no solicitar secretos por chat. Diagnóstico físico de beta56 pendiente.
