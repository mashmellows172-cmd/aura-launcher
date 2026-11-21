# Beta107 · Tres identidades PlayStation

Registro actualizado el 20 de noviembre de 2026; compilación del 19 de noviembre. Aura 0.73.0-beta107, código 107, paquete `dev.senda.launcher.debug`. Base beta106 `f887378`. Rama `codex/beta107-playstation-identities`. Diseño iniciado el 18 de noviembre.

## Cambios visibles

- Clásica · PS4/XMB: azul profundo, ondas, placas casi cuadradas y Noto Sans ligera.
- Next-Gen · PS5: grafito, luz dorada/blanca y 48 partículas de posición determinista, tarjetas redondeadas, elevación y Noto Sans con encabezados más estructurados.
- Portátil · Vita/Play: azul/cian oscuro, cinco esferas glossy, placas circulares decorativas, Roboto Condensed y detalles discretos de controles físicos.
- Buscador, A–Z, foco, superficies del Centro rápido, controles multimedia y tiempos de apertura comparten la identidad elegida. El fondo decorativo se revela en 240 ms al cambiar de identidad; los controles conservan una sola composición y texto claro.
- Selección en el editor de temas, pestaña Estilo → Identidad PlayStation, con vista previa existente del launcher.

Se conservan los IDs CLASSIC, MIDNIGHT y WHITE para preferencias, Room y respaldos. Medianoche ahora corresponde a Next-Gen; Blanco ahora corresponde a Portátil y pasa a una paleta oscura cian. Las elecciones manuales de fuentes, formas, fondos e iconos conservan prioridad. No se recorta la silueta de iconos originales o personalizados para ajustarla a las placas Vita.

## Implementación y revisión

`Ps4Design` centraliza geometría y cadencia. Los fondos son dibujos originales retenidos, con posiciones y pinceles cacheados. Las escenas PS5/Vita reaccionan mediante la capa existente a desplazamiento y apertura, sin temporizador infinito propio de partículas. Se mantienen las restricciones de movimiento reducido, ahorro, modo ligero, alto contraste y calidad adaptativa. No se añaden dependencias, fuentes propietarias ni recursos extraídos de consola.

Antigravity no ofreció un CLI/canal utilizable. Un agente de desarrollo interno implementó los dos archivos de fondos; el coordinador revisó su diff y un revisor independiente detectó y confirmó la corrección del recorte de iconos y del borde fijo. Se corrigió además un error de compilación por `drawOutline` no disponible en la versión del proyecto: se utiliza un Path cacheado construido desde Outline, sin actualizar Compose.

## Validación local

- `gradlew.bat --max-workers=2 assembleBeta lintBeta`: BUILD SUCCESSFUL, última comprobación incremental en 5 s el 19 de noviembre. Sin suites automáticas.
- Lint: 0 errores, 58 advertencias y 4 avisos. No se presenta como una entrega sin advertencias.
- Revisión source de prioridades manuales, IDs persistentes, superficies, tipografía, dibujo retenido y ausencia de recorte exterior en AppRow.
- Auditoría matemática acotada de 45 pares entre roles de texto/acento y fondos base: contraste mínimo Clásica 6.15:1, Next-Gen 9.42:1, Portátil 7.71:1. No cubre imágenes, colores personalizados, estados ambientales ni cada fotograma compuesto.
- APK local: `app/build/outputs/apk/beta/app-beta.apk`; copia estable ignorada por Git: `beta/Aura-0.73.0-beta107.apk`.
- SHA-256 del APK: `9B7EBED04468E12B550D6C5D4C9ABFA4D1D2B9D7FDA9F99B35BB87DCF53E174B`.

## Instalación y límites

La firma del candidato se comparó con el APK beta106 extraído del Motorola y coincide (SHA-256 del certificado `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`). El primer `adb install -r` falló durante la transmisión. El segundo intento de transferencia devolvió EOF tras cambiar la dirección de red del teléfono; se reconectó al mismo número de serie anunciado por mDNS y se detectó un hash parcial incorrecto. Ese archivo no se instaló. La transferencia íntegra posterior terminó en 480.705 s (15,973,529 bytes). El SHA-256 remoto coincidió con el APK local y `pm install -r` devolvió **Success**, con salida 0. La conexión desapareció antes de consultar versión, ruta y hash del paquete ya instalado. El 20 de noviembre ADB no detecta dispositivos y la reconexión al último puerto agota el tiempo de espera. Por tanto, Android confirmó instalación, pero la verificación posterior y la revisión visual permanecen pendientes; no se afirma haber contrastado el hash instalado. La última consulta de versión previa al éxito mostró beta106. No se desinstaló Aura ni se cambiaron permisos o ajustes.

La revisión visual comparativa de las tres variantes, las transiciones, texto grande, fondos personalizados y fluidez física sigue pendiente. El teléfono estaba en uso en otra aplicación: no se forzó navegación ni se capturó contenido personal. No se afirman FPS ni fidelidad exacta a las consolas. Siguen pendientes de beta106 la importación de la biblioteca retro y configuración de widgets/permisos; no se presenta esa configuración como recuperada.

## Referencias oficiales

- [PS5: interfaz y funciones](https://www.playstation.com/en-us/ps5/ps5-features/).
- [Centro de control PS5](https://www.playstation.com/en-us/support/games/customize-ps5-control-center/).
- [Pantallas de PS Vita](https://manuals.playstation.net/document/en/psvita/basic/screens.html).

Referencias adaptadas a la estructura vertical de Aura: búsqueda, lista, A–Z, favoritas, widgets y apertura con un toque permanecen disponibles.

Código publicado en `codex/beta107-playstation-identities`, commit de implementación `222114b`; documentación de entrega actualizada por separado.
