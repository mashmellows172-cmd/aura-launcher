# Beta74 · Unificación multimedia

12 de noviembre de 2026 · 0.52.0-beta74 · código 74 · rama `codex/beta74-unified-media`.

## Cambios

- Inicio muestra una fila multimedia compacta antes de favoritas durante la reproducción. Se desplaza con las apps. A–Z incluye la fila en sus índices cuando corresponde y la lista conserva las claves de las aplicaciones.
- **Inicio y gestos → Multimedia solo en el menú** retira la fila sin desactivar el reproductor. La preferencia se guarda y se incluye en el respaldo. La vista previa sigue el mismo modo.
- En PS4, la fila abre la ficha de beta73 desde la posición de la carátula. Conserva progreso real, búsqueda compatible, pausa y reanudación. Anterior y siguiente dependen de las capacidades de la sesión. El menú rápido PS4 conserva su tarjeta.
- Los demás temas conservan sus controles completos desde la fila o el menú. Se mantienen los iconos temáticos y la política de calidad adaptativa de beta73.

## Registro de operaciones

| Operación | Resultado |
| --- | --- |
| Consultar GitHub | `main` apunta a beta37 (`0714c7c`). La última beta publicada como rama era beta73 (`73fb395`), descendiente de beta72 (`5e84283`). |
| Comparar variantes | La beta59 publicada (`88451d7`, 0.39.1) ya es antecesora de beta73. La beta59 local (0.40.0, cambios sin commit sobre `4aa155f`) contenía el mini reproductor que faltaba. |
| Verificar Motorola | ADB inalámbrico confirmó beta72/código 72. El hash del APK instalado coincide con el registrado en la ficha beta72. No se halló en el APK la preferencia del modo solo menú. |
| Integrar | Se creó un worktree en una rama nueva desde beta73. Se trasladó el cambio funcional y se adaptó a los glifos y sesiones de beta73. La rama local de beta59 y `main` se preservaron. |
| Validar | `assembleBeta lintBeta` completaron en 3 min 46 s, 50 tareas ejecutadas. Lint: 0 errores, 38 advertencias y 3 avisos. `git diff --check` correcto. Sin suites automáticas. |
| Verificar artefacto | Paquete `dev.senda.launcher.debug`, versión 0.52.0-beta74, código 74. Firma local válida, SHA-256 del APK `6d83cff6ec976ad0edb62cfacbf093533903f4ed21783c3185bd03c0259b21a0`. |

## Fallos y diagnóstico

| Incidencia | Resolución o estado |
| --- | --- |
| El puerto ADB anterior agotó el tiempo de espera (10060). | mDNS recuperó la conexión inalámbrica. La lectura del APK instalado terminó correctamente. |
| La beta59 local era un downgrade con firma distinta. | Se descartó la instalación y se integró el cambio en una versión superior. |
| La primera consulta de código con una lista de rutas estilo Bash produjo `Missing argument in parameter list` en PowerShell. | Se corrigió usando filtros `rg -g`. No se modificaron archivos con la consulta fallida. |
| La copia inicial del componente referenciaba un glifo de beta59 que beta73 ya había reemplazado. | La revisión de fuente lo detectó antes de compilar. La fila usa `ThemeSystemIcon` de beta73. |
| Gradle no retiró símbolos de dos bibliotecas nativas. | Empaquetó las bibliotecas intactas y terminó correctamente. El log conserva el aviso y las advertencias de Kotlin. |
| El primer commit terminó con `Author identity unknown` (128). | Se usó una identidad de Codex solo para este comando. La configuración global quedó intacta. |
| Firma local de beta74 (`915f891c…`) distinta de la instalación beta72 (`2eb417c5…`). | Bloqueo de despliegue. Compilar o firmar desde el entorno que firmó beta72/beta73 y comparar certificado antes de `adb install -r`. La instalación y los datos del teléfono permanecen intactos. |

Log completo de compilación en el notebook, fuera de Git, bajo `work/aura-beta74-build.log`. El APK generado queda en `app/build/outputs/apk/beta/app-beta.apk` y sirve como artefacto local de validación. Pendientes físicos: inicio y pausa a mitad de lista, saltos A–Z con y sin fila, TalkBack, apertura de ficha y progreso con sesiones reales.

## Seguimiento de instalación · 12 de noviembre de 2026

- ADB inalámbrico confirmó el Motorola Edge 50 Ultra, beta72 y el mismo hash registrado en BETA72.md. La huella SHA-256 del certificado instalado sigue siendo `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`.
- La búsqueda en perfiles y ubicaciones habituales de Windows encontró dos claves de desarrollo con huellas distintas (`729385fc…` y `915f891c…`). Los almacenes personales de certificados de Windows no devolvieron coincidencias. Se revisó el índice de 24 archivos ZIP pertinentes y ninguno contiene un almacén de firma.
- Las fichas beta72 y beta73 registran la firma compatible en el entorno de compilación anterior. El paso pendiente es firmar beta74 allí y transferir el APK firmado por un canal privado. No se ejecutó la instalación y el teléfono conserva beta72 y sus datos.

## Edición local en paralelo

Con `-PauraSideBySide=true`, `assembleBeta lintBeta` genera **Aura Launcher Local**, paquete `dev.senda.launcher.local`. La beta habitual conserva `dev.senda.launcher.debug`. La edición local permite importar un respaldo de la anterior y conservar ambas instalaciones. Los datos privados, variantes Room, enlaces de widgets y permisos del paquete anterior siguen en la app original. Mantener la misma clave para actualizaciones de Aura Local.

Compilación y lint correctos en 5 min 30 s, 0 errores, 40 advertencias y 3 avisos. APK `beta/Aura-0.52.0-beta74-local.apk`, código 74, certificado SHA-256 `915f891c1f55b7dc7d01b7ee31504c97d55952515449c6857d0d55485ddea966`, hash SHA-256 `fd12ad8dd3f12bde204d794848227afa743d7c8ca084b03c4e78df40c853131e`. Log completo local `work/aura-beta74-local-build.log`.

El primer intento por ADB inalámbrico devolvió `failed to install` sin código del instalador y el dispositivo desapareció de ADB. Al reconectar, consultar el paquete `dev.senda.launcher.local` antes de repetir. Tras reconectar, una comprobación previa falló por colisión con una variable reservada de PowerShell. Se corrigió antes de ejecutar la instalación. El reintento con `adb install --no-streaming` respondió Success. Se verificó código 74 y SHA-256 instalado igual al APK local. Beta72 sigue presente y conserva el rol HOME. Pendiente: exportación e importación del respaldo actual.
