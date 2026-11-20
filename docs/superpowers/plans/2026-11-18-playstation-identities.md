# Beta107 · Identidades PlayStation
18 de noviembre de 2026. Base beta106 f887378. Rama codex/beta107-playstation-identities.

## Diseño aprobado y decisiones
El usuario solicita tres variantes diferenciadas, referencias PS4/XMB, PS5 y Vita/Xperia Play, con legibilidad y transiciones coherentes. Mantener lista vertical, búsqueda, A–Z, favoritas, un toque, widgets y accesos. El stack es Kotlin/Jetpack Compose.

Los IDs guardados CLASSIC, MIDNIGHT y WHITE se conservan para preferencias, Room y respaldos. Sus etiquetas pasan a Clásica PS4/XMB, Next-Gen PS5 y Portátil Vita/Play. La antigua Blanco pasa a un tema oscuro cian; decisión de implementación para la dirección retro-tech y para conservar la polaridad clara de texto en los cambios entre variantes.

- Clásica usa gradiente azul y ondas, placas cuadradas y Noto Sans Light.
- Next-Gen usa grafito, luz dorada/blanca, partículas retenidas, tarjetas redondeadas y tipografía Noto más estructurada.
- Portátil usa cian, esferas glossy, contenedores circulares, Roboto Condensed y detalles de controles físicos decorativos.
- Las elecciones manuales de fuente, forma, imágenes e iconos conservan prioridad.
- Fondo y colores se coordinan con el mismo cambio de estilo. No añadir bucles permanentes ni recursos de consola propietarios.

## Arquitectura
theme/Ps4Design.kt centraliza geometría y tiempos. Ps4PaletteColors/AuraTheme/ThemeTypography/HistoricalDesign definen colores, fuentes y formas. Ps4Material, QuickCenter, SonyGlass, buscador, A–Z y foco consumen esos tokens. Ps4LiveWallpaper y Ps4IdentityBackdrop resuelven fondos retenidos. El selector muestra identidad y muestra real.

## Secuencia
- [x] Perfiles, etiquetas y compatibilidad de datos.
- [x] Materiales, tipografía y superficies coherentes.
- [x] Fondos originales por variante, delegados a sony_backdrops107.
- [x] Selector y transición conservando legibilidad.
- [x] Revisión independiente y auditoría de contraste acotada.
- [x] assembleBeta lintBeta, sin suites automáticas.
- [x] Actualizar ficha, historial y continuidad.
- [ ] Publicar rama. Instalación con firma compatible y revisión física: estado real en BETA107.md.

## Delegación
Antigravity no ofrece CLI/canal utilizable en este notebook. No se afirma haberlo ejecutado. Agente de desarrollo paralelo autorizado para dos archivos de fondos, revisión del diff por coordinador y revisión independiente final.

## Verificación
Comprobar alto contraste, movimiento reducido, ahorro, personalizaciones, fuente grande, cancelación al cambiar de paleta y persistencia de IDs. No declarar FPS o fidelidad exacta sin prueba física.

## Referencias
https://www.playstation.com/en-us/ps5/ps5-features/
https://www.playstation.com/en-us/support/games/customize-ps5-control-center/
https://manuals.playstation.net/document/en/psvita/basic/screens.html

## Cierre técnico · 19 de noviembre
Implementación y revisión source terminadas; assembleBeta lintBeta correcto. Auditoría acotada de contraste y corrección de recorte revisadas. Entrega e instalación se registran en docs/BETA107.md; la revisión visual física sigue pendiente y no se marca como realizada.
