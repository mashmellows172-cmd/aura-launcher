package dev.senda.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.io.IOException

private val Context.settingsStore by preferencesDataStore(name = "senda_settings")

fun canonicalMotion(value: String?): String = when(value) {
    "CASCADE" -> "CASCADE"; "TILT" -> "TILT"
    "NONE" -> "NONE"; "FOLD", "CUBE" -> "FOLD"; "WAVE", "RIPPLE", "ZOOM" -> "WAVE"
    "ORBIT", "WOBBLE", "ELASTIC" -> "ORBIT"; "GLIDE", "FADE", "PARALLAX" -> "GLIDE"
    else -> "DEPTH"
}

data class VisualSettings(
    val motion: String = "DEPTH",
    val durationMs: Int = 360,
    val scrollIntensity: Int = 55,
    val glowEnabled: Boolean = true,
    val glowStrength: Int = 65,
    val reduceMotion: Boolean = false,
    val railEffect: String = "MAGNET",
    val menuEffect: String = "SPRING",
    val railHaptics: Boolean = false,
    val railIntensity: Int = 65,
    val railDurationMs: Int = 220,
    val railBubbleSize: Int = 56,
    val railBubbleHoldMs: Int = 420,
)

/** Stores the user's favorites and appearance choice locally. */
class FavoritesStore(context: Context) {
    private val store = context.applicationContext.settingsStore
    private val favoritesKey = stringPreferencesKey("favorite_packages")
    private val hiddenPackagesKey = stringPreferencesKey("hidden_packages")
    private val themeKey = stringPreferencesKey("launcher_theme")
    private val wallpaperPrefix = "skin_wallpaper_"
    private val motionKey = stringPreferencesKey("visual_motion")
    private val durationKey = intPreferencesKey("visual_duration_ms")
    private val scrollIntensityKey = intPreferencesKey("scroll_motion_intensity")
    private val glowKey = booleanPreferencesKey("search_glow_enabled")
    private val glowStrengthKey = intPreferencesKey("search_glow_strength")
    private val reduceMotionKey = booleanPreferencesKey("reduce_motion")
    private val railIntensityKey = intPreferencesKey("rail_intensity")
    private val railDurationKey = intPreferencesKey("rail_duration_ms")
    private val railBubbleSizeKey = intPreferencesKey("rail_bubble_size")
    private val railBubbleHoldKey = intPreferencesKey("rail_bubble_hold_ms")
    private val weatherPackageKey = stringPreferencesKey("weather_app_package")

    val retroEnabled: Flow<Map<String, Boolean>> = store.data.map { prefs ->
        RetroAssets.themes.associateWith { prefs[booleanPreferencesKey("retro_assets_$it")] ?: false }
    }

    val avatar: Flow<String?> = store.data.map { it[stringPreferencesKey("profile_avatar")] }
    suspend fun setAvatar(value: String?) { store.edit { prefs -> val key = stringPreferencesKey("profile_avatar"); if (value == null) prefs.remove(key) else prefs[key] = value } }
    val dashboard: Flow<DashboardSettings> = store.data.map { prefs ->
        val order = prefs[stringPreferencesKey("block_order")]?.split(',')?.mapNotNull { name -> HomeBlock.entries.firstOrNull { it.name == name } }?.distinct()?.takeIf { it.size == HomeBlock.entries.size } ?: HomeBlock.entries.toList()
        DashboardSettings(order, HomeBlock.entries.associateWith { block ->
            val fallback = DashboardSettings()[block]
            BlockStyle(BlockAnchor.entries.firstOrNull { it.name == prefs[stringPreferencesKey("block_anchor_${block.name}")] } ?: fallback.anchor,
                (prefs[intPreferencesKey("block_height_${block.name}")] ?: 48).coerceIn(48,160),
                (prefs[intPreferencesKey("block_text_${block.name}")] ?: 14).coerceIn(12,20), prefs[booleanPreferencesKey("block_half_${block.name}")] ?: false)
        })
    }
    suspend fun setDashboard(value: DashboardSettings) { store.edit { prefs ->
        prefs[stringPreferencesKey("block_order")] = value.order.joinToString(",") { it.name }
        HomeBlock.entries.forEach { block -> val style = value[block]
            prefs[stringPreferencesKey("block_anchor_${block.name}")] = style.anchor.name
            prefs[intPreferencesKey("block_height_${block.name}")] = style.height.coerceIn(48,160)
            prefs[intPreferencesKey("block_text_${block.name}")] = style.textSize.coerceIn(12,20)
            prefs[booleanPreferencesKey("block_half_${block.name}")] = style.halfWidth
        }
    } }

