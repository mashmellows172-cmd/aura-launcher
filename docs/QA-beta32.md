# Beta32 · comprobación acotada

## Revisión de código completada

- HomeScreen usa una sola estructura para PS4 y los demás temas.
- La búsqueda visible conserva el FocusRequester usado por los gestos; no queda un destino de búsqueda desconectado en PS4.
- AppSections crea una fila por favorita; sectionStartIndices recibe favoriteApps.size.
- La fila mantiene onClick=onLaunch y pulsación larga para acciones. La estrella queda fuera del objetivo de apertura ampliado.
- Calendario, alarmas, clima, audio, grupos de widgets y menús usan los callbacks ya existentes de Aura.
- No hay cambios de DataStore, formato de respaldo, permisos, identificador de paquete ni proveedor de iconos.
- Fondo PS4 conserva guardas de ciclo de vida, animadores del sistema y movimiento reducido; alto contraste omite el fondo animado.

## Pendiente en el Motorola

Revisar distribución con el tamaño de texto del usuario, arrastre A–Z hasta W/Y/Z, apertura con un toque, pulsación larga, estrella independiente, retorno desde apps y conservación de widgets. El teléfono no estaba conectado al comenzar; se pidió su dirección actual una vez. No se declara revisión física ni FPS medidos.

## Alcance

No se agregaron ni ejecutaron suites automáticas. La compilación y lint se anotan en BETA32.md después de finalizar. La revisión estática no certifica todas las interacciones físicas.
