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
