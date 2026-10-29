package dev.senda.launcher.ui

import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.focus.onFocusChanged

import dev.senda.launcher.widgets.WidgetController
import dev.senda.launcher.widgets.WidgetPickerDialog
import dev.senda.launcher.widgets.WidgetShelf
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.audio.LocalSoundFeedback
import dev.senda.launcher.data.SoundEvent
import dev.senda.launcher.data.BlockAnchor
import dev.senda.launcher.data.sectionStartIndices
import dev.senda.launcher.data.loadLocalImage
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import android.graphics.drawable.Drawable
import android.graphics.BitmapFactory
import android.content.Intent
import android.content.Context
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.GestureDirection
import dev.senda.launcher.NotificationShadeService
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.LocalIconPack
import dev.senda.launcher.data.VisualSettings
import dev.senda.launcher.data.moveListItem
import dev.senda.launcher.data.normalizeSearchText
import dev.senda.launcher.data.sectionLetterFor
import androidx.compose.foundation.layout.widthIn
import dev.senda.launcher.ui.theme.LocalRetroStyle
import dev.senda.launcher.ui.theme.LocalIconTreatment
import dev.senda.launcher.ui.theme.RetroClock
import dev.senda.launcher.ui.theme.themeReveal
import dev.senda.launcher.ui.theme.icons.renderFamilyAppIcon
import dev.senda.launcher.ui.theme.LocalIndustrialStyle
import dev.senda.launcher.ui.theme.IndustrialTokens
import dev.senda.launcher.ui.theme.DotClock
import dev.senda.launcher.ui.theme.IconHolder
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.ui.theme.WallpaperPattern
import dev.senda.launcher.ui.theme.wallpaper
import dev.senda.launcher.ui.theme.skinPattern
import androidx.compose.ui.draw.drawBehind
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.text.Normalizer
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    isDefaultHome: Boolean,
    onChooseHome: () -> Unit,
    widgetController: WidgetController,
    onExportSettings: () -> Unit,
    onImportSettings: () -> Unit,
    onRequestCalendar: () -> Unit,
    onAppPermissions: () -> Unit,
    onNotificationSettings: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val soundFeedback = LocalSoundFeedback.current
    val locale = LocalConfiguration.current.locales[0]
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val keyboard = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val motionController = rememberAuraMotion(listState, state.visualSettings)
    var alphabetJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var previousWidgetCount by remember { mutableStateOf(widgetController.widgets.size) }
    LaunchedEffect(widgetController.widgets.size) {
        if (widgetController.widgets.size > previousWidgetCount) listState.scrollToItem(0)
        previousWidgetCount = widgetController.widgets.size
    }
    var showFavoritesEditor by rememberSaveable { mutableStateOf(false) }
    var showHiddenApps by rememberSaveable { mutableStateOf(false) }
    var showLetterJump by rememberSaveable { mutableStateOf(false) }
    var showThemePicker by rememberSaveable { mutableStateOf(false) }
    var showVisualSettings by rememberSaveable { mutableStateOf(false) }
    var showAlphabetSettings by rememberSaveable { mutableStateOf(false) }
    var showSoundSettings by rememberSaveable { mutableStateOf(false) }
    var showPersonalization by rememberSaveable { mutableStateOf(false) }
    var showWidgetPicker by rememberSaveable { mutableStateOf(false) }
    var showWeatherPicker by rememberSaveable { mutableStateOf(false) }
    var showHomeSettings by rememberSaveable { mutableStateOf(false) }
    var showAvatar by rememberSaveable { mutableStateOf(false) }
    var showWeatherConnection by rememberSaveable { mutableStateOf(false) }
    var showDashboard by rememberSaveable { mutableStateOf(false) }
    var showNotificationHelp by rememberSaveable { mutableStateOf(false) }
    var iconTargetPackage by rememberSaveable { mutableStateOf<String?>(null) }
    var iconTargetTheme by rememberSaveable { mutableStateOf(state.theme.name) }
    var showIconSource by rememberSaveable { mutableStateOf(false) }
    var showFamilyIconPicker by rememberSaveable { mutableStateOf(false) }
    val searchFocus = remember { FocusRequester() }
    val compact = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val skin = LocalSkinStyle.current
    val context = LocalContext.current
    var clockTime by remember(locale) { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", locale))) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(locale, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
          while (true) {
            clockTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", locale))
            viewModel.refreshHeader()
            delay(60_000 - System.currentTimeMillis() % 60_000)
          }
        }
    }
    val searchInteractions = remember { MutableInteractionSource() }
    val searchFocused by searchInteractions.collectIsFocusedAsState()
    val glowAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (searchFocused && state.visualSettings.glowEnabled) state.visualSettings.glowStrength / 100f else 0f,
        animationSpec = tween(if (state.visualSettings.reduceMotion) 100 else state.visualSettings.durationMs),
        label = "searchGlow",
    )
    val wallpaperUri = state.skinWallpapers[state.theme.name] ?: dev.senda.launcher.data.LocalRetroAssets.current?.wallpapers?.firstOrNull()?.uri
    val wallpaperBitmap = rememberLocalImage(wallpaperUri, 1920)
    val wallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            iconTargetPackage?.let { name -> viewModel.setSkinIconPackage(LauncherTheme.valueOf(iconTargetTheme), name, uri.toString()) }
        }
        iconTargetPackage = null
    }
    val onChooseIcon: (LaunchableApp) -> Unit = { app ->
        iconTargetPackage = app.packageName
        iconTargetTheme = state.theme.name
        showIconSource = true
    }

    var handledHomeRequest by remember { androidx.compose.runtime.mutableIntStateOf(state.homeRequest) }
    LaunchedEffect(state.homeRequest) {
        if (state.homeRequest != handledHomeRequest) {
            handledHomeRequest = state.homeRequest
            showFavoritesEditor = false
            showHiddenApps = false
            showLetterJump = false
            showThemePicker = false
            showVisualSettings = false; showAlphabetSettings = false; showSoundSettings = false
            showPersonalization = false
            showWeatherPicker = false
            showWidgetPicker = false
            showHomeSettings = false; showAvatar = false; showWeatherConnection = false; showDashboard = false
            showNotificationHelp = false
            showIconSource = false
            showFamilyIconPicker = false
            iconTargetPackage = null
            keyboard?.hide()
            focusManager.clearFocus()
            listState.scrollToItem(0)
        }
    }

    val visibleApps = remember(state.apps, state.hiddenPackages) {
        state.apps.filterNot { it.packageName in state.hiddenPackages }
    }
    val filteredApps = remember(visibleApps, state.query, locale) {
        val query = normalizeSearchText(state.query, locale)
        if (query.isBlank()) visibleApps
        else visibleApps.filter { normalizeSearchText(it.label, locale).contains(query) }
    }
    val favoriteApps = remember(visibleApps, state.favoritePackages) {
        val appsByPackage = visibleApps.associateBy(LaunchableApp::packageName)
        state.favoritePackages.mapNotNull(appsByPackage::get)
    }
    val groups = remember(filteredApps, state.query, locale) {
        if (state.query.isNotBlank()) emptyMap()
        else filteredApps.groupBy { sectionLetterFor(it.label, locale) }
    }
    val groupLetters = remember(groups) {
        val collator = Collator.getInstance(locale)
        groups.keys.sortedWith { first, second -> collator.compare(first, second) }
    }

    BackHandler(enabled = true) {
        if (state.query.isNotEmpty()) viewModel.setQuery("")
        keyboard?.hide()
    }

    val executeGesture: (GestureDirection) -> Unit = { direction ->
        val action = state.gestures[direction]
        when (action) {
            "SEARCH" -> { viewModel.setQuery(""); scope.launch { listState.scrollToItem(0); searchFocus.requestFocus(); keyboard?.show() } }
            "NOTIFICATIONS" -> if (!NotificationShadeService.open()) showNotificationHelp = true
            "NONE" -> Unit
            else -> {
                val app = state.apps.firstOrNull { it.packageName == action.removePrefix("APP:") }
                if (app == null) android.widget.Toast.makeText(context, "La app del gesto no está disponible. Elige otra en Inicio y gestos.", android.widget.Toast.LENGTH_LONG).show()
                else { keyboard?.hide(); viewModel.launch(app) }
            }
        }
    }
    val lockDevice: () -> Unit = {
        if (!NotificationShadeService.lock()) showNotificationHelp = true
    }
    val starts = remember(groupLetters, groups, favoriteApps, state.theme, widgetController.widgets.isNotEmpty(), state.dashboard, state.headerSettings) {
        val dashboardSections = listOf(BlockAnchor.BEFORE_FAVORITES,BlockAnchor.AFTER_FAVORITES).count { dashboardBlocks(state,widgetController,it).isNotEmpty() }
        sectionStartIndices(groupLetters, groups.mapValues { it.value.size }, favoriteApps.size, false).mapValues { it.value + dashboardSections }
    }
    val activeLetter by remember(listState, starts) {
        derivedStateOf { starts.entries.lastOrNull { it.value <= listState.firstVisibleItemIndex }?.key ?: groupLetters.firstOrNull() }
    }
    val targetIndexFor: (String) -> Int = { letter -> starts[letter] ?: 0 }
    val onSelectLetter: (String) -> Unit = { letter ->
        alphabetJob?.cancel()
        alphabetJob = scope.launch {
            listState.animateScrollToItem(targetIndexFor(letter))
        }
    }
    val onScrubLetter: (String) -> Unit = { letter ->
        alphabetJob?.cancel()
        alphabetJob = scope.launch { listState.scrollToItem(targetIndexFor(letter)) }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalMotionController provides motionController) {
    Box(Modifier.fillMaxSize().semantics { testTagsAsResourceId = true }.then(if (!state.isLoading) Modifier.testTag("aura-home-ready") else Modifier).homeGestures(executeGesture).pointerInput(state.lockGesture) {
        detectTapGestures(onLongPress = { showPersonalization = true }, onDoubleTap = if (state.lockGesture.onBackground) { { lockDevice() } } else null)
    }) {
        if (wallpaperBitmap != null && !state.appearance.highContrast) {
            Image(
                bitmap = wallpaperBitmap!!,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        val consoleBackground = state.theme == LauncherTheme.PS4 && wallpaperBitmap == null && !state.appearance.highContrast
        if (consoleBackground) Ps4Wallpaper(state.visualSettings.reduceMotion, Modifier.matchParentSize(), listState)
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (consoleBackground) Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Transparent)) else if (wallpaperBitmap == null || state.appearance.highContrast) skin.wallpaper else wallpaperReadabilityScrim(state.themeFinishes[state.theme.name]?.wallpaperDim ?: 64, MaterialTheme.colorScheme.onBackground))
            .then(if (wallpaperBitmap == null && !state.appearance.highContrast && !consoleBackground) Modifier.skinPattern(skin) else Modifier)
            .imePadding()
            .padding(WindowInsets.statusBars.union(WindowInsets.navigationBars).asPaddingValues())
            .padding(horizontal = 22.dp),
    ) {
        Spacer(Modifier.height(if (compact) 4.dp else 14.dp))
        Header(
            theme = state.theme,
            clockTime = clockTime,
            avatarUri = state.avatarUri,
            widgetController = widgetController,
            weatherPackage = state.weatherPackage ?: "hr.dtakac.prognoza",
            reduceMotion = state.visualSettings.reduceMotion,
            weatherMotion = state.headerSettings.weatherMotion,
            onAvatar = { showAvatar = true },
            onWeatherConnection = { showWeatherConnection = true },
            onDashboard = { showDashboard = true },
            weatherAppLabel = state.apps.firstOrNull { it.packageName == state.weatherPackage }?.label,
            onOpenAlarms = { openAlarms(context) },
            onOpenCalendar = { openCalendar(context) },
            onOpenWeather = {
                val weatherApp = state.apps.firstOrNull { it.packageName == (state.weatherPackage ?: "hr.dtakac.prognoza") }
                if (weatherApp != null) viewModel.launch(weatherApp) else showWeatherPicker = true
            },
            onEditFavorites = { showFavoritesEditor = true },
            onManageHiddenApps = { showHiddenApps = true },
            onChooseTheme = { viewModel.clearThemeError(); showThemePicker = true },
            onLetterJump = { showLetterJump = true },
            letterJumpEnabled = state.query.isBlank() && groupLetters.isNotEmpty(),
            onVisualSettings = { showVisualSettings = true },
            onAlphabetSettings = { showAlphabetSettings = true },
            onSoundSettings = { showSoundSettings = true },
            onPersonalization = { showPersonalization = true },
            onHomeSettings = { showHomeSettings = true },
            onGesture = executeGesture,
            onDoubleTap = if (state.lockGesture.onLogo) lockDevice else null,
            onChooseWeatherApp = { showWeatherPicker = true },
            onChooseWidgets = { showWidgetPicker = true },
            showMotivationalHeader = state.headerSettings.showMotivationalHeader,
        )
        if (state.headerSettings.showAudioWidget) {
            ThemedAudioWidget(
                audioInfo = state.audioInfo,
                theme = state.theme,
                reduceMotion = state.visualSettings.reduceMotion || state.appearance.highContrast,
                onOpenAudio = viewModel::openAudioApp,
                onPause = viewModel::pauseAudio,
                onPrevious = viewModel::previousAudio,
                onNext = viewModel::nextAudio,
                onEnableAccess = { showHomeSettings = true },
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }
        if (dashboardBlocks(state,widgetController,BlockAnchor.HEADER).isNotEmpty()) {
            val dashboardLimit = with(LocalDensity.current) { (LocalWindowInfo.current.containerSize.height * .28f).toDp() }.coerceIn(120.dp,300.dp)
            Column(Modifier.fillMaxWidth().heightIn(max = dashboardLimit).verticalScroll(rememberScrollState())) {
                DashboardGroup(state,widgetController,BlockAnchor.HEADER) { showHomeSettings = true }
            }
        }
        Spacer(Modifier.height(if (compact) 4.dp else 12.dp))

        if (!isDefaultHome) {
            DefaultHomeCard(onChooseHome)
            Spacer(Modifier.height(14.dp))
        }

        ThemedSearchField(
            theme = state.theme, query = state.query, onQuery = viewModel::setQuery,
            onClear = { viewModel.setQuery(""); keyboard?.hide() },
            onSearch = { keyboard?.hide() }, focus = searchFocus,
            interactions = searchInteractions, glow = { glowAlpha },
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(10.dp))
            ErrorBanner(state.errorMessage.orEmpty(), viewModel::refreshApps)
        }

        Spacer(Modifier.height(8.dp))
        when {
            state.isLoading && state.apps.isEmpty() -> LoadingState()
            state.apps.isEmpty() -> EmptyState(
                title = "No encontramos apps lanzables",
                detail = "Cuando haya apps disponibles, aparecerán aquí.",
                onRefresh = viewModel::refreshApps,
            )
            filteredApps.isEmpty() -> EmptyState(
                title = if (state.query.isBlank()) "Todas las apps están ocultas" else "Sin coincidencias",
                detail = if (state.query.isBlank()) "Puedes volver a mostrarlas desde la gestión de apps." else "Prueba con otra parte del nombre.",
                onRefresh = { if (state.query.isBlank()) showHiddenApps = true else viewModel.setQuery("") },
                actionLabel = if (state.query.isBlank()) "Gestionar apps" else "Limpiar búsqueda",
            )
            state.query.isNotBlank() -> SearchResults(
                apps = filteredApps,
                favoritePackages = state.favoritePackages.toSet(),
                skinName = state.theme.name,
                skinTitle = state.theme.title,
                skinIcons = state.skinIcons,
                listState = listState,
                visualSettings = state.visualSettings,
                onLaunch = { soundFeedback.play(SoundEvent.LAUNCH); keyboard?.hide(); viewModel.launch(it) },
                onToggleFavorite = { soundFeedback.play(SoundEvent.FAVORITE); viewModel.toggleFavorite(it) },
                onLoadIcon = viewModel::icon,
                onHide = viewModel::toggleHidden,
                onChooseIcon = onChooseIcon,
                onResetIcon = { viewModel.setSkinIcon(state.theme, it, null) },
            )
            else -> {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppSections(
                        modifier = Modifier.weight(1f).fillMaxHeight().graphicsLayer {
                        },
                        widgetController = widgetController,
                        dashboardState = state,
                        onCalendarPermission = { showHomeSettings = true },
                        favoriteApps = favoriteApps,
                        allApps = filteredApps,
                        groups = groups,
                        groupLetters = groupLetters,
                        favoritePackages = state.favoritePackages.toSet(),
                        skinName = state.theme.name,
                        skinTitle = state.theme.title,
                        skinIcons = state.skinIcons,
                        visualSettings = state.visualSettings,
                        listState = listState,
                        onLaunch = { soundFeedback.play(SoundEvent.LAUNCH); keyboard?.hide(); viewModel.launch(it) },
                        onToggleFavorite = { soundFeedback.play(SoundEvent.FAVORITE); viewModel.toggleFavorite(it) },
                        onLoadIcon = viewModel::icon,
                        onHide = viewModel::toggleHidden,
                        onChooseIcon = onChooseIcon,
                        onResetIcon = { viewModel.setSkinIcon(state.theme, it, null) },
                    )
                    AlphabetRail(
                        theme = state.theme,
                        letters = groupLetters,
                        modifier = Modifier.testTag("aura-alphabet-rail").width(if (compact || LocalDensity.current.fontScale >= 1.4f) MotionTokens.railLandscape else MotionTokens.railPortrait).fillMaxHeight(),
                        selectedLetter = activeLetter,
                        visualSettings = state.visualSettings,
                        onClick = onSelectLetter,
                        onScrub = onScrubLetter,
                    )
                }
            }
        }
        }
    }

    } // Motion controller provider

    if (showIconSource) AuraAlertDialog(onDismissRequest = { showIconSource = false; iconTargetPackage = null },
        title = { Text("Elegir icono") }, text = {
            Column(Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Se guarda solo para esta app y este tema. Normales conserva el símbolo de Android.", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { showIconSource = false; showFamilyIconPicker = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Dibujos de las familias Aura") }
                TextButton(onClick = { showIconSource = false; wallpaperPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Imagen de mi galería") }
            }
        }, confirmButton = { TextButton(onClick = { showIconSource = false; iconTargetPackage = null }) { Text("Cancelar") } })
    if (showFamilyIconPicker) IconFamilyDialog(LauncherTheme.valueOf(iconTargetTheme), onUse = {}, onDismiss = { showFamilyIconPicker = false; iconTargetPackage = null }, onPick = { icon, artworkTheme ->
        iconTargetPackage?.let { name -> viewModel.setFamilyIcon(LauncherTheme.valueOf(iconTargetTheme), name, artworkTheme, icon) }
        showFamilyIconPicker = false; iconTargetPackage = null
    })
    if (showLetterJump) LetterJumpDialog(groupLetters, onSelect = { showLetterJump = false; onSelectLetter(it) }, onDismiss = { showLetterJump = false })
    if (showSoundSettings) SoundSettingsDialog(state.soundSettings, state.theme, viewModel::setSoundSettings) { showSoundSettings = false }
    if (showAvatar) AvatarDialog(state,viewModel) { showAvatar = false }
    if (showWeatherConnection) WeatherConnectionDialog(state,widgetController,onSettings=viewModel::setHeaderSettings,
        onOpen = { val app = state.apps.firstOrNull { it.packageName == (state.weatherPackage ?: "hr.dtakac.prognoza") }; if(app != null) viewModel.launch(app) else showWeatherPicker = true },
        onChooseApp = { showWeatherConnection = false; showWeatherPicker = true }, onDismiss = { showWeatherConnection = false })
    if (showDashboard && state.dashboardLoaded) DashboardEditorDialog(state,viewModel,widgetController,onAddWidget = { showDashboard = false; showWidgetPicker = true }) { showDashboard = false }
    if (showPersonalization) PersonalizationDialog(state, viewModel, onExportSettings, onImportSettings) { showPersonalization = false }
    if (state.restoreSummary != null) AuraAlertDialog(
        onDismissRequest = { if (!state.backupBusy) viewModel.cancelRestore() },
        title = { Text("Restaurar configuración") },
        text = { Text(state.restoreSummary!!) },
        confirmButton = { TextButton(onClick = viewModel::confirmRestore, enabled = !state.backupBusy) { Text("Restaurar ajustes") } },
        dismissButton = { TextButton(onClick = viewModel::cancelRestore, enabled = !state.backupBusy) { Text("Cancelar") } },
    )
    if (showWidgetPicker) WidgetPickerDialog(widgetController) { showWidgetPicker = false }
    if (showFavoritesEditor) {
        FavoritesEditorDialog(
            favorites = favoriteApps,
            onSave = viewModel::setFavorites,
            onDismiss = { showFavoritesEditor = false },
        )
    }
    if (showHiddenApps) {
        HiddenAppsDialog(
            hiddenApps = state.apps.filter { it.packageName in state.hiddenPackages },
            onShowApp = viewModel::toggleHidden,
            onDismiss = { showHiddenApps = false },
        )
    }
    if (showThemePicker) ThemeEditorDialog(state, viewModel, widgetController) { showThemePicker = false }
    if (showHomeSettings) HomeSettingsDialog(state, viewModel, onRequestCalendar, onAppPermissions, { showNotificationHelp = true }) { showHomeSettings = false }
    if (showNotificationHelp) AuraAlertDialog(
        onDismissRequest = { showNotificationHelp = false }, title = { Text("Gestos del dispositivo") },
        text = { Text("Para bloquear con doble toque o abrir notificaciones, activa Aura · Gestos del dispositivo en Accesibilidad. Android mostrará una autorización amplia. Aura usa estas dos acciones solo cuando ejecutas el gesto; no recupera el contenido de las pantallas. Puedes desactivar el acceso cuando quieras. Una instalación por APK puede requerir permitir ajustes restringidos.") },
        confirmButton = { TextButton(onClick = { showNotificationHelp = false; onNotificationSettings() }) { Text("Abrir accesibilidad") } },
        dismissButton = { TextButton(onClick = { showNotificationHelp = false }) { Text("Ahora no") } },
    )
    if (showVisualSettings) VisualSettingsDialog(
        current = state.visualSettings,
        onChange = viewModel::setVisualSettings,
        onDismiss = { showVisualSettings = false },
        onAlphabetSettings = { showVisualSettings = false; showAlphabetSettings = true },
    )
    if (showAlphabetSettings) AlphabetSettingsDialog(state.theme,state.visualSettings,viewModel::setVisualSettings){showAlphabetSettings=false}
    if (showWeatherPicker) WeatherAppDialog(
        apps = state.apps,
        selectedPackage = state.weatherPackage,
        onSelect = { packageName -> viewModel.setWeatherPackage(packageName); showWeatherPicker = false },
        onDismiss = { showWeatherPicker = false },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Header(
    theme: LauncherTheme,
    clockTime: String,
    avatarUri: String?,
    widgetController: WidgetController,
    weatherPackage: String,
    reduceMotion: Boolean,
    weatherMotion: dev.senda.launcher.data.WeatherMotionSettings,
    onAvatar: () -> Unit,
    onWeatherConnection: () -> Unit,
    onDashboard: () -> Unit,
    weatherAppLabel: String?,
    onOpenAlarms: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenWeather: () -> Unit,
    onEditFavorites: () -> Unit,
    onManageHiddenApps: () -> Unit,
    onChooseTheme: () -> Unit,
    onLetterJump: () -> Unit,
    letterJumpEnabled: Boolean,
    onVisualSettings: () -> Unit,
    onAlphabetSettings: () -> Unit,
    onSoundSettings: () -> Unit,
    onPersonalization: () -> Unit,
    onHomeSettings: () -> Unit,
    onGesture: (GestureDirection) -> Unit,
    onDoubleTap: (() -> Unit)?,
    onChooseWeatherApp: () -> Unit,
    onChooseWidgets: () -> Unit,
    showMotivationalHeader: Boolean = true,
) {
    val locale = LocalConfiguration.current.locales[0]
    var themeMenuExpanded by remember { mutableStateOf(false) }
    val soundFeedback = LocalSoundFeedback.current
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM", locale))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.homeGestures(onGesture).combinedClickable(onClick = onAvatar, onLongClick = onPersonalization, onDoubleClick = onDoubleTap)
                .semantics { contentDescription = "Avatar de Aura: cambiar imagen y gestos"; role = Role.Button
                    customActions = if (onDoubleTap == null) emptyList() else listOf(androidx.compose.ui.semantics.CustomAccessibilityAction("Bloquear pantalla") { onDoubleTap(); true })
                }) { if (!renderAvatarImage(avatarUri,Modifier.size(42.dp))) { if (theme == LauncherTheme.PS4) Ps4Mark() else AuraMark() } }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                val motivational = remember(theme, today, clockTime) { dev.senda.launcher.data.MotivationalCatalog.phraseFor(theme) }
                val captionText = if (showMotivationalHeader) motivational else if (LocalRetroStyle.current.enabled) LocalRetroStyle.current.caption else "AURA / LAUNCHER"
                ThemedHeaderCaption(theme, captionText, reduceMotion)
                Box(Modifier.fillMaxWidth().padding(end=8.dp)) {
                val matches=remember(widgetController.weatherWidgetId,widgetController.providerRevision,weatherPackage) {
                    widgetController.manager.getAppWidgetInfo(widgetController.weatherWidgetId)?.provider?.packageName==weatherPackage
                }
                WeatherSweep(theme,widgetController.weatherReading?.takeIf{matches},weatherMotion,reduceMotion||LocalAppearance.current.highContrast,Modifier.matchParentSize())
                Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.weight(1f)) {
                        if (LocalIndustrialStyle.current.enabled) BoxWithConstraints(Modifier.fillMaxWidth()) {
                            val clockWidth = minOf(maxWidth,62.dp*(27f/7f))
                            DotClock(clockTime,Modifier.width(clockWidth).height(62.dp).clickable(onClick=onOpenAlarms).semantics { contentDescription="Abrir alarmas · $clockTime";role=Role.Button })
                        } else if(theme == LauncherTheme.PS4) Text(clockTime,Modifier.heightIn(min=48.dp).themeReveal(theme,clockTime,reduceMotion).clickable(onClick=onOpenAlarms).semantics { contentDescription="Abrir alarmas · $clockTime";role=Role.Button },fontSize=22.sp,fontWeight=FontWeight.Light)
                        else if(LocalRetroStyle.current.enabled) RetroClock(clockTime,Modifier.clickable(onClick=onOpenAlarms).semantics { contentDescription="Abrir alarmas · $clockTime";role=Role.Button })
                        else Text(clockTime,Modifier.heightIn(min=48.dp).clickable(onClick=onOpenAlarms).semantics { contentDescription="Abrir alarmas · $clockTime";role=Role.Button },style=MaterialTheme.typography.headlineLarge)
                    }
                    HeaderWeather(widgetController,weatherPackage,weatherAppLabel ?: "Prognoza",reduceMotion,onOpenWeather,onWeatherConnection)
                }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                ) {
                    HeaderShortcut("calendar", today, "Abrir calendario · $today", onOpenCalendar)
                }
            }
        }
        Box {
            IconButton(
                onClick = { soundFeedback.play(SoundEvent.MENU); themeMenuExpanded = true },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Personalizar tema. Actual: ${theme.title}")
            }
            DropdownMenu(expanded = themeMenuExpanded, onDismissRequest = { themeMenuExpanded = false }, modifier = Modifier.animateContentSize(tween(240))) {
                DropdownMenuItem(text = { Text("Tema visual · ${theme.title}") }, onClick = { themeMenuExpanded = false; onChooseTheme() })
                DropdownMenuItem(text = { Text("Saltar a una letra · A–Z") }, enabled = letterJumpEnabled, onClick = { themeMenuExpanded = false; onLetterJump() })
                DropdownMenuItem(text = { Text("Clima en el reloj") }, onClick = { themeMenuExpanded = false; onWeatherConnection() })
                DropdownMenuItem(text = { Text("Cambiar avatar") }, onClick = { themeMenuExpanded = false; onAvatar() })
                DropdownMenuItem(text = { Text("Organizar inicio") }, onClick = { themeMenuExpanded = false; onDashboard() })
                DropdownMenuItem(text = { Text("Inicio y gestos") }, onClick = { themeMenuExpanded = false; onHomeSettings() })
                DropdownMenuItem(text = { Text("Personalización y respaldo") }, onClick = { themeMenuExpanded = false; onPersonalization() })
                DropdownMenuItem(text = { Text("Sonidos y respuesta") }, onClick = { themeMenuExpanded = false; onSoundSettings() })
                DropdownMenuItem(text = { Text("Barra A–Z · efectos y respuesta") }, onClick = { themeMenuExpanded = false; onAlphabetSettings() })
                DropdownMenuItem(text = { Text("Movimiento y efectos") }, onClick = { themeMenuExpanded = false; onVisualSettings() })
                DropdownMenuItem(text = { Text(if (weatherAppLabel == null) "Elegir app del clima" else "App del clima · $weatherAppLabel") }, onClick = { themeMenuExpanded = false; onChooseWeatherApp() })
                DropdownMenuItem(text = { Text("Añadir widget") }, onClick = { themeMenuExpanded = false; onChooseWidgets() })
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Editar favoritas") },
                    onClick = { themeMenuExpanded = false; onEditFavorites() },
                )
                DropdownMenuItem(
                    text = { Text("Apps ocultas") },
                    onClick = { themeMenuExpanded = false; onManageHiddenApps() },
                )
            }
        }
    }
}

