# Entorno local Windows de Aura

Comprobado el 28 de octubre de 2026 en este notebook. No es una validación del chat cloud ni una prueba visual del Motorola.

Actualización posterior: beta40 instalada y verificada con este entorno; ver BETA40.md para hash y revisión visual acotada. Los apartados beta39 siguientes conservan la evidencia de preparación original. El script de instalación lee la versión real de su APK candidato y se niega a degradar; no intentar reinstalar beta39 sobre beta40.

## Ubicaciones conservadas

- Checkout: `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\outputs\aura-launcher`.
- JDK completo: `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\android-toolchain\jdk\jdk-17.0.20.1+1`.
- Android SDK: `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\android-toolchain\android-sdk`.
- Caché Gradle: `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\gradle-home`.
- Clave original: `%USERPROFILE%\.android\debug.keystore`, fuera del repositorio. No se ha copiado ni publicado.
- Configuración de sesión: `C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\Enter-Aura.ps1`, local y fuera de Git.

No se ha movido el checkout ni creado otro. No se modificaron variables permanentes, PATH global, permisos del teléfono ni configuraciones de Android Studio. `local.properties` apunta al SDK existente y está ignorado por Git. Los respaldos privados deben mantenerse fuera del repositorio; no se ha extraído ninguno del teléfono durante esta preparación.

## Abrir una sesión de desarrollo

En PowerShell, cargar mediante punto y espacio:

```powershell
. C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\Enter-Aura.ps1
java -version
javac -version
adb version
git status --short --branch
.\gradlew.bat assembleBeta lintBeta
```

El script configura JAVA_HOME, ANDROID_HOME, ANDROID_SDK_ROOT, ANDROID_USER_HOME y GRADLE_USER_HOME solo en esa sesión. Usa la configuración Android original del usuario; el antiguo `work/run-aura-build.ps1` utiliza otro ANDROID_USER_HOME y puede firmar con otra clave. No usar los targets predeterminados de ese script antiguo: incluyen pruebas no autorizadas.

Herramientas verificadas: java y javac Temurin 17.0.20.1; Platform Tools 37.0.1; Platform 37.0 y 37.1; Build Tools 36.0.0 y 37.0.0. El wrapper del repositorio usa Gradle 9.4.1 por HTTPS con `validateDistributionUrl=true`. No se hicieron descargas manuales ni se desactivó TLS.

## Validación local del 28 de octubre

`assembleBeta lintBeta`: BUILD SUCCESSFUL en 43 segundos, 50 tareas, 4 ejecutadas y 46 actualizadas. Lint: 0 errores, 30 advertencias, 3 avisos. No se ejecutaron suites automáticas ni benchmarks. El primer intento dentro del sandbox no pudo conectar al daemon; la ejecución con acceso a procesos locales terminó correctamente.

APK de compilación: `app/build/outputs/apk/beta/app-beta.apk`.

Copia de entrega preservada: `beta/Aura-0.28.0-beta39-local-20261028.apk`. El APK histórico del27 permanece intacto.

Paquete: `dev.senda.launcher.debug`; versión `0.28.0-beta39`, code39.

SHA256 de esta compilación local: `eba7aba0d765ca6415b3047eb33abb6cac61a2c27eba5cc711549fd24e33d832`.

Certificado SHA256 comprobado: `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`.

La clave coincide con la firma original documentada. Esto no sustituye comparar el APK instalado cuando el Motorola esté conectado. Un APK recompilado puede tener otro hash; no reemplazar silenciosamente los hashes de entregas históricas.

## Conectar y actualizar conservando datos

No usar el primer dispositivo de `adb devices`: puede haber otros equipos conectados. Seleccionar siempre el destino explícitamente.

```powershell
adb devices -l
adb mdns services
# Solo si necesita nueva vinculación. El código se introduce en el prompt, no se guarda.
adb pair IP:PUERTO_DE_VINCULACION
adb connect IP:PUERTO_DE_CONEXION
$auraTarget = 'IP:PUERTO_DE_CONEXION'
adb -s $auraTarget shell getprop ro.product.model
adb -s $auraTarget shell getprop ro.serialno
adb -s $auraTarget shell dumpsys package dev.senda.launcher.debug
adb -s $auraTarget shell pm path dev.senda.launcher.debug
```

Los puertos de vinculación y conexión son distintos y pueden caducar. El teléfono debe ser el Edge 50 Ultra autorizado, no otro dispositivo. Consultar y comparar su identidad en privado. No registrar códigos de vinculación, tokens ni evidencia privada en Git.

Antes de instalar, copiar por `adb pull` el `base.apk` indicado por `pm path` a una carpeta privada fuera de Git. Verificar tanto ese APK como el candidato:

```powershell
$auraApkSigner = "$env:ANDROID_HOME/build-tools/37.0.0/lib/apksigner.jar"
java -jar $auraApkSigner verify --print-certs RUTA_PRIVADA_AL_APK_INSTALADO
java -jar $auraApkSigner verify --print-certs app/build/outputs/apk/beta/app-beta.apk
```

