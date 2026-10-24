package dev.senda.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import dev.senda.launcher.data.IconTreatment
import dev.senda.launcher.data.ThemeFinish
import dev.senda.launcher.data.ShapeChoice
import dev.senda.launcher.data.LauncherTheme

enum class IconHolder { SQUIRCLE, CIRCLE, ROUNDED, BEVELED, PILL }
enum class WallpaperPattern { DOTS, ORBS, DIAGONAL, WATER_RIPPLE, WAVE, HORIZON, SENSE_LANDSCAPE, NATURE, OPTIMUS_RIBBONS, XPERIA_FLOW, RAZR_METAL, NONE }

data class SkinStyle(
    val accent: Color,
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val pattern: WallpaperPattern,
    val iconHolder: IconHolder,
    val motionMillis: Int,
)

val LocalSkinStyle = staticCompositionLocalOf {
    SkinStyle(Color(0xFFFF334F), Color.Black, Color(0xFF10050A), WallpaperPattern.DOTS, IconHolder.SQUIRCLE, 280)
}

private val Ink = Color(0xFF20231F)
private val Muted = Color(0xFF5F655F)
private val NightText = Color(0xFFF4F5F2)
private val NightMuted = Color(0xFFB7BDB7)

private fun light(primary: Color, bg: Color, surface: Color, ink: Color = Ink) = lightColorScheme(
    primary = primary, onPrimary = Color.White, secondary = primary, onSecondary = Color.White,
    background = bg, onBackground = ink, surface = surface, onSurface = ink,
    surfaceVariant = surface.copy(red = (surface.red * .94f), green = (surface.green * .94f), blue = (surface.blue * .94f)),
    onSurfaceVariant = Muted, outline = Muted, error = Color(0xFFB3261E),
)

private fun dark(primary: Color, bg: Color, surface: Color, variant: Color, onPrimary: Color = Color(0xFF241014)) = darkColorScheme(
    primary = primary, onPrimary = onPrimary, secondary = primary, onSecondary = onPrimary,
    background = bg, onBackground = NightText, surface = surface, onSurface = NightText,
    surfaceVariant = variant, onSurfaceVariant = NightMuted, outline = Color(0xFF929C97), error = Color(0xFFFFB4AB),
)

internal fun skin(theme: LauncherTheme, systemIsDark: Boolean): Pair<SkinStyle, androidx.compose.material3.ColorScheme> = when (theme) {
    LauncherTheme.NOTHING -> SkinStyle(IndustrialTokens.signal, Color.Black, Color.Black, WallpaperPattern.NONE, IconHolder.CIRCLE, 240) to dark(IndustrialTokens.text, Color.Black, IndustrialTokens.surface, IndustrialTokens.raised).copy(onBackground = IndustrialTokens.text, onSurface = IndustrialTokens.text, onSurfaceVariant = IndustrialTokens.muted, outline = IndustrialTokens.outline)
    LauncherTheme.SENSE -> SkinStyle(Color(0xFFB6D88C), Color(0xFF303B39), Color(0xFF10191A), WallpaperPattern.SENSE_LANDSCAPE, IconHolder.ROUNDED, 340) to dark(Color(0xFFB6D88C), Color(0xFF10191A), Color(0xFF202A29), Color(0xFF33403D))
    LauncherTheme.TOUCHWIZ -> SkinStyle(Color(0xFF84D2FF), Color(0xFF071A38), Color(0xFF10334A), WallpaperPattern.NATURE, IconHolder.SQUIRCLE, 260) to dark(Color(0xFF84D2FF), Color(0xFF10334A), Color(0xFF10263C), Color(0xFF203E55))
    LauncherTheme.OPTIMUS -> SkinStyle(Color(0xFFFFCC96), Color(0xFF3D322A), Color(0xFF1D1A18), WallpaperPattern.OPTIMUS_RIBBONS, IconHolder.ROUNDED, 300) to dark(Color(0xFFFFCC96), Color(0xFF1D1A18), Color(0xFF302820), Color(0xFF473D31))
    LauncherTheme.XPERIA -> SkinStyle(Color(0xFFC0BCFF), Color(0xFF10162E), Color(0xFF241239), WallpaperPattern.XPERIA_FLOW, IconHolder.BEVELED, 240) to dark(Color(0xFFC0BCFF), Color(0xFF241239), Color(0xFF1C1E34), Color(0xFF34304A))
    LauncherTheme.RAZR -> SkinStyle(Color(0xFF9DD9FF), Color(0xFF303B47), Color(0xFF0D141D), WallpaperPattern.RAZR_METAL, IconHolder.BEVELED, 180) to dark(Color(0xFF9DD9FF), Color(0xFF0D141D), Color(0xFF1D2936), Color(0xFF304052)).copy(outline=Color(0xFFB1C0CC))
    LauncherTheme.AMOLED -> SkinStyle(Color(0xFFA6D1B5), Color.Black, Color.Black, WallpaperPattern.NONE, IconHolder.ROUNDED, 220) to dark(Color(0xFFA6D1B5), Color.Black, Color.Black, Color(0xFF141414), Color(0xFF173427))
    LauncherTheme.DARK -> SkinStyle(Color(0xFFA6D1B5), Color(0xFF171A17), Color(0xFF202420), WallpaperPattern.NONE, IconHolder.ROUNDED, 260) to dark(Color(0xFFA6D1B5), Color(0xFF171A17), Color(0xFF202420), Color(0xFF2B302B), Color(0xFF173427))
    LauncherTheme.LIGHT -> SkinStyle(Color(0xFF426B58), Color(0xFFF4F2EC), Color(0xFFECE9DF), WallpaperPattern.NONE, IconHolder.ROUNDED, 260) to light(Color(0xFF426B58), Color(0xFFF4F2EC), Color(0xFFFFFEFA))
    LauncherTheme.SYSTEM -> if (systemIsDark) skin(LauncherTheme.DARK, true) else skin(LauncherTheme.LIGHT, false)
}

