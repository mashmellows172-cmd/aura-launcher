# Pruebas Android

La UI nativa se recorre por textos y descripciones de accesibilidad con `uiautomator`, sin probar servicios del launcher de referencia.

1. Compilar `build-beta.ps1` y actualizar el APK firmado con `adb install -r`.
2. Compilar la app desechable con `gradlew -PincludeQaProbe=true :qa-probe:assembleDebug` usando el mismo entorno JDK/SDK del script de compilación.
3. Mantener el teléfono desbloqueado. Retirar temporalmente cualquier ventana PiP que cubra controles.
4. Ejecutar `python e2e/catalog_smoke.py --address IP:PUERTO`.

`device.py` también permite `snapshot`, `capture`, `tap`, `type`, `key` y `assert`. Las capturas y resultados quedan en `work/qa-beta14` del workspace, fuera del código distribuido. La app de prueba se elimina al finalizar. El script no desactiva ni desinstala aplicaciones del usuario.

Casos restantes y sus resultados: `../replica/test-plan.md` desde la carpeta del proyecto hermano. El permiso de cada widget y la configuración de su proveedor se verifican en el dispositivo. Las pruebas no compran, envían mensajes ni modifican alarmas o eventos.
