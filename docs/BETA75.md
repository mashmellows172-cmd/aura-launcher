# Beta75 · foco y ficha PS4

12 de noviembre de 2026. Versión 0.53.0-beta75, código 75.

## Cambios aprobados: propuestas 1 y 3
- Un único marco PS4 interpola también al cambiar de fila durante scroll. Lee la geometría en dibujo, sin cambiar alturas; conserva el desplazamiento pendiente cuando se interrumpe la animación.
- Halo azul y destello de llegada de 240 ms al detenerse. Sin bucles perpetuos. Movimiento reducido y ahorro de energía omiten el desplazamiento y el destello.
- Pulsación larga abre la ficha desde la placa del icono; un toque sigue abriendo la app. El botón de detalles usa el mismo origen.
- Ficha con Abrir aplicación, Añadir/Quitar favorita, Cambiar icono y Más opciones. Se conservan Ocultar y Restablecer icono en Más opciones; los otros temas conservan su menú.
- Reutiliza el cierre animado de la ficha antes de abrir el selector de iconos o el menú adicional.

## Validación y operaciones
- Revisión acotada de fuente: sin nuevos efectos infinitos ni cambios de preferencias/almacenamiento.
- Primer intento de Gradle falló por comunicación con daemon dentro del aislamiento. Reintento fuera del aislamiento correcto: assembleBeta lintBeta con auraSideBySide=true; BUILD SUCCESSFUL en 3m31s, 0 errores, 40 advertencias y 3 avisos. Logs locales: work/aura-beta75-local-build.log y work/aura-beta75-local-build-retry.log (fuera de Git).
- ADB dentro del aislamiento no pudo iniciar el servidor. Fuera del aislamiento se reconectó el dispositivo autorizado; identidad Motorola Edge 50 Ultra/ZY22K88ZFL confirmada.
- Aura Local instalada antes de actualizar: beta74, código 74; certificado SHA-256 915f891c1f55b7dc7d01b7ee31504c97d55952515449c6857d0d55485ddea966, comprobado extrayendo su APK.
- Objetivo de instalación: dev.senda.launcher.local mediante install -r. La edición original dev.senda.launcher.debug no se modifica.
- No se ejecutan suites automáticas. Revisión visual y medición física de fluidez pendientes; no se afirma una tasa de FPS.


## Entrega verificada
- APK local: beta/Aura-0.53.0-beta75-local.apk (artefacto excluido de Git).
- SHA-256: 02119CB6509B4F1374CD70532215A3754A30DF5FE0B39F925A49E3BF2437604D.
- Certificado coincide con beta74 Local; package dev.senda.launcher.local, code75. adb install --no-streaming -r terminó Success. APK recuperado tras instalar tiene el mismo hash.
- Edición original sigue en beta72/code72. Sin desinstalación, cambios de permisos ni ajustes.
- Al comprobar disponibilidad visual el usuario estaba en otra app. No se forzó navegación: interacción, tamaños de texto grandes y fluidez física siguen pendientes de revisión.
