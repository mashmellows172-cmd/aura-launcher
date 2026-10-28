package dev.senda.launcher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.senda.launcher.R
import dev.senda.launcher.data.LauncherTheme

// Bundled fonts make each theme independent of the handset's default typeface.
private val senseFont=FontFamily(Font(R.font.droid_sans_regular),Font(R.font.droid_sans_bold,FontWeight.Bold))
private val natureFont=FontFamily(Font(R.font.roboto_ics_regular),Font(R.font.roboto_ics_bold,FontWeight.Bold))
private val optimusFont=FontFamily(Font(R.font.lato_regular),Font(R.font.lato_bold,FontWeight.Bold))
private val xperiaFont=FontFamily(Font(R.font.roboto_condensed_regular),Font(R.font.roboto_condensed_bold,FontWeight.Bold))
private val razrFont=FontFamily(Font(R.font.share_tech_mono))
private val ps4Font=FontFamily(Font(R.font.noto_sans_regular),Font(R.font.noto_sans_light,FontWeight.Light),Font(R.font.noto_sans_medium,FontWeight.Medium),Font(R.font.noto_sans_semibold,FontWeight.SemiBold),Font(R.font.noto_sans_bold,FontWeight.Bold))

private fun themedTypography(font:FontFamily,heading:FontWeight=FontWeight.Normal,strong:FontWeight=FontWeight.Bold):Typography {
    val b=Typography()
    return b.copy(
        displayLarge=b.displayLarge.copy(fontFamily=font,fontWeight=heading),displayMedium=b.displayMedium.copy(fontFamily=font,fontWeight=heading),displaySmall=b.displaySmall.copy(fontFamily=font,fontWeight=heading),
        headlineLarge=b.headlineLarge.copy(fontFamily=font,fontWeight=heading),headlineMedium=b.headlineMedium.copy(fontFamily=font,fontWeight=heading),headlineSmall=b.headlineSmall.copy(fontFamily=font,fontWeight=heading),
        titleLarge=b.titleLarge.copy(fontFamily=font,fontWeight=heading),titleMedium=b.titleMedium.copy(fontFamily=font,fontWeight=strong),titleSmall=b.titleSmall.copy(fontFamily=font,fontWeight=strong),
        bodyLarge=b.bodyLarge.copy(fontFamily=font),bodyMedium=b.bodyMedium.copy(fontFamily=font),bodySmall=b.bodySmall.copy(fontFamily=font),
        labelLarge=b.labelLarge.copy(fontFamily=font,fontWeight=strong),labelMedium=b.labelMedium.copy(fontFamily=font,fontWeight=FontWeight.Normal),labelSmall=b.labelSmall.copy(fontFamily=font,fontWeight=FontWeight.Normal))
}
private val senseType=themedTypography(senseFont,FontWeight.Bold)
private val natureType=themedTypography(natureFont)
private val optimusType=themedTypography(optimusFont)
private val xperiaType=themedTypography(xperiaFont)
private val razrType=themedTypography(razrFont,strong=FontWeight.Normal)
private val ps4Type=themedTypography(ps4Font,FontWeight.Light,strong=FontWeight.Medium)
internal fun historicalTypography(theme:LauncherTheme):Typography=when(theme) {
    LauncherTheme.SENSE->senseType
    LauncherTheme.TOUCHWIZ->natureType
    LauncherTheme.OPTIMUS->optimusType
    LauncherTheme.XPERIA->xperiaType
    LauncherTheme.RAZR->razrType
    LauncherTheme.PS4->ps4Type
    else->Typography()
}

internal fun variantTypography(choice:String):Typography? = when(choice) {
    "NOTO" -> ps4Type
    "MONO" -> razrType
    "CONDENSED" -> xperiaType
    else -> null
}
