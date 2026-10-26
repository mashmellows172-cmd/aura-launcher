# Aura 0.26.0-beta35 · barra A–Z personalizable

26 de octubre de2026. Implementada, compilada e instalada.

Siete efectos: Imán, Onda, Lupa, Cascada, Órbita, Resorte y Suave; además Sin efecto. El menú de Inicio incluye Barra A–Z, también accesible desde ajustes de efectos. Vista previa interactiva y controles independientes de intensidad, respuesta, tamaño de burbuja, permanencia y vibración. Restablecer afecta solo a la barra. Texto grande activa dos columnas para mantener accesibles las últimas letras. Se conserva la lista vertical y la apertura con un toque.

Transformaciones de letras en graphicsLayer y posición del indicador en Layout; sin bucle permanente. Movimiento reducido, alto contraste y animaciones desactivadas del sistema limitan el movimiento. OpenCode implementó únicamente persistencia y respaldo12; el agente principal revisó esos cambios e implementó interacción/UI. Respaldos anteriores1–11 siguen admitidos. No se ejecutó una prueba de ida y vuelta de respaldos.

Validación: assembleBeta/lintBeta BUILD SUCCESSFUL en2m18s;0 errores,26 advertencias,3 avisos. Sin suites automáticas. Certificado anterior conservado. Motorola ZY22K88ZFL: pm install -r Success y PackageManager confirmó code35/version0.26.0-beta35. SHA256 de APK local, transferencia y Descargas coincidente: `8e6eb967cc57adb41803e8b344184eb72424e52d7462f9078eafdb005dd3b4bd`. No se cambiaron permisos ni ajustes ni se forzó navegación.

Pendiente: revisión visual física de efectos, texto grande, arrastre de vista previa y convivencia con scroll; no se midieron FPS. Este hito no completa el pulido nocturno: siguen Nothing sin contenedores circulares y revisión de formas/transiciones por tema.
