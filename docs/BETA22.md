# Aura 0.17.0-beta22 — entrega local

## Cambios

- Editor del inicio con esquema de cuatro zonas, selección de alarma/agenda/widgets, historial local de 32 pasos, deshacer y restablecimiento reversible. Un arrastre de deslizador constituye un paso. Guardar y cancelar tienen acciones explícitas.
- Orden por zona corregido: un widget situado en otra zona ya no impide subir/bajar alarma y agenda.
- Estado del borrador serializado en un `IntArray` acotado, restaurado por Compose junto a la selección. Se espera la carga de preferencias antes de abrir el editor restaurado. La restauración no escribe en DataStore.
- Transacción de archivos locales para fondos: elimina archivos nuevos parciales o rechazados; termina el guardado de preferencias antes de permitir que una cancelación borre un archivo referenciado.
- Estado de imágenes asociado a su origen: avatar, fondo, vista previa e iconos no conservan visualmente una imagen de la selección anterior.
- Aura Alloy · RAZR V3: fondo, reloj LCD, tipografía, superficies e iconos de categoría originales; Nothing sigue siendo el tema inicial. [Referencia y decisiones visuales](RAZR-design-reference.md).
- Controles de color de 48 dp. No se añaden permisos ni acceso a red.

## Verificación y límites

La variante beta deriva de release, con R8 y reducción de recursos. Se ejecutan `testDebugUnitTest`, `assembleBeta` y `lintBeta`; la firma se verifica con `apksigner`. El paquete conserva `dev.senda.launcher.debug` y el certificado de las betas anteriores para actualizar sin desinstalar.

La revisión independiente encontró un problema importante: rotar con el editor abierto descartaba el borrador. Se añadió una prueba fallida de restauración antes de implementar el Saver y la espera de preferencias. La suite cubre ahora historial/restauración, archivos parciales, fallo de persistencia, cancelación y recarga del nuevo tema. El resultado final de comandos y artefactos se guarda en `DELIVERY-beta22.md`.

La primera compilación beta22 se instaló correctamente. Aura abrió con sus favoritas y clima de Prognoza. Durante el intento de abrir el editor, el teléfono cambió de primer plano hacia YouTube; se detuvieron las acciones de UI. No se cerró PiP, no se alteraron alarmas/eventos y no se cambió el tiempo de apagado ni el acceso de gestos en esta sesión. La revisión no autoriza afirmar que se completó todo el recorrido físico.

Pendiente en una sesión estable:

1. Editor: editar, girar, deshacer, guardar, volver a abrir y cancelar, con fuente grande y en paisaje.
2. Aplicar RAZR/Nothing y los cuatro retro; comprobar contraste y alcance táctil con fondos reales, iconos normales y movimiento reducido.
3. Rechazar una biblioteca ZIP dañada y confirmar biblioteca anterior; recrear proceso y comprobar widgets/Prognoza/avatar.
4. Macrobenchmark repetible de arranque/desplazamiento, memoria y batería. No se atribuyen mejoras de FPS a muestras con distinta configuración o vídeo activo.

## Rendimiento

Se conservan las mejoras verificables beta21: caché aproximada de 6 MiB, R8 sin reglas amplias de Compose, estado ligado al ciclo de vida, tipos de filas y animación del clima finita. Este trabajo prioriza corrección y evita nuevos temporizadores permanentes. [Informe y mediciones inconclusas](perf-audit-2026-10-22-app.md).

## Distribución y privacidad

El ZIP de fuentes se genera desde archivos versionados. No incluye `.git`, compilaciones, claves, capturas de QA ni imágenes personales/fabricantes. Las fuentes Geist llevan su licencia OFL dentro del proyecto. La biblioteca retro privada se importa por separado y no se incluye en el APK ni en el ZIP MIT.

Actualizar mediante `adb install -r` o instalar el APK sobre la beta anterior. No desinstalar para conservar datos. La clave de firma permanece fuera de las fuentes.
