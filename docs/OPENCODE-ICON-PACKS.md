# Encargo para OpenCode: seis familias de iconos históricos de Aura

## Estado del encargo

Propuesta y orden de ejecución preparados por Codex. OpenCode no se encontró en PATH, en los directorios habituales de instalación/configuración inspeccionados ni como herramienta conectada. **No se ha ejecutado este encargo en OpenCode.** No afirmar delegación completada hasta tener un proceso o sesión real y sus resultados. No usar cuentas, claves o proveedores de otros agentes por deducción. La documentación oficial de instalación es https://opencode.ai/v2/docs; comprobar versión y capacidades antes de usar comandos de otra versión.

## Prompt de ejecución

Actúa como responsable de iconografía y desarrollo Android de Aura Launcher. Trabaja en una rama/checkout aislado creado a partir del estado actual del proyecto. No restablezcas ni sobreescribas contribuciones previas. Lee AGENTS.md aplicables y este documento completo. Tu misión es construir seis familias de iconos nativos, originales y coherentes con sus referencias históricas. Codex revisará el diff y los entregables; tu resultado no es por sí solo aprobación de calidad.

### Contexto del producto

Proyecto: `C:/Users/LOQ/Documents/Codex/2026-10-20/qui/outputs/aura-launcher`.
Kotlin y Jetpack Compose nativos, minAPI29, targetAPI37. Beta25 está instalada en un Motorola Edge50 Ultra. Nada de React, WebView ni bibliotecas gráficas externas para resolver iconos. Nothing/CMF sigue siendo el tema principal AMOLED. Conservar distribución, favoritos, widgets, gesto de bloqueo, tamaño de iconos y opciones existentes. No instalar en el teléfono ni cambiar sus ajustes desde tu encargo: entregar un cambio revisable primero.

Referencias del proyecto: `docs/HISTORICAL-THEMES.md`, `docs/RETRO-LIBRARY.md`, `docs/BETA25.md`, `docs/QA-beta25.md`.
Archivos clave:
- `app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt`: AppIcon y caché de bitmaps existente.
- `app/src/main/java/dev/senda/launcher/ui/theme/RetroDesign.kt`: 17 RetroGlyph, glyphFor(packageName), renderRetroAppIcon y dibujos compartidos.
- `app/src/main/java/dev/senda/launcher/ui/theme/IndustrialDesign.kt`: tratamiento Nothing.
- `app/src/main/java/dev/senda/launcher/ui/theme/HistoricalDesign.kt`: perfiles/materiales.
- `app/src/main/java/dev/senda/launcher/ui/ThemeEditorDialog.kt`: editor y borrador por tema.
- `app/src/main/java/dev/senda/launcher/data/IconPacks.kt`: paquetes externos.

### Problema actual

Los 17 dibujos de categoría conservan mucha geometría compartida. Cambiar fondo, grosor y brillo no produce seis familias históricas convincentes. Además glyphFor usa coincidencias de texto en el nombre del paquete: puede confundir aplicaciones y asigna el mismo dibujo a reproductores diferentes. No expandas esa heurística indiscriminadamente.

La nueva familia debe cambiar silueta, perspectiva, densidad, tratamiento del objeto, relieve y proporción del espacio vacío. Los iconos modernos deben seguir identificando la aplicación: dos reproductores o navegadores distintos no pueden volverse indistinguibles por pertenecer a la misma categoría.

### Dirección visual propuesta

| Tema | Gramática de icono | Rasgos a evitar |
| --- | --- | --- |
| Nothing/CMF | Matriz de puntos nítida, composición monocroma, silueta reconocible, señal roja/naranja contenida | Convertir cualquier logo en una mancha uniforme o animar puntos constantemente |
| HTC Sense | Objetos con volumen, materiales físicos, sombra corta, contornos libres, reflejo limitado | Encerrar todos los objetos en la misma baldosa moderna |
| TouchWiz Nature | Objetos orgánicos, color vivo con gradiente suave, reflejo acuoso y contorno flotante | Cuadrados repetidos con un símbolo blanco idéntico al resto |
| LG Optimus | Esmalte claro, pictogramas coloridos, perspectiva frontal y relieve de capas | Reutilizar las auroras Xperia o recolorear los mismos dibujos HTC |
| Xperia Flow | Siluetas limpias, detalle fino y cristal, color luminoso con bordes contenidos | Añadir cajas pesadas a todos los iconos |
| RAZR V3 | Pictogramas compactos de baja resolución aparente, biseles metálicos y acentos LCD | Llamar históricos a logos modernos que nunca existieron en el V3 |

