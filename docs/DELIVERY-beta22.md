# Entrega beta22 — 22 de octubre de 2026

## Artefactos

- `beta/Aura-0.17.0-beta22.apk`: APK optimizado firmado, **3.012.610 bytes**.
- `beta/Aura-0.17.0-beta22-source.zip`: fuentes versionadas, sin claves ni evidencia/recursos personales.
- `beta/SHA256SUMS-beta22.txt`: SHA-256 de ambos archivos, generado después del empaquetado.

APK SHA-256: `7e79265e32cc75c4b763f0cbe1084888355dc1b9ec9ac299ab68fda487d2caa6`.

Certificado SHA-256: `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`.

El APK conserva el paquete `dev.senda.launcher.debug`; no es debuggable. No se ha publicado código ni se ha creado un repositorio remoto.

## Comprobaciones ejecutadas

| Comprobación | Resultado |
|---|---|
| Suite JVM | **22 pruebas, 0 fallos, 0 errores**: uso diario 7, texto 4, borrador 6, recursos locales 3, tema Motorola 2. |
| `assembleBeta` | Correcto, R8 y reducción de recursos activos. |
| `lintBeta` | **0 errores, 6 advertencias, 3 sugerencias**. |
| Firma `apksigner verify --print-certs` | Correcta; mismo certificado que las betas anteriores. |
| `adb install -r` del APK final | **Success**, sin desinstalar. Android confirma código 22 y versión 0.17.0-beta22. |
| Launcher predeterminado | Aura permanece seleccionado. |
| Permisos del APK | Widget host, calendario y permiso interno del receptor; sin INTERNET ni ubicación. |
| Ajustes temporales del teléfono | No se cambiaron. Apagado de pantalla continúa en 60.000 ms; acceso de gestos y servicios MacroDroid siguen activos. |
| Revisión independiente | Un hallazgo importante de recreación corregido con prueba RED→GREEN; sin críticos ni menores nuevos. [Detalle](REVIEW-beta22.md). |
| `git diff --check` | Sin errores de espacios. |

La compilación final y las pruebas terminaron correctamente en 2 min 49 s en esta máquina. Ese tiempo es de construcción, no de arranque del launcher.

Métricas del compilador beta: 230 composables skippable, 324 restartable, 18 clases inferidas inestables. No se añadieron promesas falsas de estabilidad ni se interpreta este conteo como FPS.

## Pendientes visibles

La verificación física completa del editor/rotación, ZIP inválido y tema RAZR queda para una sesión estable del teléfono. El dispositivo cambió de primer plano durante el intento de QA; no se automatizó el cierre del vídeo ni se solicitó intervención nocturna. No hay una comparación válida de fluidez, consumo o batería. [Lista concreta de comprobaciones](BETA22.md).

## Cuota y decisiones

Última lectura antes del empaquetado: 68% usado en ventana de 5 horas y 42% semanal. Se reservó margen respecto del límite humano de 95%. Las ventanas pertenecen a la cuenta completa y pueden cambiar por otros chats; no equivalen a un contador exacto de tokens de este trabajo.

Las ocho decisiones del ejecutor y su coste están en [REVIEW-beta22.md](REVIEW-beta22.md). Se conserva la rama local `codex/aura-polish` y su historial.
