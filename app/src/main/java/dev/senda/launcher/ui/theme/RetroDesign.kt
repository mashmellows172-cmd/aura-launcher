package dev.senda.launcher.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.IconTreatment
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

val LocalIconTreatment = staticCompositionLocalOf { IconTreatment.AUTO }
data class RetroStyle(val theme: LauncherTheme? = null, val customHolder: Boolean = false) {
    val enabled get() = theme in setOf(LauncherTheme.SENSE, LauncherTheme.TOUCHWIZ, LauncherTheme.OPTIMUS, LauncherTheme.XPERIA, LauncherTheme.RAZR)
    val caption get() = when (theme) {
        LauncherTheme.SENSE -> "AURA BLOOM / SENSE"
        LauncherTheme.TOUCHWIZ -> "AURA PRISM / NATURE"
        LauncherTheme.OPTIMUS -> "AURA FLOW / OPTIMUS"
        LauncherTheme.XPERIA -> "AURA HORIZON / FLOW"
        LauncherTheme.RAZR -> "AURA ALLOY / V3"
        else -> "AURA"
    }
}
val LocalRetroStyle = staticCompositionLocalOf { RetroStyle() }

fun retroShapes(theme: LauncherTheme): Shapes {
    val radius = when (theme) { LauncherTheme.SENSE -> 8; LauncherTheme.TOUCHWIZ -> 18; LauncherTheme.OPTIMUS -> 12; else -> 4 }.dp
    return Shapes(extraSmall = RoundedCornerShape(3.dp), small = RoundedCornerShape(radius / 2), medium = RoundedCornerShape(radius), large = RoundedCornerShape(radius), extraLarge = RoundedCornerShape(radius + 4.dp))
}
fun retroTypography(theme: LauncherTheme): Typography {
    val base = Typography()
    if(theme==LauncherTheme.RAZR) return base.copy(
        titleLarge=base.titleLarge.copy(fontFamily=FontFamily.Monospace,fontWeight=FontWeight.Medium),
        titleMedium=base.titleMedium.copy(fontFamily=FontFamily.Monospace),
        labelLarge=base.labelLarge.copy(fontFamily=FontFamily.Monospace),
        labelSmall=base.labelSmall.copy(fontFamily=FontFamily.Monospace))
    val weight = if (theme == LauncherTheme.SENSE) FontWeight.Medium else if(theme==LauncherTheme.XPERIA)FontWeight.Light else FontWeight.Normal
    return base.copy(
        titleLarge = base.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        titleMedium = base.titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        headlineSmall = base.headlineSmall.copy(fontFamily = FontFamily.SansSerif, fontWeight = weight),
        labelSmall = base.labelSmall.copy(fontFamily = FontFamily.SansSerif,fontWeight=weight,letterSpacing=if(theme==LauncherTheme.XPERIA)1.sp else .4.sp),
        bodyLarge = base.bodyLarge.copy(fontFamily = FontFamily.SansSerif),
    )
}

/** The parent measures available space; only the visible clock receives the clickable modifier. */
@Composable
fun RetroClock(time: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    val retro = LocalRetroStyle.current
    val scale = LocalDensity.current.fontScale
    BoxWithConstraints {
        val fontSize = minOf(if (compact) 22f else 48f, (maxWidth.value - 24f).coerceAtLeast(40f) / (3.2f * scale)).sp
        if (retro.theme == LauncherTheme.RAZR) {
            Text(time,modifier.heightIn(min=if(compact)28.dp else 48.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFD6E8EE),Color(0xFF8FB2CB))),RoundedCornerShape(3.dp))
                .drawWithContent {
                    drawContent()
                    drawRoundRect(Color(0xFF65879E),cornerRadius=CornerRadius(3.dp.toPx()),style=Stroke(1.dp.toPx()))
                }.padding(horizontal=8.dp,vertical=2.dp),color=Color(0xFF132C42),fontSize=fontSize,
                fontFamily=FontFamily.Monospace,fontWeight=FontWeight.Medium,maxLines=1)
        } else if (retro.theme == LauncherTheme.SENSE) {
            Row(modifier.heightIn(min = if (compact) 28.dp else 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf(time.take(2), time.takeLast(2)).forEach { digits ->
                    Text(digits, color = Color(0xFF252925), fontSize = fontSize, fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif, maxLines = 1,
                        modifier = Modifier.background(Brush.verticalGradient(listOf(Color(0xFFFAFAF4), Color(0xFFB8BEB8))), RoundedCornerShape(4.dp))
                            .drawWithContent { drawContent(); drawLine(Color.Black.copy(alpha = .35f), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1.dp.toPx()) }
                            .padding(horizontal = 7.dp))
                }
            }
        } else Text(time, modifier.heightIn(min = if (compact) 28.dp else 48.dp), color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.SansSerif, fontWeight = if (retro.theme == LauncherTheme.XPERIA) FontWeight.ExtraLight else FontWeight.Light,
            fontSize = fontSize, maxLines = 1)
    }
}