Primero fijar para cada familia una versión/modelo de referencia y documentarla con fuente verificable. Sense clásico y recursos OneM7/Sense5 son generaciones distintas. No mezclarlas silenciosamente. Si se elige una síntesis de época, etiquetarla como reinterpretación, no reproducción exacta.

Colección personal de referencia: `C:/Users/LOQ/.gemini/antigravity/scratch/retro_phones_assets`. Leer imágenes de referencia sin ejecutar archivos. Carpetas no certifican procedencia; algunos iconos HTC son CyanogenMod y varios fondos mezclan generaciones. No copiar originales al APK/repositorio ni subir la colección a servicios externos. Crear artwork nuevo. Logos de apps modernas mantienen proporciones y reconocimiento; no prometer un icono histórico original para una app posterior a esa época.

### Decisiones UX

1. Paquetes integrados en Aura inicialmente; no seis APK adicionales. Se podrán exportar como paquetes Android en otro alcance si se solicita.
2. Mantener modos Temáticos, Normales y Monocromáticos. Añadir opciones solo si aportan una decisión útil, con vista previa y Cancelar/Aplicar existentes.
3. Prioridad explícita: en Normales, drawable nativo sin recolor/mascara; en Temáticos, reemplazo personal, paquete externo seleccionado (con fallback documentado), colección personal habilitada, familia Aura para coincidencia exacta y finalmente drawable nativo con acabado opcional no destructivo. No cambiar precedencias sin documentar la migración.
4. Para aplicaciones modernas sin artwork propio, conservar el símbolo original y armonizar el marco/escala/sombra. Nunca pintar encima del logo o recortar información para aparentar cobertura completa.
5. Una variante manual por app debe poder elegirse y revertirse. No agregar permisos.
6. Ajustar tamaño óptico: un teléfono fino y una cámara maciza deben verse equilibrados sin deformar su relación de aspecto. Reservar el mismo slot de fila; no mover etiquetas, favoritos o zonas de toque.
7. Iconos decorativos junto al nombre accesible de la app, sin duplicar su lectura. Acciones de edición mínimo48dp. Comprobar alto contraste sobre fondo oscuro, claro y personal.
8. Ninguna animación en reposo para toda la lista. Feedback de pulsación breve, sin alterar medida/layout, respetando movimiento reducido y duración de animaciones de Android.

UI/UX Pro Max: consultas de consistencia de iconos no devolvieron resultados específicos útiles; no inventar recomendaciones de base de datos. Se aplican sus reglas generales de familias coherentes, reconocimiento, tokens, zonas48dp, contraste y ausencia de saltos. Consulta jetpack-compose devolvió LazyColumn, claves estables y estado/colecciones estables; conservar esos principios y la pila instalada, sin actualizar versiones solo por una ficha de la skill.

## Trabajo por etapas y responsabilidades

### A. Investigador de referencias y cobertura

Solo documentación inicialmente. Entregar `docs/icon-packs/REFERENCES.md` y `docs/icon-packs/COVERAGE.csv`.
- Registrar modelo, año, versión de interfaz, URL/origen y qué detalle respalda cada referencia. Si no hay prueba, marcar desconocido.
- Inventariar los17 dibujos actuales, precedencias, aplicaciones que colisionan en categoría y rutas reales de renderizado Nothing/retro.
- Definir32 funciones objetivo: teléfono, contactos, mensajesSMS, correo, cámara, galería, navegador, música, vídeo, reloj, calendario, calculadora, ajustes, archivos, tienda, mapas, notas, grabadora, radio, clima, descargas, juegos, salud, navegación, documentos, lector, nube, seguridad, automatización, herramientas, comunicación y fotografía.
- Diferenciar funciones abstractas de aplicaciones concretas: cobertura no es tener32 categorías si50 apps se confunden entre sí. No inventar cifras del dispositivo.
- No modificar el renderer. Puerta: referencias y lista de colisiones revisables antes de fabricar toda la colección.

### B. Diseñador: muestra de48 iconos

Crear8 iconos por6 familias: teléfono, mensajes, cámara, navegador, música, calendario, ajustes y archivos. Total48 variantes.
- SVG maestro de cada icono, viewBox consistente y nombres estables. Crear manifiesto con themeId, iconId, versión, autoría/origen y tamaño óptico.
- Cada familia tiene geometría propia; una máscara/gradiente sobre el mismo símbolo no basta.
- Entregar contacto comparativo en PNG y fuentes SVG, con etiquetas de tema/función. Comparar a tamaño real de32/40/48dp, no únicamente ampliados.
- Referencias privadas permanecen fuera del entregable. No exportar scripts, rutas personales o claves.
- Puerta: una persona debe distinguir la familia por la forma/material y la función por el dibujo sin depender solo del color. Registrar lo que todavía no se logró; no autodeclarar fidelidad impecable.

