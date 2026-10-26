# OpenCode · corrección aprobada tras auditoría

Implementa solo este cambio verificado en4 archivos:

1. ui/ThemedHeaderCaption.kt: dentro de MaterialTheme.typography.labelSmall.copy, quitar fontFamily=...SansSerif/Monospace para heredar Geist Mono/Droid/Roboto/Lato/Share Tech/Noto según tema. Mantener peso, spacing y resto del componente.
2. ui/InformationCard.kt: quitar la familia fija del estilo de etiqueta; heredar la familia de su rol MaterialTheme. No cambiar texto, contenido, tamaños, colores o forma.
3. ui/ThemedAudioWidget.kt: quitar la familia fija del Text de info.title; mantener su style=MaterialTheme.typography.titleSmall y demás propiedades.
4. ui/theme/RetroClock.kt: solamente en la rama SENSE, el Text(digits...) del reloj flip no debe forzar FontFamily.SansSerif; usa style=MaterialTheme.typography.headlineLarge para heredar Droid Sans, conservando fontWeight.Bold y fontSize calculado. El resto de ramas permanece igual.

Todas las rutas anteriores parten de app/src/main/java/dev/senda/launcher. Elimina imports FontFamily únicamente si quedan sin usos.

No cambiar HomeScreen ni la clave de caché: el supuesto fallo F1 fue descartado porque LauncherViewModel.icon usa PackageManager y el catálogo PS4 retorna antes de esa caché. No añadir fallback genérico ni fuentes nuevas. No ejecutar comandos, pruebas, builds, Git ni ADB. El agente principal revisará diff y compilará. Devuelve un resumen concreto de los4 archivos y comprobaciones pendientes.
