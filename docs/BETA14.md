# Aura Launcher 0.9.0-beta14 — uso diario y widgets

## Instalar

### Desde el teléfono

1. Copia `Aura-0.9.0-beta14.apk` al Edge 50 Ultra y ábrelo desde Archivos.
2. Si Android lo solicita, permite instalar apps desde esa fuente y confirma la actualización.
3. Las preferencias, el rol HOME y las pieles anteriores se conservan.

### Desde este PC con ADB

```powershell
.\work\android-toolchain\android-sdk\platform-tools\adb.exe install -r .\outputs\aura-launcher\beta\Aura-0.9.0-beta14.apk
```

## Pieles

Abre el menú de tres puntos y toca **Tema visual**. Las pieles cambian fondos, colores, patrones, contenedores y animaciones sin reordenar el launcher.

- **Aura Pulse** — principal: negro AMOLED, trama de puntos más visible, acento rojo y marca geométrica de puntos propia.
- **Aura Bloom** — homenaje a HTC Sense / HTC One M7.
- **Aura Prism** — homenaje al Samsung Galaxy S III TouchWiz Nature UX, ahora con fondo de ondas concéntricas inspiradas en agua.
- **Aura Flow** — homenaje a Optimus UI de LG.
- **Aura Horizon** — homenaje al Xperia Z3 y sus formas de flujo de color.
- Siguen disponibles **AMOLED puro**, **Oscuro**, **Claro** y **Seguir el sistema**.

### Usar fondos e iconos originales

En **Tema visual → Elegir fondo**, selecciona desde Fotos el wallpaper que quieras usar con esa piel. Para asignar un icono de archivo local a una app, mantén pulsada la app, toca **Elegir icono para …** y selecciona su PNG; el cambio queda asociado a la piel activa. El mismo menú permite volver al icono original. Cada piel guarda sus propios reemplazos.

Esto permite usar extracciones personales de ROMs sin incluirlas en el repositorio. Los wallpapers e iconos de los fabricantes no son recursos libres, por lo que Aura no los redistribuye ni afirma que los patrones dibujados sean los wallpapers exactos. Para compartir una compilación open source completa, utiliza solo recursos con licencia compatible.

Aura Pulse es el valor predeterminado. Desde beta14, elegir AMOLED puro conserva esa selección al reiniciar; las preferencias explícitas dejan de convertirse a Pulse.

## Funciones del launcher

- Índice alfabético fijo a la derecha: toque o arrastre vertical.
- Búsqueda local y apertura de apps; favoritas ordenables; ocultar/restaurar apps.
- Sin conexión, anuncios, telemetría ni pagos.

## Movimiento y búsqueda

Abre los tres puntos → **Ajustes visuales**. El motor Aura Motion incluye seis perfiles (**Aura Flow, Compiz Cube, Prism Shift, Orbit, Ripple y AOSP Glide**): profundidad 3D, giro, escala y curvas progresivas ligadas a la posición de cada fila. El acento luminoso aparece solo durante el scroll. La entrada, salida y reordenación de apps usan transiciones con resorte. La física principal sigue siendo la lista nativa de Android; el indicador A–Z acompaña la sección activa con un resorte ajustable. Puedes modificar ritmo e intensidad, y **Reducir movimiento** desactiva las transformaciones y acorta las transiciones. Se migran automáticamente las preferencias de perfiles de betas anteriores.

En la cabecera, tocar la hora abre las alarmas y tocar la fecha abre el calendario. Toca **Clima** para elegir una aplicación instalada; la selección queda guardada. Puedes cambiarla desde los tres puntos → **App del clima**.

## Widgets de Android

Abre **⋮ → Añadir widget**, busca y elige un proveedor. Confirma **Crear** en Android y completa su configuración si la solicita. Aparece al principio de la lista. Desde **Editar widgets** puedes cambiar altura, subir, bajar o quitar. Hasta cuatro widgets; ID, orden y altura quedan guardados localmente. Cancelar la vinculación/configuración libera el ID reservado. Si el proveedor desaparece se muestra un aviso con una acción para quitarlo.

Los proveedores reciben actualizaciones mientras Aura está visible y solo se notifica un tamaño nuevo cuando cambia. Los recursos y el comportamiento de cada widget pertenecen a su aplicación proveedora.

## Correcciones de uso diario

- El catálogo responde a instalación, eliminación, actualización y cambios de disponibilidad de paquetes; agrupa eventos y cancela cargas anteriores.
- Las favoritas y ocultas se conservan si una app deja de estar disponible temporalmente. El editor respeta las favoritas no visibles.
- El índice A–Z cuenta la sección de widgets al calcular destinos. HOME limpia búsqueda, cierra diálogos y vuelve al principio.
- Reloj al minuto solo mientras Aura está visible; imágenes locales decodificadas a tamaño acotado; iconos renovados por versión de la app.
- Corrección de búsquedas con mayúsculas turcas y persistencia de AMOLED puro.

## Animaciones en pausa

Esta entrega conserva el movimiento de beta12. El experimento beta13 se guardó en `work/paused-motion-beta13` del workspace y no se incluye en este APK.

## Verificación beta14

Las animaciones de esta entrega conservan la base de beta12. Esta revisión se centra en uso diario, persistencia y widgets; no promete efectos nuevos ni una tasa fija de refresco.

`build-beta.ps1` ejecuta 11 pruebas unitarias, `assembleDebug`, `lintDebug`, `assembleBeta`, `lintBeta` y la verificación de firma. Se instaló en el Edge 50 Ultra mediante ADB inalámbrico. La firma coincide con beta11/beta12 y conserva los datos de usuario. Casos y resultados: `../replica/test-plan.md` y `../replica/bugs.md`; herramientas Android: `e2e/`; evidencias locales: `work/qa-beta14` del workspace.

El APK optimizado con R8 ocupa 9.756.561 bytes; el de depuración, 31.245.988 bytes. Ambos usan el mismo paquete y firma para actualizar conservando datos.

Una muestra controlada en depuración registró arranque COLD de 456 ms, 336 frames y 19 frames con retraso, con vídeo PiP activo. Es una medición breve, no una garantía de rendimiento sostenido.

Una recreación de proceso comprueba recuperación local de preferencias. El reinicio completo del Motorola pasó: se conservaron HOME, Aura Pulse y el ID del widget real. Las favoritas se contrastaron con el respaldo; los elementos QA se retiraron y se restauró el tiempo de bloqueo original. La batería durante una jornada sigue pendiente.

## Volver a otro launcher

Abre **Ajustes → Apps → Apps predeterminadas → App de inicio** y selecciona el launcher deseado.

Aura Launcher usa APIs públicas de Android y licencia MIT. Los recursos seleccionados por el usuario permanecen en el teléfono.


Última muestra optimizada, después del reinicio y sin PiP: arranque COLD de 169 ms; 397 frames, 14 con retraso (3,5 %); PSS 304.157 KB; sin nuevos errores AndroidRuntime de Aura. La carga y el estado del teléfono difieren de la muestra debug: no es una comparación causal ni una garantía sostenida.
