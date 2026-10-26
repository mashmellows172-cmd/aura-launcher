# OpenCode · ajustes persistentes A–Z

Trabaja solo FavoritesStore.kt y SettingsBackup.kt bajo app/src/main/java/dev/senda/launcher/data. Otro agente edita UI y RailEffects.kt, no tocarlos.

VisualSettings debe añadir al final4 campos con defaults: railIntensity:Int=65, railDurationMs:Int=220, railBubbleSize:Int=56, railBubbleHoldMs:Int=420.

En el Flow y setVisualSettings persistir/clamp con claves:
- rail_intensity:0..100, default65
- rail_duration_ms:80..600, default220
- rail_bubble_size:40..84, default56
- rail_bubble_hold_ms:0..1000, default420

railEffect acepta RailEffects.ids y guarda RailEffects.canonical(settings.railEffect); RailEffects.canonical existe y conserva MAGNET/SPRING/FADE/NONE, añade WAVE/LENS/CASCADE/ORBIT. No inventes aliases ni migres elecciones válidas. El campo durationMs existente sigue independiente (lista/menús), no lo cambies.

SettingsBackup: añadir las4 claves/rangos, validar rail_effect mediante RailEffects.ids. Exportar versión12 y admitir1..12 conservando validaciones y límites existentes. No reescribir ni eliminar preferencias ausentes de backups antiguos; inspecciona el flujo actual y conserva su semántica. No cambiar formato de imágenes ni otros campos.

Sin pruebas/suites ni comandos, sin otras ediciones. Devuelve resumen y puntos para revisión del agente principal. Este implementará UI y compilará todo junto.
