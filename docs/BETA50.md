# Beta50 · migración de firma y respaldo completo

2 de noviembre de 2026,0.35.1-beta50/code50. Base c715267 (beta49) recuperada de GitHub. Rama codex/beta50-migration.

## Cambios

- Gboard libera foco al pausar Inicio, pulsar Atrás, confirmar y limpiar búsqueda. SEARCH queda en avatar y barra táctil, el detector del fondo ignora esa acción.
- Respaldo14 incorpora estilo/duración/intensidad de desbloqueo y modo ligero de los archivos de preferencias existentes. Importa versiones1–14 con validación de tipos/rangos. Selecciones de beta49 conservadas.
- Se conservan las seis entradas, diagnóstico, fondo PS4, multimedia y mejoras de beta49.

## Migración autorizada

La firma de beta49 era729385fc… y no se dispone de su clave privada. El usuario autorizó expresamente respaldo, desinstalación, reinstalación y restauración el2 de noviembre.

Antes de desinstalar se extrajo el APK beta49 y verificó contra SHA256184c8b78845bd4579d8ecedb52fdfe6a1e68d3439198f14626ceab6399d4c1f2. Se exportó respaldo13 desde Aura,97 ajustes,7 favoritas,5 imágenes,0 omitidas. Se comprobó JSON/base64/referencias/firmas PNG. Copia de migración14 enriquecida con las cuatro preferencias observadas en los diálogos existentes. Original intacto y artefactos privados fuera de Git.

APK beta50 compilado/lint y firma comprobados antes de desinstalar. Desinstalación autorizada e instalación normales respondieron Success. Importación desde el selector Android confirmó Ajustes restaurados. Se volvió a seleccionar Aura como inicio predeterminado mediante diálogo del sistema y se confirmó el rol HOME.

Exportación posterior verificada contra la copia de migración:101 ajustes coincidentes y5 imágenes idénticas byte a byte,0 diferencias. Estilo NONE,271ms,intensidad100%,modo ligero desactivado. Se preservaron las elecciones del usuario.

## Validación

assembleBeta/lintBeta correctos1m49s,50 tareas,45 ejecutadas/5 al día. Lint0 errores/33 advertencias/3 avisos. Cuatro avisos Kotlin de APIs obsoletas existentes. No suites automáticas.

APK beta/Aura-0.35.1-beta50.apk,15.530.276 bytes. SHA256908c4e902cc04ef01baf85f71c4a48296460c6307598dd4b65ce7cd14bdc9838. Certificado SHA2562eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514. APK efectivamente instalado extraído para comprobar mismo hash/certificado,version50 y0.35.1-beta50. Clave de esta línea conservada con copia privada fuera de Git.

Revisión física acotada: PS4 y7 favoritas visibles tras reinicio,rol HOME correcto. Al desplazar la lista el teclado permanece cerrado. Tocar búsqueda abre Gboard y volver a Inicio lo cierra, comprobado por estado IME y captura. No se midieron FPS/batería ni se revisaron todos los efectos.

## Límites

Reinstalación revoca permisos especiales y los IDs de widgets no se transfieren mediante JSON. El reproductor aparece en modo básico hasta rehabilitar su acceso especial; clima/widgets pueden necesitar reconexión. No se concedieron permisos especiales automáticamente. El usuario conserva NONE como entrada, por lo que las animaciones no se activaron en la revisión.

El primer intento de build falló por atributos de archivo de OneDrive. El worktree se movió a Documents/Aura-development/beta50 y los intermediarios permanecen en Temp. Configuración SDK/clave,respaldos,APK y capturas excluidos de Git.
