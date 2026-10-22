# Aura: estabilidad, personalización y Motorola clásico

## Intención y autorización

El usuario aprobó ejecutar de forma autónoma el plan de ocho puntos presentado en el chat: cerrar beta21; mejorar estabilidad, rendimiento, uso y estética; añadir un tema Motorola RAZR V3; entregar una beta firmada con fuente y evidencias. También autorizó instalar y usar Superpowers. Las preguntas y la aprobación adicional del diseño quedan omitidas por su petición explícita de continuar mientras duerme.

El límite humano es no superar el 95% de cuota usada. Se consultarán ambas ventanas compartidas entre hitos; 90–92% es el margen operativo de cierre. Los porcentajes no constituyen un presupuesto exacto de tokens. No se consume cuota de forma artificial una vez cumplido el objetivo.

## Base

Kotlin 2.2.10, AGP 9.2.1, Compose BOM 2025.05.00, API 29–37, JDK17. Beta21 final instalada y firmada con el certificado existente. La app no requiere Internet ni analítica. Nothing/CMF sigue siendo el tema principal. Las imágenes de fabricantes permanecen en una biblioteca privada opcional, fuera del código MIT.

## Enfoque

Se conservan Kotlin/Compose y las APIs públicas existentes. Se elige ejecución nativa en el mismo proyecto para mantener la ruta del SDK, los scripts y el APK que usa el usuario; el proyecto no tenía Git. Se inicializa control de versiones local en una rama de trabajo, sin remotos ni publicación. Crear una app nueva o migrar frameworks añadiría riesgo sin resolver las necesidades descritas.

## Entregables y criterios

1. **Base verificable:** documentar resultados actuales, persistencia de biblioteca, rechazo de archivos inválidos y límites reales de las pruebas disponibles. Un teléfono bloqueado no detiene el trabajo local.
2. **Integridad:** preservar ajustes y recursos anteriores si una operación falla o se cancela; no dejar lecturas obsoletas tras cambios de tema, app de clima o ciclo de vida. Cubrir el comportamiento con pruebas significativas.
3. **Editor visual:** previsualización esquemática del inicio en el editor, selección del bloque, cuatro zonas existentes, tamaños y anchura; guardar explícitamente, cancelar y deshacer. La edición de distribución es una acción explícita del usuario, independiente de cambiar tema. Widgets mantienen su tamaño y autorización propios.
4. **Rendimiento:** comparar la beta optimizada en el mismo Motorola cuando esté disponible. Registrar variante, condiciones y límites; no inferir batería, FPS garantizados ni causalidad de una sola muestra. Mantener trabajo de imagen fuera del hilo UI, cachés acotadas y ciclos que terminen en reposo.
5. **Estética:** controles y superficies coherentes; estados accesibles; contraste sobre fondos reales; Nothing/CMF como principal. Respetar movimiento reducido y texto grande. No certificar como auténticos recursos de procedencia incierta.
6. **Motorola:** un tema adicional basado en referencias del RAZR V3 clásico, con ilustración original y lenguaje de pantalla LCD/metal/carbono. Mantener posiciones y funciones del inicio. El usuario delegó los detalles visuales; se investigarán fuentes antes de afirmar detalles históricos. Los iconos Normales continúan disponibles.
7. **Entrega:** APK firmado para actualización en sitio, archivo de fuente libre de imágenes privadas, sumas SHA-256, guía y reporte con pendientes explícitos.

## Fallos que deben quedar cubiertos

- Deshacer varias ediciones y luego guardar o cancelar sin alterar ajustes previos.
- Mover un bloque al límite de una zona sin cambiar orden de otras zonas.
- Cambiar rápidamente tema/fondo y recibir después una decodificación anterior.
- Importar ZIP malformado, duplicado, con ruta no permitida o imagen dañada conservando la biblioteca activa.
- Retomar la app después de recreación del proceso sin perder selección, favoritas, avatar ni widgets.

## Operación autónoma

Ejecución por hitos con registro persistente, TDD para lógica nueva y defectos reproducibles, comprobaciones proporcionales para UI/configuración y una revisión independiente al final. El usuario no estará disponible para desbloquear el teléfono hasta mañana; las pruebas que lo necesiten se registran como pendientes. Un heartbeat de este chat permite retomar hitos si la aplicación y el PC siguen disponibles.
