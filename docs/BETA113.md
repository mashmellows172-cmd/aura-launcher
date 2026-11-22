# Beta113 · Centro rápido adaptativo

Versión candidata: `0.79.0-beta113` · código `113` · 21 de noviembre de 2026.

## Cambios

- La fila contextual prioriza temporalmente Música al reproducir, Alarma cuando está dentro de las próximas 12 horas y Agenda cuando el evento comienza dentro de cuatro horas. El orden que el usuario guardó y las tarjetas fijadas siguen siendo la fuente de verdad: Aura no escribe ni cambia preferencias por contexto.
- Se añadió una escena diaria compacta encima de la fila cuando hay reproducción, una alarma cercana o un evento próximo. Tocar la escena revela su tarjeta correspondiente; sin información útil no ocupa espacio.
- **Accesos** se conserva como parte estable del rail y mantiene Linterna, Calculadora y Gemini en su orden fijo. No se retiran ni sustituyen por el orden adaptativo.
- El reproductor ampliado muestra únicamente acciones que la MediaSession de Android anuncia como disponibles: reproducción, pista anterior/siguiente y búsqueda. También explica sus capacidades disponibles; no inventa cola, salida de audio, vídeo incrustado ni controles que la fuente no expone.

## Validación local

- `gradlew.bat assembleBeta lintBeta`: `BUILD SUCCESSFUL` el 21 de noviembre de 2026, 3m47s, 50 tareas (20 ejecutadas, 30 al día).
- El XML de lint de la variante beta no contiene elementos `issue`.
- APK: `app/build/outputs/apk/beta/app-beta.apk`.
- SHA-256: `6BE3F1A29ED84C0D2D0279872B03AFE9D25C61CF42071DB2E5D8A2DE38AFB8D9`.
- Se instaló Eclipse Temurin JDK 17.0.21.1 mediante Winget firmado para restaurar la herramienta de compilación que faltaba. `JAVA_HOME` se estableció sólo en el proceso de Gradle.

## Límites y pendientes

- Beta113 no se instaló durante esta entrega y no se realizó revisión visual, táctil ni de consumo en el Motorola.
- La escena se actualiza al recomponerse el Centro rápido; no introduce un servicio, permisos, sondeo continuo ni lectura de notificaciones adicional.
- La disponibilidad multimedia depende de cada MediaSession pública.
