# Coordinación multiagente — Implementation Plan

Goal: contexto durable en GitHub y proyectos independientes para DeepSeek Harness, Antigravity y OpenCode; Codex integra.
Architecture: clones separados, ramas por tarea y protocolo asíncrono mediante handoffs/PR. Ningún servicio de conexión permanente se asume activo.
Tech Stack: Git/GitHub, Markdown/JSON, clientes locales existentes.

- [x] Revisar reglas y conservar cambios anteriores.
- [x] Preparar contexto común, contratos y registro central.
- [x] Registrar excepción de presupuesto DeepSeek solicitada por usuario.
- [x] Publicar coordinación en rama de integración.
- [x] Crear tres clones independientes y publicar sus ramas.
- [x] Verificar remoto/base/estado y actualizar registro.
- [ ] Conectar proyectos dentro de cada aplicación y verificar una tarea real cuando sus permisos/cuotas lo permitan (no confundir con preparación).

Verificación: JSON válido, enlaces locales existentes, git diff --check, hashes remotos y estado limpio. No cambios Android: no build/lint ni suites innecesarias. No secretos, backups ni evidencia privada en Git.
