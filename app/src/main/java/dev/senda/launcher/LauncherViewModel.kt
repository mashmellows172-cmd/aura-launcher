package dev.senda.launcher

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.compose.ui.graphics.asAndroidBitmap
import dev.senda.launcher.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.senda.launcher.data.FavoritesStore
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LauncherAppsRepository
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.VisualSettings
import dev.senda.launcher.data.mergeEditedFavorites
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class HomeUiState(
    val apps: List<LaunchableApp> = emptyList(),
    val favoritePackages: List<String> = emptyList(),
    val hiddenPackages: Set<String> = emptySet(),
    val theme: LauncherTheme = LauncherTheme.NOTHING,
    val skinWallpapers: Map<String, String> = emptyMap(),
    val skinIcons: Map<String, String> = emptyMap(),
    val visualSettings: VisualSettings = VisualSettings(),
    val weatherPackage: String? = null,
    val query: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val homeRequest: Int = 0,
    val appearance: AppearanceSettings = AppearanceSettings(),
    val iconPacks: Map<String, String> = emptyMap(),
    val availableIconPacks: List<IconPack> = emptyList(),
    val iconMapping: IconPackMapping? = null,
    val iconPackStatus: String? = null,
    val iconPackLoading: Boolean = false,
    val backupBusy: Boolean = false,
    val backupMessage: String? = null,
    val restoreSummary: String? = null,
    val headerSettings: HeaderSettings = HeaderSettings(),
    val headerInfo: HeaderInfo = HeaderInfo(),
    val rotation: HomeRotation = HomeRotation.SYSTEM,
    val gestures: GestureBindings = GestureBindings(),
    val lockGesture: LockGesture = LockGesture.BOTH,
    val themeFinishes: Map<String, ThemeFinish> = emptyMap(),
    val themeSaveBusy: Boolean = false,
    val themeSaveError: String? = null,
    val avatarUri: String? = null,
    val avatarBusy: Boolean = false,
    val avatarError: String? = null,
    val dashboard: DashboardSettings = DashboardSettings(),
    val dashboardLoaded: Boolean = false,
    val retroLibrary: Map<String, RetroThemeAssets> = emptyMap(),
    val retroEnabled: Map<String, Boolean> = emptyMap(),
    val retroImportBusy: Boolean = false,
    val retroImportMessage: String? = null,
    val audioInfo: ActiveAudioInfo = ActiveAudioInfo(),
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val appsRepository = LauncherAppsRepository(application)
    private val favoritesStore = FavoritesStore(application)
    private val audioMonitor = AudioPlaybackMonitor(application)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null
    private var iconJob: Job? = null
    private val packRepository = IconPacks(application)
    private val backup = SettingsBackup(application)
    private val retroAssets = RetroAssets(application)
    private var pendingRestore: PreparedBackup? = null
    private var cachedMapping: IconPackMapping? = null
    private val headerRepository = HeaderRepository(application)
    private var headerJob: Job? = null
    private var homeVisible = false
    private var clockRegistered = false
    private var calendarObserved = false
    private val clockReceiver = object : BroadcastReceiver() { override fun onReceive(context: Context?, intent: Intent?) { refreshHeader() } }
    private val calendarObserver = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) { refreshHeader() }
    }
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { refreshApps() }
    }

    init {
        ContextCompat.registerReceiver(application, packageReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }, ContextCompat.RECEIVER_EXPORTED)
        viewModelScope.launch {
            favoritesStore.favorites.collect { packages ->
                _uiState.update { it.copy(favoritePackages = packages) }
            }
        }
        viewModelScope.launch {
            favoritesStore.hiddenPackages.collect { packages ->
                _uiState.update { it.copy(hiddenPackages = packages) }
            }
        }
        viewModelScope.launch {
            favoritesStore.theme.collect { theme ->
                _uiState.update { it.copy(theme = theme) }; refreshIconPacks()
            }
        }
        viewModelScope.launch {
            favoritesStore.skinWallpapers.collect { wallpapers ->
                _uiState.update { it.copy(skinWallpapers = wallpapers) }
            }
        }
        viewModelScope.launch {
            favoritesStore.skinIcons.collect { icons ->
                _uiState.update { it.copy(skinIcons = icons) }
            }
        }
        viewModelScope.launch {
            favoritesStore.visualSettings.collect { settings ->
                _uiState.update { it.copy(visualSettings = settings) }
            }
        }
        viewModelScope.launch {
            favoritesStore.weatherPackage.collect { packageName ->
                _uiState.update { it.copy(weatherPackage = packageName) }
            }
        }
        viewModelScope.launch {
            favoritesStore.appearance.collect { value -> _uiState.update { it.copy(appearance = value) } }
        }
        viewModelScope.launch {
            favoritesStore.iconPacks.collect { value -> _uiState.update { it.copy(iconPacks = value) }; refreshIconPacks() }
        }
        viewModelScope.launch { favoritesStore.headerSettings.collect { settings ->
            _uiState.update { it.copy(headerSettings = settings) }
            if (homeVisible && settings.showAudioWidget) audioMonitor.start() else audioMonitor.stop()
            refreshHeader()
        } }
        viewModelScope.launch { favoritesStore.rotation.collect { rotation -> _uiState.update { it.copy(rotation = rotation) } } }
        viewModelScope.launch { favoritesStore.gestures.collect { gestures -> _uiState.update { it.copy(gestures = gestures) } } }
        viewModelScope.launch { favoritesStore.themeFinishes.collect { finishes -> _uiState.update { it.copy(themeFinishes = finishes) } } }
        viewModelScope.launch { favoritesStore.lockGesture.collect { value -> _uiState.update { it.copy(lockGesture = value) } } }
        viewModelScope.launch { favoritesStore.avatar.collect { uri -> _uiState.update { it.copy(avatarUri = uri) } } }
        viewModelScope.launch { favoritesStore.dashboard.collect { value -> _uiState.update { it.copy(dashboard = value, dashboardLoaded = true) } } }
        viewModelScope.launch { favoritesStore.retroEnabled.collect { value -> _uiState.update { it.copy(retroEnabled = value) } } }
        viewModelScope.launch { val catalog = withContext(Dispatchers.IO) { retroAssets.load() }; _uiState.update { it.copy(retroLibrary = catalog) } }
        viewModelScope.launch { audioMonitor.state.collect { info -> _uiState.update { it.copy(audioInfo = info) } } }
        refreshApps()
    }

    fun refreshApps() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            delay(150) // Coalesce package broadcasts and activity resume into one scan.
            _uiState.update { it.copy(isLoading = it.apps.isEmpty(), errorMessage = null) }
            runCatching { appsRepository.loadApps() }
                .onSuccess { apps ->
                    // Preserve preferences when packages are disabled, updating or unavailable.
                    _uiState.update { it.copy(apps = apps, isLoading = false, errorMessage = null) }; refreshIconPacks()
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "No se pudo leer la lista de apps.",
                        )
                    }
                }
        }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun toggleFavorite(app: LaunchableApp) {
        viewModelScope.launch { favoritesStore.toggle(app.packageName) }
    }

    fun setFavorites(packages: List<String>) {
        val current = _uiState.value
        val editable = current.apps.filterNot { it.packageName in current.hiddenPackages }.mapTo(mutableSetOf()) { it.packageName }
        val merged = mergeEditedFavorites(current.favoritePackages, packages, editable)
        viewModelScope.launch { favoritesStore.setFavorites(merged) }
    }

    fun returnHome() {
        if (!_uiState.value.backupBusy) cancelRestore()
        _uiState.update { it.copy(query = "", homeRequest = it.homeRequest + 1) }
    }

    fun toggleHidden(app: LaunchableApp) {
        viewModelScope.launch { favoritesStore.toggleHidden(app.packageName) }
    }

    fun setTheme(theme: LauncherTheme) {
        viewModelScope.launch { favoritesStore.setTheme(theme) }
    }

    fun setLockGesture(value: LockGesture) { viewModelScope.launch { favoritesStore.setLockGesture(value) } }

    fun setVisualSettings(settings: VisualSettings) {
        viewModelScope.launch { favoritesStore.setVisualSettings(settings) }
    }

    fun setWeatherPackage(packageName: String?) {
        viewModelScope.launch { favoritesStore.setWeatherPackage(packageName) }
    }

    fun setSkinWallpaper(theme: LauncherTheme, uri: String?) {
        viewModelScope.launch { favoritesStore.setSkinWallpaper(theme.name, uri) }
    }

    fun setSkinIconPackage(theme: LauncherTheme, packageName: String, uri: String?) {
        viewModelScope.launch { favoritesStore.setSkinIcon(theme.name, packageName, uri) }
    }

    fun setSkinIcon(theme: LauncherTheme, app: LaunchableApp, uri: String?) {
        viewModelScope.launch { favoritesStore.setSkinIcon(theme.name, app.packageName, uri) }
    }

    fun launch(app: LaunchableApp) {
        runCatching { appsRepository.launch(app) }
            .onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.localizedMessage ?: "No se pudo abrir ${app.label}.")
                }
            }
    }


    fun setAppearance(value: AppearanceSettings) { viewModelScope.launch { favoritesStore.setAppearance(value) } }
    fun setIconPack(packageName: String?) {
        val theme = _uiState.value.theme
        viewModelScope.launch { favoritesStore.setIconPack(theme, packageName) }
    }

    private fun refreshIconPacks() {
        iconJob?.cancel()
        iconJob = viewModelScope.launch {
            _uiState.update { it.copy(iconPackLoading = true) }
            try {
                val available = packRepository.installed()
                val selected = _uiState.value.iconPacks[_uiState.value.theme.name]
                val pack = available.firstOrNull { it.packageName == selected }
                val mapping = if (pack == null) null else cachedMapping?.takeIf { it.pack == pack } ?: packRepository.read(pack).also { cachedMapping = it }
                _uiState.update { it.copy(availableIconPacks = available, iconMapping = mapping, iconPackLoading = false,
                    iconPackStatus = if (selected != null && pack == null) "Paquete seleccionado no disponible; se usan los iconos originales." else null) }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.update { it.copy(iconMapping = null, iconPackLoading = false, iconPackStatus = "No se pudo leer el paquete: ${error.localizedMessage}. Se usan los iconos originales.") }
            }
        }
    }

    fun exportBackup(uri: Uri) = backupOperation {
        backup.export(favoritesStore.snapshot(), uri)
    }

    fun prepareRestore(uri: Uri) = backupOperation {
        val prepared = backup.prepare(uri)
        pendingRestore = prepared
        _uiState.update { it.copy(restoreSummary = prepared.summary) }
        "Respaldo validado. Revisa el resumen antes de restaurar."
    }

    fun cancelRestore() { pendingRestore = null; _uiState.update { it.copy(restoreSummary = null) } }
    fun confirmRestore() {
        val prepared = pendingRestore ?: return
        backupOperation {
            val settings = backup.materialize(prepared)
            favoritesStore.restore(settings)
            cancelRestore()
            "Ajustes restaurados."
        }
    }

    private fun backupOperation(operation: suspend () -> String) {
        if (_uiState.value.backupBusy) return
        _uiState.update { it.copy(backupBusy = true, backupMessage = null) }
        viewModelScope.launch {
            try {
                val message = withContext(Dispatchers.IO) { operation() }
                _uiState.update { it.copy(backupMessage = message) }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.update { it.copy(backupMessage = "No se pudo completar: ${error.localizedMessage ?: "archivo no válido"}") }
            } finally { _uiState.update { it.copy(backupBusy = false) } }
        }
    }


    fun setDashboard(value: DashboardSettings) { viewModelScope.launch { favoritesStore.setDashboard(value) } }
    fun importRetroLibrary(uri: Uri) {
        if (_uiState.value.retroImportBusy) return
        _uiState.update { it.copy(retroImportBusy = true, retroImportMessage = null) }
        viewModelScope.launch {
            try {
                val catalog = withContext(Dispatchers.IO) { retroAssets.importLibrary(uri) }
                _uiState.update { it.copy(retroLibrary = catalog, retroImportMessage = "Biblioteca lista: ${catalog.size} temas y ${catalog.values.sumOf { entry -> entry.wallpapers.size }} fondos. Revisa la vista previa y aplica el tema.") }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.update { it.copy(retroImportMessage = "No se pudo importar: ${error.localizedMessage ?: "biblioteca no válida"}") }
            } finally { _uiState.update { it.copy(retroImportBusy = false) } }
        }
    }
    fun setAvatar(uri: String?) {
        if (_uiState.value.avatarBusy) return
        _uiState.update { it.copy(avatarBusy = true, avatarError = null) }
        viewModelScope.launch {
            var created: java.io.File? = null
            var committed = false
            try {
                val saved = if (uri == null) null else withContext(Dispatchers.IO) {
                    val app = getApplication<Application>()
                    val image = loadLocalImage(app, uri.toUri(), 512) ?: error("No se pudo leer la imagen")
                    val dir = java.io.File(app.filesDir, "avatars").apply { mkdirs() }
                    val file = java.io.File(dir, "${java.util.UUID.randomUUID()}.png"); created = file
                    file.outputStream().use { check(image.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)) }
                    Uri.fromFile(file).toString()
                }
                favoritesStore.setAvatar(saved); committed = true
                val referenced = favoritesStore.snapshot().values.filterIsInstance<String>().toSet()
                withContext(Dispatchers.IO) { java.io.File(getApplication<Application>().filesDir,"avatars").listFiles()?.filter { Uri.fromFile(it).toString() !in referenced }?.forEach { it.delete() } }
            } catch (error: Exception) {
                withContext(Dispatchers.IO) { if (!committed) created?.delete() }
                if (error is CancellationException) throw error
                _uiState.update { it.copy(avatarError = error.localizedMessage ?: "No se pudo guardar el avatar") }
            } finally { _uiState.update { it.copy(avatarBusy = false) } }
        }
    }

    fun setHeaderSettings(settings: HeaderSettings) { viewModelScope.launch { favoritesStore.setHeaderSettings(settings) } }
    fun setRotation(rotation: HomeRotation) { viewModelScope.launch { favoritesStore.setRotation(rotation) } }
    fun setGesture(direction: GestureDirection, action: String) { viewModelScope.launch { favoritesStore.setGesture(direction, action) } }

    fun setHomeVisible(visible: Boolean) {
        homeVisible = visible
        if (visible && _uiState.value.headerSettings.showAudioWidget) audioMonitor.start() else audioMonitor.stop()
        val app = getApplication<Application>()
        if (visible && !clockRegistered) {
            ContextCompat.registerReceiver(app, clockReceiver, IntentFilter().apply {
                addAction(android.app.AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED)
                addAction(Intent.ACTION_TIME_CHANGED); addAction(Intent.ACTION_TIMEZONE_CHANGED); addAction(Intent.ACTION_DATE_CHANGED)
            }, ContextCompat.RECEIVER_NOT_EXPORTED)
            clockRegistered = true
        } else if (!visible && clockRegistered) { app.unregisterReceiver(clockReceiver); clockRegistered = false }
        if (visible) refreshHeader() else {
            headerJob?.cancel()
            if (calendarObserved) { app.contentResolver.unregisterContentObserver(calendarObserver); calendarObserved = false }
            _uiState.update { it.copy(headerInfo = it.headerInfo.copy(event = null, calendarLoading = false)) }
        }
    }

    fun refreshHeader() {
        if (!homeVisible) return
        val app = getApplication<Application>()
        val observe = _uiState.value.headerSettings.showCalendar && headerRepository.canReadCalendar()
        if (observe && !calendarObserved) {
            try { app.contentResolver.registerContentObserver(android.provider.CalendarContract.CONTENT_URI, true, calendarObserver); calendarObserved = true }
            catch (_: SecurityException) { calendarObserved = false }
        } else if (!observe && calendarObserved) { app.contentResolver.unregisterContentObserver(calendarObserver); calendarObserved = false }
        headerJob?.cancel()
        _uiState.update { it.copy(headerInfo = it.headerInfo.copy(calendarPermission = headerRepository.canReadCalendar(), calendarLoading = observe)) }
        headerJob = viewModelScope.launch {
            delay(150)
            val info = headerRepository.load(_uiState.value.headerSettings)
            _uiState.update { it.copy(headerInfo = info) }
        }
    }

    suspend fun previewIconPack(packageName: String?): IconPackMapping? {
        val pack = _uiState.value.availableIconPacks.firstOrNull { it.packageName == packageName } ?: return null
        return try { cachedMapping?.takeIf { it.pack == pack } ?: packRepository.read(pack) }
        catch (error: Exception) { if (error is CancellationException) throw error; null }
    }

    suspend fun applyTheme(theme: LauncherTheme, finish: ThemeFinish, pack: String?, wallpaper: String?, retroEnabled: Boolean = false): Boolean {
        if (_uiState.value.themeSaveBusy) return false
        _uiState.update { it.copy(themeSaveBusy = true, themeSaveError = null) }
        return try {
            val app = getApplication<Application>()
            val previous = _uiState.value
            withContext(Dispatchers.IO) {
                if (pack != null) {
                    val candidate = previous.availableIconPacks.firstOrNull { it.packageName == pack }
                    if (candidate != null) packRepository.read(candidate)
                    else require(previous.iconPacks[theme.name] == pack) { "El paquete ya no está instalado" }
                }
            }
            if (wallpaper == null || wallpaper == previous.skinWallpapers[theme.name]) {
                favoritesStore.applyTheme(theme, finish, pack, wallpaper, retroEnabled)
            } else {
                dev.senda.launcher.data.persistLocalResource(java.io.File(app.filesDir, "theme-wallpapers"), ".png", { file ->
                    val bitmap = loadLocalImage(app, Uri.parse(wallpaper), 1920) ?: error("No se pudo leer el fondo elegido")
                    file.outputStream().use { output -> check(bitmap.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output)) }
                }, { file -> favoritesStore.applyTheme(theme, finish, pack, Uri.fromFile(file).toString(), retroEnabled) })
            }
            val referenced = favoritesStore.snapshot().values.filterIsInstance<String>().toSet()
            withContext(Dispatchers.IO) {
                java.io.File(app.filesDir, "theme-wallpapers").listFiles()?.filter { Uri.fromFile(it).toString() !in referenced }?.forEach { it.delete() }
            }
            true
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            _uiState.update { it.copy(themeSaveError = "No se pudo aplicar el tema: ${error.localizedMessage}") }; false
        } finally { _uiState.update { it.copy(themeSaveBusy = false) } }
    }

    fun clearThemeError() { _uiState.update { it.copy(themeSaveError = null) } }

    fun icon(app: LaunchableApp) = appsRepository.appIcon(app)
    fun openAudioApp() { audioMonitor.openAudioApp() }
    fun pauseAudio(sessionId: String?) = audioMonitor.pause(sessionId)
    fun previousAudio(sessionId: String?) = audioMonitor.previous(sessionId)
    fun nextAudio(sessionId: String?) = audioMonitor.next(sessionId)

    override fun onCleared() {
        setHomeVisible(false)
        audioMonitor.close()
        getApplication<Application>().unregisterReceiver(packageReceiver)
    }
}


