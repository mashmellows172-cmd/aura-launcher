# Beta81 · transiciones Vita y Xperia

13 de noviembre de 2026. 0.58.0-beta81, código 81.

- Pronóstico PS4: deslizar horizontalmente sobre la tarjeta recorre las horas cargadas y cambia la temperatura, el símbolo y la escena. También hay acciones accesibles por hora y botones en el carrusel.
- Búsqueda PS4 y Xperia: marco luminoso animado al enfocar o buscar. Inicio y resultados conservan posiciones independientes.
- A–Z en PS4 y Xperia: la cápsula se retira al soltar y el encabezado de destino recibe un destello al completar el salto.
- Multimedia PS4: la carátula se funde al cambiar de pista y un destello recorre el panel. En modo expansión, los controles se recogen mientras la carátula vuelve al origen.
- Tema: una cinta de luz revela el acabado aplicado sin trasladar las filas. Los efectos siguen las opciones existentes de movimiento, brillo, paneles y clima.

## Verificación

- Primer intento de compilación falló por una referencia a CompositionLocalProvider sin resolver. Corregida antes del segundo intento. Logs locales de Gradle fuera de Git.
- Segundo intento de assembleBeta y lintBeta correcto en 3m2s.
- APK Aura Local de código 81 con certificado compatible con beta80 verificada. SHA-256 del APK: 1A9CF2A662506ADDD973BF027351ABBA50E27B99BC9C0FADA7EDC264031DEB9C.
- ADB confirmó beta80 antes de desconectarse. La extracción del APK anterior quedó incompleta y el puerto anunciado agotó el tiempo de conexión. Instalación y prueba visual pendientes.
