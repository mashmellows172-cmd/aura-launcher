# Beta92 · Fichas Vita, actividades PS5 y transiciones

14 de noviembre de 2026. Aura Local 0.65.0-beta92, código 92.

- Mantén pulsada una app con el tema PS4 para ver su ficha Vita. La portada usa el icono configurado y un degradado de Aura. Los accesos directos dependen de la app y del permiso de Android para el launcher.
- En Inicio → Actividades puedes añadir, editar y quitar enlaces HTTP o HTTPS. También puedes compartir un enlace con Aura, revisar el borrador y guardarlo. Las tarjetas se guardan localmente y tienen cuatro portadas de color. Puedes destacar una en Inicio. Android elige la app o el navegador que abre el enlace.
- La cabecera PS4 toma un acento del icono seleccionado al detener el scroll. Mantiene los colores de texto y controles.
- PS4 y Xperia muestran el recorrido de la letra A–Z al encabezado visible y lo destacan al llegar.
- El reproductor ampliado y el Centro rápido muestran una marca provisional durante el salto. La marca se rellena al confirmar y vuelve al progreso real si la sesión no responde.
- Favoritas, accesos del Centro rápido y tarjetas admiten arrastre con vista de la posición final, flechas y deshacer el último movimiento.
- El asa de la ficha permite cerrarla con un gesto hacia abajo. Al soltar antes del umbral la ficha se restaura. El botón Cerrar sigue disponible.

## Verificación

- `assembleBeta lintBeta` con `-PauraSideBySide=true` finalizó correctamente. Lint informó 0 errores, 60 advertencias y 3 avisos.
- Actualización inalámbrica por `adb install -r` sobre beta91. El paquete `dev.senda.launcher.local` conservó el mismo certificado. Se verificaron el código 92, la versión y la coincidencia del APK instalado.
- SHA-256 del APK candidato e instalado: `61C88DAE72BAC0708EF5BA9C3AA5AFD181B957414A55CA80150CA43CEC03560E`.
- Pendiente de revisión interactiva: abrir una ficha con letra grande, compartir y recuperar un enlace real, arrastrar y deshacer, saltar entre letras lejanas y confirmar saltos en sesiones de terceros.
