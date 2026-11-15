package dev.senda.launcher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import android.os.SystemClock
import androidx.compose.ui.Modifier
import dev.senda.launcher.audio.LocalSoundFeedback
import dev.senda.launcher.audio.SoundFeedback
import dev.senda.launcher.audio.SoundResult
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.*
import dev.senda.launcher.themeeditor.ThemeSnapshot
import dev.senda.launcher.ui.theme.*
import dev.senda.launcher.widgets.WidgetController
import java.text.Collator
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val SilentPreview = object : SoundFeedback { override fun play(event: SoundEvent) = SoundResult.DISABLED }

val LocalThemePreview=staticCompositionLocalOf { false }

/** Reuses the home layout with local navigation and passive widgets. */
@Composable
fun ThemeLivePreview(snapshot:ThemeSnapshot, source:HomeUiState, viewModel:LauncherViewModel, widgets:WidgetController, modifier:Modifier=Modifier) {
    val state=remember(snapshot,source){snapshot.applyTo(source).copy(visualSettings=source.visualSettings.copy(reduceMotion=true, railHaptics=false))}
    val mapping by produceState<IconPackMapping?>(null,snapshot.pack,source.availableIconPacks){value=viewModel.previewIconPack(snapshot.pack)}
    val retro=source.retroLibrary[snapshot.theme]?.takeIf{snapshot.retro}
    AuraTheme(state.theme,isSystemInDarkTheme(),state.appearance.highContrast,snapshot.finish(),reduceMotion=true,fontChoice=snapshot.font) {
        CompositionLocalProvider(LocalSoundFeedback provides SilentPreview,LocalAppearance provides state.appearance,LocalIconPack provides mapping,LocalRetroAssets provides retro,LocalThemePreview provides true,LocalMotionSettings provides state.visualSettings,LocalMotionController provides null) {
            PreviewScene(state,viewModel,widgets,modifier)
        }
    }
}