internal enum class RetroGlyph { PHONE, CAMERA, BROWSER, MUSIC, VIDEO, MESSAGE, CONTACTS, CLOCK, CALENDAR, SETTINGS, FILES, GALLERY, CALCULATOR, STORE, EMAIL, MAPS, NOTES }
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

/** Original category artwork; never substitutes a brand logo. Unknown apps keep their own drawable. */
@Composable
fun renderRetroAppIcon(app: LaunchableApp): Boolean {
    val glyph = glyphFor(app.packageName) ?: return false
    val retro = LocalRetroStyle.current
    val size = LocalAppearance.current.iconSize.dp
    val holder = LocalSkinStyle.current.iconHolder
    val color = when (glyph) {
        RetroGlyph.PHONE, RetroGlyph.MESSAGE -> Color(0xFF428A35)
        RetroGlyph.BROWSER, RetroGlyph.EMAIL, RetroGlyph.MAPS -> Color(0xFF287CBB)
        RetroGlyph.MUSIC, RetroGlyph.VIDEO -> Color(0xFFBE4C50)
        RetroGlyph.CONTACTS, RetroGlyph.NOTES, RetroGlyph.FILES -> Color(0xFFBD7B24)
        RetroGlyph.GALLERY, RetroGlyph.STORE -> Color(0xFF8761B2)
        else -> Color(0xFF526A77)
    }
    Canvas(Modifier.size(size)) {
        withTransform({ scale(this@Canvas.size.width / 48f, this@Canvas.size.height / 48f, Offset.Zero) }) {
            val radius = if (retro.customHolder) when (holder) { IconHolder.CIRCLE, IconHolder.PILL -> 24f; IconHolder.SQUIRCLE -> 16f; IconHolder.ROUNDED -> 12f; IconHolder.BEVELED -> 4f } else when (retro.theme) { LauncherTheme.SENSE -> 7f; LauncherTheme.TOUCHWIZ -> 12f; LauncherTheme.OPTIMUS -> 8f; else -> 4f }
            val alloy = retro.theme == LauncherTheme.RAZR
            val light = if(alloy)Color(0xFFDAE5EB) else if (retro.theme == LauncherTheme.OPTIMUS) Color(0xFFFAF9F0) else color
            val dark = if(alloy)Color(0xFF758B9D) else if (retro.theme == LauncherTheme.OPTIMUS) Color(0xFFCBCFCB) else color.copy(red = color.red * .48f, green = color.green * .48f, blue = color.blue * .48f)
            drawRoundRect(Brush.verticalGradient(listOf(light, dark), endY = 48f), size = Size(48f, 48f), cornerRadius = CornerRadius(radius))
            drawRoundRect(Color.White.copy(alpha = .55f), topLeft = Offset(1f, 1f), size = Size(46f, 46f), cornerRadius = CornerRadius(radius), style = Stroke(.8f))
            if (retro.theme == LauncherTheme.TOUCHWIZ || retro.theme == LauncherTheme.SENSE) {
                drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = .38f), Color.Transparent), endY = 25f), topLeft = Offset(2f, 2f), size = Size(44f, 23f), cornerRadius = CornerRadius(radius))
            }
            if (retro.theme == LauncherTheme.XPERIA) drawLine(Color.White.copy(alpha = .6f), Offset(5f, 44f), Offset(43f, 44f), 1f)
            if(alloy) {
                drawLine(Color(0xFFB3E2FF),Offset(5f,44f),Offset(43f,44f),1.5f)
                drawLine(Color.White.copy(alpha=.7f),Offset(4f,4f),Offset(44f,4f),1f)
            }
            drawRetroGlyph(glyph, if(alloy)Color(0xFF173F61) else if (retro.theme == LauncherTheme.OPTIMUS) color.copy(red = color.red * .78f, green = color.green * .78f, blue = color.blue * .78f) else Color.White)
        }
    }
    return true
}

