# Aura0.26.2-beta37 · formas, tipografía y transiciones

26 de octubre de2026. Compilada, firmada e instalada en el Motorola autorizado.

## Cambios

PS4 incorpora pesos reales Noto Sans Medium/SemiBold/Bold además de Light/Regular, bajo OFL; evita simular esos pesos. Alternativa libre, no Sony SST. Las filas usan altura mínima configurada, permitiendo crecer al texto/iconos grandes. Paneles RAZR comparten esquinas recortadas con buscador/índice; tarjetas clicables resaltan foco de teclado sin alterar su área táctil. Los diálogos usan la silueta del tema.

La animación de lista suaviza el tramo final de velocidad hasta reposo. Los diálogos restablecen su origen de transformación al cambiar efecto; la vista previa respeta movimiento reducido, alto contraste, animaciones del sistema y cambios de duración. Sin bucles permanentes ni bloqueo de refresco alto añadido.

Contenedores de iconos comparten geometría entre recursos temáticos y drawables nativos: bisel real recortado, radios coherentes y transición con movimiento reducido respetado. Nothing conserva ausencia de placa por defecto, iconos Normales, packs y reemplazos. Clave de caché incluye tinta solo si el modo monocromo afecta al bitmap.

## Auditoría delegada

OpenCode revisó fuente en modo lectura. Codex aceptó discrepancia de contenedores y clave de tinta redundante; rechazó la afirmación de que combinedClickable no fuera enfocable, contrastándola con AndroidX. Informe bruto y validación separados en docs/delegation. No añadir un segundo focusable basándose en ese informe.

## Evidencia y límites

Build final después de auditoría: assembleBeta/lintBeta BUILD SUCCESSFUL en2m20s;0 errores,26 advertencias,3 avisos. No se añadieron ni ejecutaron suites automáticas. Revisión de fuente y muestra óptica de cinco pesos con texto español realizadas. Revisión física de todos los temas, texto grande, foco, siete efectos A–Z y medición FPS pendientes; no afirmar fluidez medida ni fidelidad histórica100%.

Certificado anterior conservado. Motorola ZY22K88ZFL: pm install -r Success; PackageManager confirmó code37/version0.26.2-beta37. SHA256 local/transferencia/Descargas: `2b11910de4dc94fdcd6bc01cfbd29163e0700dcec5db4246ad5295dca19643c5`. No se cambiaron permisos ni ajustes ni se forzó navegación en otras apps. Datos conservados mediante actualización.

El plan nocturno de implementación H1–H6 queda entregado con los límites anteriores. Sigue pendiente fuera de este cierre la reconstrucción histórica individual SIII (20/176 aprobados;156 pendientes), sin confundir variantes genéricas con restauración exacta.
