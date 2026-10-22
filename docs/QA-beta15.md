# Revisión de Aura beta15

Fecha: 21 de octubre de 2026. Dispositivo: Motorola Edge 50 Ultra, Android 17.

## Compilación

- `assembleBeta` y `lintBeta`: completados. Cero errores de lint; cinco advertencias no bloqueantes.
- Variante optimizada con R8, mismo paquete y certificado que beta14; actualización con datos conservados.
- No se añadieron ni ejecutaron nuevas pruebas unitarias en este hito.

## Flujos revisados en el dispositivo

| Flujo | Resultado |
| --- | --- |
| Exportar desde el selector Android | JSON `aura-settings`, versión 1; copia original legible. |
| Paquete de iconos appfilter | El drawable vectorial de prueba se aplica a YouTube; las apps ausentes del mapeo conservan el original. |
| Desinstalar el paquete de iconos | Se muestra aviso recuperable y se usan los originales. |
| Cancelar la vista previa de restauración | Conserva los tamaños previos. |
| Restaurar ajustes e imágenes | Se aplicaron texto 20 sp, iconos 56 dp, separación 10 dp y alto contraste; PNG individual tiene prioridad sobre el paquete. |
| Volver a exportar tras restaurar | El archivo incluye las dos imágenes embebidas y los ajustes restaurados. |
| Archivo con tamaño 999 | Rechazado por rango; la configuración válida permanece. |
| Ajustes originales | Se restauró la copia y se retiró el proveedor temporal. La reexportación final coincide exactamente con los ajustes e imágenes originales. |
| Sliders y reinicio de proceso | Aplicar 24 sp / 60 dp / 20 dp conserva los valores al recrear Aura; después se restauraron los originales. |
| JSON excesivamente anidado | Rechazado antes de crear el árbol JSON. |
| Inicio durante vista previa | Descarta la restauración pendiente y vuelve al home sin aplicar. |
| Limpieza | Proveedor y cinco archivos QA retirados. Rotación y bloqueo restaurados a sus valores previos. |

## Límites de la revisión

Se usó un paquete controlado con un recurso vectorial propio. No se afirma compatibilidad con todos los paquetes comerciales, máscaras ni iconos dinámicos. No se completó una medición de batería de 24–48 horas. Las animaciones permanecen en pausa.

Se observaron cambios de orientación durante la automatización. La orientación se fijó temporalmente para estabilizar los toques. La herramienta exige una jerarquía nueva de UI Automator antes de pulsar, para no reutilizar un XML anterior si falla una lectura. No se atribuye una causa confirmada al problema de interacción observado en beta14.

Las capturas, respaldos y datos del dispositivo se conservan en `work/qa-beta15` del workspace y no se incluyen en el paquete de código fuente.
