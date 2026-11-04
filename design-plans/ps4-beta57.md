# PS4: cinco refinamientos aprobados

Base af1b3c7. Aprobación explícita del usuario: aplicar cinco propuestas y luego instalar.

## Evidencia y decisión
Superficie Inicio PS4 → Header/AppRow/Ps4QuickMenu/Ps4AppDetails/Ps4Wallpaper. Contrato: conservar lista vertical, un toque y preferencias; representar barra funcional, placas, Quick Menu, ficha y fondo PS4. La aprobación establece nueva intención visual; no se presentan estas propuestas como defectos demostrados por captura.

## Ejecución acotada
1. HomeScreen Header PS4: conservar avatar/reloj y menú; añadir línea inferior funcional con calendario, clima y controles ya existentes sin acciones ficticias, separación fina y alineación. No reducir área48dp ni duplicar atajos.
2. AppRow PS4: placas cuadradas y borde más definido; respetar tamaño configurado, color nativo y reemplazos. Conservar foco asentado y sin escalas durante scroll.
3. Ps4QuickMenu: encabezado perfil/menú y jerarquía por grupos de acciones existentes. Separadores finos, acciones48dp y esquinas rectas, sin nueva animación/blur. No reordenar callbacks ni inventar funciones.
4. Ps4AppDetails: encabezado de icono+título+categoría, acción Abrir destacada y accesos directos con separadores. Conservar validación/errores/consultasIO existentes.
5. Ps4LiveWallpaper: degradado azul-negro, menor brillo detrás de texto y profundidad lateral. Conservar fondo estático deliberado; no activar reloj ni más pasadas. Tipografía Noto existente y escalado accesible.

## Reutilización y exclusiones
Reutilizar AppIcon, MaterialTheme, HeaderShortcut, los callbacks y formas existentes. Sin nuevas dependencias, servicios, permisos, datos falsos ni fuentes Sony no disponibles. Otros temas excluidos. No alterar firma ni exportaciones.

## Verificación y registro
Compilar assembleBeta lintBeta; comprobar overflow/foco/contraste y apertura única en Motorola si disponible. No declarar fluidez medida sin evidencia. Actualizar MASTER/CONTINUITY/CHANGELOG/README/BETA57 con límites. Si firma incompatible detener antes de instalación; nunca desinstalar.
