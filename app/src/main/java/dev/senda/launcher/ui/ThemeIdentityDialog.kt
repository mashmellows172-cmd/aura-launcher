package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.*
import dev.senda.launcher.widgets.WidgetWeather

/** Product preview: no real media session, provider host, calendar read or preference write. */
@Composable
internal fun ThemeIdentityDialog(initialTheme: LauncherTheme, finish: ThemeFinish, reduced: Boolean, highContrast: Boolean, onDismiss: () -> Unit) {
    var themeName by rememberSaveable(initialTheme) { mutableStateOf(initialTheme.name) }
    val theme = LauncherTheme.valueOf(themeName)
    val previewFinish = if (theme == initialTheme) finish else ThemeFinish()
    val searchFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    val searchInteractions = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    var selected by rememberSaveable(theme) { mutableStateOf(WeatherCondition.SUN.name) }
    var playing by rememberSaveable(theme) { mutableStateOf(false) }
    val condition = WeatherCondition.valueOf(selected)
    val profile = historicalProfile(theme)
    AuraTheme(theme, isSystemInDarkTheme(), highContrast, previewFinish, reduceMotion = reduced) {
        AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Identidad del tema") }, text = {
            Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EffectChoice("Comparar familia", themeName, dev.senda.launcher.ui.theme.icons.iconFamilyThemes.map { it.name to it.title },
                    onSelect = { themeName = it; playing = false })
                Text("La comparación no aplica cambios. Las otras familias muestran sus acabados predeterminados.", style = MaterialTheme.typography.bodySmall)
                Text(profile?.reference ?: theme.title, style = MaterialTheme.typography.titleMedium)
                Text(profile?.era ?: theme.subtitle, style = MaterialTheme.typography.bodySmall)
                profile?.let { Text(it.identity, style = MaterialTheme.typography.bodyMedium) }
                Text("Datos de ejemplo · no cambia tu inicio ni reproduce contenido", style = MaterialTheme.typography.labelSmall)
                Column(Modifier.fillMaxWidth().background(LocalSkinStyle.current.wallpaper).then(if (!highContrast) Modifier.skinPattern(LocalSkinStyle.current) else Modifier).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ThemedHeaderCaption(theme, MotivationalCatalog.phraseFor(theme), reduced)
                    Box(Modifier.fillMaxWidth().heightIn(min = 74.dp), contentAlignment = Alignment.CenterStart) {
                        WeatherSweep(theme, WidgetWeather("22°", "Ejemplo", null, condition.label),
                            WeatherMotionSettings(if (playing) WeatherMotionMode.REPEAT else WeatherMotionMode.OFF, condition = condition), reduced || highContrast,
                            Modifier.matchParentSize())
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.weight(1f)) {
                                if (theme == LauncherTheme.NOTHING) DotClock("09:41", Modifier.width(180.dp).height(52.dp))
                                else if (LocalRetroStyle.current.enabled) RetroClock("09:41")
                                else Text("09:41", style = MaterialTheme.typography.headlineMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("22°", color = MaterialTheme.colorScheme.onBackground)
                                Text(condition.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                    ThemedSearchField(theme, "", {}, {}, {}, searchFocus, searchInteractions, { 0f }, readOnly = true)
                    ThemedSectionHeading("Tus favoritas", 3, theme)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf(dev.senda.launcher.ui.theme.icons.FamilyIcon.PHONE, dev.senda.launcher.ui.theme.icons.FamilyIcon.CAMERA,
                            dev.senda.launcher.ui.theme.icons.FamilyIcon.MUSIC).forEach { icon ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                dev.senda.launcher.ui.theme.icons.FamilyArtwork(icon, theme, Modifier.size(42.dp))
                                Text(icon.label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    InformationCard("Próxima alarma", "Mañana · 07:30", HomeBlock.ALARM, {})
                    InformationCard("Agenda", "Un espacio para tus planes", HomeBlock.AGENDA, {})
                    ThemedAudioWidget(ActiveAudioInfo(isPlaying = true, title = "Tu momento musical", artist = "Vista previa", appLabel = "Aura", canPause = true),
                        theme, reduced || highContrast || !playing, {}, preview = true)
                }
                EffectChoice("Clima de ejemplo", selected,
                    WeatherCondition.entries.filter { it != WeatherCondition.AUTO }.map { it.name to it.label }, onSelect = { selected = it })
                TextButton(onClick = { playing = !playing }, enabled = !reduced && !highContrast, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (playing) "Detener animaciones de ejemplo" else "Reproducir animaciones de ejemplo")
                }
                Text(if (reduced || highContrast) "El movimiento está desactivado por tus ajustes de accesibilidad." else "La escena y el visualizador son decorativos. Los controles multimedia de esta muestra están desactivados.", style = MaterialTheme.typography.bodySmall)
                Text("Referencia visual reinterpretada para Aura. Tus recursos de colección y acabados personales pueden variar el resultado.", style = MaterialTheme.typography.bodySmall)
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text("Volver al editor") } })
    }
}
