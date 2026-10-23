package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.*
import dev.senda.launcher.widgets.WidgetController
import dev.senda.launcher.widgets.WidgetShelf
import dev.senda.launcher.ui.theme.*

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
                val half=dashboardHalfWidth(block,style,width.value,scale)
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
    // Preservar borrador sin descartarlo al recrear la configuración métrica (rotación)
    var draft by rememberSaveable(stateSaver = Saver<DashboardDraft, IntArray>(save = { it.saveState() }, restore = { DashboardDraft.restoreState(it) })) {
        mutableStateOf(DashboardDraft(state.dashboard))
    }
    var selectedName by rememberSaveable { mutableStateOf(HomeBlock.ALARM.name) }
    var previewExpanded by rememberSaveable { mutableStateOf(false) }
    val selected = HomeBlock.entries.firstOrNull { it.name == selectedName } ?: HomeBlock.ALARM
    val config = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val maxDialogHeight = if (isLandscape) (config.screenHeightDp - 96).coerceAtLeast(220).dp else 480.dp

    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Organizar inicio") }, text = {
        Column(Modifier.heightIn(max = maxDialogHeight).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Elige un bloque. Los cambios se aplican al guardar.",style=MaterialTheme.typography.bodySmall)
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                HomeBlock.entries.forEach { block ->
                    FilterChip(selected=selected==block,onClick={selectedName=block.name},label={Text(block.label)},modifier=Modifier.heightIn(min=48.dp).semantics { role=Role.RadioButton })
                }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                TextButton(onClick={draft=draft.undo()},enabled=draft.canUndo){Text("Deshacer")}
                TextButton(onClick={draft=draft.reset()}){Text("Restablecer")}
            }
            run {
                val block = selected
                val settings = draft.current
                val style=settings[block]
                val zone = settings.order.filter { settings[it].anchor == style.anchor }
                val index = zone.indexOf(block)
                Surface(shape=MaterialTheme.shapes.large,color=MaterialTheme.colorScheme.surfaceVariant){
                    Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                        Text(block.label,style=MaterialTheme.typography.titleMedium)
                        EffectChoice("Posición de ${block.label}",style.anchor.name,BlockAnchor.entries.map{it.name to it.label},onSelect={draft=draft.edit(settings.with(block,style.copy(anchor=BlockAnchor.valueOf(it))))})
                        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            TextButton(onClick={draft=draft.edit(settings.move(block,-1))},enabled=index>0){Text("Subir ${block.label}")}
                            TextButton(onClick={draft=draft.edit(settings.move(block,1))},enabled=index<zone.lastIndex){Text("Bajar ${block.label}")}
                        }
                        if(block!=HomeBlock.WIDGETS){
                            Text("Altura mínima: ${style.height} dp",style=MaterialTheme.typography.labelMedium)
                            Slider(value=style.height.toFloat(),onValueChange={draft=draft.edit(draft.current.with(block,draft.current[block].copy(height=it.toInt())),"${block.name}-height")},onValueChangeFinished={draft=draft.finishChange()},valueRange=48f..160f,steps=13,modifier=Modifier.semantics { contentDescription="Altura de ${block.label}" })
                            Text("Texto: ${style.textSize} sp",style=MaterialTheme.typography.labelMedium)
                            Slider(value=style.textSize.toFloat(),onValueChange={draft=draft.edit(draft.current.with(block,draft.current[block].copy(textSize=it.toInt())),"${block.name}-text")},onValueChangeFinished={draft=draft.finishChange()},valueRange=12f..20f,steps=7,modifier=Modifier.semantics { contentDescription="Tamaño del texto de ${block.label}" })
                            Row(Modifier.fillMaxWidth().selectable(style.halfWidth,role=Role.Switch,onClick={draft=draft.edit(settings.with(block,style.copy(halfWidth=!style.halfWidth)))}).padding(vertical=8.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("Media anchura",Modifier.weight(1f));Switch(style.halfWidth,null)}
                            Text("Se adapta a anchura completa y aumenta la altura si el texto necesita más espacio.",style=MaterialTheme.typography.bodySmall)
                        }else Text("${controller.widgets.size} widgets. Su tamaño se ajusta desde Editar widgets en su bloque.",style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
            TextButton(onClick={previewExpanded=!previewExpanded}){Text(if(previewExpanded)"Ocultar vista previa" else "Mostrar vista previa")}
            if(previewExpanded)DashboardPreview(draft.current,selected,state,controller){selectedName=it.name}
            TextButton(onClick={viewModel.setDashboard(draft.current);onDismiss();onAddWidget()}){Text("Guardar y añadir widget")}
            Text("La zona bajo el reloj tiene un alto limitado y se desplaza si añades mucho contenido, para conservar acceso al buscador y las apps. Alarma y agenda se muestran según Inicio y gestos.",style=MaterialTheme.typography.bodySmall)
        }
    },confirmButton={TextButton(onClick={viewModel.setDashboard(draft.current);onDismiss()}){Text("Guardar distribución")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DashboardPreview(settings: DashboardSettings, selected: HomeBlock, state: HomeUiState, controller: WidgetController, onSelect: (HomeBlock) -> Unit) {
    Surface(shape=MaterialTheme.shapes.large,color=MaterialTheme.colorScheme.background,border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.outline)) {
        Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("VISTA PREVIA · DATOS DE EJEMPLO",style=MaterialTheme.typography.labelSmall)
            if(LocalIndustrialStyle.current.enabled)DotClock("09:41",Modifier.width(170.dp).height(48.dp))
            else if(LocalRetroStyle.current.enabled)RetroClock("09:41",compact=true)
            else Text("09:41",style=MaterialTheme.typography.headlineMedium)
            BlockAnchor.entries.forEach { anchor ->
                Text(anchor.label,style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                val width=maxWidth
                FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp),maxItemsInEachRow=2) {
                    val blocks=settings.order.filter { settings[it].anchor==anchor }
                    if(blocks.isEmpty()) Text("Zona libre",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    blocks.forEach { block ->
                        val style=settings[block]
                        val active=block==selected
                        val visible=when(block){HomeBlock.ALARM->state.headerSettings.showAlarm;HomeBlock.AGENDA->state.headerSettings.showCalendar;HomeBlock.WIDGETS->controller.widgets.isNotEmpty()}
                        val half=dashboardHalfWidth(block,style,width.value,LocalDensity.current.fontScale)
                        if(block!=HomeBlock.WIDGETS) {
                            Column(Modifier.width(if(half)(width-8.dp)/2 else width)) {
                                InformationCard(block.label,if(block==HomeBlock.ALARM)"06:30" else "Sin eventos próximos",block,{onSelect(block)},Modifier.fillMaxWidth(),style,active)
                                if(!visible)Text("Oculto en el inicio",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else Surface(modifier=Modifier.width(width).heightIn(min=48.dp)
                            .selectable(active,role=Role.RadioButton,onClick={onSelect(block)}),shape=MaterialTheme.shapes.medium,
                            color=if(active)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border=androidx.compose.foundation.BorderStroke(if(active)2.dp else 1.dp,if(active)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)) {
                            Column(Modifier.padding(8.dp).heightIn(min=(style.height/2).coerceIn(32,80).dp)) {
                                Text(block.label,style=MaterialTheme.typography.labelLarge,fontSize=style.textSize.sp)
                                Text(if(visible)"${style.height} dp · ${if(half)"½ ancho" else "ancho completo"}" else "Oculto en el inicio",style=MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                }
                if(anchor==BlockAnchor.BEFORE_FAVORITES)Text("Favoritas",style=MaterialTheme.typography.bodySmall)
                if(anchor==BlockAnchor.AFTER_FAVORITES)Text("Lista de aplicaciones",style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}
