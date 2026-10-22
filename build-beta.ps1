param([string]$SigningKeystore = "$env:USERPROFILE\.android\debug.keystore")
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $toolchain = (Resolve-Path '..\..\work\android-toolchain').Path
    $env:JAVA_HOME = Join-Path $toolchain 'jdk\jdk-17.0.20.1+1'
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    $env:ANDROID_HOME = Join-Path $toolchain 'android-sdk'
    $env:ANDROID_USER_HOME = $toolchain
    $env:GRADLE_USER_HOME = (Resolve-Path '..\..\work\gradle-home').Path
    & .\gradlew.bat --no-daemon '-Pkotlin.compiler.execution.strategy=in-process' '-Pkotlin.incremental=false' testDebugUnitTest assembleDebug lintDebug assembleBeta lintBeta
    if ($LASTEXITCODE -ne 0) { throw 'La compilación o sus comprobaciones fallaron.' }
    if (!(Test-Path -LiteralPath $SigningKeystore)) { throw 'No se encuentra la clave de firma. Indica -SigningKeystore con la clave de las betas ya instaladas.' }
    $version = [regex]::Match((Get-Content app\build.gradle.kts -Raw), 'versionName = "([^"]+)"').Groups[1].Value
    $apk = Join-Path $PSScriptRoot "beta\Aura-$version-debug.apk"
    $signer = Join-Path $env:ANDROID_HOME 'build-tools\37.0.0\apksigner.bat'
    & $signer sign --ks $SigningKeystore --ks-pass pass:android --key-pass pass:android --out $apk app\build\outputs\apk\debug\app-debug.apk
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo firmar la beta.' }
    & $signer verify --print-certs $apk
    if ($LASTEXITCODE -ne 0) { throw 'La verificación de firma falló.' }
    Get-FileHash -LiteralPath $apk -Algorithm SHA256
    $optimizedApk = Join-Path $PSScriptRoot "beta\Aura-$version.apk"
    & $signer sign --ks $SigningKeystore --ks-pass pass:android --key-pass pass:android --out $optimizedApk app\build\outputs\apk\beta\app-beta.apk
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo firmar la beta optimizada.' }
    & $signer verify --print-certs $optimizedApk
    if ($LASTEXITCODE -ne 0) { throw 'La verificación de firma optimizada falló.' }
    Get-FileHash -LiteralPath $optimizedApk -Algorithm SHA256
} finally { Pop-Location }
