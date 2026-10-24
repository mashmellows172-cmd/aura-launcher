# Referencias de las familias de iconos

## Procedencia

Todo el arte de beta26 se dibuja en `tools/generate-icon-families.py`. SVG, VectorDrawable y manifiesto derivan de esas coordenadas. No contiene imágenes de la colección privada ni extracción de APK/firmware. La biblioteca personal conserva sus recursos y prioridad cuando el usuario la elige.

## Linajes visuales y certeza

| Familia | Referencia de diseño | Alcance de la interpretación |
| --- | --- | --- |
| Nothing/CMF | Matriz y AMOLED contemporáneos | Puntos circulares, señal monocroma; símbolos propios de Aura |
| Sense | Herencia clásica de HTC Sense2009–2013 | Relieve, teléfono verde, cámara física, agenda y objetos; no se presenta como un set exacto de Sense5/OneM7 |
| TouchWiz | Galaxy SIII Nature UX2012 | Objetos flotantes, agua, luz, verdes/azules y carpeta cálida |
| LG | Optimus UX/G2 de2013 | Baldosas claras esmaltadas, objetos frontales, color y capas |
| Xperia | Xperia Z1/Flow de2013 | Cristal fino, música naranja con onda original, álbum ámbar y cámara azul; no usa el logotipo Walkman |
| RAZR | RAZR V3 de2004 | Metal y sprites LCD azules; reinterpretación de interfaz/hardware, no copia exacta del menú original |

[Fuentes primarias y límites de época](../HISTORICAL-THEMES.md) contiene Samsung, LG, Sony y Motorola. Esas referencias respaldan el linaje del producto; no certifican que cada coordenada o color nuevo coincida con un icono oficial. La colección OneM7 mezcla otras generaciones/CyanogenMod y no se usa como prueba automática de HTC clásico.

## Cobertura verificable

`COVERAGE.csv` separa funciones, variantes y alias exactos.192 variantes no significan192 aplicaciones reconocidas. Hay78 alias explícitos de paquetes del sistema; las restantes funciones también pueden elegirse manualmente para cualquier app. Los nombres que contienen music, maps o camera ya no bastan para asignar el nuevo arte por defecto. La heurística heredada se conserva únicamente para compatibilidad de la colección privada activada expresamente.

No se obtuvo ni publicó un inventario de apps personales del Motorola para construir el catálogo. Las apps modernas desconocidas conservan su símbolo y los reemplazos individuales existentes mantienen prioridad.
