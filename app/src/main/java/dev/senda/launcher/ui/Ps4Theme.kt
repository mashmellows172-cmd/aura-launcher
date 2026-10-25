package dev.senda.launcher.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.rememberHistoricalPhase
import kotlin.math.PI
import kotlin.math.sin

/** Original console light-wave scene; lifecycle/frame-clock driven, state read only in draw. */
@Composable
internal fun Ps4Wallpaper(reduced: Boolean, modifier: Modifier = Modifier) {
    val phase = rememberHistoricalPhase(LauncherTheme.PS4, true, reduced)
    Box(modifier.drawWithCache {
        val backdrop = Brush.verticalGradient(listOf(Color(0xFF1057C4), Color(0xFF07358A), Color(0xFF031B58)))
        val wash = Brush.verticalGradient(listOf(Color(0xFF8AD8FF).copy(alpha = .10f), Color.Transparent))
        val paths = List(4) { Path() }
        val contour = Stroke(.8.dp.toPx())
        onDrawBehind {
            drawRect(backdrop)
            val time = phase.value * (2 * PI).toFloat()
            paths.forEachIndexed { index, path ->
                val lift = sin(time + index * .6f) * size.height * .035f
                val y = size.height * (.40f + index * .045f)
                path.reset(); path.moveTo(-size.width * .2f, y + lift)
                path.cubicTo(size.width * .28f, y - size.height * .15f - lift,
                    size.width * .59f, y + size.height * .19f + lift,
                    size.width * 1.2f, y - size.height * .08f + lift)
                drawPath(path, Color(0xFFA9EAFF).copy(alpha = .12f + index * .035f), style = contour)
                path.lineTo(size.width * 1.2f, size.height); path.lineTo(-size.width * .2f, size.height); path.close()
                drawPath(path, wash)
            }
            // Sparse controller geometry drifts with the same clock, no per-particle timer.
            repeat(8) { index ->
                val x = size.width * (index * .137f + sin(time + index) * .025f).coerceIn(.02f,.98f)
                val y = size.height * (.12f + index * .096f) + sin(time + index) * 8.dp.toPx()
                val r = (3 + index % 3).dp.toPx()
                val ink = Color.White.copy(alpha = .08f)
                when (index % 4) {
                    0 -> drawCircle(ink, r, Offset(x,y), style = contour)
                    1 -> drawRect(ink, Offset(x-r,y-r), Size(r*2,r*2), style = contour)
                    2 -> { drawLine(ink, Offset(x-r,y-r), Offset(x+r,y+r), contour.width); drawLine(ink, Offset(x+r,y-r), Offset(x-r,y+r), contour.width) }
                    else -> { val triangle = paths[0]; triangle.reset(); triangle.moveTo(x,y-r); triangle.lineTo(x+r,y+r); triangle.lineTo(x-r,y+r); triangle.close(); drawPath(triangle, ink, style = contour) }
                }
            }
        }
    })
}

/** Content ribbon: native horizontal scroll and selection scale, no fake console-only actions. */
@Composable
internal fun Ps4Favorites(apps: List<LaunchableApp>, overrides: Map<String,String>, reduced: Boolean,
    onLaunch: (LaunchableApp) -> Unit, onLoadIcon: (LaunchableApp) -> Drawable) {
    val list = rememberLazyListState()
    val threshold = with(LocalDensity.current) { 56.dp.roundToPx() }
    val current by remember(list,threshold) { derivedStateOf { list.firstVisibleItemIndex + if (list.firstVisibleItemScrollOffset > threshold) 1 else 0 } }
    var focused by remember { mutableStateOf<String?>(null) }
    LazyRow(Modifier.fillMaxWidth().heightIn(min = 154.dp), state = list,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(apps, key = { _, app -> app.packageName }, contentType = { _, _ -> "console-app" }) { index, app ->
            val selected = if (focused == null) index == current else app.packageName == focused
            val scale by animateFloatAsState(if (selected) 1f else .90f,
                tween(if (reduced) 0 else 280, easing = FastOutSlowInEasing), label = "console-selection")
            Surface(onClick = { onLaunch(app) }, modifier = Modifier.width(104.dp).heightIn(min = 126.dp)
                .onFocusChanged { if (it.hasFocus) focused = app.packageName else if (focused == app.packageName) focused = null }
                .graphicsLayer { scaleX = scale; scaleY = scale },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                color = if (selected) Color(0xFF2164C1) else Color(0xFF103D88),
                contentColor = Color.White, border = BorderStroke(if (selected) 2.dp else 1.dp,
                    if (selected) Color(0xFFC7F1FF) else Color(0xFF5B94D7))) {
                Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CompositionLocalProvider(LocalAppearance provides LocalAppearance.current.copy(iconSize = 56)) {
                        AppIcon(app, overrides["PS4_${app.packageName}"], onLoadIcon)
                    }
                    Text(app.label, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(if (selected) "Iniciar" else " ", fontSize = 12.sp, color = Color(0xFFD0E9FF))
                }
            }
        }
    }
}