@Composable
private fun PreviewScene(state:HomeUiState,viewModel:LauncherViewModel,widgets:WidgetController,modifier:Modifier) {
    val locale=LocalConfiguration.current.locales[0]
    val compact=LocalConfiguration.current.orientation==android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val apps=remember(state.apps,state.hiddenPackages){state.apps.filterNot{it.packageName in state.hiddenPackages}}
    val favorites=remember(apps,state.favoritePackages){val map=apps.associateBy{it.packageName};state.favoritePackages.mapNotNull(map::get)}
    val groups=remember(apps,locale){apps.groupBy{sectionLetterFor(it.label,locale)}}
    val letters=remember(groups,locale){val c=Collator.getInstance(locale);groups.keys.sortedWith{a,b->c.compare(a,b)}}
    val list=rememberLazyListState()
    val time=remember{LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm",locale))}
    val wallpaper=state.skinWallpapers[state.theme.name] ?: LocalRetroAssets.current?.wallpapers?.firstOrNull()?.uri
    val bitmap=rememberLocalImage(wallpaper,1280)
    val skin=LocalSkinStyle.current
    val focus=remember{FocusRequester()};val interactions=remember{MutableInteractionSource()}
    val consoleBackground=state.theme==LauncherTheme.PS4 && bitmap==null && !state.appearance.highContrast
    val ps4 = state.theme == LauncherTheme.PS4
    val scope = rememberCoroutineScope()
    var letterJob by remember { mutableStateOf<Job?>(null) }
    val inputFocus = remember { mutableStateOf<String?>(null) }
    val focusKey = rememberPs4FocusedKey(list, ps4 && !state.appearance.highContrast, inputFocus)
    val recent = if (ps4) apps.firstOrNull { it.component.flattenToString() == state.lastLaunchedComponent } else null
    val selection = rememberPs4HeaderSelection(ps4 && !state.appearance.highContrast, true,
        list, focusKey, apps, recent, state.skinIcons, viewModel::icon)
    val audio = state.audioInfo
    val showCompactAudio = state.headerSettings.showAudioWidget && !state.headerSettings.audioInMenuOnly && audio.isPlaying
    val prefix = listOf(BlockAnchor.BEFORE_FAVORITES, BlockAnchor.AFTER_FAVORITES)
        .count { dashboardBlocks(state, widgets, it).isNotEmpty() } +
        (if (recent != null) 2 else 0) + (if (showCompactAudio) 1 else 0)
    val starts = remember(letters, groups, favorites.size, prefix) {
        sectionStartIndices(letters, groups.mapValues { it.value.size }, favorites.size, false)
            .mapValues { (_, index) -> index + prefix }
    }
    val activeLetter by remember(starts, list) {
        derivedStateOf { starts.entries.lastOrNull { it.value <= list.firstVisibleItemIndex }?.key ?: letters.firstOrNull() }
    }
    val jump: (String) -> Unit = { letter ->
        starts[letter]?.let { index ->
            inputFocus.value = null
            letterJob?.cancel()
            letterJob = scope.launch { list.scrollToItem(index) }
        }
    }
    var quickPreview by rememberSaveable(state.theme) { mutableStateOf(false) }
    CompositionLocalProvider(LocalPs4FocusedKey provides focusKey, LocalPs4InputFocus provides inputFocus) {
    Box(modifier.fillMaxSize().background(skin.wallpaper)) {
        if(bitmap!=null&&!state.appearance.highContrast)Image(bitmap,null,Modifier.matchParentSize(),contentScale=ContentScale.Crop)
        if(consoleBackground)Ps4Wallpaper(true,Modifier.matchParentSize(), strength=(100-(state.themeFinishes[state.theme.name]?.wallpaperDim?:64))/40f)
        Column(Modifier.fillMaxSize().background(if(consoleBackground)androidx.compose.ui.graphics.Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent,androidx.compose.ui.graphics.Color.Transparent)) else if(bitmap!=null&&!state.appearance.highContrast)wallpaperReadabilityScrim(state.themeFinishes[state.theme.name]?.wallpaperDim?:64,MaterialTheme.colorScheme.onBackground) else skin.wallpaper).then(if(bitmap==null&&!state.appearance.highContrast&&!consoleBackground)Modifier.skinPattern(skin)else Modifier).padding(horizontal=22.dp)) {
            Spacer(Modifier.height(if(compact)4.dp else 14.dp))
            Box(Modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                }
            }.focusProperties { canFocus = false }.clearAndSetSemantics { contentDescription = "Cabecera de Inicio" }) {
            Header(theme=state.theme,clockTime=time,selectionTint=selection.animated.takeIf { ps4 && !state.appearance.highContrast },avatarUri=state.avatarUri,widgetController=widgets,weatherPackage=state.weatherPackage?:"hr.dtakac.prognoza",reduceMotion=true,weatherMotion=state.headerSettings.weatherMotion,clockMotion=state.headerSettings.ps4ClockMotion,
                onAvatar={},onWeatherConnection={},onDashboard={},weatherAppLabel=state.apps.firstOrNull{it.packageName==state.weatherPackage}?.label,
                onOpenAlarms={},onOpenCalendar={},onOpenWeather={},onEditFavorites={},onManageHiddenApps={},onChooseTheme={},onLetterJump={},letterJumpEnabled=false,onVisualSettings={},onAlphabetSettings={},onSoundSettings={},onPersonalization={},onHomeSettings={},onGesture={},onDoubleTap=null,onChooseWeatherApp={},onChooseWidgets={},showMotivationalHeader=state.headerSettings.showMotivationalHeader, clockSize=state.headerSettings.clockSize,
                headerHeight=state.headerSettings.headerHeight)
            }
            if(dashboardBlocks(state,widgets,BlockAnchor.HEADER).isNotEmpty())Column(Modifier.heightIn(max=with(LocalDensity.current){(LocalWindowInfo.current.containerSize.height*.28f).toDp()}.coerceIn(120.dp,300.dp)).verticalScroll(rememberScrollState())){DashboardGroup(state,widgets,BlockAnchor.HEADER){}}
            Spacer(Modifier.height(if(compact)4.dp else 12.dp))
            ThemedSearchField(state.theme,androidx.compose.ui.text.input.TextFieldValue(),{}, {},{},focus,interactions,{0f},readOnly=true)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.weight(1f).fillMaxWidth()) {
                AppSections(modifier=Modifier.weight(1f),widgetController=widgets,dashboardState=state,onCalendarPermission={},
                    media = if (showCompactAudio) ({ CompactAudioWidget(state.audioInfo, state.theme) }) else null,
                    favoriteApps=favorites,allApps=apps,groups=groups,groupLetters=letters,favoritePackages=state.favoritePackages.toSet(),skinName=state.theme.name,skinTitle=state.theme.title,skinIcons=state.skinIcons,visualSettings=state.visualSettings,listState=list,selectionTint=selection.animated.takeIf { ps4 && !state.appearance.highContrast },
                    onLaunch={ app ->
                        inputFocus.value = list.layoutInfo.visibleItemsInfo.firstOrNull {
                            it.key == "all-${app.packageName}" || it.key == "favorite-${app.packageName}" ||
                                (it.key == "recent-app" && recent?.packageName == app.packageName)
                        }?.key as? String
                    },onToggleFavorite={},onHide={},onLoadIcon=viewModel::icon,onChooseIcon={},onResetIcon={})
                AlphabetRail(letters=letters,theme=state.theme,modifier=Modifier.width(if(compact||LocalDensity.current.fontScale>=1.4f)MotionTokens.railLandscape else MotionTokens.railPortrait).fillMaxHeight(),selectedLetter=activeLetter,visualSettings=state.visualSettings,onClick=jump,onScrub=jump)
            }
            if (ps4) QuickCenterButton(selection.animated.takeIf { !state.appearance.highContrast }) {
                quickPreview = true
            }
        }
    }
    if (ps4 && quickPreview) {
        QuickCenterSample(audio, selection.settled.value) { quickPreview = false }
    }
    }
}

