# Verificación beta25

## Evidencia de esta entrega

- Variante `beta`, paquete `dev.senda.launcher.debug`, versión `0.19.0-beta25`, código25, minAPI29/targetAPI37.
- Gradle `assembleBeta lintBeta`: **BUILD SUCCESSFUL**, 2m32s; 49 tareas (16 ejecutadas, 33 actualizadas). R8 y reducción de recursos activos.
- Lint: **0 errores, 7 advertencias, 3 sugerencias**. Dos constantes de la pantalla de notificaciones API30 tienen alternativa a la lista general si Android no resuelve el detalle; otras advertencias son dependencias, reflexión necesaria para paquetes externos de iconos y altura del editor existente. No se actualizó la pila de dependencias en esta entrega.
- APK firmado y verificado. Certificado SHA256 `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`, coincide con las betas instaladas.
- APK SHA256 `1c17470ddbe555e34cfdb1f7160c4128ae6053031793622edde0dfac7833769a`.
- Una revisión independiente de código, seguida de correcciones: esquema completo de contenedores Material3 y colores de botón para LG claro; explicación del acceso desplazable. También se corrigieron formato del reloj, escalado de texto condicionado al espacio y nacimiento gradual de ondas.
- Compilaciones intermedias detectaron una referencia de bloque incorrecta y receptores de dibujo/medida implícitos; corregidos antes de la compilación final.
- Sin suites automáticas ni nuevas pruebas ejecutadas. Compilar/lint/revisar código no sustituye una revisión física.

## Contraste de roles nominales

Medición matemática de colores sólidos del perfil, mínimo frente a sus dos extremos. No representa una captura ni prueba el contraste sobre fondos personales o todos los estados de Android.

| Tema | Texto | Secundario | Señal |
| --- | ---: | ---: | ---: |
| Nothing | 17.01 | 9.43 | 8.26 |
| Sense | 10.82 | 7.80 | 8.11 |
| TouchWiz | 9.36 | 7.29 | 7.45 |
| LG | 11.25 | 5.99 | 5.45 |
| Xperia | 13.79 | 9.74 | 8.88 |
| RAZR | 12.54 | 9.20 | 10.52 |

## Dispositivo y entrega

El Motorola vinculado se identificó al inicio y estaba bloqueado. Después dejó de aparecer en ADB y mDNS. Los dos extremos ADB previamente conocidos agotaron el tiempo de conexión; el PC volvió a otra red. **La beta25 no está instalada ni copiada al teléfono todavía.** La última versión instalada conocida es beta24.

No se cambió el tiempo de pantalla, el launcher predeterminado, los permisos ni los datos del teléfono. No se concedió acceso a notificaciones; su activación sigue siendo voluntaria en Android.

## Comprobaciones físicas pendientes

- Instalar con `adb install -r`, copiar APK a Downloads y confirmar versión25 con PackageManager.
- Comparar los seis temas en Inicio y en su muestra, texto grande, horizontal, acabado personalizado y alto contraste.
- Revisar movimiento reducido, sistema sin animaciones y salida/entrada de Inicio.
- Reproducción con y sin permiso, retirada del permiso durante reproducción, dos reproductores, sesión destruida y cambio de pista/caratula rápido.
- Confirmar comportamiento de pausa/anterior/siguiente según acciones publicadas por cada reproductor del Motorola.
- Validar restauración de un respaldo previo y conservar favoritos/widgets sin sustituir datos actuales.
- Aprobar visualmente densidad, brillo y fluidez en el panel real. No hay cifras nuevas de FPS/energía ni garantía de equivalencia exacta con firmware histórico.