    val lockGesture: Flow<LockGesture> = store.data.map { prefs -> LockGesture.entries.firstOrNull { it.name == prefs[stringPreferencesKey("double_tap_lock")] } ?: LockGesture.BOTH }
    suspend fun setLockGesture(value: LockGesture) { store.edit { it[stringPreferencesKey("double_tap_lock")] = value.name } }
    val headerSettings: Flow<HeaderSettings> = store.data.map { prefs -> HeaderSettings(
        showAlarm = prefs[booleanPreferencesKey("header_show_alarm")] ?: true,
        showCalendar = prefs[booleanPreferencesKey("header_show_calendar")] ?: false,
        weatherMotion = WeatherMotionSettings(
            WeatherMotionMode.fromStored(prefs[stringPreferencesKey("weather_motion_mode")]),
            prefs[intPreferencesKey("weather_motion_duration")] ?: 8000,
            prefs[intPreferencesKey("weather_motion_intensity")] ?: 60,
            WeatherCondition.fromStored(prefs[stringPreferencesKey("weather_scene_condition")])
        ).bounded(),
        showMotivationalHeader = prefs[booleanPreferencesKey("header_show_motivational")] ?: true,
        showAudioWidget = prefs[booleanPreferencesKey("header_show_audio_widget")] ?: true
    ) }
    val rotation: Flow<HomeRotation> = store.data.map { prefs -> HomeRotation.entries.firstOrNull { it.name == prefs[stringPreferencesKey("home_rotation")] } ?: HomeRotation.SYSTEM }
    val gestures: Flow<GestureBindings> = store.data.map { prefs ->
        fun action(key: String, fallback: String) = prefs[stringPreferencesKey(key)]?.takeIf(GestureBindings::valid) ?: fallback
        GestureBindings(action("gesture_up", "SEARCH"), action("gesture_down", "NONE"), action("gesture_left", "NONE"), action("gesture_right", "NONE"))
    }
    val themeFinishes: Flow<Map<String, ThemeFinish>> = store.data.map { prefs -> LauncherTheme.entries.associate { theme ->
        theme.name to ThemeFinish(AccentChoice.entries.firstOrNull { it.name == prefs[stringPreferencesKey("skin_accent_${theme.name}")] } ?: AccentChoice.DEFAULT,
            ShapeChoice.entries.firstOrNull { it.name == prefs[stringPreferencesKey("skin_shape_${theme.name}")] } ?: ShapeChoice.DEFAULT,
            IconTreatment.entries.firstOrNull { it.name == prefs[stringPreferencesKey("skin_icons_${theme.name}")] } ?: IconTreatment.AUTO,
            (prefs[intPreferencesKey("skin_dim_${theme.name}")] ?: 64).coerceIn(60, 90))
    } }
    suspend fun setHeaderSettings(value: HeaderSettings) { store.edit {
        it[booleanPreferencesKey("header_show_alarm")] = value.showAlarm
        it[booleanPreferencesKey("header_show_calendar")] = value.showCalendar
        it[booleanPreferencesKey("header_show_motivational")] = value.showMotivationalHeader
        it[booleanPreferencesKey("header_show_audio_widget")] = value.showAudioWidget
        val motion = value.weatherMotion.bounded()
        it[stringPreferencesKey("weather_motion_mode")] = motion.mode.name
        it[stringPreferencesKey("weather_scene_condition")] = motion.condition.name
        it[intPreferencesKey("weather_motion_duration")] = motion.durationMs
        it[intPreferencesKey("weather_motion_intensity")] = motion.intensity
    } }
    suspend fun setRotation(value: HomeRotation) { store.edit { it[stringPreferencesKey("home_rotation")] = value.name } }
    suspend fun setGesture(direction: GestureDirection, action: String) {
        require(GestureBindings.valid(action))
        store.edit { it[stringPreferencesKey("gesture_${direction.name.lowercase(java.util.Locale.ROOT)}")] = action }
    }
    suspend fun applyTheme(theme: LauncherTheme, finish: ThemeFinish, iconPack: String?, wallpaper: String?, retroEnabled: Boolean = false) {
        store.edit { prefs ->
            prefs[themeKey] = theme.name
            prefs[stringPreferencesKey("skin_accent_${theme.name}")] = finish.accent.name
            prefs[stringPreferencesKey("skin_shape_${theme.name}")] = finish.shape.name
            prefs[stringPreferencesKey("skin_icons_${theme.name}")] = finish.icons.name
            prefs[intPreferencesKey("skin_dim_${theme.name}")] = finish.wallpaperDim.coerceIn(60, 90)
            if (theme.name in RetroAssets.themes) prefs[booleanPreferencesKey("retro_assets_${theme.name}")] = retroEnabled
            val packKey = stringPreferencesKey("icon_pack_${theme.name}")
            if (iconPack == null) prefs.remove(packKey) else prefs[packKey] = iconPack
            val wallpaperKey = stringPreferencesKey("skin_wallpaper_${theme.name}")
            if (wallpaper == null) prefs.remove(wallpaperKey) else prefs[wallpaperKey] = wallpaper
        }
    }

