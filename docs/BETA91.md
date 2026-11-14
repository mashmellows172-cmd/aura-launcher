# Beta91 · Temporizadores y miniatura del Centro rápido

14 de noviembre de 2026. Aura Local 0.64.0-beta91, código 91.

- Hasta doce temporizadores independientes de 1 a 999 minutos, con nombre, pausa, reanudación, reinicio, +1 min y eliminación. La tarjeta compacta muestra el próximo en terminar.
- Los temporizadores persisten en el dispositivo. La cuenta visible se actualiza mientras se muestra y la pantalla está activa. El sistema programa el siguiente vencimiento en segundo plano.
- En Centro rápido → Miniatura puedes elegir Reproductor, Temporizadores, Nota fijada u Oculta. Tocar la miniatura abre su sección directamente desde Inicio con la transición configurada del panel. La opción de nota guarda un solo texto local.
- Los avisos requieren notificaciones de Aura. En Android 12 o posterior, los avisos exactos son opcionales y requieren que la persona conceda acceso en Android. Sin ese acceso, un aviso en reposo puede demorarse.

## Verificación

- `assembleBeta lintBeta` con `-PauraSideBySide=true`: correcto, 0 errores de lint, 55 advertencias y 3 avisos. El primer análisis de lint falló internamente mientras los archivos cambiaban. La ejecución aislada señaló indentación en QuickCenter, corregida antes de la validación final.
- Actualizada por ADB inalámbrico con `install -r` sobre Aura Local beta90 en el Motorola Edge 50 Ultra. Certificado previo y posterior coincidentes. Paquete `dev.senda.launcher.local`, versión y código confirmados.
- SHA-256 del APK instalado y del candidato: `9FE5A894BFF22F4D1B45F12A42E9CBC9F0F0C40A06F23A6A7DCEF322486848D2`.
- Pendiente de prueba interactiva en el Motorola: miniatura, teclado, temporizadores simultáneos y entrega de avisos con pantalla apagada. La instalación no concedió permisos ni modificó ajustes del teléfono.
