package dev.senda.launcher.ui

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import dev.senda.launcher.R
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LocalAppearance
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.filter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val ConsoleBlue: Color @Composable get() = if(LocalAppearance.current.highContrast) Color.Black else Color(0xFF003087)
private val ConsoleHighlight: Color @Composable get() = if(LocalAppearance.current.highContrast) Color.Black else Color(0xFF0070D1)
private val ConsoleInk = Color.White
private val ConsoleShape = RoundedCornerShape(1.dp)

/** Sony guide assets, preserved at their published resolution; never labelled an HD icon kit. */
@Composable
private fun ConsoleGlyph(resource: Int, size: Dp = 26.dp) {
    // The guide glyphs contain opaque white cutouts (eyes/toolbox handle), not only alpha.
    // Preserve those cutouts when converting black publication ink into white console ink.
    val whiteInk = remember { ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
        0f,0f,0f,0f,255f, 0f,0f,0f,0f,255f, 0f,0f,0f,0f,255f,
        -1f,0f,0f,1f,0f))) }
    Image(painterResource(resource), null, Modifier.size(size), colorFilter = whiteInk)
}

@Composable
internal fun Ps4Dashboard(
    apps: List<LaunchableApp>, favorites: List<LaunchableApp>, overrides: Map<String,String>,
    avatarUri: String?, clock: String, reduced: Boolean, query: String, onQuery: (String)->Unit,
    onLaunch: (LaunchableApp)->Unit, onLoadIcon: (LaunchableApp)->Drawable,
    onAvatar: ()->Unit, onSettings: ()->Unit, onTheme: ()->Unit, onFavorites: ()->Unit,
    onNotifications: ()->Unit, onMusic: ()->Unit, onCalendar: ()->Unit,
    onPlayStation: ()->Unit, onStore: ()->Unit, onSound: ()->Unit, onWidgets: ()->Unit,
    onHomeSettings: ()->Unit, onLock: ()->Unit, onChooseIcon: (String)->Unit,
    onSelection: ()->Unit, dashboard: @Composable ()->Unit,
) {
    var library by rememberSaveable { mutableStateOf(false) }
    var quick by rememberSaveable { mutableStateOf(false) }
    var selectedPackage by rememberSaveable { mutableStateOf<String?>(null) }
    var psnFeature by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val ribbon = rememberLazyListState()
    val featured = remember(apps, favorites) { (favorites + apps).distinctBy { it.packageName }.take(16) }
    val selected = featured.firstOrNull { it.packageName == selectedPackage } ?: featured.firstOrNull()
    val filtered = remember(apps,query) { apps.filter { it.label.contains(query,ignoreCase=true) } }
    LaunchedEffect(ribbon, featured) {
        snapshotFlow { ribbon.isScrollInProgress }.filter { !it }.collect {
            val index = (ribbon.firstVisibleItemIndex - 1).coerceIn(0, (featured.size - 1).coerceAtLeast(0))
            if (ribbon.firstVisibleItemIndex > 0) selectedPackage = featured.getOrNull(index)?.packageName
        }
    }
    LaunchedEffect(query) { if (query.isNotEmpty()) library = true }
    BackHandler(library || quick || psnFeature != null) {
        when { psnFeature != null -> psnFeature=null; quick -> quick=false; else -> { library=false; onQuery("") } }
    }
    Column(Modifier.fillMaxSize().background(if(LocalAppearance.current.highContrast)Color.Black else Color.Transparent).padding(WindowInsets.safeDrawing.asPaddingValues()).imePadding()
        .padding(horizontal=16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(top=8.dp), verticalAlignment=Alignment.CenterVertically) {
            TextButton(onClick={quick=true}, colors=ButtonDefaults.textButtonColors(contentColor=ConsoleInk)) {
                Text("Menú rápido")
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment=Alignment.End) {
                Text(clock,color=ConsoleInk,style=MaterialTheme.typography.titleMedium)
                Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE d MMM",androidx.compose.ui.platform.LocalConfiguration.current.locales[0])),
                    color=ConsoleInk.copy(alpha=.85f),style=MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick=onAvatar,modifier=Modifier.size(48.dp).semantics { contentDescription="Perfil · cambiar avatar" }) {
                if (!renderAvatarImage(avatarUri,Modifier.size(30.dp))) Ps4Mark()
            }
        }
        val functionNames=listOf("Notificaciones","Amigos","Eventos","Mensajes","Party","Trofeos","Ajustes","Bloquear")
        val functionIcons=listOf(R.drawable.ps4_official_notification,R.drawable.ps4_official_friends,
            R.drawable.ps4_official_events,R.drawable.ps4_official_messages,R.drawable.ps4_official_party,
            R.drawable.ps4_official_trophies,R.drawable.ps4_official_settings,R.drawable.ps4_official_power)
        LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp), contentPadding=PaddingValues(horizontal=4.dp)) {
            items(functionNames.size,key={functionNames[it]}) { i ->
                var focused by remember { mutableStateOf(false) }
                val interactions=remember { MutableInteractionSource() }; val hovered by interactions.collectIsHoveredAsState()
                val emphasis by animateFloatAsState(if(focused||hovered)1.12f else 1f,tween(if(reduced)0 else 350, easing=FastOutSlowInEasing),label="ps4-function")
                Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.width(76.dp)) {
                    Surface(onClick={onSelection();when(i){0->onNotifications();2->onCalendar();6->onSettings();7->onLock();else->psnFeature=functionNames[i]}},
                        interactionSource=interactions,modifier=Modifier.size(52.dp).semantics { contentDescription=functionNames[i] }.onFocusChanged { focused=it.hasFocus }
                            .graphicsLayer { scaleX=emphasis;scaleY=emphasis }.hoverable(interactions),
                        color=if(focused||hovered)ConsoleHighlight else Color.Transparent,
                        shape=ConsoleShape,border=if(focused||hovered)BorderStroke(1.dp,ConsoleInk) else null) {
                        Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){ConsoleGlyph(functionIcons[i])}
                    }
                    Text(functionNames[i],color=ConsoleInk,fontSize=12.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
                }
            }
        }
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text(if(library) "Biblioteca" else "Contenido",color=ConsoleInk,style=MaterialTheme.typography.titleLarge,
                modifier=Modifier.weight(1f))
            TextButton(onClick={library=!library;onQuery("")},colors=ButtonDefaults.textButtonColors(contentColor=ConsoleInk)) {
                ConsoleGlyph(R.drawable.ps4_official_library,20.dp);Spacer(Modifier.width(8.dp));Text(if(library)"Inicio" else "Biblioteca")
            }
        }
        if (library) {
            OutlinedTextField(value=query,onValueChange=onQuery,singleLine=true,label={Text("Buscar en la biblioteca")},
                modifier=Modifier.fillMaxWidth(),textStyle=MaterialTheme.typography.bodyLarge.copy(color=ConsoleInk),
                colors=OutlinedTextFieldDefaults.colors(focusedTextColor=ConsoleInk,unfocusedTextColor=ConsoleInk,
                    focusedBorderColor=ConsoleInk,unfocusedBorderColor=ConsoleInk.copy(alpha=.65f),
                    focusedLabelColor=ConsoleInk,unfocusedLabelColor=ConsoleInk,cursorColor=ConsoleInk))
            if(filtered.isEmpty()) Text("No hay aplicaciones que coincidan.",color=ConsoleInk)
            LazyVerticalGrid(columns=GridCells.Adaptive(96.dp),modifier=Modifier.weight(1f),
                verticalArrangement=Arrangement.spacedBy(12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),
                contentPadding=PaddingValues(bottom=24.dp)) {
                items(filtered,key={it.packageName},contentType={"ps4-library-app"}) { app ->
                    Surface(onClick={onLaunch(app)},shape=ConsoleShape,color=ConsoleBlue.copy(alpha=.82f),
                        border=BorderStroke(1.dp,ConsoleInk.copy(alpha=.45f))) {
                        Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            ConsoleAppIcon(app,overrides,onLoadIcon,56)
                            Spacer(Modifier.height(8.dp));Text(app.label,color=ConsoleInk,maxLines=2,
                                overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(16.dp),contentPadding=PaddingValues(bottom=24.dp)) {
                item(key="console-content") {
                    LazyRow(state=ribbon,flingBehavior=rememberSnapFlingBehavior(ribbon),
                        contentPadding=PaddingValues(horizontal=8.dp,vertical=14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        item(key="store") {
                            ConsoleShortcut("PlayStation Store",R.drawable.ps4_official_ps_store_c,onStore)
                        }
                        itemsIndexed(featured,key={_,a->a.packageName},contentType={_,_->"ps4-content"}) { index,app ->
                            val active=selected?.packageName==app.packageName
                            var focused by remember { mutableStateOf(false) }
                            val interactions=remember { MutableInteractionSource() };val hovered by interactions.collectIsHoveredAsState()
                            val emphasis by animateFloatAsState(if(active||focused||hovered)1f else .84f,
                                tween(if(reduced)0 else 350,easing=FastOutSlowInEasing),label="ps4-content-focus")
                            Surface(onClick={if(active)onLaunch(app) else {selectedPackage=app.packageName;onSelection();scope.launch {
                                if(reduced)ribbon.scrollToItem(index+1) else ribbon.animateScrollToItem(index+1)
                            }}},interactionSource=interactions,modifier=Modifier.width(136.dp).height(164.dp).semantics { this.selected=active; contentDescription=app.label }
                                .onFocusChanged { focused=it.hasFocus;if(it.hasFocus)selectedPackage=app.packageName }
                                .hoverable(interactions).graphicsLayer { scaleX=emphasis;scaleY=emphasis;transformOrigin=TransformOrigin(0.5f,0f) }
                                .shadow(if(active)10.dp else 0.dp,ConsoleShape,ambientColor=ConsoleHighlight,spotColor=ConsoleHighlight),
                                color=if(active)ConsoleHighlight else ConsoleBlue,contentColor=ConsoleInk,shape=ConsoleShape,
                                border=BorderStroke(if(active||focused)2.dp else 1.dp,ConsoleInk.copy(alpha=if(active||focused)1f else .30f))) {
                                Column(horizontalAlignment=Alignment.CenterHorizontally) {
                                    Box(Modifier.weight(1f).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.White.copy(alpha=.08f),Color.Transparent))),contentAlignment=Alignment.Center){
                                        ConsoleAppIcon(app,overrides,onLoadIcon,82)
                                    }
                                    Text(if(active)"Iniciar" else app.label,modifier=Modifier.fillMaxWidth().background(Color.Black.copy(alpha=.24f)).padding(8.dp),
                                        maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.labelLarge,
                                        textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                                }
                            }
                        }
                        item(key="library") { ConsoleShortcut("Biblioteca",R.drawable.ps4_official_library){library=true} }
                    }
                }
                item(key="content-info") {
                    Column(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text(selected?.label ?: "Tu biblioteca",color=ConsoleInk,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Light)
                        Text(if(selected==null)"Las aplicaciones instaladas aparecerán aquí." else "Aplicación · lista para iniciar",color=ConsoleInk.copy(alpha=.8f),style=MaterialTheme.typography.bodySmall)
                        if(selected!=null) Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick={onLaunch(selected)},shape=ConsoleShape,border=BorderStroke(1.dp,ConsoleInk),colors=ButtonDefaults.outlinedButtonColors(contentColor=ConsoleInk)) { Text("Iniciar") }
                            TextButton(onClick={onChooseIcon(selected.packageName)},colors=ButtonDefaults.textButtonColors(contentColor=ConsoleInk)){Text("Opciones de icono")}
                        }
                    }
                }
                item(key="console-services") {
                    Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick=onMusic,shape=ConsoleShape,colors=ButtonDefaults.outlinedButtonColors(contentColor=ConsoleInk)){ConsoleGlyph(R.drawable.ps4_official_usbmusicplayer,24.dp);Spacer(Modifier.width(8.dp));Text("Música")}
                        OutlinedButton(onClick=onFavorites,shape=ConsoleShape,colors=ButtonDefaults.outlinedButtonColors(contentColor=ConsoleInk)){Text("Editar favoritas")}
                    }
                }
                item(key="console-widgets") { dashboard() }
            }
        }
    }
    if(quick) {
        androidx.compose.ui.window.Dialog(onDismissRequest={quick=false},properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
            Row(Modifier.fillMaxSize().background(Color.Black.copy(alpha=.35f))) {
                Column(Modifier.fillMaxHeight().fillMaxWidth(.84f).widthIn(max=420.dp)
                    .background(Brush.verticalGradient(listOf(ConsoleBlue,Color(0xFF06132B))))
                    .padding(WindowInsets.safeDrawing.asPaddingValues()).padding(20.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("Menú rápido",color=ConsoleInk,style=MaterialTheme.typography.headlineMedium)
                    val labels=listOf("Biblioteca","Sonido y dispositivos","Música","Notificaciones","Personalizar inicio","Tema visual","Widgets","Bloquear pantalla")
                    val actions=listOf<()->Unit>({library=true},onSound,onMusic,onNotifications,onHomeSettings,onTheme,onWidgets,onLock)
                    labels.forEachIndexed { i,label ->
                        var rowFocused by remember { mutableStateOf(false) }
                        TextButton(onClick={quick=false;actions[i]()},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp).onFocusChanged { rowFocused=it.hasFocus }.border(1.dp,if(rowFocused)ConsoleInk else Color.Transparent,ConsoleShape),
                            shape=ConsoleShape,colors=ButtonDefaults.textButtonColors(contentColor=ConsoleInk)) {
                            Text(label,Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodyLarge)
                        }
                    }
                    HorizontalDivider(color=ConsoleInk.copy(alpha=.3f))
                    TextButton(onClick={quick=false},colors=ButtonDefaults.textButtonColors(contentColor=ConsoleInk)){Text("Volver")}
                }
                Box(Modifier.weight(1f).fillMaxHeight().clickable { quick=false })
            }
        }
    }
    if(psnFeature!=null) {
        AlertDialog(onDismissRequest={psnFeature=null},title={Text(psnFeature.orEmpty())},
            text={Text("Esta función pertenece a PlayStation Network. Abre PlayStation App para consultar tu cuenta. Aura no accede a tus amigos, mensajes ni trofeos.")},
            confirmButton={TextButton(onClick={psnFeature=null;onPlayStation()}){Text("Abrir PlayStation App")}},
            dismissButton={TextButton(onClick={psnFeature=null}){Text("Volver")}})
    }
}

@Composable private fun ConsoleAppIcon(app: LaunchableApp, overrides: Map<String,String>, load:(LaunchableApp)->Drawable,size:Int) {
    CompositionLocalProvider(LocalAppearance provides LocalAppearance.current.copy(iconSize=size)) {
        AppIcon(app,overrides["PS4_${app.packageName}"],load)
    }
}
@Composable private fun ConsoleShortcut(label:String,icon:Int,action:()->Unit) {
    Surface(onClick=action,modifier=Modifier.width(104.dp).height(136.dp),shape=ConsoleShape,color=ConsoleBlue,
        border=BorderStroke(1.dp,ConsoleInk.copy(alpha=.4f))) {
        Column(Modifier.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
            ConsoleGlyph(icon,40.dp);Spacer(Modifier.height(12.dp));Text(label,color=ConsoleInk,style=MaterialTheme.typography.labelMedium,maxLines=2)
        }
    }
}
