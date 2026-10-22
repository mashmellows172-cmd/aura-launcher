# Aura Launcher 0.12.0-beta17

## Doble toque para bloquear

**Inicio y gestos → Doble toque para bloquear** permite elegir logotipo, fondo libre, ambos o desactivado. El valor inicial es ambos. Aplicaciones, widgets y el índice conservan sus interacciones. El logotipo mantiene su acceso a ajustes con un toque y ofrece una acción de accesibilidad para bloquear.

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

Los perfiles antiguos se traducen al nuevo motor. No se borran favoritas, temas, fondos ni widgets. El respaldo exportado usa formato 3; se importan formatos 1, 2 y 3. No incluye permisos ni autoriza accesibilidad al restaurar.

Actualizar el APK firmado sin desinstalar. Mismo paquete y certificado que beta16. Código MIT, sin publicidad ni permiso de Internet.

## Referencias

- [Bloqueo mediante acción pública de accesibilidad](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService#GLOBAL_ACTION_LOCK_SCREEN).
- [Lecturas de animación en capas gráficas](https://developer.android.com/develop/ui/compose/animation/quick-guide).
- [Observación de desplazamiento anidado](https://developer.android.com/develop/ui/compose/touch-input/scroll/nested-scroll-modifiers).

La compilación optimizada y lint se completaron con cero errores, siete advertencias y dos sugerencias. No se añadieron ni ejecutaron nuevas pruebas unitarias. La revisión de dispositivo y sus límites se registran en [QA-beta17.md](QA-beta17.md).
