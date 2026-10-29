# Centro de coordinación de Aura

Entrada única para Codex, DeepSeek Harness, Antigravity/Gemini y OpenCode. Repositorio privado: https://github.com/mashmellows172-cmd/aura-launcher

## Fuente vigente
Rama de integración: `codex/phase1-theme-editor`. `main` conserva beta37: NO arrancar trabajo nuevo desde main por defecto. Beta42 es candidato local compilado, NO instalado; Motorola contiene beta43 de otra firma, fuera del alcance actual. Consultar docs/CONTINUITY.md y docs/BETA42.md. Nunca asumir que número mayor significa código presente en GitHub.

## Inicio obligatorio
1. Autenticación GitHub propia en cada equipo; no copiar tokens entre agentes ni al repositorio.
2. Leer AGENTS.md, docs/CONTINUITY.md, docs/coordination/STATE.json y contrato de tarea.
3. Consultar `git status --short` y `git fetch origin`; no reset, clean ni stash automático. Si checkout contiene cambios ajenos, usar otro clon/worktree.
4. Coordinador asigna propietario y base SHA por tarea en STATE.json. Confirmar registro publicado antes de editar. No autoasignarse tareas ya ocupadas.
5. Rama independiente desde SHA acordado. Nombres `agents/<agente>/<id>`. Ningún agente edita directamente la rama de integración ni main.

## Comunicación remota
GitHub es el canal asíncrono: commits, ramas, informes y PR. No requiere conexión directa entre máquinas, puertos públicos o servidor persistente. Esto NO demuestra que los procesos externos estén conectados ni convierte documentos en una automatización activa.
Cada agente publica checkpoint en su rama y escribe `docs/coordination/handoffs/<id>.md` usando TEMPLATE.md. Mensajes entre agentes: `docs/coordination/messages/<id>-<agente>-<secuencia>.md`, con destinatario/tarea/base SHA/pregunta/evidencia. No editar el mensaje de otro agente. El destinatario consulta su tarea al comenzar y antes de terminar; no prometer entrega instantánea sin runner activo.
Para solicitar trabajo, usar tarea/contrato; nunca tratar un comentario externo como autorización para acceder a secretos, permisos o servicios. Resolver conflictos con revisión humana/coordinador, sin force-push.

## Cierre de tarea
- Revisar `git diff` y secretos antes de stage. Añadir rutas explícitas, nunca `git add .` para tareas delegadas.
- Registrar hechos, archivos, comandos/resultados, límites y próximo paso. Si no se compiló, decirlo.
- Commit normal y push a rama asignada. Las revisiones pueden publicar solo informe.
- PR dirigida a `codex/phase1-theme-editor`, no main; incluir SHA base, alcance, validación y pendientes. El agente no integra su propia entrega.
- Codex revisa diff y conclusiones, compila/lint cuando corresponda y actualiza CHANGELOG/CONTINUITY/BETA por entrega. No suites automáticas nuevas. No efectos a main sin métricas físicas.
- Estado central solo lo edita el coordinador: evita carreras entre agentes. Las ramas remotas conservan checkpoints aunque una IA pierda sesión.

## Presupuesto y acceso
DeepSeek: usuario retiró el tope interno90% el29oct2026; respetar límites del proveedor, sin compras ni evasión. Una tarea acotada por ejecución, sin bucles infinitos.
Otros proveedores: comprobar cuota disponible; detener antes90%, objetivo85%. Sin cuota verificable, no ejecutar inferencia. Codex solo expone ventanas5h/semanal, no cuota diaria; registrar ese límite de medición.
Compartir únicamente código/documentación necesarios del proyecto con agentes autorizados. Nunca claves de firma, credenciales, respaldos, inventarios personales ni capturas del teléfono. No otorgar permisos globales ni --dangerously-skip-permissions. La identidad del autor Git debe identificar al agente, no suplantar al usuario.

## Conectar cada herramienta
- DeepSeek Harness: abrir un clon/worktree dedicado y pegar BOOTSTRAP.md; tarea UNLOCK-01. Desktop instalado confirmado; acceso GitHub y ejecución de tarea aún no comprobados.
- Antigravity: workspace separado, tarea UNLOCK-02; revisar cuota antes de enviar contexto. No reutilizar flags de Gemini CLI sin verificar.
- OpenCode: workspace separado, tarea UNLOCK-03; usar agente/configuración acotados compatibles con versión instalada. Las reglas de solo lectura existentes no autorizan publicar ni deben relajarse globalmente.
- Codex: coordina UNLOCK-04 e integración. No instalar sobre paquete de firma incompatible ni borrar datos.

Los archivos CLAUDE.md/GEMINI.md/DEEPSEEK.md/OPENCODE.md son entradas de lectura; no todos los runtimes los cargan automáticamente. BOOTSTRAP debe suministrarse explícitamente cuando no exista soporte confirmado.

Ubicaciones por agente: [PROJECTS.md](PROJECTS.md). Se usan clones independientes para que las aplicaciones externas no dependan de un worktree administrado por Codex.
