# Beta105 · Cristal y continuidad Sony

18 de noviembre de 2026. Solicitud aprobada por el usuario, propuestas 1, 2, 3 y 5.

## Diseño

Centro rápido con cristal localizado inspirado en PS5, portada suavizada y bordes teñidos sin perder legibilidad. Buscador expansivo inspirado en Vita con resultados conectados y conservación de posición. Tarjeta multimedia panorámica Xperia con una portada lateral que crece al ampliar y controles de sesión compatibles. Reloj y clima únicos que viajan al encabezado compacto, evitando duplicados durante el plegado.

Decisiones de implementación de Codex. Reutilizar Kotlin/Compose, Material y efectos existentes, sin nuevas dependencias. Desenfoque únicamente de la imagen decorativa y acotado a su tarjeta, disponible desde Android 12. Fallback de degradado con alto contraste, modo ligero, ahorro y calidad restringida. Sin cola ficticia ni control de vídeo no soportado por MediaSession.

## Arquitectura y archivos

- SonyGlass.kt contiene fondo decorativo de portada, iluminación y contraste.
- QuickCenter.kt mantiene sus gestos, orden y progreso confirmado. El medio amplía su propia portada y reserva los controles en una fila independiente.
- ThemedSearchField.kt integra expansión y cierre con un progreso finito y conserva TextFieldValue.
- HomeScreen.kt mantiene estados separados de lista normal y búsqueda, e integra las transiciones.
- Ps4HeaderMotion.kt / Ps4FoldingHeader.kt usan un reloj y un clima por árbol. Cambian posiciones en placement y el viewport solo después de detener el desplazamiento.
- README, CHANGELOG, CONTINUITY y BETA105 registran validación real.

## Plan de ejecución

- [x] Implementar un único encabezado plegable, con objetivos de toque de al menos 48 dp y contenido adaptable.
- [x] Integrar apertura/cierre del buscador y conexión visual de resultados sin reiniciar listas ni cursor.
- [x] Aplicar cristal localizado y tarjeta multimedia expansiva, respetando acciones soportadas.
- [x] Revisar código independientemente y corregir hallazgos.
- [x] Ejecutar assembleBeta lintBeta, sin suites automáticas.
- [ ] Comparar certificado y versión, instalar con adb install -r si el Motorola autorizado está disponible.
- [ ] Registrar evidencia, límites y publicar rama.

## Revisión obligatoria

Pantalla estrecha y fuente grande, reversión rápida de gesto, rotación y teclado, reproducción sin carátula/sin seek, alto contraste y movimiento reducido, dispositivos anteriores a API31. Revisar que el reloj compacto y amplio sean una única instancia y que los gestos conservan su sentido. Métricas físicas y fidelidad visual se declaran solo si se observan.


## Resultado

Código y revisión completados, compilación/lint correctos. La comparación de certificados bloqueó la instalación antes de ejecutarla. Falta la clave 2eb417c5 del equipo que actualizó beta104. Límites y evidencia en docs/BETA105.md. Validación visual y de fluidez pendiente.
