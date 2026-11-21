# Beta108 · Corrección Sony y Centro rápido por familia

21 de noviembre de 2026. Aura 0.74.0-beta108, código 108, paquete `dev.senda.launcher.debug`. Base beta107 `3c2e767`, rama `codex/beta108-theme-fidelity`.

## Corrección de dirección visual

El usuario rechazó las variantes alternativas de beta107 por perder la identidad Sony. La corrección retira las esferas gigantes y los controles ficticios del wallpaper Vita; conserva las burbujas como placas decorativas de aplicaciones, sin recortar iconos originales o personales. El fondo recupera azul saturado y tres bandas de cristal. La fuente deja de ser Roboto Condensed: se usa la alternativa libre Noto ligera, sin afirmar que sea la fuente propietaria de Sony.

PS5 cambia el acento principal dorado por blanco neutro, recupera azul grafito y reduce el radio de paneles de 24 a 10 dp y el de placas de 14 a 8 dp. El oro queda limitado a algunas motas ambientales; las superficies y textos ya no dependen de ese color para parecer otra interfaz. Clásica conserva sus ondas azules. Mantener ergonomía vertical de Aura sigue siendo un requisito: no se reinstala el menú horizontal rechazado.

Los fondos son procedimientos originales y cacheados. No se añaden relojes infinitos de partículas ni dependencias.

## Funciones PS4 disponibles en las siete familias

Centro rápido, botón inferior y miniatura se habilitan en Nothing, Sense, TouchWiz, LG, Xperia, RAZR y PS4. Se reutilizan controles de reproducción y carátula, ampliación/contracción por gesto, alarmas, agenda, notas y temporizadores existentes. No se añaden permisos.

| Familia | Material aplicado al Centro rápido |
| --- | --- |
| Nothing | Negro, puntos y señal mínima; conserva fuentes industriales existentes |
| HTC Sense | Grafito mecánico, aristas y Droid Sans |
| Galaxy SIII | Brillo de agua, curva Nature UX y Roboto ICS |
| LG Optimus | Papel cálido con tinta oscura, capas y Lato |
| Xperia | Cristal oscuro, línea de luz y Roboto Condensed |
| RAZR V3 | Metal/LCD, bisel y Share Tech Mono |
| PlayStation | Paleta y geometría de la variante elegida |

Los iconos funcionales usan ahora el tema activo. La forma manual se aplica mediante el mismo motor de formas usado por las otras superficies. La cadencia de los paneles usa la duración histórica de cada familia y sigue multiplicando la duración elegida por el usuario. El efecto configurado, movimiento reducido, alto contraste y ahorro conservan prioridad. El desenfoque de portada queda limitado a las familias Sony; no se aplica a Nothing o al papel LG.

Las sesiones pausadas válidas pueden conservar sus controles en los demás temas si el widget de audio está habilitado. No se convierte una sesión genérica en un reproductor controlable.

## Revisión y validación

- UI/UX Pro Max consultado para Compose/animación y separación entre lógica UI y estado. La búsqueda inicial específica no encontró coincidencias; una búsqueda acotada sí devolvió tres reglas. La dirección artística se basa en referencias oficiales Sony, no en una plantilla web.
- Revisión source independiente: detectó fondo/tinta incompatibles en LG, forma personalizada ignorada y falta de material histórico en el botón/miniatura. Las tres correcciones fueron revisadas de nuevo sin hallazgos accionables restantes.
- Primer intento de compilación falló por `LauncherTheme?` donde se requería un enum no nulo. Se centralizó un fallback en `activeQuickTheme`.
- Un intento de lint detectó `SuspiciousIndentation` en el condicional del fondo Sony; se corrigió con bloque explícito, sin desactivar la regla.
- Validación final: `gradlew.bat --max-workers=2 assembleBeta lintBeta`, BUILD SUCCESSFUL en 1 min 37 s; 0 errores, 58 advertencias y 4 avisos. El aviso Kotlin de condición siempre verdadera en HomeScreen ya existía.
- Auditoría matemática acotada de tinta/texto secundario contra superficies base: PS4 6.41:1, PS5 9.42:1, Vita 4.68:1; Nothing 9.43:1, Sense 7.80:1, SIII 7.29:1, LG 5.99:1, Xperia 9.74:1, RAZR 9.20:1. No cubre fotografías, composiciones translúcidas, estados ambientales ni colores manuales.
- No se ejecutaron ni añadieron suites automáticas, ni generadores de iconos o nuevas dependencias.

## Entrega y límites

APK: `app/build/outputs/apk/beta/app-beta.apk`. Copia estable ignorada por Git: `beta/Aura-0.74.0-beta108.apk`. SHA-256: `6BCAD5936A8F93F0EF589BAFF88E212BCBE9987551CEB0B1235F88A4BD52822D`. Certificado candidato verificado: `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`. El 21 de noviembre se comprobó además el mismo hash en el APK realmente instalado.

El 21 de noviembre se vinculó el Motorola Edge 50 Ultra autorizado y se comprobó que tenía beta107. Su APK se extrajo a almacenamiento privado fuera de Git y se comparó el certificado con beta108: compatible. Se transfirió el candidato, se verificó su hash remoto y `pm install -r` devolvió Success sin desinstalar. Verificación posterior: `versionCode=108`, `versionName=0.74.0-beta108`, SHA-256 del APK instalado idéntico al candidato y Aura conserva el rol HOME. No se cambiaron ajustes ni permisos. Comparación visual física pendiente. No se afirma haber perfeccionado visualmente las siete familias ni fidelidad exacta a Sony por una revisión de código. La restauración retro/widgets/permisos pendiente de beta106 continúa pendiente; no se han cambiado permisos ni ajustes del teléfono.

GitHub contrastado: base propia beta107 sin cambios remotos nuevos; `main` anuncia beta99 `43b2789`. Esta entrega publica su propia rama sin sobrescribir ni incorporar silenciosamente esa línea.

## Referencias oficiales

- [Primer recorrido de la interfaz PS5](https://blog.playstation.com/2020/10/15/first-look-playstation-5s-next-generation-user-experience/).
- [Inicio y LiveArea de PS Vita](https://manuals.playstation.net/document/en/psvita/basic/screens.html), con capturas oficiales consultadas.
- [Historia de diseño PS5 de Sony](https://www.sony.com/en/SonyInfo/design/stories/PS5/).
