package dev.senda.launcher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.IconTreatment
import dev.senda.launcher.data.LauncherTheme

val LocalIconTreatment = staticCompositionLocalOf { IconTreatment.AUTO }
data class RetroStyle(val theme: LauncherTheme? = null, val customHolder: Boolean = false) {
    val enabled get() = theme in setOf(LauncherTheme.SENSE, LauncherTheme.TOUCHWIZ, LauncherTheme.OPTIMUS, LauncherTheme.XPERIA, LauncherTheme.RAZR, LauncherTheme.PS4)
    val caption get() = when (theme) {
        LauncherTheme.SENSE -> "AURA BLOOM / SENSE"
        LauncherTheme.TOUCHWIZ -> "AURA PRISM / NATURE"
        LauncherTheme.OPTIMUS -> "AURA FLOW / OPTIMUS"
        LauncherTheme.XPERIA -> "AURA HORIZON / FLOW"
        LauncherTheme.RAZR -> "AURA ALLOY / V3"
        LauncherTheme.PS4 -> "AURA / DYNAMIC MENU"
        else -> "AURA"
    }
}
val LocalRetroStyle = staticCompositionLocalOf { RetroStyle() }

fun retroShapes(theme: LauncherTheme): Shapes {
    val radius = (historicalProfile(theme)?.radius ?: 4).dp
    return Shapes(extraSmall = RoundedCornerShape(3.dp), small = RoundedCornerShape(radius / 2), medium = RoundedCornerShape(radius), large = RoundedCornerShape(radius), extraLarge = RoundedCornerShape(radius + 4.dp))
}
fun retroTypography(theme: LauncherTheme): Typography {
    val base = Typography()
    if(theme==LauncherTheme.RAZR) return base.copy(
        titleLarge=base.titleLarge.copy(fontFamily=FontFamily.Monospace,fontWeight=FontWeight.Medium),
        titleMedium=base.titleMedium.copy(fontFamily=FontFamily.Monospace),
        labelLarge=base.labelLarge.copy(fontFamily=FontFamily.Monospace),
        labelSmall=base.labelSmall.copy(fontFamily=FontFamily.Monospace))
    val weight = if (theme == LauncherTheme.SENSE) FontWeight.Medium else if(theme==LauncherTheme.XPERIA || theme==LauncherTheme.PS4)FontWeight.Light else FontWeight.Normal
    return base.copy(
        titleLarge = base.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        titleMedium = base.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        headlineSmall = base.headlineSmall.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        labelSmall = base.labelSmall.copy(fontFamily = FontFamily.SansSerif,fontWeight=weight,letterSpacing=if(theme==LauncherTheme.XPERIA)1.sp else .4.sp),
        bodyLarge = base.bodyLarge.copy(fontFamily = FontFamily.SansSerif),
    )
}

enum class RetroGlyph { PHONE, CAMERA, BROWSER, MUSIC, VIDEO, MESSAGE, CONTACTS, CLOCK, CALENDAR, SETTINGS, FILES, GALLERY, CALCULATOR, STORE, EMAIL, MAPS, NOTES }
/** Legacy category lookup for an explicitly enabled personal collection, never default artwork. */
internal fun glyphFor(packageName: String): RetroGlyph? {
    val name = packageName.lowercase(java.util.Locale.ROOT)
    return when {
        "calculator" in name -> RetroGlyph.CALCULATOR
        "calendar" in name -> RetroGlyph.CALENDAR
        "camera" in name -> RetroGlyph.CAMERA
        "dialer" in name || name.endsWith(".phone") -> RetroGlyph.PHONE
        "contacts" in name -> RetroGlyph.CONTACTS
        "youtube.music" in name || "spotify" in name || "music" in name -> RetroGlyph.MUSIC
        "youtube" in name || "vlc" in name || "video" in name -> RetroGlyph.VIDEO
        "browser" in name || "chrome" in name || "firefox" in name || "vivaldi" in name || "brave" in name -> RetroGlyph.BROWSER
        "messaging" in name || "messages" in name || "mms" in name -> RetroGlyph.MESSAGE
        "deskclock" in name || name.endsWith(".clock") -> RetroGlyph.CLOCK
        name == "com.android.settings" -> RetroGlyph.SETTINGS
        "documentsui" in name || "files" in name || "filemanager" in name -> RetroGlyph.FILES
        "photos" in name || "gallery" in name -> RetroGlyph.GALLERY
        "vending" in name || "appstore" in name -> RetroGlyph.STORE
        "gmail" in name || "email" in name -> RetroGlyph.EMAIL
        "maps" in name -> RetroGlyph.MAPS
        "keep" in name || "notes" in name -> RetroGlyph.NOTES
        else -> null
    }
}
