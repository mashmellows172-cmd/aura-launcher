package dev.senda.launcher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
import dev.senda.launcher.data.WeatherMotionSettings
import dev.senda.launcher.data.WeatherMotionMode
import dev.senda.launcher.ui.theme.LocalIndustrialStyle
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
fun HeaderWeather(controller: WidgetController, packageName: String, sourceLabel: String, reduceMotion: Boolean, onOpen: () -> Unit, onConfigure: () -> Unit) {
    val id=controller.weatherWidgetId
    val info=remember(id,packageName,controller.providerRevision){controller.manager.getAppWidgetInfo(id)}
    val connected=info?.provider?.packageName==packageName
    val reading=if(connected)controller.weatherReading else null
    Box(Modifier.width(104.dp).heightIn(min=72.dp).combinedClickable(onClick=if(reading!=null)onOpen else onConfigure,onLongClick=onConfigure)
        .semantics(mergeDescendants=true){contentDescription=if(reading!=null)"Clima de ${reading.place}: ${reading.temperature}. Datos de $sourceLabel" else "Configurar clima de $sourceLabel";role=Role.Button},contentAlignment=Alignment.Center){
        if(connected) key(id){
            Box(Modifier.size(1.dp).clearAndSetSemantics{}) {
            AndroidView(factory={context->controller.host.createView(context,id,info).apply{visibility=android.view.View.INVISIBLE;importantForAccessibility=android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS;
                @Suppress("DEPRECATION") updateAppWidgetSize(null,120,90,120,90)}},
                modifier=Modifier.requiredSize(120.dp,90.dp).clearAndSetSemantics{})
            }
        }
        Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.padding(4.dp)){
            if(reading!=null){
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
                    reading.icon?.let { icon->Image(icon,null,Modifier.size(30.dp),colorFilter=if(LocalIndustrialStyle.current.enabled) ColorFilter.tint(MaterialTheme.colorScheme.onSurface) else null)}
                    Text(reading.temperature,style=MaterialTheme.typography.titleMedium,maxLines=1)
                }
                Text(reading.place.ifBlank{sourceLabel},style=MaterialTheme.typography.labelSmall,maxLines=1,overflow=TextOverflow.Ellipsis,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }else{
                Text(if(connected)"Sin lectura" else "Clima",style=MaterialTheme.typography.labelLarge)
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
            Text("Aura refleja la temperatura, ciudad y símbolo que publica su widget. La ubicación, unidades y actualización se configuran en $sourceLabel.")
            Text(if(info?.provider?.packageName==source)"Widget conectado" else "Conecta el widget para mostrar sus datos en el reloj",color=MaterialTheme.colorScheme.primary)
            controller.weatherReading?.let {Text("${it.place} · ${it.temperature}",style=MaterialTheme.typography.titleLarge)}
            Button(onClick={controller.connectWeather(source)}){Text("Conectar widget del clima")}
            TextButton(onClick=onOpen){Text("Abrir $sourceLabel")}
            TextButton(onClick=onChooseApp){Text("Elegir aplicación del clima")}
            if(info!=null) TextButton(onClick=controller::disconnectWeather){Text("Desconectar clima de la cabecera")}
            controller.weatherError?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            HorizontalDivider()
            WeatherMotionControls(state.headerSettings.weatherMotion){onSettings(state.headerSettings.copy(weatherMotion=it))}
            Text("El barrido usa el símbolo real del proveedor y el acabado del tema. Movimiento reducido, alto contraste y las animaciones desactivadas de Android detienen el efecto. El widget se vincula en este teléfono; no se transfiere con el respaldo.",style=MaterialTheme.typography.bodySmall)
        }
    },confirmButton={TextButton(onClick=onDismiss){Text("Listo")}})
}

@Composable
private fun WeatherMotionControls(settings:WeatherMotionSettings,onChange:(WeatherMotionSettings)->Unit) {
    var duration by remember(settings.durationMs){mutableFloatStateOf(settings.durationMs.toFloat())}
    var intensity by remember(settings.intensity){mutableFloatStateOf(settings.intensity.toFloat())}
    Text("Animaciones del clima",style=MaterialTheme.typography.titleMedium)
    WeatherMotionMode.entries.forEach { mode ->
        TextButton(onClick={onChange(settings.copy(mode=mode))},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
            RadioButton(settings.mode==mode,null)
            Text(mode.label,Modifier.weight(1f))
        }
    }
    Text("Recorrido: ${(duration/1000).toInt()} s",style=MaterialTheme.typography.labelLarge)
    Slider(duration,{duration=it},onValueChangeFinished={onChange(settings.copy(durationMs=duration.toInt()))},valueRange=3000f..12000f,steps=8,
        modifier=Modifier.semantics {contentDescription="Duración del barrido del clima"},enabled=settings.mode!=WeatherMotionMode.OFF)
    Text("Intensidad: ${intensity.toInt()}%",style=MaterialTheme.typography.labelLarge)
    Slider(intensity,{intensity=it},onValueChangeFinished={onChange(settings.copy(intensity=intensity.toInt()))},valueRange=20f..100f,steps=7,
        modifier=Modifier.semantics {contentDescription="Intensidad del barrido del clima"},enabled=settings.mode!=WeatherMotionMode.OFF)
    Text("Al entrar realiza un recorrido y queda en reposo. Repetición mantiene ciclos mientras Inicio está visible y consume más energía.",style=MaterialTheme.typography.bodySmall)
}
