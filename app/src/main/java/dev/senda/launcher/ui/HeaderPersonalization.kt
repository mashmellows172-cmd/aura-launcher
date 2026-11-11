package dev.senda.launcher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.animation.ValueAnimator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.loadLocalImage
import dev.senda.launcher.data.HeaderSettings
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.WeatherCondition
import dev.senda.launcher.data.WeatherMotionSettings
import dev.senda.launcher.data.WeatherMotionMode
import dev.senda.launcher.data.providerWeatherCondition
import dev.senda.launcher.data.atmosphericScene
import dev.senda.launcher.ui.theme.LocalIndustrialStyle
import dev.senda.launcher.ui.theme.themeReveal
import dev.senda.launcher.ui.theme.LocalRetroStyle
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.widgets.WidgetController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

@Composable
fun renderAvatarImage(uri: String?, modifier: Modifier = Modifier): Boolean {
    val image = rememberLocalImage(uri, 512)
    if (image == null) return false
    Image(image!!,null,modifier.clip(CircleShape),contentScale=ContentScale.Crop)
    return true
}

@Composable
fun AvatarDialog(state: HomeUiState, viewModel: LauncherViewModel, onDismiss: () -> Unit) {
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let { viewModel.setAvatar(it.toString()) } }
    AuraAlertDialog(onDismissRequest={if(!state.avatarBusy) onDismiss()}, title={Text("Tu avatar")},text={
        Column(verticalArrangement=Arrangement.spacedBy(12.dp), horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth()) {
            if (!renderAvatarImage(state.avatarUri,Modifier.size(80.dp).border(1.dp,MaterialTheme.colorScheme.outline,CircleShape))) Text("A",style=MaterialTheme.typography.displayMedium)
            Text("Tu imagen se muestra en todos los temas. Doble toque sigue bloqueando; la pulsación larga abre personalización.",style=MaterialTheme.typography.bodySmall)
            Button(onClick={picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!state.avatarBusy){Text("Elegir imagen de avatar")}
            TextButton(onClick={viewModel.setAvatar(null)},enabled=state.avatarUri!=null&&!state.avatarBusy){Text("Restablecer logotipo Aura")}
            if(state.avatarBusy) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.avatarError?.let {Text(it,color=MaterialTheme.colorScheme.error)}
        }
    },confirmButton={TextButton(onClick=onDismiss,enabled=!state.avatarBusy){Text("Listo")}})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderWeather(
    controller: WidgetController,
    packageName: String,
    sourceLabel: String,
    reduceMotion: Boolean,
    onOpen: () -> Unit,
    onConfigure: () -> Unit,
    modifier: Modifier = Modifier,
    ps4Motion: WeatherMotionSettings? = null,
    prominent: Boolean = false,
    onOpenPanel: ((Rect?) -> Unit)? = null,
) {
    val id=controller.weatherWidgetId
    val info=remember(id,packageName,controller.providerRevision){controller.manager.getAppWidgetInfo(id)}
    val connected=info?.provider?.packageName==packageName
    val reading=if(connected)controller.weatherReading else null
    val condition = remember(reading?.conditionText) { providerWeatherCondition(reading?.conditionText.orEmpty()) }
    val iconCondition = if (condition == WeatherCondition.AMBIENT) reading?.symbolCondition ?: condition else condition
    val change = if (ps4Motion != null) dev.senda.launcher.ui.theme.rememberClockChange(
        "${reading?.temperature}|${reading?.place}|${reading?.conditionText}", LauncherTheme.PS4,
        reduceMotion || LocalThemePreview.current || ps4Motion.mode == WeatherMotionMode.OFF, durationMs = 760,
        playOnEntry = true, ready = reading != null,
    ) else null
    val width = if (ps4Motion == null) modifier.width(104.dp) else modifier
    var bounds by remember { mutableStateOf<Rect?>(null) }
    Box(width.heightIn(min = if (prominent) 94.dp else 72.dp)
        .onGloballyPositioned { coordinates ->
            if (onOpenPanel != null) bounds = Rect(coordinates.positionOnScreen(),
                Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
        }
        .combinedClickable(onClick = {
            if (onOpenPanel != null) onOpenPanel(bounds)
            else if (reading != null) onOpen() else onConfigure()
        },
            onLongClick = onConfigure, onLongClickLabel = "Ver detalles del clima")
        .semantics(mergeDescendants = true) {
            contentDescription = if (reading != null) {
                val event = if (ps4Motion != null && iconCondition != WeatherCondition.AMBIENT) "${iconCondition.label}. " else ""
                "Clima de ${reading.place}: ${reading.temperature}. ${event}Datos de $sourceLabel"
            } else "Configurar clima de $sourceLabel"
            role = Role.Button
        }, contentAlignment = Alignment.Center) {
        if(connected && !LocalThemePreview.current) key(id){
            Box(Modifier.size(1.dp).clearAndSetSemantics{}) {
            AndroidView(factory={context->controller.host.createView(context,id,info).apply{visibility=android.view.View.INVISIBLE;importantForAccessibility=android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS;
                @Suppress("DEPRECATION") updateAppWidgetSize(null,120,90,120,90)}},
                modifier=Modifier.requiredSize(120.dp,90.dp).clearAndSetSemantics{})
            }
        }
        if (ps4Motion != null && ps4Motion.condition != WeatherCondition.AUTO) {
            WeatherSweep(LauncherTheme.PS4, reading, ps4Motion, reduceMotion || LocalThemePreview.current,
                Modifier.matchParentSize(), finiteUpdates = true)
        }
        val contentMotion = if (change != null) Modifier.graphicsLayer {
            val p = change.value
            alpha = .7f + .3f * p
            translationY = (1f - p) * (if (prominent) 12.dp else 4.dp).toPx()
        } else Modifier.themeReveal(
            if (LocalIndustrialStyle.current.enabled) LauncherTheme.NOTHING else LocalRetroStyle.current.theme ?: LauncherTheme.NOTHING,
            reading?.temperature to reading?.conditionText, reduceMotion,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = contentMotion.padding(4.dp)) {
            if(reading!=null){
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
                    if (ps4Motion != null) {
                        Ps4WeatherIcon(iconCondition, ps4Motion, reduceMotion || LocalThemePreview.current,
                            Modifier.size(if (prominent) 48.dp else 34.dp))
                    } else {
                        reading.icon?.let { icon->Image(icon,null,Modifier.size(if (prominent) 39.dp else 30.dp),colorFilter=if(LocalIndustrialStyle.current.enabled) ColorFilter.tint(MaterialTheme.colorScheme.onSurface) else null)}
                    }
                    Text(reading.temperature, style = if (prominent) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(reading.place.ifBlank{sourceLabel},style=if (prominent) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.labelSmall,maxLines=1,overflow=TextOverflow.Ellipsis,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }else{
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (ps4Motion != null) Ps4WeatherIcon(WeatherCondition.AMBIENT, ps4Motion, true, Modifier.size(32.dp))
                    Text(if (connected) "Sin lectura" else "Clima", style = MaterialTheme.typography.labelLarge)
                }
                Text(if(connected)"Abrir ajustes" else "Conectar",style=MaterialTheme.typography.labelSmall,color=LocalSkinStyle.current.accent)
            }
        }
    }
}

@Composable
fun WeatherConnectionDialog(state: HomeUiState, controller: WidgetController, onSettings:(HeaderSettings)->Unit, onOpen: () -> Unit, onChooseApp: () -> Unit, onDismiss: () -> Unit) {
    val source=state.weatherPackage?:"hr.dtakac.prognoza"
    val sourceLabel=state.apps.firstOrNull{it.packageName==source}?.label?:"Prognoza"
    val info=remember(controller.weatherWidgetId,controller.providerRevision){controller.manager.getAppWidgetInfo(controller.weatherWidgetId)}
    AuraAlertDialog(onDismissRequest=onDismiss,title={Text("Clima en el reloj")},text={
        Column(Modifier.heightIn(max=420.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
            Text("Fuente: $sourceLabel",style=MaterialTheme.typography.titleMedium)
            Text(if (state.theme == LauncherTheme.PS4)
                "Aura muestra la temperatura y la ciudad del widget. PS4 usa su propio icono y muestra un símbolo neutro si la condición no se identifica. Configura la ubicación, las unidades y la actualización en $sourceLabel."
                else "Aura refleja la temperatura, ciudad y símbolo que publica su widget. La ubicación, unidades y actualización se configuran en $sourceLabel.")
            Text(if(info?.provider?.packageName==source)"Widget conectado" else "Conecta el widget para mostrar sus datos en el reloj",color=MaterialTheme.colorScheme.primary)
            if (info?.provider?.packageName == source) controller.weatherReading?.let { reading ->
                Text("${reading.place} · ${reading.temperature}", style = MaterialTheme.typography.titleLarge)
                val description = reading.conditionText.ifBlank { reading.symbolCondition?.label.orEmpty() }
                if (description.isNotBlank()) Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick={controller.connectWeather(source)}){Text("Conectar widget del clima")}
            TextButton(onClick=onOpen){Text("Abrir $sourceLabel")}
            TextButton(onClick=onChooseApp){Text("Elegir aplicación del clima")}
            if(info!=null) TextButton(onClick=controller::disconnectWeather){Text("Desconectar clima de la cabecera")}
            controller.weatherError?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            HorizontalDivider()
            if (state.theme == LauncherTheme.PS4) {
                Ps4WeatherPreview(state.headerSettings.weatherMotion,
                    state.visualSettings.reduceMotion || LocalAppearance.current.highContrast || LocalSystemPowerSaving.current)
            }
            WeatherMotionControls(state.headerSettings.weatherMotion, finite = state.theme == LauncherTheme.PS4) {onSettings(state.headerSettings.copy(weatherMotion=it))}
            Text("La escena manual solo añade un efecto decorativo. Movimiento reducido y alto contraste detienen el efecto.",style=MaterialTheme.typography.bodySmall)
        }
    },confirmButton={TextButton(onClick=onDismiss){Text("Listo")}})
}

@Composable
private fun Ps4WeatherPreview(settings: WeatherMotionSettings, reduced: Boolean) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(WeatherCondition.SUN.name) }
    var replay by remember { mutableIntStateOf(0) }
    TextButton(onClick = { expanded = !expanded }) {
        Text(if (expanded) "Cerrar muestra PS4" else "Ver iconos y animaciones PS4")
    }
    if (expanded) {
        val condition = WeatherCondition.valueOf(selected)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            key(replay) {
                Ps4WeatherIcon(condition, settings.copy(mode = WeatherMotionMode.ONCE), reduced, Modifier.size(56.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(if (condition == WeatherCondition.AMBIENT) "Condición sin identificar" else condition.label,
                    style = MaterialTheme.typography.titleMedium)
                Text("Vista previa · no cambia el clima de Inicio", style = MaterialTheme.typography.bodySmall)
            }
        }
        EffectChoice("Evento de ejemplo", selected, ps4WeatherExamples, onSelect = { selected = it })
        TextButton(onClick = { replay++ }, enabled = !reduced && ValueAnimator.areAnimatorsEnabled()) {
            Text("Repetir animación")
        }
    }
}

private val ps4WeatherExamples = WeatherCondition.entries.filter { it != WeatherCondition.AUTO }
    .map { it.name to if (it == WeatherCondition.AMBIENT) "Condición sin identificar" else it.label }

@Composable
private fun WeatherMotionControls(settings: WeatherMotionSettings, finite: Boolean, onChange: (WeatherMotionSettings) -> Unit) {
    val maxDuration = if (finite) 5000 else 12000
    var duration by remember(settings.durationMs, maxDuration) {
        mutableFloatStateOf(settings.durationMs.coerceAtMost(maxDuration).toFloat())
    }
    var intensity by remember(settings.intensity){mutableFloatStateOf(settings.intensity.toFloat())}
    Text("Animaciones del clima",style=MaterialTheme.typography.titleMedium)
    val modes = if (finite) listOf(WeatherMotionMode.OFF, WeatherMotionMode.ONCE) else WeatherMotionMode.entries
    val selected = if (finite && settings.mode == WeatherMotionMode.REPEAT) WeatherMotionMode.ONCE else settings.mode
    modes.forEach { mode ->
        TextButton(onClick={onChange(settings.copy(mode=mode))},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
            RadioButton(selected == mode, null)
            Text(if (finite && mode == WeatherMotionMode.ONCE) "Al entrar y al cambiar la condición" else mode.label, Modifier.weight(1f))
        }
    }
    val scenes = WeatherCondition.entries.filter { it.atmosphericScene() == it }
    EffectChoice("Escena meteorológica",settings.condition.atmosphericScene().name,scenes.map{it.name to it.label},onSelect={onChange(settings.copy(condition=WeatherCondition.valueOf(it)))})
    Text("La selección manual cambia el efecto; no modifica los datos del proveedor.",style=MaterialTheme.typography.bodySmall)
    Text("Duración ${(duration / 1000).toInt()} s", style = MaterialTheme.typography.labelLarge)
    Slider(duration,{duration=it},onValueChangeFinished={onChange(settings.copy(durationMs=duration.toInt()))},valueRange=3000f..maxDuration.toFloat(),steps=(maxDuration - 3000) / 1000 - 1,
        modifier=Modifier.semantics {contentDescription="Duración del ciclo del clima"},enabled=settings.mode!=WeatherMotionMode.OFF)
    Text("Intensidad: ${intensity.toInt()}%",style=MaterialTheme.typography.labelLarge)
    Slider(intensity,{intensity=it},onValueChangeFinished={onChange(settings.copy(intensity=intensity.toInt()))},valueRange=20f..100f,steps=7,
        modifier=Modifier.semantics {contentDescription="Intensidad de la escena del clima"},enabled=settings.mode!=WeatherMotionMode.OFF)
    Text(
        if (finite) "La escena vuelve al reposo después de cada cambio."
        else "Al entrar realiza un recorrido y queda en reposo. Repetición mantiene ciclos mientras Inicio está visible y consume más energía.",
        style = MaterialTheme.typography.bodySmall,
    )
}
