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
import androidx.compose.ui.Modifier
import dev.senda.launcher.audio.LocalSoundFeedback
import dev.senda.launcher.audio.SoundFeedback
import dev.senda.launcher.audio.SoundResult
import androidx.compose.ui.focus.FocusRequester
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

/** The real Header/AppSections/DashboardGroup, with inert external actions and passive widgets. */
@Composable
fun ThemeLivePreview(snapshot:ThemeSnapshot, source:HomeUiState, viewModel:LauncherViewModel, widgets:WidgetController, modifier:Modifier=Modifier) {
    val state=remember(snapshot,source){snapshot.applyTo(source).copy(query="",visualSettings=source.visualSettings.copy(reduceMotion=true))}
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
    Box(modifier.fillMaxSize().background(skin.wallpaper).pointerInput(Unit) {
        // A render-only preview: no app launch, popup, sound, haptic or widget interaction.
        awaitPointerEventScope { while(true) { awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() } } }
    }) {
        if(bitmap!=null&&!state.appearance.highContrast)Image(bitmap,null,Modifier.matchParentSize(),contentScale=ContentScale.Crop)
        if(consoleBackground)Ps4Wallpaper(true,Modifier.matchParentSize())
        Column(Modifier.fillMaxSize().background(if(consoleBackground)androidx.compose.ui.graphics.Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent,androidx.compose.ui.graphics.Color.Transparent)) else if(bitmap!=null&&!state.appearance.highContrast)wallpaperReadabilityScrim(state.themeFinishes[state.theme.name]?.wallpaperDim?:64,MaterialTheme.colorScheme.onBackground) else skin.wallpaper).then(if(bitmap==null&&!state.appearance.highContrast&&!consoleBackground)Modifier.skinPattern(skin)else Modifier).padding(horizontal=22.dp)) {
            Spacer(Modifier.height(if(compact)4.dp else 14.dp))
            Header(theme=state.theme,clockTime=time,avatarUri=state.avatarUri,widgetController=widgets,weatherPackage=state.weatherPackage?:"hr.dtakac.prognoza",reduceMotion=true,weatherMotion=state.headerSettings.weatherMotion,
                onAvatar={},onWeatherConnection={},onDashboard={},weatherAppLabel=state.apps.firstOrNull{it.packageName==state.weatherPackage}?.label,
                onOpenAlarms={},onOpenCalendar={},onOpenWeather={},onEditFavorites={},onManageHiddenApps={},onChooseTheme={},onLetterJump={},letterJumpEnabled=false,onVisualSettings={},onAlphabetSettings={},onSoundSettings={},onPersonalization={},onHomeSettings={},onGesture={},onDoubleTap=null,onChooseWeatherApp={},onChooseWidgets={},showMotivationalHeader=state.headerSettings.showMotivationalHeader)
            if(state.headerSettings.showAudioWidget)ThemedAudioWidget(audioInfo=state.audioInfo,theme=state.theme,reduceMotion=true,onOpenAudio={},onPause={},onPrevious={},onNext={},onEnableAccess={},modifier=Modifier.padding(vertical=4.dp))
            if(dashboardBlocks(state,widgets,BlockAnchor.HEADER).isNotEmpty())Column(Modifier.heightIn(max=with(LocalDensity.current){(LocalWindowInfo.current.containerSize.height*.28f).toDp()}.coerceIn(120.dp,300.dp)).verticalScroll(rememberScrollState())){DashboardGroup(state,widgets,BlockAnchor.HEADER){}}
            Spacer(Modifier.height(if(compact)4.dp else 12.dp))
            ThemedSearchField(state.theme,"",{}, {},{},focus,interactions,{0f},readOnly=true)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.weight(1f).fillMaxWidth()) {
                AppSections(modifier=Modifier.weight(1f),widgetController=widgets,dashboardState=state,onCalendarPermission={},favoriteApps=favorites,allApps=apps,groups=groups,groupLetters=letters,favoritePackages=state.favoritePackages.toSet(),skinName=state.theme.name,skinTitle=state.theme.title,skinIcons=state.skinIcons,visualSettings=state.visualSettings,listState=list,onLaunch={},onToggleFavorite={},onHide={},onLoadIcon=viewModel::icon,onChooseIcon={},onResetIcon={})
                AlphabetRail(letters=letters,theme=state.theme,modifier=Modifier.width(MotionTokens.railPortrait).fillMaxHeight(),selectedLetter=letters.firstOrNull(),visualSettings=state.visualSettings,onClick={},onScrub={})
            }
        }
    }
}
