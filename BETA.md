# Aura Launcher 0.16.0-beta21

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

Los perfiles antiguos se traducen al nuevo motor. No se borran favoritas, temas, fondos ni widgets. El respaldo exportado usa formato 6 y admite importar formatos 1–6. La biblioteca retro se transfiere con su ZIP por separado. No incluye permisos ni autoriza accesibilidad al restaurar.

Actualizar el APK firmado sin desinstalar. Mismo paquete y certificado que las betas anteriores. Código MIT y fuentes Geist bajo SIL OFL1.1, sin publicidad ni permiso de Internet.

## Referencias

- [Bloqueo mediante acción pública de accesibilidad](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService#GLOBAL_ACTION_LOCK_SCREEN).
- [Lecturas de animación en capas gráficas](https://developer.android.com/develop/ui/compose/animation/quick-guide).
- [Observación de desplazamiento anidado](https://developer.android.com/develop/ui/compose/touch-input/scroll/nested-scroll-modifiers).

La compilación optimizada y lint se completaron con cero errores, siete advertencias y dos sugerencias. No se añadieron ni ejecutaron nuevas pruebas unitarias. La revisión de dispositivo y sus límites se registran en [QA-beta17.md](docs/QA-beta17.md).