Solo continuar si los certificados coinciden y la versión instalada permite actualizar a code39. Si el teléfono tiene una versión más reciente, detenerse; no usar downgrade. La autorización del usuario permite actualizar sin volver a preguntar cuando se cumplan estas condiciones.

```powershell
adb -s $auraTarget install -r app/build/outputs/apk/beta/app-beta.apk
adb -s $auraTarget shell dumpsys package dev.senda.launcher.debug
```

Comprobar `Success`, versión y certificado del APK efectivamente instalado. No utilizar `uninstall`, `pm clear`, `-d` ni `-g`. No cambiar permisos, launcher predeterminado ni ajustes para resolver problemas. Los datos se conservan mediante actualización del mismo paquete y firma; no se debe afirmar revisión visual de preferencias sin realizarla.

Este notebook tiene además un guardián local de instalación, fuera de Git. Su sintaxis y ejecución real quedaron verificadas el28 de octubre. Verifica serial/modelo autorizado, certificado original en ambos APK, versión no inferior y hash/certificado/versión después de instalar. Conserva copias privadas de ambos APK fuera del repositorio. Usarlo después de `adb connect`:

```powershell
& C:\Users\LOQ\Documents\Codex\2026-10-20\qui\work\Install-Aura.ps1 -Device 'IP:PUERTO_DE_CONEXION'
```

Si falla cualquier comprobación, se detiene sin desinstalar ni limpiar datos. El script selecciona únicamente el dispositivo indicado, aunque haya otros conectados.

## Cambios cloud beta38 que siguen faltando

Se consultó GitHub con `git fetch origin`. La rama local `codex/phase1-theme-editor`, HEAD `d7d49c8`, estaba limpia y sincronizada, sin commits pendientes de publicar. Main apunta a beta37 (`0714c7c`). La beta38 disponible es la entrega del editor/Room, diferente de la descrita en el chat cloud.

No se encontró `Ps4Carousel.kt` en el checkout, el historial obtenido ni los árboles de las otras ramas remotas disponibles. Esto no demuestra que no exista en otro chat o rama aún sin publicar. No se tiene acceso al filesystem `/workspace/aura-launcher` del chat cloud. No se recreó ni sobrescribió código para simular esa entrega.

En el chat cloud, pedir que guarde los archivos exactos y prepare una entrega descargable, incluyendo los nuevos archivos sin seguimiento:

```sh
cd /workspace/aura-launcher
git status --short --branch
git rev-parse HEAD > /tmp/aura-cloud-base.txt
git diff --binary HEAD > /tmp/aura-cloud-tracked.patch
tar -czf /tmp/aura-cloud-beta38-files.tar.gz \
  app/build.gradle.kts \
  app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt \
  app/src/main/java/dev/senda/launcher/ui/Ps4Carousel.kt \
  app/src/main/java/dev/senda/launcher/ui/theme/ThemeChrome.kt \
  app/src/main/java/dev/senda/launcher/ui/theme/ThemeTypography.kt \
  README.md CHANGELOG.md docs/CONTINUITY.md docs/BETA38.md
sha256sum /tmp/aura-cloud-tracked.patch /tmp/aura-cloud-beta38-files.tar.gz
```

Si falta cualquiera de esos archivos, solicitar un manifiesto de los que realmente existen; no inventarlo. Descargar patch, archivo, hash y commit base por un canal privado. El patch de `git diff` por sí solo no incluye archivos nuevos sin seguimiento; el archivo de fuentes sí debe incluirlos. Revisar que no incluya secretos antes de transferir.

Aquí, guardar la entrega fuera del checkout, listar el contenido del archivo antes de extraerlo y comparar en una carpeta de staging privada. Ejecutar `git apply --check` antes de aplicar cualquier patch. Si las bases difieren, integrar mediante una rama de revisión y comparación de tres vías con el commit base; no copiar encima del código actual. No bajar versionCode de 39 a 38. Mantener el carrusel PS4 vertical aprobado, iconos reconocibles a color, placas cuadradas, ondas azules y tipografía ligera; conservar búsqueda, A–Z, favoritas, widgets, gestos, packs y accesibilidad. No restaurar el carrusel horizontal.

## Pendiente

- Conexión y vinculación ADB completadas el28 de octubre. Instalación mediante `adb install -r` correcta en el Motorola autorizado. Certificados del APK previo y candidato compatibles con la clave original; versión posterior0.28.0-beta39/code39 y hash del APK instalado eba7aba0d765ca6415b3047eb33abb6cac61a2c27eba5cc711549fd24e33d832 verificados. Sin desinstalación, limpieza de datos, cambios de permisos ni ajustes. Los puertos futuros deben consultarse de nuevo si caducan.
- Recuperar la variante cloud mediante entrega real y revisar conflictos con beta39.
- Revisión visual física y mediciones de rendimiento continúan pendientes; no inferirlas de una compilación correcta.
