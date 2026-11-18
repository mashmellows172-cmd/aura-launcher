# Beta105 · Cristal y continuidad Sony

18 de noviembre de 2026. Aura 0.71.0-beta105, código 105. Base beta104 `8190209`, rama `codex/beta105-sony-continuity`.

## Cambios

- El Centro rápido recibe una capa de portada suavizada de hasta 200 dp, degradado oscuro y bordes tintados. Solo se desenfoca la imagen decorativa, desde API31. Alto contraste, movimiento reducido, ahorro de energía, modo ligero y calidad restringida aplican el acabado ligero.
- El buscador PS4/Xperia se expande desde su posición con una transición de 260 ms y comparte su progreso con la apertura de luz de los resultados. El campo conserva TextFieldValue, selección y composición IME. El gesto de búsqueda conserva el desplazamiento de Inicio. Cerrar devuelve la barra a su forma inicial.
- La tarjeta multimedia adopta una composición lateral con portada que crece en 240 ms, título y artista, progreso teñido con contraste y una fila propia de controles. Ampliar revela saltos y búsqueda confirmada si la sesión los soporta. Los botones dependen de las capacidades reales del reproductor.
- Un reloj y un proveedor de clima se desplazan entre cabecera amplia y compacta, conservando sus accesos a alarmas y meteorología. Calendario ocupa el espacio real disponible y baja de fila si hace falta. La cabecera se recoge al buscar y al descansar la lista, con la misma política de cancelación de beta104.

## Decisiones y conservación

Se reutilizan Compose, Material y las sesiones locales existentes. El cristal suaviza la portada, con degradado alternativo en versiones anteriores de Android. El alcance aprobado corresponde a 1, 2, 3 y 5 de las propuestas. El borrador beta98 de otro worktree se conserva por separado.

La revisión independiente de fuente encontró conflictos de calendario/clima y de objetivos táctiles durante el plegado. Las correcciones y el resultado final se registran abajo. La primera compilación falló por un import faltante y el primer lint encontró un escape incorrecto del SDK en local.properties. Ambos se corrigieron antes de validar la entrega.

## Validación

- `assembleBeta lintBeta` final terminó correctamente en 1 min 31 s, con 0 errores, 54 advertencias y 4 avisos. `git diff --check` correcto. No se añadieron ni ejecutaron suites automáticas.
- Revisión independiente de fuente completada. Corregidos los tres hallazgos de distribución y objetivos táctiles. Segunda revisión sin otros hallazgos importantes en los ajustes revisados.
- APK en `app/build/outputs/apk/beta/app-beta.apk`, paquete `dev.senda.launcher.debug`, 0.71.0-beta105, código 105. SHA-256 `70b5bd3f0697c29d5dbca43626589153417f6ea1711f6bc444f76ca86e983fcc`.
- Se conectó por ADB inalámbrico el Motorola Edge 50 Ultra autorizado. Se verificó beta104 instalada y certificado `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`. El APK de este equipo usa `729385fc…`; la otra clave Android local usa `915f891c…`. Se canceló antes de ejecutar `install -r`.
- Se buscaron archivos de firma en la configuración Android, directorios privados del proyecto y ubicaciones personales habituales del notebook. No se encontró una clave con certificado compatible. El APK anterior extraído para comparar queda fuera de Git.
- Beta105 no está instalada. Beta104 permanece en el Motorola. Se necesita la clave privada compatible por un canal privado y firmado local, manteniéndola fuera de Git. No recuperar la clave privada a partir de un APK.
- La app Fotos estaba en primer plano durante la consulta. No se forzó navegación ni se cambiaron permisos, ajustes o datos del teléfono.

## Pendientes físicos

Revisión de apariencia, cancelación rápida del plegado, interacción con fuentes grandes y RTL, teclado real y composición IME, multimedia con/sin carátula y acciones compatibles. No se acredita ganancia de fluidez ni equivalencia exacta con consolas Sony mediante compilación o revisión de fuente.
