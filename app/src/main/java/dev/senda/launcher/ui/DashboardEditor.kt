package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.*
import dev.senda.launcher.widgets.WidgetController
import dev.senda.launcher.widgets.WidgetShelf

fun dashboardBlocks(state: HomeUiState, controller: WidgetController, anchor: BlockAnchor) = state.dashboard.blocks(anchor,state.headerSettings.showAlarm,state.headerSettings.showCalendar,controller.widgets.isNotEmpty())

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardGroup(state: HomeUiState, controller: WidgetController, anchor: BlockAnchor, onCalendarPermission: () -> Unit) {
    val blocks=dashboardBlocks(state,controller,anchor)
    if(blocks.isEmpty())return
    val scale=LocalDensity.current.fontScale
    BoxWithConstraints(Modifier.fillMaxWidth()){
        val width=maxWidth
        FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp),maxItemsInEachRow=2){
            blocks.forEach {block->
                val style=state.dashboard[block]
                val half=style.halfWidth&&block!=HomeBlock.WIDGETS&&width>=280.dp&&scale<=1.5f
                Box(Modifier.width(if(half)(width-8.dp)/2 else width)){
                    if(block==HomeBlock.WIDGETS)WidgetShelf(controller)
                    else HomeInformation(state,onCalendarPermission,block,style)
                }
            }
        }
    }
}

@Composable
fun DashboardEditorDialog(state: HomeUiState, viewModel: LauncherViewModel, controller: WidgetController, onAddWidget: () -> Unit, onDismiss: () -> Unit) {
    var draft by remember(state.dashboard){mutableStateOf(state.dashboard)}
    AuraAlertDialog(onDismissRequest=onDismiss,title={Text("Organizar inicio")},text={
        Column(Modifier.heightIn(max=480.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
            Text("Elige dónde colocar cada bloque. Subir y Bajar cambian el orden dentro de la misma zona.",style=MaterialTheme.typography.bodySmall)
            draft.order.forEachIndexed {index,block->
                val style=draft[block]
                Surface(shape=MaterialTheme.shapes.large,color=MaterialTheme.colorScheme.surfaceVariant){
                    Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                        Text(block.label,style=MaterialTheme.typography.titleMedium)
                        EffectChoice("Posición de ${block.label}",style.anchor.name,BlockAnchor.entries.map{it.name to it.label},onSelect={draft=draft.with(block,style.copy(anchor=BlockAnchor.valueOf(it)))})
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            TextButton(onClick={draft=draft.move(block,-1)},enabled=index>0){Text("Subir ${block.label}")}
                            TextButton(onClick={draft=draft.move(block,1)},enabled=index<draft.order.lastIndex){Text("Bajar ${block.label}")}
                        }
                        if(block!=HomeBlock.WIDGETS){
                            Text("Altura: ${style.height} dp",style=MaterialTheme.typography.labelMedium)
                            Slider(value=style.height.toFloat(),onValueChange={draft=draft.with(block,style.copy(height=it.toInt()))},valueRange=48f..160f,steps=13)
                            Text("Texto: ${style.textSize} sp",style=MaterialTheme.typography.labelMedium)
                            Slider(value=style.textSize.toFloat(),onValueChange={draft=draft.with(block,style.copy(textSize=it.toInt()))},valueRange=12f..20f,steps=7)
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Media anchura");Switch(style.halfWidth,{draft=draft.with(block,style.copy(halfWidth=it))})}
                            Text("Se adapta a anchura completa si la pantalla o el texto necesitan más espacio.",style=MaterialTheme.typography.bodySmall)
                        }else Text("${controller.widgets.size} widgets. Su tamaño se ajusta desde Editar widgets en su bloque.",style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
            TextButton(onClick=onAddWidget){Text("Añadir widget en esta zona")}
            TextButton(onClick={draft=DashboardSettings()}){Text("Restablecer distribución")}
            Text("La zona bajo el reloj tiene un alto limitado y se desplaza si añades mucho contenido, para conservar acceso al buscador y las apps. Alarma y agenda se muestran según Inicio y gestos.",style=MaterialTheme.typography.bodySmall)
        }
    },confirmButton={TextButton(onClick={viewModel.setDashboard(draft);onDismiss()}){Text("Guardar distribución")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}
