# Beta58 — glow móvil, movimiento e identidad de iconos

3 de noviembre de2026. 0.39.0-beta58/code58. Usuario rechazó las cinco propuestas de transiciones de editor/galería/ficha: no se implementaron.

## Cambios
- Glow PS4: vuelve a seguir clave central durante scroll como antes de beta55. La selección sigue calculándose una vez para toda la lista; respuesta160ms sin escalas de fila ni animación infinita. Modos reducido/contraste conservados. Se revierte deliberadamente la pausa visual de beta55 a petición del usuario; requiere nueva medición de coste.
- Listas: nuevo perfil opcional Seda, profundidad2,5%, desplazamiento máximo6dp y atenuación6% ponderados por velocidad/intensidad existentes. Sin giros. Selector, normalización y respaldo aceptan SILK; preferencias existentes no cambian. Respaldos que elijan SILK pueden ser rechazados por versiones anteriores.
- Reloj: cambio de minuto PS4 con fundido70–100% y desplazamiento3dp. Sin timer nuevo. Respuesta finita de relojes limitada160–280ms. Movimiento reducido evita desplazamiento.
- Multimedia: siete tratamientos temáticos, entrada220ms/salida160ms, escalas.96–.99 y recorridos pequeños según tamaño real. Conserva gracia2,5s, controles y retención. Se retiró shrinkVertically redundante; mantiene un animateContentSize existente. Sin nuevos bucles.
- Iconos: FamilyArtwork resuelve aliases exactos de funciones del sistema antes del pack derivado en Nothing/Sense/Optimus/Xperia/Razr. Colecciones importadas, packs elegidos, ORIGINAL y reemplazos siguen teniendo prioridad desde AppIcon. TouchWiz conserva SIII específico; PS4 conserva su pipeline.

## Diagnóstico y límites de iconos
No cambiaron recursos ni código de iconos beta49→55. Existen176 identidades por seis familias; los archivos no se perdieron. Los packs derivados tienen marcos uniformes y prioridad sobre parte de los objetos autorados: corregida esa prioridad de apps del sistema. No equivale a restaurar todos los iconos OEM ni a sustituir176apps por arte histórico. El estilo repetitivo de terceras apps requiere rediseño individual o recuperar colección importada del usuario; pendiente. No usar labels/categorías para suplantar identidades.

El usuario pidió posponer la identificación/rediseño de iconos terceros durante esta sesión. Mantener corrección core existente y no ampliar ese trabajo hasta retomarlo explícitamente. Revisión cruzada detectó reloj activo fuera de RESUMED: corregido con observador lifecycle cancelable y estado final al pausar; no repite al volver. Primera build/lint correcta4m27s; se recompila final tras esa corrección.

## Validación
Dos agentes locales: revisión de pipeline/iconos y multimedia; supervisor integra glow/lista/reloj y valida. Sin nuevas dependencias ni suites automáticas. Compilación/lint pendientes. Instalación sigue bloqueada por certificado instalado2eb417c5… frente al disponible729385fc…. No desinstalar ni cambiar datos para forzarla. Sin aceptación visual ni mejora de FPS acreditada.

## Resultado final
assembleBeta/lintBeta final correcto4m32s,50 tareas11 ejecutadas/39 al día;0 errores/35 advertencias/3 avisos. Diff revisado, SILK verificado en selector/normalización/backup. APK local beta/Aura-0.39.0-beta58.apk (ignorado). SHA25665B3272BFB68E2543957EB576573A8CEFB6005219937C1C76A355439B6A3D1CB; firma729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c. No instalada por incompatibilidad conocida; no se revisaron físicamente las nuevas entradas ni el coste del glow. No declarar lag corregido.


## Instalación verificada — 4 de noviembre de2026

Solicitud del usuario: instalar la última beta. Fuente remota4aa155f de codex/beta56-delegated, sin cambios de código; worktree independiente beta58 y rama codex/beta58-install. La prueba local beta56-raster-background se conserva aparte y NO forma parte de este APK.

Recompilación assembleBeta/lintBeta con Android user home compatible: BUILD SUCCESSFUL en3m1s,50 tareas ejecutadas;0 errores/35 advertencias/3 avisos. Sin suites automáticas. Se leyó el certificado del APK instalado beta55 y se comparó con el candidato: ambos SHA256 `2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514`. La clave no se copió al repositorio.

APK15.546.664bytes. SHA256 local e instalado coincidentes:
`dac3d0f0d4f36b0204bb49c8f3ab05310cbd85a26601320923ea30b12eda7b71`

ADB inalámbrico, `install -r`: Success. Consulta posterior confirma versionCode58/versionName0.39.0-beta58. No se desinstaló, borró datos ni cambiaron permisos/ajustes del teléfono. No se ejecutó comparación de swipe ni revisión visual: instalación correcta no demuestra corrección del lag. Los bloqueos por firma descritos arriba corresponden al candidato del otro entorno y quedan superados para este artefacto.
