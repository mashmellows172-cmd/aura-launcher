# Beta55 — foco PS4 asentado

3 de noviembre de 2026. 0.36.4-beta55/code55. Rama codex/beta55-settled-focus.

## Cambio y revisión
El foco central visual se desactiva durante scroll y se anima al terminar. El foco por teclado/pulsación y la clave compartida de audio permanecen activos. Fondo estático de beta54, preferencias y lista vertical conservados.

OpenCode (nemotron-3.5-super-vl-free) entregó revisión de solo lectura: eventos read/glob/grep, sin shell observado. Se probó su hipótesis de evitar resortes de foco durante scroll. Se rechazó que un State estable en CompositionLocal necesariamente recomponga todas las filas y su confusión entre fondo propio estático e imagen personalizada. Ollama deepseek-coder:6.7b revisó velocidad: sin bug concreto; sugerencias de retirar fallback/reset rechazadas por falta de respaldo. Antigravity IDE recibió una solicitud ask con dos archivos; respuesta no recuperada. Skills: systematic-debugging, ponytail, frontend-design, caveman y verification-before-completion. No nuevas suites.

## Validación
assembleBeta/lintBeta: BUILD SUCCESSFUL en3m11s;0 errores,35 advertencias,3 avisos. APK15.546.664bytes. Firma SHA256 compatible2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514. Instalación ADB install -r exitosa; package code55/name0.36.4-beta55. Hash local e instalado coincidentes:

`38ca4c597671c2aee28e09bb48b5bdfe71736d14224be1029bd2aae6d8d5f9b5`

Dos muestras manuales, cuatro swipes alternados600ms por muestra, Aura confirmada visible antes/después. Sin cambiar permisos ni ajustes del sistema.

| Muestra | Frames | Jank | Mediana | p90 | p95 | p99 |
|---|---:|---:|---:|---:|---:|---:|
|1|237|32 (13,50%)|17ms|32ms|38ms|121ms|
|2|205|50 (24,39%)|21ms|32ms|40ms|53ms|

Beta54 previa:14,74% y3,57%; muestras cortas, condiciones no controladas. NO demuestran mejora. Framestats últimos120 frames válidos: animación mediana1,04ms/p909,29ms; layout0,04/0,06; grabación dibujo0,47/1,49; emisión de dibujo RenderThread4,31/8,77. Son intervalos aproximados de pipeline, no atribución a una función ni perfil GPU. Percentil GPU4950ms inválido excluido.

## Pendiente
No está solucionada la fluidez ni aislada la primera versión regresiva. El fondo estático aún emite curvas/transparencias al renderizador: retener geometría no equivale a rasterizar imagen. Investigar este coste con comparación acotada, sin afirmar causalidad todavía. Evidencia privada fuera del repositorio.
