# Aura 0.14.0-beta19

## Iconos: elección explícita

Tema visual → Iconos → Temáticos / Normales / Monocromáticos. Se guarda por tema y se confirma con Aplicar tema. Normales muestra el drawable original de Android, sin contenedor ni recolor de Aura; ignora temporalmente paquetes y PNG individuales sin borrarlos. Temáticos conserva esas personalizaciones y, cuando no hay reemplazo, utiliza el estilo del tema. Nothing utiliza el recurso monocromático de Android o desaturación. Los cuatro retro incluyen ilustraciones vectoriales originales para17categorías de apps; las demás conservan su drawable con un soporte coherente con el tema. No se extraen iconos comerciales.

## Doble toque en espacios vacíos

Se acotó el objetivo del reloj a su dibujo visible y el de fecha/clima al contenido, con un mínimo táctil48dp. Las filas de apps solo abren desde icono/nombre; el espacio restante entre el nombre y el botón de favoritas queda libre. El detector de fondo ya existente recibe esos espacios, los márgenes y los huecos entre componentes; los controles, widgets e índice conservan sus propias acciones. Activar Logotipo y espacios vacíos o Todos los espacios vacíos en Inicio y gestos. El acceso de accesibilidad sigue siendo voluntario y se conserva si ya estaba autorizado. Las barras del sistema pertenecen a Android.

## Temas retro

- Aura Bloom · Sense: reloj de dos láminas claras, carbón verdoso, paisaje propio y acabado con relieve.
- Aura Prism · TouchWiz: reloj ligero, azul profundo, motivo propio de semillas/agua y soportes brillantes.
- Aura Flow · LG: cintas de color, superficies ciruela y dibujo de iconos coloreado sobre esmalte claro.
- Aura Horizon · Xperia: reloj fino, formas más angulares, cristal oscuro y ondas luminosas.

Cada tema aplica sus colores, tipografía, formas y recursos al inicio, buscador, controles, menús, diálogos y vista previa. Restablecer estilo del tema prepara un borrador con el acabado completo; Aplicar lo confirma. La distribución general y el motor de animación se mantienen.

## Referencias y límites

Interpretaciones originales inspiradas en HTC Sense clásico, Galaxy SIII, LG Optimus/G2 y Xperia clásico. Fuentes primarias consultadas21/10/2026:

- HTC: https://www.prnewswire.com/news-releases/htc-unveils-htc-desire-hd-and-htc-desire-z-with-new-htc-sense-and-htcsensecom-102944494.html (comunicado de HTC).
- Samsung: https://news.samsung.com/global/galaxy-s-iii-lets-talk-design y https://news.samsung.com/global/natural-growth-of-galaxy-s-iii .
- LG: https://www.lgnewsroom.it/download/2013/09/2013094_LG_G2_Reviewer_Guide.pdf .
- Sony: https://www.sony.com/en/SonyInfo/design/gallery/XperiaZ1/ .

Los rasgos elegidos son una interpretación de Aura; no se redistribuyen wallpapers, logotipos, iconos o tipografías propietarias. Los widgets externos conservan la interfaz de su proveedor. Respaldo formato4 compatible1–4, sin nueva migración destructiva. Código MIT; Geist/Geist Mono conservan SIL OFL1.1.

Resultados de entrega y límites de la revisión: QA-beta19.md.