### C. Ingeniero: catálogo e integración

Depende deA/B. Crear archivos pequeños en `ui/theme/icons/`; no seguir aumentando RetroDesign.kt indefinidamente.
- Catálogo puro: tema y coincidencia exacta de componente/paquete a identificador de artwork. Fallback explícito y seguro. Separar catálogo, material y renderer.
- Reutilizar vectores nativos/ImageVector o assets rasterizados cuando aporte ventaja; SVG no se interpreta con una dependencia nueva en cada frame.
- La caché debe distinguir tema, versión de familia, componente, revisión de app, origen y acabado relevante. Bounded cache; decodificar fuera del hilo principal; no crear bitmaps/path complejos por frame.
- Integrar primero las48 variantes y el fallback, preservando Normales, PNG individual, packs externos, colección personal y formas elegidas.
- Añadir galería accesible al editor para comparar origen/variante, sin escribir preferencias antes de Aplicar. Persistir solo cambios necesarios; si cambia el respaldo, diseñar migración que acepte formatos1–9 y no restaure permisos.
- Puerta: compilar variante beta y pasar lint. No atribuir rendimiento a una inspección de código.

### D. Expansión de colección

Depende de la muestra e integración, no fabricar192 variantes antes de revisar8 por familia.
- Completar32 diseños funcionales por6 familias:192 variantes originales.
- Añadir asignaciones exactas para apps conocidas donde el origen esté comprobado. Para apps modernas con identidad propia, preferir su logo legible en el material elegido si no hay variante distinguible.
- Medir cobertura como variantes terminadas, apps con coincidencia exacta y apps que usan fallback, por separado.
- Registrar fuentes, hashes y artefactos reproducibles. No afirmar cobertura total de cualquier app Android.

### E. Revisor independiente y entrega

No modificar los mismos archivos mientrasC trabaja. Revisar el diff después.
- Buscar colisiones, falsos positivos, colores ilegibles, iconos cortados, tamaño óptico desigual, precedencias rotas, claves de caché incompletas y pérdida de configuración.
- Comparar contacto de seis familias, fondo claro/oscuro, acabado personalizado y tamaños pequeños. Distinguir intención artística de evidencia histórica.
- No agregar ni ejecutar suites automáticas: la instrucción vigente limita las pruebas automáticas sin petición explícita. Compilación, lint, revisión de fuentes y revisión visual documentada sí corresponden.
- Toolchain existente: desde el proyecto `& ../../work/run-aura-build.ps1 -Targets @('assembleBeta','lintBeta')`. Pasar Targets explícitos: el valor por defecto del helper incluye pruebas no solicitadas. Redirigir salida a archivo privado del workspace.
- No usar `build-beta.ps1` sin revisar: hoy ejecuta suites y variantes adicionales.
- Entregar informe con cambios, resultados exactos, pendientes, lista de archivos y límites de fidelidad. Codex revisará antes de firmar/instalar. No desinstalar, publicar, cambiar claves, activar permisos, ejecutar borrados o modificar configuraciones globales.

## Otras mejoras estéticas propuestas para una etapa posterior

- Galería comparativa de tema completo usando las mismas apps/reloj/clima, para ver diferencias antes de aplicar.
- Tres controles por tema: fidelidad de época, intensidad de material y contraste. Usar presets coherentes antes de una colección de deslizadores difíciles de entender.
- Armonizar icono, reloj, tarjeta de música y clima mediante materiales compartidos, sin uniformar las seis familias.
- Validar fondos personales con una base/sombra local opcional detrás de texto, nunca oscurecer toda la pantalla sin control.
- Perfeccionar estados pulsado, seleccionado y deshabilitado con el gesto visual de cada época; reservar animaciones grandes para clima/media y mantener reposo tranquilo.

## Formato de devolución de OpenCode

1. Tarea y commit de partida; archivos tocados y cambios reales.
2. Referencias verificadas y aspectos reinterpretados.
3. Contacto visual y manifiesto; cobertura exacta y fallback.
4. Comandos ejecutados y salida decisiva; no afirmar verificación no realizada.
5. Riesgos o fallos pendientes y siguiente tarea dependiente.
6. Confirmación de que no se instaló ni se cambiaron datos/permisos del Motorola.