@Composable
private fun HeaderShortcut(glyph: String, label: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val iconTint = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier.widthIn(min = 48.dp).heightIn(min = 48.dp).clickable(onClick = onClick)
            .semantics { contentDescription = description; role = Role.Button },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Canvas(Modifier.size(13.dp)) {
            val tint = iconTint
            val stroke = 1.35.dp.toPx()
            when (glyph) {
                "clock" -> {
                    drawCircle(tint, radius = size.minDimension * .42f, style = Stroke(stroke))
                    drawLine(tint, androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .25f), androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .52f), stroke)
                    drawLine(tint, androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .52f), androidx.compose.ui.geometry.Offset(size.width * .72f, size.height * .64f), stroke)
                }
                "calendar" -> {
                    val rect = androidx.compose.ui.geometry.Offset(size.width * .12f, size.height * .2f)
                    drawRoundRect(tint, topLeft = rect, size = androidx.compose.ui.geometry.Size(size.width * .76f, size.height * .7f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()), style = Stroke(stroke))
                    drawLine(tint, androidx.compose.ui.geometry.Offset(size.width * .12f, size.height * .4f), androidx.compose.ui.geometry.Offset(size.width * .88f, size.height * .4f), stroke)
                    drawLine(tint, androidx.compose.ui.geometry.Offset(size.width * .32f, size.height * .08f), androidx.compose.ui.geometry.Offset(size.width * .32f, size.height * .3f), stroke)
                    drawLine(tint, androidx.compose.ui.geometry.Offset(size.width * .68f, size.height * .08f), androidx.compose.ui.geometry.Offset(size.width * .68f, size.height * .3f), stroke)
                }
                else -> {
                    val path = Path().apply {
                        moveTo(size.width * .2f, size.height * .75f)
                        cubicTo(size.width * .02f, size.height * .72f, size.width * .12f, size.height * .44f, size.width * .34f, size.height * .48f)
                        cubicTo(size.width * .39f, size.height * .14f, size.width * .86f, size.height * .22f, size.width * .83f, size.height * .54f)
                        cubicTo(size.width * 1.05f, size.height * .55f, size.width * 1.02f, size.height * .78f, size.width * .78f, size.height * .78f)
                        close()
                    }
                    drawPath(path, tint)
                }
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun WeatherAppDialog(
    apps: List<LaunchableApp>,
    selectedPackage: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aplicación del clima") },
        text = {
            Column {
                Text("Elige qué aplicación abrir desde el icono del clima.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyColumn(Modifier.heightIn(max = 430.dp).padding(top = 8.dp)) {
                    item(key = "weather-none") {
                        DropdownMenuItem(
                            text = { Text("Preguntar al tocar Clima") },
                            onClick = { onSelect(null) },
                        )
                    }
                    items(apps.sortedBy { it.label.lowercase() }, key = { it.packageName }) { app ->
                        DropdownMenuItem(
                            text = { Text(if (app.packageName == selectedPackage) "✓  ${app.label}" else app.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            onClick = { onSelect(app.packageName) },
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } },
    )
}

private fun openAlarms(context: Context) {
    val showAlarms = Intent(AlarmClock.ACTION_SHOW_ALARMS)
    runCatching { context.startActivity(showAlarms) }.onFailure {
        runCatching { context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM)) }
    }
}

private fun openCalendar(context: Context) {
    val calendar = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR)
    runCatching { context.startActivity(calendar) }.onFailure {
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, CalendarContract.CONTENT_URI)) }
    }
}

