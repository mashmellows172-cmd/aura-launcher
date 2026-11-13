# Beta76 · mensajes junto al logo

12 de noviembre de 2026. 0.54.0-beta76, código 76.

- PS4: mensaje motivacional junto al logo/avatar, debajo de Aura; sustituye Inicio cuando la preferencia está activa. Se elimina la línea separada debajo de reloj/clima para liberar espacio vertical.
- Frases cambian cada cinco minutos aprovechando las actualizaciones del reloj existente. Sin temporizador nuevo ni anuncios automáticos de TalkBack.
- Catálogo PS4 propio: 18 frases, distribuidas entre mañana, tarde y noche. Los demás temas conservan sus frases y adoptan la misma rotación.
- Fundido mediante ThemedHeaderCaption existente, hasta dos líneas y elipsis; respeta movimiento reducido, alto contraste, ahorro y animaciones desactivadas en PS4. No cambia datos ni preferencias.

## Verificación
- Revisión de los consumidores de phraseFor y de las dos posiciones de cabecera. git diff --check correcto.
- assembleBeta lintBeta con auraSideBySide=true correctos en 3m6s; 0 errores, 40 advertencias, 3 avisos; no suites automáticas. Log privado local work/aura-beta76-local-build.log.
- Validación visual en teléfono pendiente.

## Instalación
- Motorola ZY22K88ZFL verificado por ADB inalámbrico. Una comprobación inicial con selector USB devolvió no devices found; se corrigió al selector inalámbrico anunciado.
- Firma previa y nueva: 915f891c1f55b7dc7d01b7ee31504c97d55952515449c6857d0d55485ddea966.
- install --no-streaming -r correcto; dev.senda.launcher.local code76/0.54.0-beta76 verificado. No se toca la edición original, permisos ni preferencias.
- APK: beta/Aura-0.54.0-beta76-local.apk. Hash SHA-256 local y extraído tras instalar: CDB99BE1473D4FEF60E65660A748C49E1F5BA98E78EF2CC2D9AD7B9273DC4F0C.
- Usuario en otra aplicación; no se interrumpió su uso para revisión visual.
