# Aura0.24.1-beta32 · PS4 con la ergonomía de Aura

Fecha:26 de octubre de2026. Estado: compilada y firmada; instalación y revisión física pendientes por ausencia del Motorola.

## Corrección principal

La imitación de la consola en beta31 cambió demasiado la forma de usar Aura. Se retira la pantalla separada, el carrusel, la Biblioteca adicional y los accesos a PSN. PS4 vuelve a ser un tema sobre la estructura común del launcher.

- Un toque abre cada aplicación. Favoritas y todas las apps vuelven a filas verticales.
- Búsqueda siempre disponible, índice A–Z lateral y salto accesible a letras restaurados.
- Los índices de sección vuelven a contar todas las filas favoritas, evitando saltos incorrectos tras retirar el carrusel.
- Gestos de Inicio restaurados. Pulsación larga abre acciones, estrella independiente edita favoritas y doble toque en huecos conserva el bloqueo configurado.
- Regresan reloj/clima, accesos a alarmas/calendario, widgets y reproducción con su ubicación configurada.
- Las filas PS4 amplían la zona de apertura al ancho disponible antes de la estrella, con respuesta de pulsación y contorno de foco. No requieren seleccionar antes de abrir ni encoger/escalar el contenido.
- Se conservan Noto Sans, ondas de luz con ciclo de vida, degradado azul oscuro y símbolo de perfil. Movimiento reducido, alto contraste y fondos personales mantienen su comportamiento.

No se migran ni borran preferencias, iconos o datos. La corrección sustituye la interpretación de PS4 de beta31; sus notas históricas describen esa versión anterior.

## Verificación

No se ejecutan suites automáticas por preferencia del usuario. assembleBeta/lintBeta: BUILD SUCCESSFUL en2m31s,0 errores/25 advertencias/3 avisos (incluyen recursos de consola que ya no se usan). APK code32/0.24.1-beta32 verificado con aapt; firma con el mismo certificado que betas anteriores. SHA256: `a1b07677292177a60a9dfc16c87f4e6404c6df05e67273d127e11e19f870a722`. Teléfono ausente de ADB; no se instaló ni se cambiaron sus ajustes. Ver QA-beta32.md para revisión estática y comprobaciones físicas pendientes. No se afirman FPS medidos ni equivalencia exacta con una consola.
