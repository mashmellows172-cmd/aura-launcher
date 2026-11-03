# Beta51 · centro de estado y movimiento adaptativo

3 de noviembre de2026. `0.36.0-beta51`/code51, base beta50/df8dfb6,
rama `codex/beta51-adaptive-status`.

## Cambios

- Centro de estado en el menú de Inicio/Quick Menu PS4. Reúne inicio
  predeterminado, reproductor, clima, widgets, agenda, respaldo y movimiento,
  con estado y acceso a los flujos existentes. Desplazable y controles48dp.
- Movimiento adaptativo reversible en Diagnóstico de fluidez: suspende el
  clock decorativo PS4 al desplazar la lista, limita actualizaciones en reposo
  y aplica modo ligero temporal bajo ahorro de batería. No cambia los ajustes
  visuales guardados ni la frecuencia de pantalla. El sonido de índice mantiene
  su opt-in/control de frecuencia en un observador separado.
- Entradas por tema una vez por clave, únicamente en RESUMED; una interrupción
  deja contenido visible y no repite la entrada al volver. Movimiento reducido
  usa fundido150ms sin transformaciones espaciales. Ahorro, alto contraste,
  vista previa y animaciones del sistema desactivadas muestran el estado final.
- Diálogos con resorte amortiguado sin rebote. Se conserva identidad PS4,
  lista vertical, búsqueda, A–Z, un toque, iconos y preferencias personales.
- Preferencias de rendimiento observables: restaurar un respaldo actualiza
  el modo ligero/adaptativo en la sesión actual. Respaldo15 añade
  `performance_adaptive` e importa versiones1–15.

## Delegación y skills

DeepSeek Coder en Ollama produjo borradores del centro y de preferencias;
requirieron correcciones e integración manual. Propuestas de fondo/entradas
rechazadas e implementadas mediante supervisión directa. Ver
[registro de delegación](delegation/2026-11-03-local-motion.md) y
[runner local](../tools/local-ai/README.md). No se acredita ahorro neto de
recursos. Skills frontend-design, accessible-animation, Impeccable animate,
ponytail y verificación antes de finalizar.

## Validación y límites

`assembleBeta lintBeta --max-workers=2` correctos en6m33s:50 tareas,
22 ejecutadas/28 al día. Lint0 errores/35 advertencias/3 avisos; las dos
advertencias adicionales son sugerencias UseKtx al editar preferencias.
Revisión fuente y `git diff --check` correctos; runner verificado con
`py_compile`. Sin suites automáticas.

APK15.546.660 bytes,16.384 más que beta50. SHA256
`5fc548b189156a750d249fce68e3a088079ad262484844c9e3bd6d76daa71f9a`.
Versión51/0.36.0-beta51 comprobada con aapt2. Certificado SHA256
`2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`,
igual al APK beta50 extraído del Motorola en esta sesión.

**Instalada el3 de noviembre de2026:** reconectado el Motorola con la dirección
actual facilitada por el usuario. Identidad del dispositivo y beta50 previas
comprobadas; `adb install -r` respondió Success, sin desinstalar ni borrar datos.
Versión51/0.36.0-beta51 y SHA256 del APK instalado coinciden con el candidato.
Aura abre; captura revisada con PS4, lista vertical, búsqueda, A–Z y siete
favoritas visibles. Revisión acotada; pendientes comparación física de
fluidez/reposo, batería y accesibilidad TalkBack. No hay FPS ni mejora
porcentual acreditada. Evidencia privada fuera de Git.

Los permisos especiales y widgets desconectados en la migración anterior
continúan dependiendo de la reconexión elegida por el usuario. No se cambian
permisos ni ajustes del teléfono. Se conserva la selección de desbloqueo NONE.
