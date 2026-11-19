# Beta99 Local — Fondo reactivo, Arte dinámico y Gestos rápidos PS4

Fecha: 19 de noviembre de 2026  
Versión: `0.69.0-beta99` (código 99)  
Rama: `main` / `feature/beta100-features-1-4-5`  

## 🎯 Resumen de características implementadas

### 1. Fondo dinámico PS4: reactividad a la música
- **Pulso armónico de respiración:** `Ps4WallpaperMotion` ahora expone `musicPulse` que oscila rítmicamente cuando hay reproducción activa sin bloquear hilos.
- **Tinte de carátula en ribbons:** Las ondas consulares en `Ps4LiveWallpaper` combinan el cian nativo con el acento de la carátula activa (`LocalMediaAtmosphere.current.color`).
- **Destello en partículas:** Las motas de luz ambientales en el fondo flotan y varían su opacidad al compás del audio.
- **Overlay reactivo suave:** `Ps4WallpaperFeedback` refuerza sutilmente la presencia del álbum durante la escucha activa.

### 2. Pack dinámico de arte PS4 y almacenamiento local
- **Resolvedor `Ps4ArtStorage`:** Capa de almacenamiento local persistente en `context.filesDir/ps4-artwork/` con caché en memoria (`ConcurrentHashMap<String, ImageBitmap>`).
- **Cobertura total y resolución fluida:** `renderFamilyAppIcon()` consulta recursos estáticos, caché local en disco/memoria, y si no existe muestra de inmediato `Ps4FallbackArtwork` mientras intenta resolver arte en segundo plano.
- **Acabado procedural biselado:** `Ps4FallbackArtwork` incorpora un brillo superior (`topGloss`) y borde biselado que evoca las carátulas oficiales de PlayStation 4.

### 3. Gestos de acceso rápido
- **Acciones expandidas:** En `HomeSettings.kt` y `GestureActionDialog`, los gestos de deslizamiento (arriba, abajo, izquierda, derecha) ahora permiten configurar:
  - `QUICK_MENU`: Abrir el Menú Rápido PS4 directamente.
  - `MEDIA_PANEL`: Desplegar los controles multimedia dedicados.
  - `STATUS_CENTER`: Abrir el Centro de Estado.
- **Compatibilidad y persistencia:** Validación garantizada en `GestureBindings.valid`, `FavoritesStore` y el sistema de respaldo.

## 📦 Verificación técnica
- Cero dependencias externas añadidas: 100% Kotlin nativo y Android Jetpack Compose.
- Ergonomía vertical de Aura rigurosamente preservada.
- Compilación: `BUILD SUCCESSFUL in 1m 18s` (0 errores).
- APK: `app/build/outputs/apk/beta/app-beta.apk` (15.941.885 bytes).
- SHA-256: `FC03668907F28CBAEEBE092090108F526AA34C66797817CDEAC9940BB43B4269`.
