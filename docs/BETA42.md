# Aura 0.31.0-beta42 / code42

28 de octubre de2026. Rama de validación codex/phase1-theme-editor.

## Cambios

- PS4: encabezado compacto con avatar cuadrado, Aura, hora/alarmas y ajustes. Clima y calendario desplegables; conserva gestos del avatar y acción de bloqueo para TalkBack.
- Quick Menu lateral translúcido con entrada320ms, cierre accesible, safe insets y desplazamiento. Conserva todas las rutas del menú anterior. Multimedia activo arriba del menú usando el estado existente.
- Ficha de apps con categoría oficial Android y hasta5 shortcuts reales. Botón de detalles separado: tocar la fila sigue abriendo la aplicación. Consulta diferida180ms de categoría solo al foco en reposo, caché96 y trabajo IO. No se solicitan ni conceden permisos.
- Mantiene lista vertical, búsqueda, A–Z, favoritas, iconos normales, packs, reemplazos y widgets. No implementa menú horizontal.

## Revisión

Dos agentes locales implementaron componentes separados; coordinador integró y un tercer agente revisó. Se corrigió pérdida de acción accesible de bloqueo detectada en revisión y cierre del panel al navegar desde audio.

## Validación y límites

Compilación final assembleBeta/lintBeta correcta (1m18s;50 tareas,5 ejecutadas/45 cacheadas). Un intento anterior quedó interrumpido y se repitió la validación completa. Lint:0 errores/30 advertencias/3 avisos. Sin suites automáticas.

APK local beta/Aura-0.31.0-beta42.apk. SHA256: 2cc56ee4e7328eeb8a325b1afcda4bde69da03a4ac8dd93b9f076737b0983914. Certificado original verificado:729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c.

Motorola reconectado e identidad confirmada. Instalación bloqueada ANTES de adb install: contiene0.30.2-beta43/code43 y certificado2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514, distinto al candidato. Git fetch no encontró esa entrega. La clave alternativa local tampoco coincide. No se instaló, desinstaló, degradó ni cambió ningún permiso/ajuste/dato. Se solicitó origen del código beta43 y clave correspondiente para integración segura. No aumentar versionCode a ciegas. Revisión visual de beta42 pendiente.

Anomalía del encabezado beta41: revisión de fuente no identifica una capa que oculte todo el encabezado. El encabezado nuevo no anima su contenedor ni el reloj, pero eso no demuestra causa ni reparación. Pendiente reproducción física. Sin FPS/batería medidos; no promover efectos a main.

Decisión del usuario: continuar esta rama sin recuperar beta43 por ahora. La instalación sobre el paquete actual sigue bloqueada por firma; no implica autorización para desinstalar o borrar datos.
