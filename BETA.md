# Aura Launcher 0.24.0-beta31

Estado actual y limitaciones: [beta31](docs/BETA31.md). Configuración de otro equipo: [desarrollo](docs/DEVELOPMENT.md).

## Nuevo en beta28

Cobertura de176 apps del Motorola en seis temas (1.056 variantes), galería por app, recursos precalculados y ajustes visuales reversibles. [Entrega y límites de validación](docs/BETA28.md). El acabado histórico sigue en revisión.

## Nuevo en beta26

**Tema visual → Iconos → Explorar familias de iconos** permite comparar32 funciones en cada tema. **Usar esta familia** prepara el borrador; **Aplicar tema** guarda. Para elegir un dibujo para una sola app: pulsación larga → **Elegir icono** → **Dibujos de las familias Aura**. Se puede restablecer y no modifica otras apps. [Detalle](docs/BETA26.md) y [evidencia](docs/QA-beta26.md).

## Nuevo en beta25

Identidades históricas en reloj, superficies, clima y menús. En **Tema visual → Acabado → Explorar identidad y clima** puedes comparar la muestra del tema sin cambiar Inicio. En los ajustes de Inicio puedes mostrar mensajes por tema y reproducción multimedia. Carátula y controles requieren acceso opcional en Android; el modo básico no identifica el contenido. [Detalles](docs/BETA25.md) y [verificación](docs/QA-beta25.md).

## Barrido climático por tema

Abre **Clima en el reloj → Animaciones del clima**. Elige **Al entrar al inicio**, **Repetición** o **Desactivadas**; ajusta recorrido e intensidad. El símbolo meteorológico real se desplaza detrás del reloj hasta el extremo del clima. Cada tema tiene sus propias estelas. Movimiento reducido, alto contraste y animaciones desactivadas del sistema detienen el efecto. [Tratamientos y límites](docs/BETA23.md).

## Organizar inicio

Abre **Organizar inicio** desde los tres puntos. Toca un bloque del esquema para cambiar su posición, altura, texto y anchura. **Subir/Bajar** ordena solo dentro de su zona. **Deshacer** revierte el último cambio; **Restablecer** también se puede deshacer. La vista previa indica los bloques ocultos por ajustes o por falta de widgets.

**Guardar distribución** aplica el borrador. **Cancelar** lo descarta. **Guardar y añadir widget** guarda antes de abrir el selector de Android. La vista previa es un esquema, no una captura exacta del widget. El borrador y su historial se conservan al recrear la actividad; salir con Inicio descarta el editor.

## Motorola clásico

En **Tema visual**, elige **Aura Alloy · RAZR V3** y pulsa **Aplicar tema**. Fondo de metal oscuro, reloj LCD azul y dibujos propios de categorías. Conserva la distribución y ofrece iconos normales. [Referencia histórica y alcance](docs/RAZR-design-reference.md).

## Colección retro personal

En **Tema visual → Acabado**, importa el ZIP de tu colección y activa **Recursos de mi colección**. En **Fondo** puedes elegir entre sus imágenes. **Aplicar tema** guarda la selección; **Cancelar** descarta el borrador. [Guía de recursos](docs/RETRO-LIBRARY.md), [novedades](docs/BETA21.md) y [verificación](docs/QA-beta21.md).

## Iconos y temas retro

**Tema visual → Iconos → Temáticos / Normales / Monocromáticos**. La elección es por tema. Normales conserva los iconos de cada app sin acabado Aura. Los retro se renovaron con reloj, fondo, superficies, formas e iconos de categoría originales. **Restablecer estilo del tema** prepara el acabado completo; **Aplicar tema** lo guarda. [Novedades y límites](docs/BETA19.md).

## Tema principal Nothing

Negro AMOLED, tipografía Geist/Geist Mono, reloj de puntos propio y tratamiento monocromático por tema. Busca **Tema visual → Restablecer estilo Nothing → Aplicar tema**. El acabado naranja está en **Acabado → Naranja CMF**; el tratamiento de iconos en **Iconos → Del tema / Monocromáticos / A color**. Inicio, búsqueda, índice, indicadores y ajustes comparten el mismo sistema visual. [Detalle y límites de beta18](docs/BETA18.md).

## Doble toque para bloquear

**Inicio y gestos → Doble toque para bloquear** permite elegir logotipo, todos los espacios vacíos, ambos o desactivado. El valor inicial es ambos. Icono/nombre de las aplicaciones, widgets e índice conservan sus interacciones. Los huecos entre nombre y favorita y entre reloj y menú quedan disponibles para el doble toque. El logotipo mantiene su acceso a ajustes con un toque y ofrece una acción de accesibilidad para bloquear.

Requiere activar voluntariamente **Aura · Gestos del dispositivo** en Accesibilidad. Android muestra una autorización amplia. Esta implementación solo ejecuta dos acciones públicas cuando el usuario realiza el gesto: abrir notificaciones o bloquear la pantalla. No recupera contenido de ventanas ni procesa mensajes. No requiere administrador del dispositivo, root ni un servicio de vigilancia de toques fuera de Aura. Se puede desactivar desde Android en cualquier momento.

## Motor visual nuevo

**Movimiento y efectos** reemplaza el selector anterior.

- Lista: Profundidad, Pliegue 3D, Onda, Órbita, Deslizamiento o Sin efecto. Las transformaciones siguen la velocidad real; la intensidad y el tiempo de respuesta se ajustan por separado.
- El efecto se calcula en capas gráficas sin recomponer cada fila por cada frame. Un reloj de frames por lista funciona durante el desplazamiento y la recuperación; se detiene al quedar en reposo o salir de Aura. No usa capturas de pantalla ni desenfoque de pantalla completa.
- Vista previa con reproducción finita y arrastre, usando la misma función de transformación que las filas reales.
- Índice A–Z: Imán, Resorte, Suave o Sin animación. Indicador ampliado de sección, selección por arrastre y vibración opcional limitada en frecuencia. Una nueva selección cancela el salto anterior para evitar colas.
- Menús: Resorte 3D, Deslizar, Desvanecer o Sin animación, aplicados de forma coherente a los diálogos de Aura.
- Glow de búsqueda más visible; controles de intensidad y movimiento reducido. La preferencia de duración de animaciones de Android se respeta; no se fuerza una frecuencia permanente de pantalla.
- Los deslizadores guardan al soltarlos; la vista previa responde al ajuste local sin escribir cada movimiento en DataStore.

Las transformaciones afectan al contenido de Aura. Las ventanas de otras apps y las transiciones de apertura del sistema dependen de Android.

## Compatibilidad

Los perfiles antiguos se traducen al nuevo motor. No se borran favoritas, temas, fondos ni widgets. El respaldo exportado usa formato 9 y admite importar formatos 1–9. La biblioteca retro se transfiere con su ZIP por separado. No incluye permisos ni autoriza accesibilidad al restaurar.

Actualizar el APK firmado sin desinstalar. Mismo paquete y certificado que las betas anteriores. Código MIT y fuentes Geist bajo SIL OFL1.1, sin publicidad ni permiso de Internet.

## Referencias

- [Bloqueo mediante acción pública de accesibilidad](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService#GLOBAL_ACTION_LOCK_SCREEN).
- [Lecturas de animación en capas gráficas](https://developer.android.com/develop/ui/compose/animation/quick-guide).
- [Observación de desplazamiento anidado](https://developer.android.com/develop/ui/compose/touch-input/scroll/nested-scroll-modifiers).

Resultados actuales y límites de verificación: [beta22](docs/BETA22.md). Los informes de betas anteriores describen sus propias ejecuciones.