private fun DrawScope.drawRetroGlyph(glyph: RetroGlyph, ink: Color) {
    val stroke = Stroke(2.7f)
    fun line(a: Float, b: Float, c: Float, d: Float) = drawLine(ink, Offset(a, b), Offset(c, d), 2.7f)
    fun rect(x: Float, y: Float, w: Float, h: Float) = drawRoundRect(ink, Offset(x,y), Size(w,h), CornerRadius(2f), style = stroke)
    when (glyph) {
        RetroGlyph.PHONE -> {
            val p = Path().apply { moveTo(14f,11f); cubicTo(7f,18f,20f,39f,34f,36f); lineTo(38f,31f); lineTo(29f,26f); lineTo(25f,30f); cubicTo(21f,28f,18f,24f,18f,20f); lineTo(22f,17f); lineTo(18f,10f); close() }; drawPath(p,ink)
        }
        RetroGlyph.CAMERA -> { rect(10f,16f,28f,21f); drawCircle(ink,7f,Offset(24f,26f),style=stroke); rect(16f,11f,10f,5f) }
        RetroGlyph.BROWSER -> { drawCircle(ink,14f,Offset(24f,24f),style=stroke); drawOval(ink,Offset(18f,10f),Size(12f,28f),style=stroke); line(10f,24f,38f,24f); line(13f,17f,35f,17f); line(13f,31f,35f,31f) }
        RetroGlyph.MUSIC -> { line(22f,12f,22f,32f); line(22f,12f,35f,9f); line(35f,9f,35f,28f); drawOval(ink,Offset(12f,28f),Size(11f,8f)); drawOval(ink,Offset(25f,24f),Size(11f,8f)) }
        RetroGlyph.VIDEO -> { rect(9f,13f,30f,24f); val p=Path().apply { moveTo(21f,19f);lineTo(31f,25f);lineTo(21f,31f);close() };drawPath(p,ink) }
        RetroGlyph.MESSAGE -> { rect(9f,12f,30f,23f);line(15f,35f,12f,40f);line(12f,40f,23f,35f);line(15f,20f,32f,20f);line(15f,27f,28f,27f) }
        RetroGlyph.CONTACTS -> { drawCircle(ink,6f,Offset(24f,17f));drawArc(ink,180f,180f,true,Offset(12f,25f),Size(24f,22f)) }
        RetroGlyph.CLOCK -> { drawCircle(ink,14f,Offset(24f,24f),style=stroke);line(24f,14f,24f,25f);line(24f,25f,32f,29f) }
        RetroGlyph.CALENDAR -> { rect(11f,12f,26f,26f);line(11f,20f,37f,20f);line(17f,9f,17f,16f);line(31f,9f,31f,16f); for(x in 0..2) for(y in 0..1) drawCircle(ink,1.5f,Offset(17f+x*7f,26f+y*6f)) }
        RetroGlyph.SETTINGS -> { drawCircle(ink,10f,Offset(24f,24f),style=Stroke(5f));drawCircle(ink,3f,Offset(24f,24f));for(i in 0..7) { val a=i*Math.PI/4; val x=kotlin.math.cos(a).toFloat();val y=kotlin.math.sin(a).toFloat();line(24+x*11,24+y*11,24+x*16,24+y*16) } }
        RetroGlyph.FILES -> { val p=Path().apply {moveTo(9f,15f);lineTo(20f,15f);lineTo(24f,19f);lineTo(39f,19f);lineTo(36f,37f);lineTo(9f,37f);close()};drawPath(p,ink);line(12f,12f,22f,12f) }
        RetroGlyph.GALLERY -> {rect(9f,11f,30f,27f);drawCircle(ink,3f,Offset(29f,18f));val p=Path().apply {moveTo(12f,34f);lineTo(22f,22f);lineTo(29f,30f);lineTo(34f,26f);lineTo(37f,34f);close()};drawPath(p,ink) }
        RetroGlyph.CALCULATOR -> {rect(12f,9f,24f,31f);rect(16f,13f,16f,7f);for(x in 0..2) for(y in 0..2) drawCircle(ink,1.5f,Offset(18f+x*6f,26f+y*5f)) }
        RetroGlyph.STORE -> {rect(11f,17f,26f,23f);drawArc(ink,180f,180f,false,Offset(17f,8f),Size(14f,19f),style=stroke) }
        RetroGlyph.EMAIL -> {rect(9f,14f,30f,22f);line(10f,15f,24f,26f);line(24f,26f,38f,15f) }
        RetroGlyph.MAPS -> { val p=Path().apply {moveTo(24f,38f);cubicTo(10f,24f,11f,12f,24f,10f);cubicTo(37f,12f,38f,24f,24f,38f);close()};drawPath(p,ink,style=stroke);drawCircle(ink,4f,Offset(24f,21f),style=stroke) }
        RetroGlyph.NOTES -> {rect(12f,10f,24f,29f);for(y in 0..3) line(17f,17f+y*5,31f,17f+y*5) }
    }
}
