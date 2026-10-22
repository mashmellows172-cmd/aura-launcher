# Revisión de entrega — beta18

Fecha:21/10/2026. Motorola Edge50 Ultra, Android17/API37. APK optimizado R8, paquete dev.senda.launcher.debug, código18/nombre0.13.0-beta18, certificado previo verificado.

## Comprobado

- assembleBeta y lintBeta completados:0errores,7advertencias,2sugerencias. No se añadieron ni ejecutaron nuevas suites de pruebas.
- Paleta declarada:17pares,0fallos AA mediante replica-design/contrast.py.
- Revisión visual de inicio, búsqueda con foco, menús, Inicio y gestos y editor/vista previa. Acabado CMF aplicado y regreso a Nothing. Reloj y accesos mantienen etiquetas accesibles; indicadores de favoritas conservan objetivos48dp.
- Instalación final sin desinstalar. Aura permanece como aplicación de inicio. Proceso activo; no aparece FATAL EXCEPTION en el registro AndroidRuntime consultado de ese proceso. Es una observación limitada a esta sesión.
- Exportación original v3 y final v4: solo cambian skin_accent_NOTHING BLUE→DEFAULT y skin_icons_NOTHING ausente→AUTO. El resto de ajustes exportables permanece idéntico, incluidas favoritas, gestos, efectos y otros temas. No se modificaron eventos ni permisos de calendario.
- Para inspeccionar la jerarquía de UI se desactivó temporalmente el servicio de accesibilidad desde los ajustes Android y se restauró mediante su diálogo. El usuario ya había autorizado mantenerlo activo. El conjunto de servicios finales coincide con el inicial. No se alteraron por escritura directa en secure settings.
- Tiempo de pantalla guardado antes de la revisión y restaurado a60000ms. No se modificó la rotación del sistema.

## Estado final

```json
{
  "service_restored": true,
  "aura_service_enabled": true,
  "screen_timeout_restored": true,
  "timeout_ms": 60000,
  "version_code_18": true,
  "version_name_beta18": true,
  "default_home": true,
  "process_running": true,
  "android_runtime_fatal_seen": false
}
```

## Límites

No es una certificación de fidelidad píxel a píxel con una versión concreta de Nothing OS. Aura utiliza su marca y reloj originales y fuentes Geist OFL. Los iconos monocromáticos dependen de cada aplicación; el respaldo visual es desaturación. No se altera el diseño interno de widgets externos. No se realizaron nuevas medidas de rendimiento, batería ni una revisión completa de todas las funciones anteriores. Capturas, XML, respaldos personales y registros se mantienen en work/qa-beta18, fuera de los entregables.