    val appearance: Flow<AppearanceSettings> = store.data.map { prefs -> AppearanceSettings(
        prefs[intPreferencesKey("appearance_text_size")] ?: 16,
        prefs[intPreferencesKey("appearance_icon_size")] ?: 42,
        prefs[intPreferencesKey("appearance_row_spacing")] ?: 0,
        prefs[booleanPreferencesKey("appearance_high_contrast")] ?: false,
    ).bounded() }

    val iconPacks: Flow<Map<String, String>> = store.data.map { prefs ->
        prefs.asMap().mapNotNull { (key, value) -> if (key.name.startsWith("icon_pack_") && value is String) key.name.removePrefix("icon_pack_") to value else null }.toMap()
    }

    suspend fun setAppearance(value: AppearanceSettings) {
        val settings = value.bounded()
        store.edit {
            it[intPreferencesKey("appearance_text_size")] = settings.textSize
            it[intPreferencesKey("appearance_icon_size")] = settings.iconSize
            it[intPreferencesKey("appearance_row_spacing")] = settings.rowSpacing
            it[booleanPreferencesKey("appearance_high_contrast")] = settings.highContrast
        }
    }

    suspend fun setIconPack(theme: LauncherTheme, packageName: String?) {
        store.edit { prefs ->
            val key = stringPreferencesKey("icon_pack_${theme.name}")
            if (packageName == null) prefs.remove(key) else prefs[key] = packageName
        }
    }

    suspend fun snapshot(): Map<String, Any> {
        val settings = store.data.first().asMap().entries.filter { SettingsBackup.allowed(it.key.name) }
            .associate { it.key.name to it.value }.toMutableMap()
        // Export canonical values even when an older beta stored a legacy motion name.
        settings["visual_motion"] = visualSettings.first().motion
        return settings
    }

    /** Single DataStore transaction: readers never observe a half-restored configuration. */
    suspend fun restore(settings: Map<String, Any>) {
        store.edit { prefs ->
            prefs.clear()
            settings.forEach { (key, value) -> when (value) {
                is String -> prefs[stringPreferencesKey(key)] = value
                is Int -> prefs[intPreferencesKey(key)] = value
                is Boolean -> prefs[booleanPreferencesKey(key)] = value
            } }
        }
    }