@Composable
fun AuraTheme(theme: LauncherTheme, systemIsDark: Boolean, highContrast: Boolean = false, finish: ThemeFinish = ThemeFinish(), reduceMotion: Boolean = false, content: @Composable () -> Unit) {
    val (presetStyle, presetScheme) = skin(theme, systemIsDark)
    val requested = finish.accent.argb?.let { Color(it) }
    val adjusted = ensureReadableAccent(requested ?: presetStyle.accent, listOf(presetScheme.background, presetStyle.backgroundTop, presetStyle.backgroundBottom, presetScheme.surface, presetScheme.surfaceVariant))
    val baseStyle = presetStyle.copy(accent = adjusted,
        iconHolder = if (finish.shape == ShapeChoice.DEFAULT) presetStyle.iconHolder else IconHolder.valueOf(finish.shape.name))
    val baseScheme = presetScheme.copy(primary = adjusted, secondary = adjusted, tertiary = adjusted, surfaceTint = adjusted,
        onTertiary = if (adjusted.luminance() > .179f) Color.Black else Color.White,
        tertiaryContainer = presetScheme.surfaceVariant, onTertiaryContainer = presetScheme.onSurface,
        outlineVariant = presetScheme.outline,
        onPrimary = if (adjusted.luminance() > .179f) Color.Black else Color.White,
        onSecondary = if (adjusted.luminance() > .179f) Color.Black else Color.White,
        surfaceContainerLowest = presetScheme.background, surfaceContainerLow = presetScheme.surface,
        surfaceContainer = presetScheme.surface, surfaceContainerHigh = presetScheme.surfaceVariant,
        surfaceContainerHighest = presetScheme.surfaceVariant, surfaceBright = presetScheme.surfaceVariant, surfaceDim = presetScheme.background,
        primaryContainer = presetScheme.surfaceVariant, onPrimaryContainer = presetScheme.onSurface,
        secondaryContainer = presetScheme.surfaceVariant, onSecondaryContainer = presetScheme.onSurface)
    val dark = theme.isDarkSkin(systemIsDark)
    val foreground = if (dark) Color.White else Color.Black
    val background = if (dark) Color.Black else Color.White
    val contrastAccent = if (dark) Color(0xFFBCE7FF) else Color(0xFF003B65)
    val style = if (highContrast) baseStyle.copy(accent = contrastAccent, backgroundTop = background, backgroundBottom = background, pattern = WallpaperPattern.NONE) else baseStyle
    val scheme = if (highContrast) baseScheme.copy(primary = contrastAccent, secondary = contrastAccent,
        onPrimary = background, onSecondary = background, background = background, onBackground = foreground,
        surface = background, onSurface = foreground, surfaceVariant = background, onSurfaceVariant = foreground, outline = foreground,
        surfaceContainerLowest = background, surfaceContainerLow = background, surfaceContainer = background,
        surfaceContainerHigh = background, surfaceContainerHighest = background, surfaceBright = background, surfaceDim = background,
        primaryContainer = background, onPrimaryContainer = foreground, secondaryContainer = background, onSecondaryContainer = foreground) else baseScheme
    val colorDuration = if (highContrast || reduceMotion) 0 else style.motionMillis
    val accent by animateColorAsState(style.accent, tween(colorDuration), label = "skin-accent")
    val top by animateColorAsState(style.backgroundTop, tween(colorDuration), label = "skin-background-top")
    val bottom by animateColorAsState(style.backgroundBottom, tween(colorDuration), label = "skin-background-bottom")
    val industrial = theme == LauncherTheme.NOTHING
    val retro = RetroStyle(theme, finish.shape != ShapeChoice.DEFAULT)
    val animatedScheme = if (industrial && !highContrast) scheme.copy(
        primary = IndustrialTokens.text, onPrimary = Color.Black,
        secondary = IndustrialTokens.text, onSecondary = Color.Black,
        tertiary = accent, onTertiary = Color.Black,
        tertiaryContainer = IndustrialTokens.raised, onTertiaryContainer = IndustrialTokens.text,
        outlineVariant = IndustrialTokens.outline,
        inverseSurface = IndustrialTokens.text, inverseOnSurface = Color.Black, inversePrimary = Color.Black,
        surfaceTint = Color.Transparent, background = bottom,
    ) else scheme.copy(primary = accent, secondary = accent, background = bottom)
    val animatedStyle = style.copy(accent = accent, backgroundTop = top, backgroundBottom = bottom)
    MaterialTheme(colorScheme = animatedScheme,
        typography = if (industrial) IndustrialTypography else if (retro.enabled) retroTypography(theme) else androidx.compose.material3.Typography(),
        shapes = if (industrial) IndustrialShapes else if (retro.enabled) retroShapes(theme) else androidx.compose.material3.Shapes(), content = {
        CompositionLocalProvider(LocalContentColor provides animatedScheme.onBackground, LocalSkinStyle provides animatedStyle,
            LocalIconTreatment provides finish.icons, LocalRetroStyle provides if (highContrast) RetroStyle() else retro,
            LocalIndustrialStyle provides IndustrialStyle(industrial, finish.icons == IconTreatment.MONOCHROME || (industrial && finish.icons == IconTreatment.AUTO)), content = content)
    })
}

fun LauncherTheme.statusBarColor(systemIsDark: Boolean): Int {
    val (_, scheme) = skin(this, systemIsDark)
    return android.graphics.Color.rgb((scheme.background.red * 255).toInt(), (scheme.background.green * 255).toInt(), (scheme.background.blue * 255).toInt())
}

fun LauncherTheme.isDarkSkin(systemIsDark: Boolean) = skin(this, systemIsDark).second.background.luminance() < .5f

private fun ensureReadableAccent(accent: Color, backgrounds: List<Color>): Color {
    fun contrast(a: Color, b: Color): Float = (maxOf(a.luminance(), b.luminance()) + .05f) / (minOf(a.luminance(), b.luminance()) + .05f)
    val endpoint = if (backgrounds.first().luminance() < .179f) Color.White else Color.Black
    for (step in 0..20) {
        val candidate = lerp(accent, endpoint, step / 20f)
        if (backgrounds.all { contrast(candidate, it) >= 4.5f }) return candidate
    }
    return endpoint
}

val SkinStyle.wallpaper: Brush get() = Brush.verticalGradient(listOf(backgroundTop, backgroundBottom))
