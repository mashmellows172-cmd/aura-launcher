# Beta108 · Recuperar identidad Sony y compartir funciones

**Goal:** Corregir las variantes Sony rechazadas y ofrecer Centro rápido en las siete familias sin convertirlas en copias azules.

**Architecture:** Reutilizar estado, controles y gestos existentes. Los materiales, fuentes, formas y cadencia proceden de los perfiles históricos; conservar IDs, preferencias y prioridad de recursos manuales. Kotlin/Compose, sin dependencias nuevas.

## Diseño

La beta107 confundía diferenciación con decoración: esferas gigantes y controles dibujados como wallpaper no equivalen a LiveArea; grafito con oro no reproduce por sí solo PS5. PS5 recupera blanco neutro, paneles compactos, radios moderados y haces tenues sobre azul/gris oscuro. Vita recupera azul saturado, bandas luminosas y brillo en las placas de apps; se retira el mando ficticio de fondo. Mantener lista vertical de Aura: no restaurar menú horizontal ni inventar indicadores interactivos.

Centro rápido se abre también en Nothing, HTC, SIII, LG, Xperia y RAZR. Transporte, alarmas, agenda, temporizadores, nota y miniatura existentes se comparten. Cada familia utiliza su iconografía funcional, tipografía y material: puntos negros, grafito mecánico, agua, papel, cristal o metal/LCD. El texto debe derivar de la superficie real, especialmente en LG claro. Los iconos personales y normales no se recortan ni se reemplazan.

## Pasos

- [x] Corregir paletas, radios, fuentes y fondos PS5/Vita; mantener Clásica.
- [x] Habilitar Centro rápido en las siete familias y eliminar colores/iconos PS4 fijos en los controles compartidos.
- [x] Aplicar silueta, acabado y cadencia históricos al Centro rápido, miniatura y botones; respetar alto contraste, movimiento reducido y ahorro.
- [x] Compilar assembleBeta lintBeta; revisión source y contraste acotado, sin suites automáticas.
- [x] Registrar resultados, límites y entrega beta108; publicar rama propia.

## Referencias verificadas

- https://blog.playstation.com/2020/10/15/first-look-playstation-5s-next-generation-user-experience/
- https://manuals.playstation.net/document/en/psvita/basic/screens.html (pantalla Inicio y LiveArea)
- https://www.sony.com/en/SonyInfo/design/stories/PS5/

UI/UX Pro Max: búsqueda específica sin coincidencias; búsqueda Compose «animation» devuelve separación de lógica UI, estado UI y APIs de animación. Las referencias Sony gobiernan la dirección artística, no una plantilla genérica de la skill.

## Límites y criterios de revisión

No afirmar fidelidad exacta ni fluidez física sin capturas/comparación en Motorola. No instalar con firma incompatible. No modificar permisos. Nada de suites de iconos automáticas. Comprobar que cambiar tema no altera ajustes, que LG no hereda texto blanco sobre papel, que la miniatura usa el tema activo y que fondos manuales siguen teniendo prioridad.

## Resultado
Implementación y revisión independientes cerradas; compilación/lint correctos. Contraste auditado solo sobre roles base. La ficha BETA108 registra errores intermedios corregidos, hash, certificado y límites. Beta108 instalada el 21 de noviembre sobre beta107; firma, versión, hash instalado y HOME verificados. Fidelidad visual física pendiente; no se confunde con validación source.
