# Aura Launcher 0.19.0-beta25

## Identidad histórica

Seis contratos visuales compartidos por reloj, tarjetas, menús y muestra del editor. Nothing mantiene el inicio AMOLED principal. Las superficies LG tienen su propio esquema claro dentro del inicio oscuro; los controles y menús anidados heredan colores legibles.

| Tema | Referencia | Material y movimiento |
| --- | --- | --- |
| Nothing / CMF | Matriz luminosa | Negro, puntos brillantes, revelado de dígitos y clima pixelado |
| Aura Bloom | HTC Sense clásico, 2009–2013 | Grafito, hojas flip, biseles y clima suave |
| Aura Prism | Galaxy S III Nature UX, 2012 | Cristal azul, ondas de agua y tipografía ligera |
| Aura Flow | LG G2 Optimus UX, 2013 | Papel cálido, notas, capas y mosaicos de luz |
| Aura Horizon | Xperia Z1 Flow, 2013 | Cristal fino, líneas continuas y auroras |
| Aura Alloy | Motorola RAZR V3, 2004 | LCD azul, segmentos, metal y trama digital |

Son reinterpretaciones originales con referencias identificadas, no copias exactas del firmware. No se distribuyen fondos, iconos ni fuentes propietarios. Los recursos de colección siguen siendo importaciones personales opcionales; algunas carpetas mezclan generaciones. Véase [referencias y procedencia](HISTORICAL-THEMES.md).

**Tema visual → Acabado → Explorar identidad y clima** muestra los componentes reales con datos de ejemplo. La reproducción de esa muestra se activa explícitamente y no cambia el inicio, accede a proveedores ni reproduce audio.

## Reproducción multimedia

El widget aparece durante reproducción detectada y adopta el material del tema. Con acceso opcional a sesiones ofrece título, artista, carátula y controles que la sesión realmente admite. Las acciones se vinculan a la sesión mostrada para evitar controlar otro reproductor tras un cambio.

**Inicio y gestos → Activar carátula y controles** abre una explicación y después los ajustes de Android. Android expone estas sesiones mediante acceso a notificaciones, de alcance amplio. Aura no procesa ni guarda su contenido. No se concede este acceso automáticamente.

Sin acceso, funciona un modo básico de detección de audio local mientras Inicio está visible. No inventa metadatos, carátula ni controles; puede no detectar vídeo silencioso. Los visualizadores son decorativos, no mediciones del sonido. Al ocultar Inicio o desactivar el widget se retiran observadores y trabajo periódico. Las carátulas se reducen fuera del hilo de interfaz y no se guardan.

## Clima y encabezados

Las escenas del clima tienen tratamientos propios, sin sustituir la lectura de Prognoza. AUTO solo interpreta texto accesible de la lectura actual; pronósticos, probabilidades y condición desconocida usan ambiente neutral. Movimiento reducido, alto contraste y animaciones del sistema desactivadas detienen los efectos. Las partículas se dibujan sin recomponer toda la pantalla en cada cuadro.

108 mensajes cortos en español, por tema y franja horaria. Cambian con la fecha y la hora ya observadas por el reloj. El usuario puede ocultarlos. No generan anuncios de accesibilidad continuos.

## Compatibilidad y límites

Mismo paquete y certificado que beta24; actualizar sin desinstalar. Favoritas, temas, fondos, widgets y opciones se conservan. Respaldo formato9, importación1–9; los permisos nunca se conceden desde un respaldo.

La compilación optimizada, lint, revisión de código y entrega se documentan en [QA beta25](QA-beta25.md). Las comprobaciones físicas de contraste, orientación, sesiones competidoras y revocación requieren una sesión con el teléfono desbloqueado. No hay mediciones nuevas de FPS, batería ni aprobación visual del usuario en esta entrega.
