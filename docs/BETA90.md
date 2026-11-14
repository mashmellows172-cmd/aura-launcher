# Beta90 · Centro rápido con gestos multimedia

13 de noviembre de 2026. 0.63.0-beta90, código 90.

- Desde el asa, arrastrar hacia arriba amplía la tarjeta y la lleva al comienzo del panel. Un arrastre corto hacia abajo contrae la tarjeta ampliada; un arrastre largo cierra el Centro rápido.
- El desplazamiento de las tarjetas y la barra de progreso conservan sus zonas de interacción. El asa ofrece acciones accesibles y se respeta la configuración de movimiento.
- El reproductor ampliado muestra una carátula mayor, controles de pista y saltos de −10/+30 segundos en sesiones con duración y búsqueda disponibles. Usa la confirmación de posición ya existente. Abrir app lleva al reproductor original.
- Las capacidades dependen de la sesión multimedia de Android. La tarjeta muestra carátula y controles; la imagen de video en directo permanece en la app de origen.

## Verificación

- assembleBeta y lintBeta correctos en 3m39s.
- APK de Aura Local código 90 con certificado coincidente con beta89. SHA-256: CBCC53286E4D2A8E2384D7824CD2755247ACFBB4A54A5044F927787F29FEA646.
- ADB mostró el Motorola fuera de línea y no se descubrió un puerto nuevo. Instalación y prueba de gestos y video pendientes.

- El 13 de noviembre de 2026, se recuperó la conexión inalámbrica y se actualizó el Motorola de beta89 a beta90 con install -r. Certificado coincidente, versión 0.63.0-beta90 y hash instalado verificados. Prueba interactiva pendiente.
