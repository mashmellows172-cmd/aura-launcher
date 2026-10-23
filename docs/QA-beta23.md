# QA — Aura 0.18.0-beta23

Fecha: 2026-10-22. Dispositivo: Motorola Edge 50 Ultra, API37. Variante beta optimizada, R8 y reducción de recursos.

## Evidencia confirmada

- `testDebugUnitTest assembleBeta lintBeta -Reports`: compilación correcta, 30 pruebas JVM correctas, lint con 0 errores, 7 advertencias y 3 indicaciones. Las advertencias incluyen actualizaciones disponibles; no se actualizaron dependencias en esta entrega.
- APK firmado con la misma clave de desarrollo y actualización instalada con `adb install -r`, conservando datos. Certificado SHA256: `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`.
- Revisión independiente del código: sin hallazgos críticos ni importantes. Corregida una contradicción documental sobre el alcance de las animaciones.
- Respaldo público reciente exportado y validado antes de modificar preferencias: formato7, 61 ajustes. Los respaldos y capturas quedan fuera de las fuentes distribuibles.
- Prueba nativa: selección de bloque, cambio de posición, deshacer y cancelar correctos.
- La primera rutina terminó restaurando el respaldo reciente y los ajustes temporales de pantalla, fuente y rotación. No se modificaron servicios de accesibilidad.

## Validación pendiente

La comprobación del borrador tras girar no encontró el texto esperado. Aún no se distingue entre pérdida del estado y control fuera del área visible; no se declara aprobada. Se añadió captura previa y búsqueda con desplazamiento para diagnosticarlo. La segunda ejecución encontró otra aplicación en primer plano y terminó antes de modificar preferencias, por lo que no produjo evidencia nueva de rotación.

No se completaron el recorrido de los seis temas, el editor con fuente2.0, el rechazo nativo de ZIP inválido ni el barrido meteorológico final. Las pruebas unitarias cubren las reglas de anchura/altura, colores, límites y habilitación del movimiento; no sustituyen la observación en el teléfono. Queda pendiente comprobar también la legibilidad con cada imagen personal importada. La entrega es una beta, no una certificación visual completa.

## Rendimiento: alcance de las muestras

Muestras exploratorias privadas, sin Macrobenchmark ni condiciones controladas entre versiones. Beta22: 806 frames de desplazamiento y 2 con jank; sus arranques registrados como0 son inválidos. Beta23 anterior al nuevo barrido: arranques411/371/389/390/389ms (mediana389), 635 frames de desplazamiento y1 con jank; reposo80 frames y1 con jank. Los temas y la carga externa pudieron variar. Estas muestras no prueban una mejora causal y no miden el consumo de batería ni el rendimiento del efecto final.

## Reanudación

Con el teléfono libre: reproducir y capturar el giro del editor; completar la ruta de temas, texto grande, importación inválida y controles del clima; comprobar un ciclo completo de cada tratamiento y la restauración del respaldo. Medir de nuevo la versión final bajo condiciones registradas antes de afirmar mejoras de rendimiento.

## Decisiones y costes

Se mantuvo el checkout local autorizado; recuperación mediante commits. Replica se adaptó a Compose/UIAutomator. La ampliación aprobada incorporó el barrido, con repetición opcional y mayor coste de dibujo. El revisor dejó las comprobaciones nativas y de consumo al ejecutor. El registro completo se conserva en `superpowers/reports/2026-10-22-beta23-execution-ledger.md`.