@Composable
private fun QuickCenterSample(source: ActiveAudioInfo, accent: androidx.compose.ui.graphics.Color, onDismiss: () -> Unit) {
    var audio by remember {
        val duration = source.durationMs?.takeIf { it > 0L } ?: 240_000L
        mutableStateOf(source.copy(title = source.title ?: "Reproducción de ejemplo", durationMs = duration,
            isPlaying = false, sessionId = null, canPlay = true, canPause = true, canSeek = true,
            canPrevious = false, canNext = false, positionMs = (source.positionMs ?: 45_000L).coerceIn(0L, duration),
            positionUpdatedAt = SystemClock.elapsedRealtime(), speed = 1f))
    }
    QuickCenter(audio = audio, alarmAt = null, event = null, reduced = true, origin = null, appAccent = accent,
        target = QuickCenterTarget.PANEL, miniTarget = QuickCenterTarget.PANEL, onMiniTargetChange = {},
        pinnedNote = "", onSavePinnedNote = {}, timers = emptyList(), timerStore = null,
        onDismiss = onDismiss, onSearch = {}, onPrevious = {}, onNext = {},
        onSeek = { audio = audio.copy(positionMs = it, positionUpdatedAt = SystemClock.elapsedRealtime()) },
        onPlayback = {
            val now = SystemClock.elapsedRealtime()
            val elapsed = if (audio.isPlaying) (now - audio.positionUpdatedAt).coerceAtLeast(0L) else 0L
            val position = ((audio.positionMs ?: 0L) + elapsed).coerceAtMost(audio.durationMs ?: Long.MAX_VALUE)
            audio = audio.copy(isPlaying = !audio.isPlaying, positionMs = position, positionUpdatedAt = now)
        },
        onAlarm = {}, onEvent = {}, onCalendar = {}, onOpenPlayer = {}, onOpenMediaSource = {})
}
