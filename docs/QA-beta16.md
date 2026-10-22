# Revisión de Aura beta16

Fecha: 21 de octubre de 2026. Motorola Edge 50 Ultra, Android 17 / API 37.

## Compilación e instalación

- `assembleBeta` y `lintBeta`: completados; cero errores, siete advertencias y dos sugerencias. Advertencias: versión Gradle, recursos de paquetes externos, estados Compose, shrink de recursos y recomendaciones KTX.
- APK optimizado con R8, versionCode 16. Instalado como actualización con el certificado original y datos conservados.
- No se añadieron ni ejecutaron nuevas pruebas unitarias en este hito.

## Pruebas del dispositivo

| Flujo | Resultado |
| --- | --- |
| Vista previa y cancelar | Conserva el tema activo. |
| Pulsación larga sobre fondo | Abre ajustes. |
| Gesto arriba sobre logo | Enfoca la búsqueda. |
| Gesto derecha asignado | Abre Ajustes de Android. |
| Calendario denegado | Agenda desactivada; launcher utilizable. |
| Calendario permitido | Consulta sin error del proveedor; no se crearon ni modificaron eventos. |
| Próxima alarma | Abre Google Clock con el APK corregido; véase AURA-007. |
| Agenda | Abre Google Calendar. |
| Notificaciones sin servicio | Explica el acceso opcional. |
| Notificaciones con servicio | Gesto abajo abre la ventana NotificationShade; después se desactivó el servicio y se comprobó el estado. No se examinaron los mensajes del panel. |
| Orientación e índice A–Z | Editor accesible en horizontal; en el APK corregido se ven 26 letras en tres columnas y tocar Z lleva a Zip Extractor. |
| Fondo propio | Imagen de prueba seleccionada mediante Photo Picker, aplicada con paquete de iconos de prueba; continúa visible tras recrear proceso y eliminar el archivo fuente. |

## Respaldo

Se importó el respaldo real de versión 1 guardado antes de beta16. El formato 2 exportó los campos nuevos y una imagen embebida. Tras restaurarlo y exportarlo otra vez, los objetos de ajustes e imágenes coincidieron exactamente. Se restauraron después las preferencias actuales del usuario, conservando sus nuevas favoritas y retirando los gestos y la agenda temporales de QA.

## Muestra de rendimiento del APK final

Un arranque de proceso frío y ocho desplazamientos: 253 ms de TotalTime, 500 frames y uno marcado con retraso (0,2 %), PSS 326.769 KB. Sin nuevas entradas AndroidRuntime que identifiquen el proceso de Aura durante esa muestra. Había reproducción PiP: la carga del dispositivo no equivale a la muestra histórica de beta14. No es una garantía sostenida ni una medición de autonomía.

## Cierre y limpieza

La exportación final coincide exactamente en ajustes e imágenes con la copia de preferencias actuales que se preparó para conservar los cambios del usuario. Se conservaron sus seis favoritas y AMOLED con su acabado elegido.

Servicio de accesibilidad apagado y conjunto de servicios comparado con el original. Permiso de calendario temporal revocado. Proveedor y archivos QA retirados; imagen de galería y su registro multimedia eliminados. Tiempo de bloqueo y rotación del sistema restaurados y leídos de nuevo para confirmar. El respaldo final `Aura-final-beta16.json` permanece en Descargas para el usuario.

## Privacidad y alcance

Se detectaron preferencias nuevas del usuario durante la pausa y se tomó un respaldo adicional para conservarlas. Las capturas y respaldos con información privada permanecen en `work/qa-beta16`, fuera del ZIP de código fuente. El proveedor y la imagen usados para los temas son propios.

La prueba del servicio se hizo con autorización expresa temporal del usuario; no se eludieron los diálogos de Android. El servicio no recupera contenido de ventanas y su callback no procesa eventos. Abrir el panel mediante accesibilidad exige una autorización que Android presenta como amplia; no se restaura mediante JSON.

No se afirma una equivalencia completa con Niagara ni compatibilidad universal con proveedores de iconos/widgets. Autonomía de 24–48 horas pendiente. Las animaciones siguen en pausa.
