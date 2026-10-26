package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.RailEffects
import dev.senda.launcher.data.VisualSettings

@Composable
fun AlphabetSettingsDialog(theme:LauncherTheme,current:VisualSettings,onChange:(VisualSettings)->Unit,onDismiss:()->Unit){
    var draft by remember(current){mutableStateOf(current)}
    fun commit(value:VisualSettings){draft=value;onChange(value)}
    val motionEnabled=!draft.reduceMotion && draft.railEffect!="NONE"
    AuraAlertDialog(onDismissRequest=onDismiss,title={Text("Barra A–Z")},text={
        Column(Modifier.heightIn(max=560.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
            Text("Arrastra el índice de la muestra. Cada efecto cambia las letras y su indicador sin mover las zonas de toque.",style=MaterialTheme.typography.bodySmall)
            AlphabetEffectPreview(theme,draft)
            EffectChoice("Efecto del índice",draft.railEffect,RailEffects.options){commit(draft.copy(railEffect=it))}
            Text(when(draft.railEffect){
                "WAVE"->"Una onda desplaza y gira las letras vecinas al recorrido del dedo."
                "LENS"->"La letra próxima al dedo se amplía como una lupa, sin arrastre lateral."
                "CASCADE"->"Las letras cercanas se abren en abanico con altura y giro escalonados."
                "ORBIT"->"Las letras se inclinan con perspectiva y recorren un arco lateral."
                "SPRING"->"Indicador y letras responden con un rebote elástico al tocar y soltar."
                "FADE"->"Énfasis y cambio de letra con desvanecimiento, sin giro ni desplazamiento."
                "NONE"->"Navegación directa e indicador estático, sin transformaciones."
                else->"Las letras próximas se acercan al dedo y aumentan progresivamente."
            },style=MaterialTheme.typography.bodySmall)
            if(draft.reduceMotion)Text("Movimiento reducido está activo. Se omiten las transformaciones y se conserva el salto de letras.",style=MaterialTheme.typography.bodySmall)
            RailSlider("Intensidad",draft.railIntensity,"%",0f..100f,motionEnabled,{draft=draft.copy(railIntensity=it)},{onChange(draft)})
            RailSlider("Respuesta",draft.railDurationMs," ms",80f..600f,motionEnabled,{draft=draft.copy(railDurationMs=it)},{onChange(draft)})
            RailSlider("Tamaño del indicador",draft.railBubbleSize," dp",40f..84f,true,{draft=draft.copy(railBubbleSize=it)},{onChange(draft)})
            RailSlider("Permanencia al soltar",draft.railBubbleHoldMs," ms",0f..1000f,true,{draft=draft.copy(railBubbleHoldMs=it)},{onChange(draft)})
            Row(Modifier.fillMaxWidth().heightIn(min=48.dp).toggleable(draft.railHaptics,role=Role.Switch,onValueChange={commit(draft.copy(railHaptics=it))}),verticalAlignment=Alignment.CenterVertically){
                Text("Vibración suave al cambiar de letra",Modifier.weight(1f));Switch(draft.railHaptics,null)
            }
            Text("La vibración y el sonido respetan los ajustes del sistema. El movimiento se detiene al terminar la interacción.",style=MaterialTheme.typography.bodySmall)
            TextButton(onClick={commit(draft.copy(railEffect="MAGNET",railIntensity=65,railDurationMs=220,railBubbleSize=56,railBubbleHoldMs=420,railHaptics=false))},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Restablecer solo A–Z")}
        }
    },confirmButton={TextButton(onClick=onDismiss){Text("Listo")}})
}

@Composable private fun RailSlider(label:String,value:Int,unit:String,range:ClosedFloatingPointRange<Float>,enabled:Boolean,onValue:(Int)->Unit,onFinish:()->Unit){
    Column{
        Text("$label · $value$unit",style=MaterialTheme.typography.labelLarge)
        Slider(value.toFloat().coerceIn(range.start,range.endInclusive),{onValue(it.toInt())},valueRange=range,enabled=enabled,
            onValueChangeFinished=onFinish,modifier=Modifier.semantics {contentDescription=label})
    }
}

@Composable private fun AlphabetEffectPreview(theme:LauncherTheme,settings:VisualSettings){
    val letters=remember{listOf("A","C","E","G","J","M","P","R","S","V","W","Y","Z")}
    var selected by remember{mutableStateOf("A")}
    val height=(230*LocalDensity.current.fontScale.coerceIn(1f,2f)).dp
    Surface(shape=MaterialTheme.shapes.medium,color=MaterialTheme.colorScheme.surfaceVariant,border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.outline)){
        Row(Modifier.fillMaxWidth().height(height).padding(12.dp),verticalAlignment=Alignment.CenterVertically){
            Column(Modifier.weight(1f).padding(end=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Vista previa",style=MaterialTheme.typography.labelMedium)
                Text(selected,style=MaterialTheme.typography.displayMedium)
                Text("Prueba también W, Y y Z.",style=MaterialTheme.typography.bodySmall)
            }
            AlphabetRail(letters=letters,theme=theme,modifier=Modifier.width(38.dp).fillMaxHeight(),selectedLetter=selected,
                visualSettings=settings,onClick={selected=it},onScrub={selected=it})
        }
    }
}
