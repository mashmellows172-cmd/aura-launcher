# Aura Launcher 0.21.0-beta27 — entrega intermedia

Incluye las seis familias de192 variantes vectoriales de beta26, comparación y selección manual por aplicación. Se añaden30 sonidos originales: cinco acciones por cada familia visual. No son muestras extraídas de teléfonos de fabricantes.

## Sonidos

Abre los tres puntos → **Sonidos y respuesta**. Están desactivados inicialmente. Ajusta familia automática/independiente, volumen relativo y eventos: abrir apps, menús, elegir opciones, favoritas e índice. El índice está silenciado por defecto y tiene límite de frecuencia.

**Escuchar muestra** es una acción explícita. Respeta silencio/vibración, No molestar, volumen/sonidos táctiles de Android y, por defecto, la reproducción multimedia. Aura no cambia esos ajustes ni solicita permisos nuevos. No reproduce sonidos por recomposición o temporizadores. El pool se libera al salir de Inicio; las muestras pendientes se cancelan al cerrar sus ajustes.

Respaldo10, compatible con importación1–10. Preferencias antiguas sin audio arrancan en silencio. Sigue el trabajo visual de la sesión nocturna; esta entrega permite usar los avances ya compilados.

## Evidencia

`assembleBeta lintBeta`: BUILD SUCCESSFUL in1m46s. Lint0 errores,11 advertencias,3 sugerencias. Sin suites automáticas ni validación física de los sonidos.

APK firmado con el certificado habitual. Instalación `pm install -r`: Success; PackageManager confirmó código27/versión0.21.0-beta27. Copia a Descargas confirmada por SHA256.

SHA256 APK: `57a993e96abde19e161195c7aee78cffa6edc05c8b06b101b3cc7f69407699ac`.

La copia SYNC fallaba y el envío binario por shell quedó truncado en465bytes. Se usó base64 sobre shell sinPTY, se reconstruyó localmente y solo se permitió instalar después de que el hash coincidiera. No se desinstaló la app ni se modificaron ajustes/permisos del sistema. La experiencia visual completa y las políticas de audio del Motorola siguen pendientes de revisión física.
