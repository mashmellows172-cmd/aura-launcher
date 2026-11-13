# Beta79 · transiciones PS4 configurables

13 de noviembre de 2026. 0.56.0-beta79, código 79.

- Referencia de diseño: [Windows Animations de ReDrag](https://windhawk.net/mods/windows-animations). Aura toma la selección de efectos y la duración regulable y las aplica en Compose.
- Movimiento y efectos ofrece expansión desde el origen, deslizamiento, fundido y modo instantáneo, con vista previa de apertura y cierre.
- Se reutilizan las preferencias de efecto y duración. Las transiciones afectan al menú rápido, las fichas de apps, multimedia y clima. El desplazamiento de la lista PS4 mantiene el marco alineado con las baldosas.

## Verificación

- Compilación y lint correctos en 3m18s. Cero errores, 40 advertencias y 3 avisos.
- Actualización por ADB inalámbrico con install -r. Certificado, código 79 y hash del APK instalado verificados.
- SHA-256 del APK instalado: 9E018E02E1236C01189E50B7A8986FA95C46244368A57382F2E68337AB45311B.
- El teléfono estaba en otra aplicación. Queda pendiente la revisión visual de los efectos y su fluidez.