    val weatherPackage: Flow<String?> = store.data
        .catch { cause -> if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause }
        .map { it[weatherPackageKey] }

    suspend fun setWeatherPackage(packageName: String?) {
        store.edit { prefs -> if (packageName == null) prefs.remove(weatherPackageKey) else prefs[weatherPackageKey] = packageName }
    }

    val soundSettings: Flow<SoundSettings> = store.data
        .catch { cause -> if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause }
        .map { prefs -> SoundSettings(
            enabled = prefs[booleanPreferencesKey("sound_enabled")] ?: false,
            volume = (prefs[intPreferencesKey("sound_volume")] ?: 35).coerceIn(0, SoundSettings.MAX_VOLUME),
            family = SoundFamily.fromStored(prefs[stringPreferencesKey("sound_family")]),
            launch = prefs[booleanPreferencesKey("sound_launch")] ?: true,
            menu = prefs[booleanPreferencesKey("sound_menu")] ?: true,
            select = prefs[booleanPreferencesKey("sound_select")] ?: true,
            favorite = prefs[booleanPreferencesKey("sound_favorite")] ?: true,
            index = prefs[booleanPreferencesKey("sound_index")] ?: false,
            muteDuringMedia = prefs[booleanPreferencesKey("sound_mute_media")] ?: true,
        ) }.distinctUntilChanged()

    suspend fun setSoundSettings(value: SoundSettings) { val settings = value.bounded(); store.edit { prefs ->
        prefs[booleanPreferencesKey("sound_enabled")] = settings.enabled
        prefs[intPreferencesKey("sound_volume")] = settings.volume
        prefs[stringPreferencesKey("sound_family")] = settings.family.name
        prefs[booleanPreferencesKey("sound_launch")] = settings.launch
        prefs[booleanPreferencesKey("sound_menu")] = settings.menu
        prefs[booleanPreferencesKey("sound_select")] = settings.select
        prefs[booleanPreferencesKey("sound_favorite")] = settings.favorite
        prefs[booleanPreferencesKey("sound_index")] = settings.index
        prefs[booleanPreferencesKey("sound_mute_media")] = settings.muteDuringMedia
    } }

    val visualSettings: Flow<VisualSettings> = store.data
        .catch { cause -> if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause }
        .map { prefs -> VisualSettings(
            motion = canonicalMotion(prefs[motionKey]),
            railEffect = prefs[stringPreferencesKey("rail_effect")]?.takeIf { it in RailEffects.ids } ?: "MAGNET",
            menuEffect = prefs[stringPreferencesKey("menu_effect")]?.takeIf { it in setOf("SPRING", "SLIDE", "FADE", "NONE") } ?: "SPRING",
            railHaptics = prefs[booleanPreferencesKey("rail_haptics")] ?: false,
            durationMs = (prefs[durationKey] ?: 360).coerceIn(150, 800),
            scrollIntensity = (prefs[scrollIntensityKey] ?: 55).coerceIn(10, 100),
            glowEnabled = prefs[glowKey] ?: true,
            glowStrength = (prefs[glowStrengthKey] ?: 65).coerceIn(10, 100),
            reduceMotion = prefs[reduceMotionKey] ?: false,
            railIntensity = (prefs[railIntensityKey] ?: 65).coerceIn(0, 100),
            railDurationMs = (prefs[railDurationKey] ?: 220).coerceIn(80, 600),
            railBubbleSize = (prefs[railBubbleSizeKey] ?: 56).coerceIn(40, 84),
            railBubbleHoldMs = (prefs[railBubbleHoldKey] ?: 420).coerceIn(0, 1000),
        ) }

