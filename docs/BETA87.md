# Beta87 · alarmas en Centro rápido

13 de noviembre de 2026. 0.60.3-beta87, código 87.

- El Centro rápido muestra la próxima alarma de Android aunque la tarjeta de alarma de Inicio esté oculta.
- Abrir el Centro rápido solicita una lectura actualizada.
- Diagnóstico previo: Android informó una próxima alarma a las 06:00. La preferencia activa del usuario no se pudo leer mediante run-as. No se modificaron alarmas ni ajustes.

## Verificación

- assembleBeta y lintBeta correctos en 3m35s con 0 errores, 40 advertencias y 3 avisos.
- Actualización inalámbrica sobre beta86 con firma coincidente. Versión 87 y hash del APK instalado verificados.
- SHA-256: EB0E2316BF93BB058E7DD6A9EB4E8043BB10D7DBE52EEB67FD507B062624BD11.
- Inicio mostró el acceso al Centro rápido. Al ir a abrirlo, Aura ya no estaba en primer plano. Prueba de la tarjeta pendiente.
