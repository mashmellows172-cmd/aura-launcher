# Beta109 · Manipulación directa y portada continua

**Objetivo:** Centro rápido con anclas ampliada/compacta/cerrada, seguimiento directo y liberación por distancia/velocidad; una sola portada durante el viaje desde miniatura hasta reproductor.

**Base:** beta108 `8a6777e`, rama `codex/beta109-direct-media-motion`. Kotlin y Compose existentes; sin dependencias nuevas, sin suites automáticas. Conservar temas, datos, preferencias, accesibilidad y transporte multimedia.

## Diseño técnico

- `QuickPanelDragState.kt`: desplazamiento en píxeles, progreso derivado de expansión, resorte interrumpible y función pura de destino. Un gesto inverso puede detener el asentamiento. Desde ampliado, una bajada corta lleva a compacto antes de cerrar; recorrido largo puede cerrar. Ninguna animación por fotograma en ViewModel.
- `QuickCenter.kt`: seguimiento en el asa; dimensiones de portada y controles derivadas del mismo progreso. No interceptar slider ni lista de tarjetas. Con movimiento reducido se mantienen botones/gestos funcionales con cambios inmediatos. Los controles ocultos no deben ser accesibles ni pulsables.
- `QuickArtworkTransition.kt`: capa decorativa dentro de la ventana del diálogo; no intentar SharedTransitionLayout entre dos ventanas Android. Solo una imagen visible: los slots mantienen geometría y acción, con imagen oculta mientras la capa posee la portada. Rectángulos en coordenadas de pantalla; convertir al espacio local de la ventana. Mantener Crop en música y Fit en vídeo.
- `QuickTimerCard.kt`, `HomeScreen.kt`: capturar posición real de portada de miniatura y clave del contenido, ocultar su imagen mientras vive el Centro rápido y pasar origen. Si cambia contenido o falta imagen/origen, no viajar desde una portada equivocada.
- La portada continúa al ampliar/contraer el Centro rápido; la ficha multimedia separada conserva su transición existente. No declarar esa ficha parte de la nueva transición compartida.

## Delegación real

OpenCode está instalado (v2.1.4), pero listar modelos falla al activar complementos: ServiceUnavailableError. No se inventará ejecución. Qwen2.5-Coder 7B mediante Ollama local propone función de anclas y casos límite. DeepSeek R1 8B local revisará interacción y cancelación con contexto acotado. Agente de desarrollo interno implementa exclusivamente la nueva capa de portada; coordinador integra gestos y callsites. Sin modelos cloud ni credenciales nuevas. Salidas crudas en `work/beta109-delegation`, fuera de Git; registrar qué se acepta y corrige.

## Hitos

- [x] Obtener/revisar aportes locales y portada aislada.
- [x] Integrar física y progreso compartido, controles accesibles y carátula única.
- [x] Revisión independiente de cancelación, cambio de contenido, coordenadas, movimiento reducido y temas.
- [x] assembleBeta lintBeta; sin suites automáticas. Registrar errores y resultados reales.
- [x] Actualizar BETA109, historial y continuidad; publicar rama propia.
- [x] Instalación compatible y verificación física si el Motorola está disponible. No cambiar permisos, ajustes o datos.

## Resultado
Código revisado, compilado y beta109 instalada con versión/hash comprobados. Ampliación por asa probada en Motorola. La verificación física es parcial: imagen real, vídeo y gestos invertidos pendientes porque la sesión actual es genérica y Aura carece de acceso de notificaciones. El permiso no se modificó. Los intentos de agentes locales y su rechazo/no-final se registran sin atribuirles una implementación inexistente.

## Continuación física
Con autorización específica se activó acceso multimedia y se observó portada real YouTube, ampliación/contracción y apertura desde miniatura. No se certificó cada fotograma ni FPS. Se cambió temporalmente Oculta→Reproductor para probar el origen y queda pendiente restaurar Oculta cuando el usuario vuelva a Aura. No seguir pulsando mientras otra superficie esté activa.
