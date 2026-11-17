# Beta101 · Una sola variante de Aura Launcher

17 de noviembre de 2026. 0.68.3-beta101, código 101. Base beta100 `6a6d74f`.

La beta genera un solo paquete instalable de Aura Launcher (`dev.senda.launcher.debug`) con los cambios integrados de beta100. Se eliminó el selector de compilación que creaba Aura Launcher Local. El paquete conservado permite actualizar con la firma de la beta100 ya instalada.

La lista de Inicio y su vista previa omiten la fila de reproducción y su índice en los saltos A–Z. Se retiró el ajuste «Multimedia solo en el menú», puesto que ese es el flujo único. Se conserva «Reproductor multimedia» como interruptor. El menú de Aura y, en PS4, el Centro rápido mantienen los controles existentes. Se sigue aceptando la antigua clave de posición en los respaldos.

## Pasar de Aura Launcher Local a Aura Launcher

1. Guarda un respaldo en cada copia desde **Personalización y respaldo → Respaldo local → Exportar respaldo**, con nombres distintos. Conserva los archivos de forma privada.
2. En Aura Launcher beta101, importa el respaldo de Aura Launcher Local si quieres usar sus ajustes. La confirmación de restauración indica que reemplaza los ajustes del destino. Verifica favoritas e imágenes; comprueba los elementos que se guardan fuera del respaldo.
3. Los widgets, los permisos de Android y la selección de Inicio pertenecen a cada paquete. Vuelve a vincular lo que uses en Aura Launcher y selecciónala como aplicación de Inicio. Comprueba que el botón Inicio abre beta101.
4. Retira Aura Launcher Local solo después de comprobar el traslado. El respaldo de ajustes no representa una copia íntegra de la aplicación: la biblioteca retro se importa por separado y los datos propios de la copia anterior, como temporizadores y variantes del editor, requieren revisión antes de retirarla.

## Validación e instalación

La primera compilación detectó que la muestra del Centro rápido aún usaba una referencia de audio retirada. Se conectó a la sesión de la vista previa. La pasada final `assembleBeta lintBeta` terminó en 3 min 47 s con 0 errores, 54 advertencias y 3 avisos. No se ejecutaron suites automáticas. AAPT y apksigner verificaron el único paquete beta y su firma compatible.

Se actualizó Aura Launcher beta100 mediante ADB inalámbrico con `install -r`. Android confirmó 0.68.3-beta101, código 101. El APK de 15 924 377 bytes y su copia instalada coinciden en SHA-256 `164c6fb1e7ce451b33fcd225b373760588cb60eee0fa93e360a512746025741a`. La revisión física con una sesión musical sigue pendiente.

Aura Launcher Local beta97 sigue instalada y es el Inicio predeterminado. Restaurar preferencias, elegir Inicio y retirar esa copia quedan pendientes de confirmar qué configuración desea conservar el usuario.
