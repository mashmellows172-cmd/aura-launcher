package dev.senda.launcher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.*
import dev.senda.launcher.themeeditor.*
import dev.senda.launcher.widgets.WidgetController
import kotlin.math.roundToInt

@Composable
fun ThemeEditorDialog(state:HomeUiState,viewModel:LauncherViewModel,widgetController:WidgetController,onDismiss:()->Unit) {
    val editor:ThemeEditorViewModel=viewModel(key="aura-theme-editor")
    val draft by editor.draft.collectAsStateWithLifecycle()
    val undo by editor.canUndo.collectAsStateWithLifecycle();val redo by editor.canRedo.collectAsStateWithLifecycle()
    val busy by editor.busy.collectAsStateWithLifecycle();val error by editor.error.collectAsStateWithLifecycle()
    val variants by editor.variants.collectAsStateWithLifecycle()
    var name by rememberSaveable{mutableStateOf("Mi variante")}
    var section by rememberSaveable{mutableIntStateOf(0)}
    var fullPreview by rememberSaveable{mutableStateOf(false)}
    var showS3 by rememberSaveable{mutableStateOf(false)}
    var showWallpapers by rememberSaveable{mutableStateOf(false)}
    val context=LocalContext.current
    LaunchedEffect(Unit){editor.open(ThemeSnapshot.from(state))}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
        if(uri!=null){runCatching{context.contentResolver.takePersistableUriPermission(uri,android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)};draft?.let{editor.edit(it.copy(wallpaper=uri.toString()))}}
    }
    val libraryPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){it?.let(viewModel::importRetroLibrary)}
    Dialog(onDismissRequest={if(!busy)editor.cancel(onDismiss)},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    TextButton(onClick={editor.cancel(onDismiss)},enabled=!busy){Text("Cancelar")}
                    TextButton(onClick=editor::undo,enabled=undo&&!busy){Text("Deshacer")}
                    TextButton(onClick=editor::redo,enabled=redo&&!busy){Text("Rehacer")}
                    TextButton(onClick={fullPreview=!fullPreview}){Text(if(fullPreview)"Controles" else "Ampliar")}
                }
                val value=draft
                if(value==null){Box(Modifier.weight(1f)){CircularProgressIndicator()}}
                else {
                    Box(Modifier.weight(1f).fillMaxWidth()) { ThemeLivePreview(value,state,viewModel,widgetController) }
                    if(!fullPreview) {
                        TabRow(section){listOf("Estilo","Iconos","Fondo","Variantes").forEachIndexed{i,label->Tab(selected=section==i,onClick={editor.finishGesture();section=i},modifier=Modifier.heightIn(min=48.dp)){Text(label,style=MaterialTheme.typography.labelSmall)}}}
                        Column(Modifier.fillMaxWidth().heightIn(max=240.dp).verticalScroll(rememberScrollState()).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                            when(section){
                                0->{
                                    EditorChoice("Tema base",value.theme,LauncherTheme.entries.map{it.name to it.title},!busy){editor.edit(ThemeSnapshot.from(state,LauncherTheme.valueOf(it)))}
                                    EditorChoice("Acento",value.accent,AccentChoice.entries.map{it.name to it.label},!busy){editor.edit(value.copy(accent=it))}
                                    EditorChoice("Contenedor",value.shape,ShapeChoice.entries.map{it.name to it.label},!busy){editor.edit(value.copy(shape=it))}
                                    EditorChoice("Tipografía",value.font,listOf("THEME" to "Del tema","NOTO" to "Noto Sans","MONO" to "Monoespaciada","CONDENSED" to "Condensada"),!busy){editor.edit(value.copy(font=it))}
                                    EditorSlider("Texto",value.textSize,14..24,!busy,{editor.edit(value.copy(textSize=it),false)},editor::finishGesture)
                                    EditorSlider("Espacio entre filas",value.spacing,0..20,!busy,{editor.edit(value.copy(spacing=it),false)},editor::finishGesture)
                                }
                                1->{
                                    EditorChoice("Tratamiento",value.icons,IconTreatment.entries.map{it.name to it.label},!busy){editor.edit(value.copy(icons=it))}
                                    EditorChoice("Paquete",value.pack?:"",listOf("" to "Recursos del tema")+state.availableIconPacks.map{it.packageName to it.label},!busy&&value.icons!="ORIGINAL"){editor.edit(value.copy(pack=it.ifBlank{null}))}
                                    EditorSlider("Tamaño del icono",value.iconSize,32..60,!busy,{editor.edit(value.copy(iconSize=it),false)},editor::finishGesture)
                                    if(value.theme==LauncherTheme.TOUCHWIZ.name)TextButton(onClick={showS3=true}){Text("Revisar catálogo Galaxy SIII")}
                                    if(value.theme in RetroAssets.themes){
                                        Row{Switch(value.retro,{editor.edit(value.copy(retro=it))},enabled=!busy&&state.retroLibrary[value.theme]!=null);Text("Usar mi colección importada")}
                                        TextButton(onClick={libraryPicker.launch(arrayOf("application/zip","application/octet-stream"))},enabled=!busy){Text("Importar colección")}
                                    }
                                }
                                2->{
                                    Button(onClick={showWallpapers=true},enabled=!busy,modifier=Modifier.fillMaxWidth()){Text("Explorar fondos y colecciones")}
                                    TextButton(onClick={picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!busy){Text("Elegir imagen de mi galería")}
                                    TextButton(onClick={editor.edit(value.copy(wallpaper=null))},enabled=!busy){Text("Restablecer fondo predeterminado")}
                                    if(value.theme in RetroAssets.themes)TextButton(onClick={libraryPicker.launch(arrayOf("application/zip","application/octet-stream"))},enabled=!busy&&!state.retroImportBusy){Text(if(state.retroImportBusy)"Importando colección…" else "Importar colección de fondos (ZIP)")}
                                    state.retroImportMessage?.let{Text(it,style=MaterialTheme.typography.bodySmall)}
                                    EditorSlider("Protección de lectura",value.dim,60..90,!busy,{editor.edit(value.copy(dim=it),false)},editor::finishGesture)
                                    Text("La imagen se copia al guardar. La configuración base se conserva.",style=MaterialTheme.typography.bodySmall)
                                }
                                else->{
                                    OutlinedTextField(name,{name=it.take(60)},label={Text("Nombre de la variante")},singleLine=true,enabled=!busy)
                                    TextButton(onClick={editor.useBase(onDismiss)},enabled=!busy){Text("Volver al tema base")}
                                    variants.forEach{variant->TextButton(onClick={editor.load(variant)},enabled=!busy){Text(variant.name)}}
                                    if(variants.isEmpty())Text("Aún no hay variantes guardadas")
                                }
                            }
                            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal=12.dp),horizontalArrangement=Arrangement.SpaceBetween){
                        TextButton(onClick={editor.finishGesture();editor.save(name,false,onDismiss)},enabled=!busy){Text("Guardar copia")}
                        Button(onClick={editor.finishGesture();editor.save(name,true,onDismiss)},enabled=!busy&&!state.isLoading){Text(if(busy)"Guardando…" else "Guardar y usar")}
                    }
                }
            }
        }
    }
    if(showWallpapers)draft?.let { value ->
        ThemeWallpaperGallery(LauncherTheme.valueOf(value.theme),state.retroLibrary,value.wallpaper,value.retro,
            onSelect={uri->editor.finishGesture();editor.edit(value.copy(wallpaper=uri))},
            onChoosePhoto={showWallpapers=false;picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},
            onDismiss={showWallpapers=false})
    }
    if(showS3)S3ReviewDialog(iconSize=draft?.iconSize?:42,onDismiss={showS3=false})
}

@Composable
private fun EditorChoice(label:String,value:String,options:List<Pair<String,String>>,enabled:Boolean,onSelect:(String)->Unit){
    var expanded by remember{mutableStateOf(false)}
    Box{OutlinedButton(onClick={expanded=true},enabled=enabled,modifier=Modifier.fillMaxWidth()){Text("$label: ${options.firstOrNull{it.first==value}?.second?:value}")}
        DropdownMenu(expanded,{expanded=false},Modifier.heightIn(max=280.dp)){options.forEach{(key,title)->DropdownMenuItem(text={Text(title)},onClick={expanded=false;onSelect(key)})}}
    }
}
@Composable
private fun EditorSlider(label:String,value:Int,range:IntRange,enabled:Boolean,onChange:(Int)->Unit,onFinish:()->Unit){
    Text("$label: $value",style=MaterialTheme.typography.labelLarge)
    Slider(value.toFloat(),{onChange(it.roundToInt().coerceIn(range))},enabled=enabled,valueRange=range.first.toFloat()..range.last.toFloat(),steps=(range.last-range.first-1).coerceAtLeast(0),onValueChangeFinished=onFinish)
}
