# Beta80 · escena del clima PS4

13 de noviembre de 2026. 0.57.0-beta80, código 80.

- El símbolo y la cinta de luz comparten una secuencia finita de 3 a 12 segundos. Arranca junto al clima, atraviesa la franja del reloj y se desvanece. La condición del proveedor gobierna la escena automática y la selección manual conserva su función decorativa.
- El pronóstico comparte ese tratamiento y responde al seleccionar una hora. En Clima en el reloj se puede elegir un evento de ejemplo y repetir la animación completa.
- La intensidad se ajusta con la preferencia existente. El ahorro de energía, alto contraste y movimiento reducido suprimen la escena. La calidad adaptativa puede omitir las partículas para aligerar el dibujo.

## Verificación

- Compilación y lint correctos en 3m7s. Cero errores, 40 advertencias y 3 avisos.
- Actualización por ADB inalámbrico con install -r. Certificado, código 80 y hash del APK instalado verificados.
- SHA-256 del APK instalado: 176DBF067C10CFCC92D401691BA91995C5AF1C60C7C7805E55CCF05D34B4B9CB.
- La cortina de notificaciones cubría Inicio. Queda pendiente la revisión visual de la escena.
