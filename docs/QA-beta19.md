# Revisión de entrega — beta19

21/10/2026. Motorola Edge50 Ultra, Android17/API37. Paquete dev.senda.launcher.debug, código19/nombre0.14.0-beta19, R8, mismo certificado de las betas anteriores.

## Comprobaciones

- assembleBeta y lintBeta correctos,0errores/7advertencias/2sugerencias. No se añadieron ni ejecutaron nuevas suites unitarias; se realizó una revisión dirigida por UI/ADB con permiso del usuario.
-82pares de la paleta declarada,0fallos AA con replica-design/contrast.py. Incluye texto, superficies, contornos y glifos de los nuevos acabados; no certifica iconos externos ni fondos personalizados.
- Elección Normales aplicada en Nothing: iconos de Android con sus colores y sin contenedor de Aura. Regreso a Temáticos aplicado y conservado.
- Cuatro retro aplicados y revisados en inicio/vista previa: Sense, TouchWiz, LG y Xperia. Capturas de reloj, fondos propios, dibujo de categorías y formas. Al terminar quedó activo Nothing.
- Corregido recorte de Acabado en las pestañas con fontScale1.3 del dispositivo; el APK final se revisó en la sección Iconos y muestra Temáticos/Normales. Las opciones inferiores tienen desplazamiento vertical.
- Reconocimiento de doble toque en cuatro espacios vacíos con servicio temporalmente desactivado, evitando bloqueos repetidos: los cuatro invocaron la misma acción de bloqueo (se mostró la explicación de acceso), sin abrir alarmas/calendario.

```json
{
  "header-gap": true,
  "app-row-gap": true,
  "left-margin": true,
  "date-right-gap": true
}
```

- Después de restaurar el servicio por la UI Android, el doble toque en el hueco entre reloj y menú bloqueó realmente el teléfono: suspensión y pantalla de bloqueo confirmadas por Android. Se encendió la pantalla sin desbloquearla y el usuario confirmó el desbloqueo.

```json
{
  "header_empty_space_real_lock": true,
  "power_sleep_or_doze": true,
  "keyguard_showing": true
}
```

- El conjunto de servicios de accesibilidad coincide con el original y Aura permanece activo por autorización previa. No se modificó por escritura directa en secure settings. Tiempo de pantalla restaurado a60000ms; rotación sin cambios.
- Respaldo original/final formato4: solo cambian las opciones de los cuatro temas retro (acento, forma e iconos), al aplicar sus nuevos acabados. Sense pasa de BLUE/BEVELED a DEFAULT/DEFAULT. Se conservan favoritas, gestos, Nothing, acento AMOLED, permisos y efectos. No se cambiaron eventos ni contenido de widgets.

```json
{
  "skin_accent_OPTIMUS": {
    "before": null,
    "after": "DEFAULT"
  },
  "skin_accent_SENSE": {
    "before": "BLUE",
    "after": "DEFAULT"
  },
  "skin_accent_TOUCHWIZ": {
    "before": null,
    "after": "DEFAULT"
  },
  "skin_accent_XPERIA": {
    "before": null,
    "after": "DEFAULT"
  },
  "skin_icons_OPTIMUS": {
    "before": null,
    "after": "AUTO"
  },
  "skin_icons_SENSE": {
    "before": null,
    "after": "AUTO"
  },
  "skin_icons_TOUCHWIZ": {
    "before": null,
    "after": "AUTO"
  },
  "skin_icons_XPERIA": {
    "before": null,
    "after": "AUTO"
  },
  "skin_shape_OPTIMUS": {
    "before": null,
    "after": "DEFAULT"
  },
  "skin_shape_SENSE": {
    "before": "BEVELED",
    "after": "DEFAULT"
  },
  "skin_shape_TOUCHWIZ": {
    "before": null,
    "after": "DEFAULT"
  },
  "skin_shape_XPERIA": {
    "before": null,
    "after": "DEFAULT"
  }
}
```

## Estado de entrega

```json
{
  "service_restored": true,
  "aura_service_enabled": true,
  "screen_timeout_restored": true,
  "timeout_ms": 60000,
  "version_code_19": true,
  "version_name_beta19": true,
  "default_home": true,
  "process_running": true,
  "android_runtime_fatal_seen": false
}
```

## Límites

Revisión dirigida de las funciones modificadas, no certificación de todas las funciones anteriores ni fidelidad píxel a píxel a una ROM concreta. No se realizaron nuevas mediciones de rendimiento, batería o de todas las configuraciones de orientación. Las ilustraciones de categoría cubren17categorías de paquetes; los demás usan fallback Android. Los widgets externos y las barras del sistema conservan el control de su proveedor/Android. Las primeras ejecuciones del guion de navegación requirieron retomar manualmente el selector; los cuatro acabados se revisaron después y no se observó una excepción AndroidRuntime de Aura. Capturas, XML, respaldos y registros privados quedan fuera de los entregables, en work/qa-beta19.
