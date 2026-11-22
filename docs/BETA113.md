# Beta113 · Coreografía por tema, clima integrado y cápsulas continuas

22 de noviembre de 2026. Versión 0.79.0-beta113, código 113. Base beta112 5e7f20e. Rama codex/beta113-themed-choreography.

## Alcance autorizado

El usuario seleccionó las propuestas 1, 3 y 4. Se mantiene la estructura vertical de Aura. Tres agentes con archivos asignados implementaron movimiento, clima y Centro rápido. Ollama qwen3.5:4b aportó una revisión local acotada del plan (380 tokens generados). Los asistentes revisaron cambios de otro componente y el coordinador integra y compila. Las sesiones externas de OpenCode y Claude no se invocaron: su cuota disponible no estaba verificada.

## Implementación

- Paneles, foco del buscador y A–Z comparten cadencia, curva y tratamiento por familia. Nothing revela segmentos, Sense se articula desde el borde, SIII se expande suavemente, LG despliega capas, Xperia desliza y RAZR combina segmentos y bisagra. PlayStation distingue PS4, PS5 y Vita mediante su identidad elegida. Las entradas desde un elemento conservan su origen y destino medidos.
- La escena de clima utiliza las lecturas conectadas y distingue condiciones nocturnas, viento, granizo, llovizna y lluvia intensa. PS4 incorpora gotas y reflejos, Sense nubes, SIII ondas bajo condiciones húmedas y Nothing segmentos. El clima se reproduce una vez al entrar, reanudar, cambiar condición o volver a abrir la cabecera PS4. La antigua elección de repetición pasa a interpretarse como escena finita en ejecución, conservando el valor guardado.
- Las cápsulas del Centro rápido conservan el detalle durante el cierre para poder regresar al origen. Música reutiliza la transición de carátula y las acciones reales de la sesión multimedia. Las acciones de temporizadores, agenda, alarma y accesos conservan su contenido existente.

## Revisión cruzada

Se corrigieron la dirección RTL de las máscaras y los bordes del buscador, la distancia de cámara de las capas, el respeto del efecto Fundido, la retención del detalle durante cambio de cápsula y la visibilidad de los controles multimedia al terminar o cerrar. Los revisores confirmaron estas correcciones en fuente. La primera compilación encontró una llamada implícita a drawContent dentro de clipPath; se corrigió haciendo explícito su receptor.

## Validación

Validación final: assembleBeta lintBeta --max-workers=2, BUILD SUCCESSFUL en 1 min 35 s, con 0 errores, 59 advertencias y 4 avisos. La compilación anterior tras corregir drawContent también pasó en 3 min 24 s. Se repitió al limitar las lecturas de progreso del Centro rápido mediante estados derivados, evitando invalidar la composición en cada fotograma para condiciones booleanas. git diff --check correcto. No se añadieron ni ejecutaron suites automáticas.

APK privado fuera de Git: C:/Users/victus/Documents/Aura-private-beta113/aura-beta113.apk. AAPT confirma dev.senda.launcher.debug, código 113 y 0.79.0-beta113. Apksigner verifica su firma; certificado SHA-256 2eb417c55c60a8ca8db1fb6f7687c797eea645b423616f9d9495ea1bce7a5514. SHA-256 del APK 3c1a1036304c63236fc5a27b89fae6f8e9d3734e50042d6b5a751786000292c2. La compatibilidad con la firma de beta114 no se ha comprobado.

Revisión física pendiente. No se acredita mejora de FPS por revisión de fuente. Debe cubrir cancelación rápida, RTL, fuente grande, arrastre horizontal del rail, cierre de cápsula y sesión multimedia con/sin carátula. Temporizadores y agenda usan continuidad del contenedor. La carátula usa continuidad de imagen durante la transición y vuelve a su slot real al terminar.

## Continuidad con la instalación actual

El 22 de noviembre ADB informó 0.80.0-beta114, código 114, instalada en el Motorola. La consulta remota de GitHub seguía situando la última entrega disponible en beta112 (5e7f20e). Esta rama implementa la petición sobre esa base pública y requiere integración con el código de beta114 antes de sustituir la instalación vigente. Se solicitó su ubicación al usuario. No se intentó instalar ni degradar el paquete.
