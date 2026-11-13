package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.senda.launcher.data.*
import dev.senda.launcher.widgets.WidgetWeather
import kotlinx.coroutines.CancellationException
import java.time.ZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
internal fun WeatherForecastPanel(
    reading: WidgetWeather?, settings: WeatherMotionSettings, reduced: Boolean, origin: Rect?,
    onOpenProvider: () -> Unit, onConfigure: () -> Unit, onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val repository = remember(context) { WeatherForecast(context.applicationContext) }
    val city = reading?.place.orEmpty()
    var place by remember(city) { mutableStateOf(repository.selectedFor(city)) }
    var query by rememberSaveable(city) { mutableStateOf(city) }
    var results by remember { mutableStateOf<List<ForecastPlace>?>(null) }
    var report by remember(place) { mutableStateOf<ForecastReport?>(null) }
    var selectedHour by remember(place) { mutableStateOf<ForecastHour?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var searchRequest by remember { mutableIntStateOf(0) }
    var refresh by remember(place) { mutableIntStateOf(0) }
    val still = reduced || LocalAppearance.current.highContrast || LocalSystemPowerSaving.current
    val motion = rememberPs4PanelMotion(still)
    val close = { motion.close(still, onDismiss) }
    var destination by remember { mutableStateOf<Rect?>(null) }
    val maxHeight = (LocalConfiguration.current.screenHeightDp * .84f).dp
    val timeFormat = remember { DateTimeFormatter.ofPattern("HH:mm") }

    LaunchedEffect(place, searchRequest, refresh) {
        error = null
        results = null
        if (place == null && searchRequest == 0) return@LaunchedEffect
        loading = true
        try {
            val chosen = place
            if (chosen == null) results = repository.search(query)
            else {
                report = repository.load(chosen, refresh > 0)
                selectedHour = null
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            error = "No se pudo cargar el pronóstico. Comprueba la conexión y vuelve a intentarlo."
        } finally {
            loading = false
        }
    }
    val currentCondition = reading?.let {
        providerWeatherCondition(it.conditionText).takeUnless { it == WeatherCondition.AMBIENT }
            ?: it.symbolCondition
    } ?: WeatherCondition.AMBIENT
    val heroCondition = selectedHour?.condition ?: currentCondition
    val heroTemperature = selectedHour?.temperature?.let { "${it.roundToInt()} °C" } ?: reading?.temperature ?: "—"
    val heroTitle = selectedHour?.time?.format(DateTimeFormatter.ofPattern("EEE HH:mm"))
        ?: reading?.place?.ifBlank { "Clima" } ?: "Clima"
    val heroPhase = rememberPs4WeatherPhase("$heroTitle:$heroCondition:$heroTemperature", settings, still,
        ready = reading != null || selectedHour != null)

    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = .5f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null,
                    onClickLabel = "Cerrar clima", onClick = close))
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(18.dp).widthIn(max = 480.dp).fillMaxWidth()
                    .safeDrawingPadding()
                    .onGloballyPositioned { coordinates -> destination = Rect(coordinates.positionOnScreen(), Size(
                        coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) }
                    .ps4PanelTransform(motion.progress.asState(), motion.effect,
                        origin = { origin }, destination = { destination })
                    .semantics { paneTitle = "Clima y pronóstico" },
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(Modifier.heightIn(max = maxHeight).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Clima", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        TextButton(onClick = close) { Text("Cerrar") }
                    }
                    Box(Modifier.fillMaxWidth().ps4Material()) {
                    Ps4WeatherRibbon(heroCondition, settings, heroPhase, Modifier.matchParentSize(), fromRight = false)
                    Row(Modifier.fillMaxWidth().heightIn(min = 132.dp).padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Ps4WeatherIcon(heroCondition, settings, still, Modifier.size(90.dp), heroPhase)
                        Column(Modifier.weight(1f)) {
                            Text(heroTemperature, style = MaterialTheme.typography.headlineLarge,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(heroTitle, style = MaterialTheme.typography.titleMedium)
                            Text(if (selectedHour == null) "Lectura del widget" else "Pronóstico de Open-Meteo",
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    }
                    if (selectedHour != null) TextButton(onClick = { selectedHour = null }) { Text("Volver al clima actual") }
                    val chosen = place
                    if (chosen == null) {
                        Text("Elige la localidad del pronóstico", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(query, { query = it.take(120) }, modifier = Modifier.fillMaxWidth(),
                            label = { Text("Localidad, región o país") }, singleLine = true)
                        TextButton(onClick = { focus.clearFocus(); searchRequest++ },
                            enabled = query.trim().length >= 2 && !loading) { Text("Buscar localidad") }
                        results?.let { places ->
                            if (places.isEmpty()) Text("No se encontraron localidades. Añade la región o el país.")
                            places.forEach { candidate ->
                                TextButton(onClick = {
                                    repository.select(city, candidate)
                                    place = candidate
                                }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                    Text(candidate.label, Modifier.fillMaxWidth())
                                }
                            }
                        }
                    } else {
                        Text(chosen.label, style = MaterialTheme.typography.titleMedium)
                        Text("Próximas horas · °C", style = MaterialTheme.typography.labelLarge)
                        report?.let { data ->
                            Text("Horas de la localidad · ${data.zone}", style = MaterialTheme.typography.labelSmall)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(data.hours, key = { index, hour -> "$index-${hour.time}" }) { _, hour ->
                                    val selected = selectedHour == hour
                                    OutlinedButton(onClick = { selectedHour = hour },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant
                                            else androidx.compose.ui.graphics.Color.Transparent)) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(hour.time.format(timeFormat), style = MaterialTheme.typography.labelLarge)
                                            Ps4WeatherIcon(hour.condition, settings.copy(mode = WeatherMotionMode.OFF),
                                                true, Modifier.size(30.dp))
                                            Text("${hour.temperature.roundToInt()}°")
                                            Text(hour.precipitation?.let { "$it %" } ?: "—",
                                                style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                            Text("Cada hora muestra temperatura y probabilidad de precipitación.",
                                style = MaterialTheme.typography.bodySmall)
                            HorizontalDivider()
                            Text("Amanecer · ${data.sunrise?.format(timeFormat) ?: "Sin dato"}")
                            Text("Atardecer · ${data.sunset?.format(timeFormat) ?: "Sin dato"}")
                            val updated = Instant.ofEpochMilli(data.fetchedAt).atZone(ZoneId.systemDefault()).format(timeFormat)
                            Text("Open-Meteo · actualizado $updated${if (data.offline) " · copia sin conexión" else ""}",
                                style = MaterialTheme.typography.labelSmall)
                            TextButton(onClick = { uriHandler.openUri("https://open-meteo.com/") }) {
                                Text("Fuente del pronóstico · Open-Meteo")
                            }
                        }
                        Row {
                            TextButton(onClick = { refresh++ }, enabled = !loading) { Text("Actualizar") }
                            TextButton(onClick = {
                                repository.clearSelection()
                                searchRequest = 0
                                place = null
                            }) { Text("Cambiar localidad") }
                        }
                    }
                    if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
                    HorizontalDivider()
                    TextButton(onClick = { motion.close(still, onOpenProvider) }) { Text("Abrir aplicación del clima") }
                    TextButton(onClick = { motion.close(still, onConfigure) }) { Text("Configurar widget y efectos") }
                }
            }
        }
    }
}
