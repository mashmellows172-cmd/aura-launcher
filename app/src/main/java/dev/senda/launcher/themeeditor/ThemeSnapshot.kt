package dev.senda.launcher.themeeditor

import dev.senda.launcher.HomeUiState
import dev.senda.launcher.data.*
import org.json.JSONObject

/** Small immutable memento: resources are references, never pixels. */
data class ThemeSnapshot(
    val theme: String, val revision: Int = 1,
    val accent: String = "DEFAULT", val shape: String = "DEFAULT", val icons: String = "AUTO",
    val pack: String? = null, val wallpaper: String? = null, val retro: Boolean = false,
    val dim: Int = 64, val iconSize: Int = 42, val textSize: Int = 16, val spacing: Int = 0,
    val font: String = "THEME",
    val ps4Palette: String = "CLASSIC", val sound: SoundSettings? = null,
) {
    fun validated(): ThemeSnapshot {
        Ps4Palette.valueOf(ps4Palette); require(sound == null || sound.volume in 0..SoundSettings.MAX_VOLUME)
        LauncherTheme.valueOf(theme); AccentChoice.valueOf(accent); ShapeChoice.valueOf(shape); IconTreatment.valueOf(icons)
        require(revision == 1 && dim in 60..90 && iconSize in 32..60 && textSize in 14..24 && spacing in 0..20)
        require(font in fontChoices && (pack == null || pack.length <= 200))
        require(wallpaper == null || wallpaper.length <= 4096)
        return this
    }
    fun finish() = ThemeFinish(AccentChoice.valueOf(accent), ShapeChoice.valueOf(shape), IconTreatment.valueOf(icons), dim, Ps4Palette.valueOf(ps4Palette))
    fun appearance(highContrast: Boolean) = AppearanceSettings(textSize, iconSize, spacing, highContrast)
    fun json(): String = JSONObject().put("schema",1).put("theme",theme).put("revision",revision)
        .put("accent",accent).put("shape",shape).put("icons",icons).put("pack",pack ?: JSONObject.NULL)
        .put("wallpaper",wallpaper ?: JSONObject.NULL).put("retro",retro).put("dim",dim)
        .put("iconSize",iconSize).put("textSize",textSize).put("spacing",spacing).put("font",font).put("ps4Palette",ps4Palette).put("sound",sound?.let { soundJson(it) } ?: JSONObject.NULL).toString()
    fun applyTo(state: HomeUiState): HomeUiState = state.copy(
        soundSettings = sound ?: state.soundSettings,
        theme = LauncherTheme.valueOf(theme), appearance = appearance(state.appearance.highContrast),
        themeFinishes = state.themeFinishes + (theme to finish()),
        skinWallpapers = if (wallpaper == null) state.skinWallpapers - theme else state.skinWallpapers + (theme to wallpaper),
        iconPacks = if (pack == null) state.iconPacks - theme else state.iconPacks + (theme to pack),
        retroEnabled = state.retroEnabled + (theme to retro), fontChoice = font,
    )
    companion object {
        val fontChoices = listOf("THEME", "NOTO", "MONO", "CONDENSED")
        private fun soundJson(s: SoundSettings) = JSONObject().put("enabled",s.enabled).put("volume",s.volume).put("family",s.family.name).put("launch",s.launch).put("menu",s.menu).put("select",s.select).put("favorite",s.favorite).put("index",s.index).put("muteDuringMedia",s.muteDuringMedia)
        private fun parseSound(j: JSONObject) = SoundSettings(j.getBoolean("enabled"),j.getInt("volume"),SoundFamily.valueOf(j.getString("family")),j.getBoolean("launch"),j.getBoolean("menu"),j.getBoolean("select"),j.getBoolean("favorite"),j.getBoolean("index"),j.getBoolean("muteDuringMedia"))
        fun from(state: HomeUiState, theme: LauncherTheme = state.theme): ThemeSnapshot {
            val f=state.themeFinishes[theme.name] ?: ThemeFinish()
            return ThemeSnapshot(theme.name, accent=f.accent.name,shape=f.shape.name,icons=f.icons.name,
                pack=state.iconPacks[theme.name],wallpaper=state.skinWallpapers[theme.name],retro=state.retroEnabled[theme.name]==true,
                dim=f.wallpaperDim,iconSize=state.appearance.iconSize,textSize=state.appearance.textSize,spacing=state.appearance.rowSpacing,font=if(theme==state.theme)state.fontChoice else "THEME",ps4Palette=f.ps4Palette.name,sound=state.soundSettings)
        }
        fun parse(text: String): ThemeSnapshot {
            require(text.length <= 16384); val j=JSONObject(text);require(j.getInt("schema")==1)
            fun nullable(key:String)=if(j.isNull(key))null else j.getString(key)
            return ThemeSnapshot(j.getString("theme"),j.getInt("revision"),j.getString("accent"),j.getString("shape"),j.getString("icons"),nullable("pack"),nullable("wallpaper"),j.getBoolean("retro"),j.getInt("dim"),j.getInt("iconSize"),j.getInt("textSize"),j.getInt("spacing"),j.getString("font"),ps4Palette=j.optString("ps4Palette","CLASSIC"),sound=j.optJSONObject("sound")?.let(::parseSound)).validated()
        }
    }
}

class ThemeHistory(initial: ThemeSnapshot) {
    var present = initial; private set
    private val past = ArrayDeque<ThemeSnapshot>()
    private val future = ArrayDeque<ThemeSnapshot>()
    val canUndo get() = past.isNotEmpty()
    val canRedo get() = future.isNotEmpty()
    fun commit(next: ThemeSnapshot) {
        next.validated(); if(next==present)return
        if(past.size==50)past.removeFirst()
        past.addLast(present);present=next;future.clear()
    }
    fun undo(): ThemeSnapshot { if(canUndo){future.addLast(present);present=past.removeLast()};return present }
    fun redo(): ThemeSnapshot { if(canRedo){past.addLast(present);present=future.removeLast()};return present }
}
