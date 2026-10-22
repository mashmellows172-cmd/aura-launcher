# Revisión de Aura beta17

Motorola Edge 50 Ultra, Android 17 / API 37. Fecha: 21 de octubre de 2026.

## Compilación

Variante beta optimizada con R8; mismo certificado y paquete que beta16. `assembleBeta` y `lintBeta` completados. Cero errores de lint, siete advertencias y dos sugerencias. No se añadieron ni ejecutaron nuevas pruebas unitarias.

Una primera ejecución de lint falló dentro del indexador Kotlin/UAST. La repetición con fuentes estables terminó correctamente; no se desactivaron detectores para ocultar el fallo.

## Revisión de interfaz

- Actualización sin desinstalar, respaldando antes las preferencias actuales mediante el selector de Android.
- Pantalla Movimiento y efectos, preset Intenso y captura durante desplazamiento real de la lista.
- Tocar Z y arrastrar A–Z llevan a Zip Extractor; no activan la búsqueda del fondo.
- Selección de los cinco perfiles y Sin efecto revisada en el dispositivo.
- Movimiento reducido activado, proceso recreado y estado comprobado como seleccionado.
- Se amplió el área del índice y se conserva su presentación en columnas en horizontal.
- La inspección automática de jerarquía falló con el servicio de accesibilidad activo. Se apagó mediante el diálogo de Android durante la revisión y se reactivó al finalizar con autorización expresa para dejarlo activo. No se escribió la lista de servicios en ajustes seguros.

## Corrección de orientación

Se reprodujo AURA-009: el evento HOME ya procesado cerraba el menú al recrearse la actividad. Se guardó el contador consumido para distinguir un evento nuevo de una recreación. En el APK corregido, Inicio y gestos permaneció abierto al pasar a horizontal y a vertical. El salto Z en horizontal mostró Zip Extractor.

## Alcance

Las capturas y los respaldos se guardan en `work/qa-beta17`, fuera del código fuente distribuido. El reloj del motor se limita al desplazamiento/recuperación con Aura visible; no se afirma una mejora de autonomía sin una medición de 24–48 horas.

Los efectos son transformaciones del contenido del launcher; no se presentan como un compositor de ventanas de Android ni como una reproducción completa de Compiz.

## Preferencias finales

Se importó el respaldo real previo, formato 2, y se exportó la configuración final en formato 3. La comparación conservó exactamente los ajustes ajenos a este hito y los recursos de imagen. Cambios deliberados: perfil FOLD, doble toque BOTH y opciones nuevas de índice/menús. Intensidad, duración, temas, favoritas, ocultas y demás preferencias anteriores se conservaron.

Pliegue 3D quedó como perfil de lista activo, con Imán en el índice y Resorte en los menús. El usuario autorizó expresamente mantener activo el acceso de accesibilidad para bloquear.

## Bloqueo y autorización

Con el servicio desactivado, el doble toque sobre logotipo y fondo libre abrió la explicación opcional. Después se activó mediante el diálogo normal de Android con autorización humana expresa para mantenerlo activo. El conjunto de otros servicios se comparó con el inicial y no cambió.

El doble toque en el logotipo produjo bloqueo real: estado de energía Asleep/Dozing y Keyguard mostrando bloqueo. Se encendió la pantalla sin introducir credenciales y el usuario confirmó **desbloqueo con huella**. No se usó un evento del botón de encendido para simular el resultado.

## Rendimiento y cierre

Muestra final con perfil FOLD y accesibilidad activa: ocho desplazamientos, 596 frames, cuatro marcados con retraso (0,67 %); PSS 149.841 KB; sin nuevas entradas AndroidRuntime que identifiquen el proceso de Aura. No se forzó el cierre del paquete para preservar el servicio de bloqueo activo. No es una comparación causal con betas anteriores ni una prueba de autonomía.

Tiempo de pantalla y rotación del sistema restaurados y leídos para confirmar. Archivo intermedio de restauración retirado; se conservan en Descargas `Aura-antes-beta17.json` y `Aura-beta17-final.json`. El servicio queda activo por autorización explícita del usuario. No se alteró el permiso de calendario durante este hito.
