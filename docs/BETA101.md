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

## Cierre de la unificación en el Motorola

El 17 de noviembre de 2026, el usuario eligió conservar los ajustes de Aura Launcher beta100 y autorizó seleccionar beta101 como Inicio y retirar Aura Launcher Local. Se conservaron los ajustes de la copia actualizada mediante `install -r`. No se importó un respaldo sobre ellos.

Se verificó primero que beta101 abrió como Inicio y quedó en primer plano. Después, Android retiró Aura Local beta97 del perfil principal mediante `pm uninstall -k --user 0 --versionCode 97 dev.senda.launcher.local`; `-k` conserva los directorios de datos y caché de la copia retirada. El APK original verificado queda archivado en privado. No se probó la reinstalación.

La lista de paquetes del perfil principal devolvió una sola copia de Aura (`dev.senda.launcher.debug`) y el rol HOME confirmó ese mismo paquete. Se verificaron de nuevo la versión 0.68.3-beta101 y el SHA-256 instalado indicado arriba. La prueba física con música sigue pendiente.
