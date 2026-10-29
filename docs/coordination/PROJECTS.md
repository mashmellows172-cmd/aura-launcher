# Proyectos por agente

Cada proyecto es un clon Git independiente; comparte remoto, no archivos de trabajo. No comparten credenciales por copia. La autenticación disponible en el equipo se verifica al publicar. En otro equipo clonar y leer BOOTSTRAP.md.

| Agente | Carpeta local (desde qui/outputs) | Rama inicial | Responsabilidad |
|---|---|---|---|
| Codex | aura-launcher | codex/phase1-theme-editor | coordinación, revisión, integración |
| DeepSeek Harness | aura-agents/deepseek | agents/deepseek/unlock-01 | físicas e interpolación |
| Antigravity | aura-agents/antigravity | agents/antigravity/unlock-02 | storyboard y accesibilidad |
| OpenCode | aura-agents/opencode | agents/opencode/unlock-03 | renderizadores Compose |

Cada carpeta incluye instrucciones de entrada compartidas y docs/coordination. Abrir esa carpeta como proyecto en su aplicación. El archivo Aura-<agente>.code-workspace local facilita elegir la carpeta en editores compatibles; no constituye registro automático en aplicaciones que no soporten ese formato.

No lanzar tres builds simultáneas: validación Android central en Codex evita competir por Gradle/CPU. No copiar local.properties ni claves al Git; configuración Android local según docs/LOCAL-DEVELOPMENT.md. Cada nueva tarea exige rama nueva y SHA base confirmado.

Estado de preparación y conexión real se registran por separado en STATE.json. Preparar una carpeta y publicar una rama no significa haber autenticado un agente o ejecutado una tarea.
