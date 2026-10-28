# Desarrollo desde otro equipo

Notebook Windows actual: consultar [rutas, configuración de sesión, compilación, firma y recuperación cloud](LOCAL-DEVELOPMENT.md). Ese registro distingue la beta38 local de la variante PS4 cloud aún no recuperada.

## Preparación

Instala Git, Android Studio, JDK17, Android SDK Platform37 y los Build Tools solicitados por Gradle. Abre esta carpeta como proyecto Android y configura la ruta SDK mediante Android Studio o un local.properties local (sdk.dir). Nunca compartas ese archivo.

```sh
git clone https://github.com/mashmellows172-cmd/aura-launcher.git
cd aura-launcher
```

En Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat assembleBeta lintBeta
```

En macOS/Linux, si el wrapper no tiene permiso ejecutable:

```sh
sh ./gradlew assembleDebug
sh ./gradlew assembleBeta lintBeta
```

Los scripts build-beta.ps1 y los scripts antiguos de entrega dependen del workspace original. En un equipo nuevo usa directamente el wrapper Gradle. No se necesita ese workspace para compilar el código de la aplicación. Algunos generadores de recursos requieren Python/Pillow o Node/Sharp y rutas configuradas; los recursos de producción ya están incluidos.

## Firma y datos

Cada equipo genera su propia clave de desarrollo Android. La beta instalada en el Motorola solo puede actualizarse conservando datos con la misma clave original. Esa clave NO está en GitHub: consérvala y transfiérela únicamente por un canal privado seguro si necesitas firmar desde otro equipo. No desinstales Aura para resolver una firma incompatible. Para una instalación separada, configura primero un applicationIdSuffix propio.

El repositorio no sincroniza favoritas, widgets, avatar, permisos ni preferencias del teléfono; usa la exportación de Aura y su biblioteca retro por separado. No publiques esos respaldos en Git.

## Flujo de cambios

Antes de trabajar: git pull --ff-only. Trabaja en una rama propia por mejora; haz commit y push al terminar. Evita editar simultáneamente la misma rama desde varios dispositivos. La rama main contiene el checkpoint compartido; codex/aura-polish conserva la línea de trabajo existente.

## Recursos

Los iconos y fuentes incluyen procedencia/licencias donde corresponde. Las marcas de aplicaciones y los recursos históricos de terceros no se convierten en propiedad de Aura. Mantener este repositorio privado mientras se revisan los recursos para una eventual distribución pública.
