# Aura Launcher 0.11.0-beta16

## Inicio y gestos

⋮ → **Inicio y gestos**.

- Próxima alarma del sistema, con acceso a su aplicación. Hora, fecha y clima mantienen sus accesos.
- Próximo evento de los calendarios visibles durante siete días. Requiere permiso opcional de lectura; consulta solo mientras Aura está visible. No escribe eventos ni los incluye en respaldos.
- Cuatro gestos sobre el logo o el fondo libre: buscar, abrir notificaciones, abrir una app o no realizar ninguna acción. La lista, el índice alfabético y los widgets conservan sus gestos.
- Abrir notificaciones requiere activar voluntariamente **Aura · Abrir notificaciones** en Accesibilidad. Android muestra una autorización amplia: actívala solo si quieres esta función. La implementación abre el panel con una acción pública; no recupera contenido de ventanas ni procesa eventos de otras apps.
- Orientación del launcher: seguir sistema, vertical u horizontal. La política de Android puede limitar estas opciones en otros dispositivos.
- Pulsación larga sobre el fondo libre para abrir ajustes.
- Índice alfabético en tres columnas en horizontal para conservar letras legibles; en vertical mantiene una columna.

## Temas con vista previa

⋮ → **Tema visual**.

Selecciona un tema y ajusta su acento, forma del contenedor, paquete de iconos instalado y fondo. La vista previa permite revisar el conjunto antes de **Aplicar tema**. Cancelar conserva el tema actual. Cada tema recuerda sus elecciones; la distribución del inicio se mantiene.

El fondo elegido se copia al almacenamiento privado de Aura al aplicar, a resolución acotada. La imagen original puede eliminarse después. Los paquetes de iconos deben permanecer instalados; las apps no cubiertas conservan su icono. El alto contraste tiene prioridad sobre el fondo y el acento.

## Respaldo y actualización

El JSON exportado usa versión 2 e incluye los nuevos ajustes. Se siguen importando respaldos versión 1. No exporta permisos, acceso de accesibilidad, datos de calendario ni IDs de widgets.

APK optimizado con R8, versionCode 16 y mismo certificado de las betas anteriores. Actualizar sin desinstalar conserva los datos. Las animaciones permanecen en beta12 y siguen en pausa.

## Validación

`assembleBeta` y `lintBeta` completados; cero errores, siete advertencias y dos sugerencias. No se añadieron ni ejecutaron nuevas pruebas unitarias en este hito. Consulta [QA-beta16.md](QA-beta16.md) para el alcance real de la revisión en el Motorola. La autonomía de 24–48 horas sigue pendiente.

## Referencias técnicas

- [Restricciones de apertura de actividades y PendingIntent](https://developer.android.com/guide/components/activities/secure-bal).
- [AlarmManager](https://developer.android.com/reference/android/app/AlarmManager).
- [CalendarContract.Instances](https://developer.android.com/reference/android/provider/CalendarContract.Instances).
- [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService).
