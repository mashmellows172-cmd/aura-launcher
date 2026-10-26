package dev.senda.launcher.ui.theme.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.IconHolder
import dev.senda.launcher.ui.theme.LocalSkinStyle

/** Compiled native vectors: no bitmap decoding, animation timer or path construction in a draw loop. */
@Composable
fun FamilyArtwork(icon: FamilyIcon, theme: LauncherTheme, modifier: Modifier = Modifier, customHolder: Boolean = false) {
    ArtworkResource(if (theme == LauncherTheme.TOUCHWIZ) s3FunctionArtwork(icon) ?: icon.resource(theme) else icon.resource(theme), modifier, customHolder)
}

@Composable
private fun ArtworkResource(resource: Int, modifier: Modifier, customHolder: Boolean) {
    val style = LocalSkinStyle.current
    val shape = dev.senda.launcher.ui.theme.animatedIconHolderShape(style.iconHolder, style.motionMillis)
    Box(modifier.then(if (customHolder) Modifier.background(Color(0xFF353C43), shape) else Modifier), contentAlignment = Alignment.Center) {
        Image(painterResource(resource), contentDescription = null,
            modifier = Modifier.fillMaxSize(if (customHolder) .82f else 1f), contentScale = ContentScale.Fit)
    }
}

/** True only for a known system function; modern/unmatched applications keep their actual drawable. */
@Composable
fun renderFamilyAppIcon(app: LaunchableApp, theme: LauncherTheme, customHolder: Boolean): Boolean {
    if (theme == LauncherTheme.PS4) return renderInstalledAppArtwork(app.packageName, theme, Modifier.size(LocalAppearance.current.iconSize.dp), customHolder)
    if (theme !in iconFamilyThemes) return false
    if (renderInstalledAppArtwork(app.packageName, theme, Modifier.size(LocalAppearance.current.iconSize.dp), customHolder)) return true
    if (theme == LauncherTheme.TOUCHWIZ) return false
    val icon = familyIconFor(app.packageName) ?: return false
    FamilyArtwork(icon, theme, Modifier.size(LocalAppearance.current.iconSize.dp), customHolder)
    return true
}

/** Exact installed-app identity lookup; individual replacements are resolved before this function. */
@Composable
fun renderInstalledAppArtwork(packageName: String, theme: LauncherTheme, modifier: Modifier = Modifier, customHolder: Boolean = false): Boolean {
    val resource = installedAppArtwork(packageName, theme) ?: return false
    ArtworkResource(resource, modifier, customHolder)
    return true
}