    suspend fun setVisualSettings(settings: VisualSettings) {
        store.edit { prefs ->
            prefs[motionKey] = canonicalMotion(settings.motion)
            prefs[stringPreferencesKey("rail_effect")] = RailEffects.canonical(settings.railEffect)
            prefs[stringPreferencesKey("menu_effect")] = settings.menuEffect
            prefs[booleanPreferencesKey("rail_haptics")] = settings.railHaptics
            prefs[durationKey] = settings.durationMs.coerceIn(150, 800)
            prefs[scrollIntensityKey] = settings.scrollIntensity.coerceIn(10, 100)
            prefs[glowKey] = settings.glowEnabled
            prefs[glowStrengthKey] = settings.glowStrength.coerceIn(10, 100)
            prefs[reduceMotionKey] = settings.reduceMotion
            prefs[railIntensityKey] = settings.railIntensity.coerceIn(0, 100)
            prefs[railDurationKey] = settings.railDurationMs.coerceIn(80, 600)
            prefs[railBubbleSizeKey] = settings.railBubbleSize.coerceIn(40, 84)
            prefs[railBubbleHoldKey] = settings.railBubbleHoldMs.coerceIn(0, 1000)
        }
    }

    val skinWallpapers: Flow<Map<String, String>> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { preferences ->
            preferences.asMap().mapNotNull { (key, value) ->
                val skin = key.name.removePrefix(wallpaperPrefix)
                if (key.name.startsWith(wallpaperPrefix) && value is String) skin to value else null
            }.toMap()
        }

    val skinIcons: Flow<Map<String, String>> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { preferences ->
            preferences.asMap().mapNotNull { (key, value) ->
                val identity = key.name.removePrefix("skin_icon_")
                if (key.name.startsWith("skin_icon_") && value is String) identity to value else null
            }.toMap()
        }

    val favorites: Flow<List<String>> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { preferences ->
            preferences[favoritesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
        }

    val hiddenPackages: Flow<Set<String>> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { preferences ->
            preferences[hiddenPackagesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
                .toSet()
        }

    val theme: Flow<LauncherTheme> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { preferences -> LauncherTheme.fromStoredValue(preferences[themeKey]) }

    suspend fun setTheme(theme: LauncherTheme) {
        store.edit { preferences -> preferences[themeKey] = theme.name }
    }

    suspend fun setSkinWallpaper(skinName: String, uri: String?) {
        val key = stringPreferencesKey("$wallpaperPrefix$skinName")
        store.edit { preferences -> if (uri == null) preferences.remove(key) else preferences[key] = uri }
    }

    suspend fun setSkinIcon(skinName: String, packageName: String, uri: String?) {
        val key = stringPreferencesKey("skin_icon_${skinName}_$packageName")
        store.edit { preferences -> if (uri == null) preferences.remove(key) else preferences[key] = uri }
    }

    suspend fun toggle(packageName: String) {
        store.edit { preferences ->
            val current = preferences[favoritesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
                .toMutableList()
            if (packageName in current) current.remove(packageName) else current.add(packageName)
            preferences[favoritesKey] = current.joinToString("\n")
        }
    }

    suspend fun setFavorites(packageNames: List<String>) {
        store.edit { preferences ->
            preferences[favoritesKey] = packageNames.distinct().joinToString("\n")
        }
    }

    suspend fun toggleHidden(packageName: String) {
        store.edit { preferences ->
            val hidden = preferences[hiddenPackagesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
                .toMutableSet()
            if (!hidden.add(packageName)) hidden.remove(packageName)
            preferences[hiddenPackagesKey] = hidden.joinToString("\n")
        }
    }

    suspend fun removeMissing(installedPackages: Set<String>) {
        store.edit { preferences ->
            val current = preferences[favoritesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
            val kept = current.filter(installedPackages::contains)
            if (kept != current) preferences[favoritesKey] = kept.joinToString("\n")

            val hidden = preferences[hiddenPackagesKey]
                .orEmpty()
                .split('\n')
                .filter(String::isNotBlank)
            val visiblePackages = hidden.filter(installedPackages::contains)
            if (visiblePackages != hidden) preferences[hiddenPackagesKey] = visiblePackages.joinToString("\n")
        }
    }
}