@Composable
private fun AuraMark() {
    val skin = LocalSkinStyle.current
    if (skin.pattern == WallpaperPattern.DOTS || LocalIndustrialStyle.current.enabled) {
        Box(Modifier.size(42.dp).background(androidx.compose.ui.graphics.Color(0xFF111216), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(28.dp)) {
                val grid = listOf("00100", "01110", "11011", "10001", "10001")
                val pitch = size.width / 5f
                grid.forEachIndexed { row, marks -> marks.forEachIndexed { col, mark ->
                    drawCircle(
                        color = if (mark == '1') skin.accent else androidx.compose.ui.graphics.Color.White.copy(alpha = .17f),
                        radius = if (mark == '1') pitch * .25f else pitch * .12f,
                        center = androidx.compose.ui.geometry.Offset((col + .5f) * pitch, (row + .5f) * pitch),
                    )
                } }
            }
        }
    } else {
        Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
            Text("a", color = MaterialTheme.colorScheme.onPrimary, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FavoritesEditorDialog(
    favorites: List<LaunchableApp>,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var orderedFavorites by remember(favorites) { mutableStateOf(favorites) }
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar favoritas") },
        text = {
            if (orderedFavorites.isEmpty()) {
                Text("Añade una app tocando su estrella en la lista.")
            } else {
                LazyColumn(Modifier.heightIn(max = 480.dp)) {
                    itemsIndexed(orderedFavorites, key = { _, app -> app.packageName }) { index, app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                app.label,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            IconButton(
                                modifier = Modifier.semantics { contentDescription = "Subir ${app.label}" },
                                enabled = index > 0,
                                onClick = {
                                    val updated = moveListItem(orderedFavorites, index, index - 1)
                                    orderedFavorites = updated
                                    onSave(updated.map(LaunchableApp::packageName))
                                },
                            ) { Text("↑", color = MaterialTheme.colorScheme.primary) }
                            IconButton(
                                modifier = Modifier.semantics { contentDescription = "Bajar ${app.label}" },
                                enabled = index < orderedFavorites.lastIndex,
                                onClick = {
                                    val updated = moveListItem(orderedFavorites, index, index + 1)
                                    orderedFavorites = updated
                                    onSave(updated.map(LaunchableApp::packageName))
                                },
                            ) { Text("↓", color = MaterialTheme.colorScheme.primary) }
                            IconButton(
                                modifier = Modifier.semantics { contentDescription = "Quitar ${app.label} de favoritas" },
                                onClick = {
                                    val updated = orderedFavorites.filterNot { it.packageName == app.packageName }
                                    orderedFavorites = updated
                                    onSave(updated.map(LaunchableApp::packageName))
                                },
                            ) { Text("×", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } },
    )
}

@Composable
private fun HiddenAppsDialog(
    hiddenApps: List<LaunchableApp>,
    onShowApp: (LaunchableApp) -> Unit,
    onDismiss: () -> Unit,
) {
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apps ocultas") },
        text = {
            if (hiddenApps.isEmpty()) {
                Text("No hay apps ocultas. Mantén pulsada una app y elige Ocultar app para quitarla de la lista y de la búsqueda.")
            } else {
                LazyColumn(Modifier.heightIn(max = 480.dp)) {
                    items(hiddenApps, key = LaunchableApp::packageName) { app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(app.label, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            TextButton(onClick = { onShowApp(app) }, modifier = Modifier.semantics { contentDescription = "Mostrar ${app.label}" }) { Text("Mostrar") }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } },
    )
}

@Composable
private fun DefaultHomeCard(onChooseHome: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = if (LocalIndustrialStyle.current.enabled) RoundedCornerShape(IndustrialTokens.radius) else MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Usa Aura Launcher como inicio", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text("Elige esta app en el diálogo del sistema.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onChooseHome) { Text("Elegir") }
        }
    }
}

@Composable
internal fun AppSections(
    modifier: Modifier = Modifier,
    widgetController: WidgetController,
    dashboardState: HomeUiState,
    onCalendarPermission: () -> Unit,
    favoriteApps: List<LaunchableApp>,
    allApps: List<LaunchableApp>,
    groups: Map<String, List<LaunchableApp>>,
    groupLetters: List<String>,
    favoritePackages: Set<String>,
    skinName: String,
    skinTitle: String,
    skinIcons: Map<String, String>,
    visualSettings: VisualSettings,
    listState: LazyListState,
    onLaunch: (LaunchableApp) -> Unit,
    onToggleFavorite: (LaunchableApp) -> Unit,
    onHide: (LaunchableApp) -> Unit,
    onLoadIcon: (LaunchableApp) -> Drawable,
    onChooseIcon: (LaunchableApp) -> Unit,
    onResetIcon: (LaunchableApp) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val collator = remember(locale) { Collator.getInstance(locale) }
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("aura-app-list").nestedScroll(LocalMotionController.current?.connection ?: remember { object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {} }),
        state = listState,
        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (dashboardBlocks(dashboardState,widgetController,BlockAnchor.BEFORE_FAVORITES).isNotEmpty()) item(key="dashboard-before", contentType="dashboard") { DashboardGroup(dashboardState,widgetController,BlockAnchor.BEFORE_FAVORITES,onCalendarPermission) }
        if (favoriteApps.isNotEmpty()) {
            item(key = "favorites-heading", contentType = "heading") { SectionHeading("Tus favoritas", favoriteApps.size, LauncherTheme.valueOf(skinName)) }
            items(favoriteApps, key = { "favorite-${it.packageName}" }, contentType = { "app" }) { app ->
                AppRow(app, isFavorite = true, skinName = skinName, skinTitle = skinTitle, customIconUri = skinIcons["${skinName}_${app.packageName}"], listState = listState, visualSettings = visualSettings, rowKey = "favorite-${app.packageName}", modifier = Modifier.animateItem(fadeInSpec = if (visualSettings.reduceMotion) null else tween(160), placementSpec = if (visualSettings.reduceMotion) null else spring(stiffness = 700f, dampingRatio = .9f), fadeOutSpec = if (visualSettings.reduceMotion) null else tween(100)), onLaunch = { onLaunch(app) }, onToggleFavorite = { onToggleFavorite(app) }, onHide = { onHide(app) }, onChooseIcon = { onChooseIcon(app) }, onResetIcon = { onResetIcon(app) }, onLoadIcon = onLoadIcon)
            }
        }
        if (dashboardBlocks(dashboardState,widgetController,BlockAnchor.AFTER_FAVORITES).isNotEmpty()) item(key="dashboard-after", contentType="dashboard") { DashboardGroup(dashboardState,widgetController,BlockAnchor.AFTER_FAVORITES,onCalendarPermission) }
        item(key = "all-heading", contentType = "heading") { SectionHeading("Todas las apps", allApps.size, LauncherTheme.valueOf(skinName)) }
        groupLetters.forEach { letter ->
            item(key = "section-$letter", contentType = "letter") {
                Text(
                    letter,
                    modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            val sectionApps = groups[letter].orEmpty().sortedWith { a, b -> collator.compare(a.label, b.label) }
            items(sectionApps, key = { "all-${it.packageName}" }, contentType = { "app" }) { app ->
                AppRow(
                    app,
                    isFavorite = app.packageName in favoritePackages,
                    skinName = skinName,
                    skinTitle = skinTitle,
                    customIconUri = skinIcons["${skinName}_${app.packageName}"],
                    listState = listState,
                    visualSettings = visualSettings,
                    rowKey = "all-${app.packageName}",
                    modifier = Modifier.animateItem(fadeInSpec = if (visualSettings.reduceMotion) null else tween(160), placementSpec = if (visualSettings.reduceMotion) null else spring(stiffness = 700f, dampingRatio = .9f), fadeOutSpec = if (visualSettings.reduceMotion) null else tween(100)),
                    onLaunch = { onLaunch(app) },
                    onToggleFavorite = { onToggleFavorite(app) },
                    onHide = { onHide(app) },
                    onChooseIcon = { onChooseIcon(app) },
                    onResetIcon = { onResetIcon(app) },
                    onLoadIcon = onLoadIcon,
                )
            }
        }
        if (dashboardBlocks(dashboardState,widgetController,BlockAnchor.END).isNotEmpty()) item(key="dashboard-end", contentType="dashboard") { DashboardGroup(dashboardState,widgetController,BlockAnchor.END,onCalendarPermission) }
    }
}

@Composable
private fun SearchResults(
    apps: List<LaunchableApp>,
    favoritePackages: Set<String>,
    skinName: String,
    skinTitle: String,
    skinIcons: Map<String, String>,
    listState: LazyListState,
    visualSettings: VisualSettings,
    onLaunch: (LaunchableApp) -> Unit,
    onToggleFavorite: (LaunchableApp) -> Unit,
    onHide: (LaunchableApp) -> Unit,
    onLoadIcon: (LaunchableApp) -> Drawable,
    onChooseIcon: (LaunchableApp) -> Unit,
    onResetIcon: (LaunchableApp) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().nestedScroll(LocalMotionController.current?.connection ?: remember { object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {} }),
        state = listState,
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        item(key = "search-count", contentType = "count") {
            Text(
                if (apps.size == 1) "1 resultado" else "${apps.size} resultados",
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(apps, key = { "result-${it.packageName}" }, contentType = { "app" }) { app ->
            AppRow(
                app,
                isFavorite = app.packageName in favoritePackages,
                skinName = skinName,
                skinTitle = skinTitle,
                customIconUri = skinIcons["${skinName}_${app.packageName}"],
                listState = listState,
                visualSettings = visualSettings,
                rowKey = "result-${app.packageName}",
                modifier = Modifier.animateItem(fadeInSpec = if (visualSettings.reduceMotion) null else tween(160), placementSpec = if (visualSettings.reduceMotion) null else spring(stiffness = 700f, dampingRatio = .9f), fadeOutSpec = if (visualSettings.reduceMotion) null else tween(100)),
                onLaunch = { onLaunch(app) },
                onToggleFavorite = { onToggleFavorite(app) },
                onHide = { onHide(app) },
                onChooseIcon = { onChooseIcon(app) },
                onResetIcon = { onResetIcon(app) },
                onLoadIcon = onLoadIcon,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppRow(
    app: LaunchableApp,
    isFavorite: Boolean,
    skinName: String,
    skinTitle: String,
    customIconUri: String?,
    modifier: Modifier = Modifier,
    listState: LazyListState? = null,
    visualSettings: VisualSettings = VisualSettings(),
    rowKey: String? = null,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onHide: () -> Unit,
    onChooseIcon: () -> Unit,
    onResetIcon: () -> Unit,
    onLoadIcon: (LaunchableApp) -> Drawable,
) {
    var actionsExpanded by remember(app.packageName) { mutableStateOf(false) }
    val appearance = LocalAppearance.current
    val console = skinName == LauncherTheme.PS4.name && !appearance.highContrast
    val rowInteractions = remember(app.packageName) { MutableInteractionSource() }
    val pressed by rowInteractions.collectIsPressedAsState()
    var rowFocused by remember { mutableStateOf(false) }
    // The result changes once per focused item, not once per scroll pixel.
    val centered by remember(listState, rowKey, console) { derivedStateOf {
        if (!console || listState == null || rowKey == null) false else {
            val layout = listState.layoutInfo
            val center = layout.viewportStartOffset + (layout.viewportEndOffset-layout.viewportStartOffset)*.42f
            layout.visibleItemsInfo.asSequence().filter {
                val key = it.key.toString()
                key.startsWith("favorite-") || key.startsWith("all-") || key.startsWith("result-")
            }.minByOrNull { kotlin.math.abs(it.offset+it.size*.5f-center) }?.key == rowKey
        }
    } }
    val still = visualSettings.reduceMotion || appearance.highContrast || LocalThemePreview.current || !android.animation.ValueAnimator.areAnimatorsEnabled()
    val focusProgress = animateFloatAsState(if (console && (centered || rowFocused || pressed)) 1f else 0f,
        tween(if (still) 0 else 320, easing=androidx.compose.animation.core.FastOutSlowInEasing), label="ps4-row-focus")
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = if(console) maxOf(appearance.rowHeight, appearance.iconSize+28).dp else appearance.rowHeight.dp).auraListMotion(listState, rowKey, visualSettings),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .then(if (console) Modifier.graphicsLayer {
                    val scale = if(still) 1f else 1f+focusProgress.value*.025f
                    scaleX=scale; scaleY=scale
                }.ps4FocusFrame { focusProgress.value } else Modifier)
                .onFocusChanged { rowFocused = it.hasFocus }
                .semantics(mergeDescendants = true) {
                    stateDescription = if (isFavorite) "Favorita" else "No favorita"
                    customActions = listOf(androidx.compose.ui.semantics.CustomAccessibilityAction(
                        if (isFavorite) "Quitar de favoritas" else "Añadir a favoritas"
                    ) { onToggleFavorite(); true })
                }
                .combinedClickable(interactionSource=rowInteractions,indication=androidx.compose.foundation.LocalIndication.current,
                    onClick = onLaunch, onLongClickLabel = "Opciones de ${app.label}", onLongClick = { actionsExpanded = true })
                .padding(start = if(console)8.dp else 4.dp, top=if(console)8.dp else 0.dp, bottom=if(console)8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (console) {
                Box(Modifier.size((appearance.iconSize+12).dp).graphicsLayer {
                    val scale=if(still)1f else 1f+focusProgress.value*.08f
                    scaleX=scale; scaleY=scale
                }
                    .background(Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color(0xFF173C78).copy(alpha=.65f),androidx.compose.ui.graphics.Color(0xFF001538).copy(alpha=.7f))),RoundedCornerShape(2.dp))
                    .border(.8.dp,androidx.compose.ui.graphics.Color.White.copy(alpha=.24f),RoundedCornerShape(2.dp)),contentAlignment=Alignment.Center) {
                    AppIcon(app, customIconUri, onLoadIcon)
                }
            } else AppIcon(app, customIconUri, onLoadIcon)
            Text(
                text = app.label,
                modifier = Modifier.weight(1f).padding(start = 14.dp, end = 8.dp)
                    .then(if(console)Modifier.graphicsLayer {
                        transformOrigin=androidx.compose.ui.graphics.TransformOrigin(0f,.5f)
                        val scale=if(still)1f else .82f+focusProgress.value*.18f
                        scaleX=scale; scaleY=scale
                    } else Modifier),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = (if(console) appearance.textSize+8 else appearance.textSize).sp,
                fontWeight = if(console) FontWeight.Light else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isFavorite) {
                // The section already identifies favorites; this quiet mark also works in A–Z/search.
                Box(Modifier.padding(end = 12.dp).size(5.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape))
            }
        }
        }
        DropdownMenu(expanded = actionsExpanded, onDismissRequest = { actionsExpanded = false }) {
            DropdownMenuItem(
                text = { Text("Elegir icono para $skinTitle") },
                onClick = { actionsExpanded = false; onChooseIcon() },
            )
            if (customIconUri != null) DropdownMenuItem(
                text = { Text("Restablecer icono") },
                onClick = { actionsExpanded = false; onResetIcon() },
            )
            DropdownMenuItem(
                text = { Text(if (isFavorite) "Quitar de favoritas" else "Añadir a favoritas") },
                onClick = { actionsExpanded = false; onToggleFavorite() },
            )
            DropdownMenuItem(
                text = { Text("Ocultar app") },
                onClick = { actionsExpanded = false; onHide() },
            )
        }
    }
}

@Composable
internal fun AppIcon(app: LaunchableApp, customIconUri: String?, onLoadIcon: (LaunchableApp) -> Drawable) {
    val style = LocalSkinStyle.current
    val treatment = LocalIconTreatment.current
    val normal = treatment == dev.senda.launcher.data.IconTreatment.ORIGINAL
    val retro = LocalRetroStyle.current
    val importedUri = if (retro.enabled && treatment == dev.senda.launcher.data.IconTreatment.AUTO && customIconUri == null && LocalIconPack.current == null)
        dev.senda.launcher.data.LocalRetroAssets.current?.icon(app.packageName, dev.senda.launcher.ui.theme.glyphFor(app.packageName)?.name) else null
    val familyTheme = if (LocalIndustrialStyle.current.enabled) dev.senda.launcher.data.LauncherTheme.NOTHING else retro.theme
    // PS4 reference uses recognizable full-colour app identities. Explicit packs/replacements win.
    val consoleColour = familyTheme == LauncherTheme.PS4 && treatment == dev.senda.launcher.data.IconTreatment.AUTO && importedUri == null && customIconUri == null && LocalIconPack.current == null
    val bareConsole = consoleColour && !retro.customHolder
    if (!consoleColour && importedUri == null && familyTheme != null && !LocalAppearance.current.highContrast && treatment == dev.senda.launcher.data.IconTreatment.AUTO && customIconUri == null && LocalIconPack.current == null && renderFamilyAppIcon(app, familyTheme, retro.customHolder)) return
    val holderShape = dev.senda.launcher.ui.theme.animatedIconHolderShape(style.iconHolder, style.motionMillis)
    val monochrome = LocalIndustrialStyle.current.monochromeIcons
    val bareNothing = familyTheme == dev.senda.launcher.data.LauncherTheme.NOTHING && !normal && !retro.customHolder
    val context = LocalContext.current
    val appearance = LocalAppearance.current
    val monoInk = if (bareNothing) MaterialTheme.colorScheme.onSurface.toArgb() else android.graphics.Color.rgb(20, 20, 20)
    val pack = if (normal) null else LocalIconPack.current
    val sourceUri = if (normal) null else customIconUri ?: importedUri
    val preserveArtwork = sourceUri != null && !retro.customHolder
    val iconKey = "${app.component.flattenToString()}:${app.revision}:${pack?.pack?.packageName}:${pack?.pack?.revision}:$monochrome:$sourceUri:${if (monochrome) monoInk else 0}"
    val cached = remember(iconKey) { AppIconCache.get(iconKey) }
    val bitmap by key(iconKey, sourceUri) { produceState<ImageBitmap?>(initialValue = cached) {
        value = withContext(Dispatchers.IO) {
            val custom = sourceUri?.takeIf { cached == null }?.let { uri ->
                loadLocalImage(context, uri.toUri(), 128)
            }
            custom?.also { AppIconCache.put(iconKey, it) } ?: cached ?: runCatching {
                val drawable = pack?.drawable(app) ?: onLoadIcon(app)
                val nativeMono = if (monochrome && android.os.Build.VERSION.SDK_INT >= 33 && drawable is android.graphics.drawable.AdaptiveIconDrawable) drawable.monochrome else null
                (nativeMono?.mutate()?.apply { setTint(monoInk) } ?: drawable).toBitmap(width = 128, height = 128).asImageBitmap()
            }
                .getOrNull()?.also { AppIconCache.put(iconKey, it) }
        }
    }
    }
    Box(
        modifier = Modifier.size(appearance.iconSize.dp).animateContentSize(tween(style.motionMillis))
            .background(if (normal || preserveArtwork || bareNothing || bareConsole) androidx.compose.ui.graphics.Color.Transparent else if (monochrome) IndustrialTokens.iconBackground else MaterialTheme.colorScheme.surfaceVariant, holderShape)
            .then(if (retro.enabled && !normal && !monochrome && !preserveArtwork && !bareConsole) Modifier.background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)), holderShape).border(.8.dp, MaterialTheme.colorScheme.outline, holderShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        bitmap?.let { loadedBitmap ->
            androidx.compose.foundation.Image(loadedBitmap, contentDescription = null, modifier = Modifier.size((appearance.iconSize * if (normal || preserveArtwork || bareNothing || bareConsole) 1f else 30f / 42f).dp), contentScale = ContentScale.Fit,
                colorFilter = if (monochrome) androidx.compose.ui.graphics.ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }) else null)
        } ?: Text(app.label.take(1).uppercase(LocalConfiguration.current.locales[0]), color = if (bareNothing) MaterialTheme.colorScheme.onSurface else if (monochrome) IndustrialTokens.iconInk else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}

private object AppIconCache {
    private val bitmaps = object : LruCache<String, ImageBitmap>(6 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = value.width * value.height * 4
    }

    fun get(packageName: String): ImageBitmap? = bitmaps.get(packageName)

    fun put(packageName: String, bitmap: ImageBitmap) {
        bitmaps.put(packageName, bitmap)
    }
}

@Composable
private fun SectionHeading(title: String, count: Int, theme: LauncherTheme) {
    dev.senda.launcher.ui.theme.ThemedSectionHeading(title, count, theme)
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyState(title: String, detail: String, onRefresh: () -> Unit, actionLabel: String = "Actualizar") {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onRefresh) { Text(actionLabel) }
    }
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(message, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer, maxLines = 2, overflow = TextOverflow.Ellipsis)
            TextButton(onClick = onRetry) { Text("Reintentar") }
        }
    }
}