/** Controller-button motif, decorative; the surrounding avatar retains its actual actions. */
@Composable
internal fun Ps4Mark() {
    Canvas(Modifier.size(42.dp)) {
        val w=size.width; val stroke=Stroke(1.5.dp.toPx()); val ink=Color.White
        drawCircle(ink,w*.12f,Offset(w*.72f,w*.48f),style=stroke)
        drawRect(ink,Offset(w*.13f,w*.36f),Size(w*.23f,w*.23f),style=stroke)
        val triangle=Path().apply { moveTo(w*.5f,w*.10f);lineTo(w*.62f,w*.30f);lineTo(w*.38f,w*.30f);close() }
        drawPath(triangle,ink,style=stroke)
        drawLine(ink,Offset(w*.40f,w*.69f),Offset(w*.60f,w*.89f),stroke.width)
        drawLine(ink,Offset(w*.60f,w*.69f),Offset(w*.40f,w*.89f),stroke.width)
    }
}

/** Real Aura actions behind console-style function symbols, each with a48dp target. */
@Composable
internal fun Ps4FunctionBar(onAvatar: () -> Unit, onFavorites: () -> Unit, onLibrary: () -> Unit, onTheme: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        val labels = listOf("Cambiar avatar", "Editar favoritas", "Índice de aplicaciones", "Tema visual")
        val actions = listOf(onAvatar,onFavorites,onLibrary,onTheme)
        labels.forEachIndexed { kind,label ->
            androidx.compose.material3.IconButton(onClick = actions[kind], modifier = Modifier.size(48.dp)
                .then(Modifier.semanticsLabel(label))) {
                Canvas(Modifier.size(24.dp)) {
                    val w=size.width; val ink=Color.White; val stroke=Stroke(1.5.dp.toPx())
                    when(kind) {
                        0 -> { drawCircle(ink,w*.17f,Offset(w*.5f,w*.25f),style=stroke); drawArc(ink,180f,180f,false,Offset(w*.18f,w*.5f),Size(w*.64f,w*.6f),style=stroke) }
                        1 -> { val cup=Path().apply { moveTo(w*.25f,w*.13f);lineTo(w*.75f,w*.13f);quadraticTo(w*.73f,w*.59f,w*.5f,w*.62f);quadraticTo(w*.27f,w*.59f,w*.25f,w*.13f) }; drawPath(cup,ink,style=stroke);drawLine(ink,Offset(w*.5f,w*.62f),Offset(w*.5f,w*.86f),stroke.width);drawLine(ink,Offset(w*.30f,w*.87f),Offset(w*.70f,w*.87f),stroke.width) }
                        2 -> repeat(3) { book -> drawRect(ink,Offset(w*(.13f+book*.25f),w*.18f),Size(w*.18f,w*.66f),style=stroke) }
                        else -> { drawRect(ink,Offset(w*.12f,w*.33f),Size(w*.76f,w*.5f),style=stroke);drawRect(ink,Offset(w*.34f,w*.14f),Size(w*.32f,w*.2f),style=stroke);drawLine(ink,Offset(w*.13f,w*.5f),Offset(w*.87f,w*.5f),stroke.width) }
                    }
                }
            }
        }
    }
}
private fun Modifier.semanticsLabel(label: String): Modifier = this.then(Modifier.semantics { contentDescription = label })
