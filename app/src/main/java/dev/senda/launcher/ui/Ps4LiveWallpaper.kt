package dev.senda.launcher.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.LocalAppearance
import kotlin.math.*

/** Original console light-wave scene; lifecycle/frame-clock driven, state read only in draw. */
@Composable
internal fun Ps4Wallpaper(reduced: Boolean, modifier: Modifier = Modifier, listState: LazyListState? = null, strength: Float = 1f) {
    val decorationStrength = strength.coerceIn(0f, 1f)
    val context = LocalContext.current.applicationContext
    val power = remember(context) { context.getSystemService(PowerManager::class.java) }
    var saver by remember(power) { mutableStateOf(power?.isPowerSaveMode == true) }
    DisposableEffect(context, power) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) { saver = power?.isPowerSaveMode == true }
        }
        ContextCompat.registerReceiver(context, receiver, IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED), ContextCompat.RECEIVER_NOT_EXPORTED)
        saver = power?.isPowerSaveMode == true
        onDispose { context.unregisterReceiver(receiver) }
    }
    val targetPalette = dev.senda.launcher.ui.theme.ps4PaletteColors(
        dev.senda.launcher.ui.theme.LocalPs4Palette.current, dev.senda.launcher.ui.theme.LocalPs4Ambient.current)
    val middle by androidx.compose.animation.animateColorAsState(targetPalette.middle,
        androidx.compose.animation.core.tween(if (reduced || saver) 0 else 700), label = "ps4-ambient-middle")
    val skin = dev.senda.launcher.ui.theme.LocalSkinStyle.current
    val palette = targetPalette.copy(top = skin.backgroundTop, middle = middle, bottom = skin.backgroundBottom)
    val whitePalette = dev.senda.launcher.ui.theme.LocalPs4Palette.current == dev.senda.launcher.data.Ps4Palette.WHITE
    val entry = LocalPs4EntryProgress.current
    val entryIntensity = LocalPs4EntryIntensity.current
    val scene = if (reduced) null else LocalPs4WallpaperMotion.current
    val focusedKey = LocalPs4FocusedKey.current
    val feedback = dev.senda.launcher.audio.LocalSoundFeedback.current
    val disabled = reduced || saver || LocalAppearance.current.highContrast || LocalThemePreview.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val adaptive = LocalAdaptiveMotion.current
    // Focus feedback follows navigation, independently of the decorative frame clock.
    LaunchedEffect(lifecycle, listState, disabled, feedback, focusedKey, adaptive) {
        if (disabled) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var previousFocus: Any? = null
            snapshotFlow { focusedKey.value }.collect { focus ->
                if (focus != null && previousFocus != null && focus != previousFocus) {
                    if (listState?.isScrollInProgress == true) feedback.play(dev.senda.launcher.data.SoundEvent.INDEX)
                }
                previousFocus = focus
            }
        }
    }
    Box(modifier.drawWithCache {
        val base=Brush.linearGradient(listOf(palette.top,palette.middle,palette.bottom),end=Offset(size.width*.8f,size.height))
        onDrawBehind { drawRect(base) }
    }) {
    Box(Modifier.matchParentSize().graphicsLayer {
        // Animate a retained display list rather than rebuilding ribbon geometry per frame.
        val amount = scene?.amount ?: 0f
        val panel = if (scene?.enabled == true) scene.panel?.value ?: 0f else 0f
        val depth = maxOf((scene?.search ?: 0f) * .45f, panel)
        translationX=sin(.7f)*size.width*.014f
        translationY=cos(.7f*.8f)*size.height*.009f + (scene?.drift ?: 0f) * 6.dp.toPx() * amount
        val ribbonPhase = ((entry?.value ?: 1f) / .78f).coerceIn(0f, 1f)
        val opening = (1f - androidx.compose.animation.core.FastOutSlowInEasing.transform(ribbonPhase)) * entryIntensity
        scaleX = 1.04f - opening * .29f - depth * .012f * amount
        scaleY = 1.04f - depth * .012f * amount
        translationX += opening * size.width * .065f
        translationY += opening * size.height * .045f
        alpha=1f // Keep the full-screen layer opaque; avoid an offscreen alpha buffer.
    }.drawWithCache {
        val backdrop=Brush.linearGradient(listOf(palette.top,palette.middle,palette.bottom),end=Offset(size.width*.8f,size.height))
        val atmosphere=Brush.radialGradient(listOf(Color(0xFF0070D1).copy(alpha=.34f*decorationStrength),Color.Transparent),center=Offset(size.width,size.height*.18f),radius=size.height*.7f)
        val ribbons=List(3) { ConsoleRibbon(it).apply { update(size.width,size.height,.7f,0f) } }
        val glass=Brush.linearGradient(listOf(Color(0xFF0055D9).copy(alpha=.04f),Color(0xFF137EFF).copy(alpha=.12f),Color(0xFF2FCBFF).copy(alpha=.23f),Color(0xFF0070D1).copy(alpha=.04f)),start=Offset(0f,size.height*.9f),end=Offset(size.width,size.height*.1f))
        val edge=Brush.horizontalGradient(listOf(Color.Transparent,Color(0xFF008CFF),Color(0xFF9DEEFF),Color(0xFF0070D1).copy(alpha=.1f)))
        val glowStrokes=listOf(Stroke(9.dp.toPx()),Stroke(3.dp.toPx()))
        val hairline=Stroke(.65.dp.toPx())
        val foregroundShade=Brush.horizontalGradient(listOf(palette.bottom.copy(alpha=if(whitePalette).14f else .42f),palette.middle.copy(alpha=.16f),Color.Transparent))
        onDrawBehind {
            drawRect(backdrop)
            drawRect(atmosphere)
            val time=.7f
            val drift=0f
            val light=0f
            ribbons.forEach { ribbon ->
                // Nested translucent surfaces approximate a soft cross-section without blur.
                repeat(2) { layer ->
                    ribbon.surface(1f-layer*.42f)
                    drawPath(ribbon.fill,glass,alpha=(.78f+light*.3f)*decorationStrength)
                }
                glowStrokes.forEachIndexed { index, stroke ->
                    drawPath(ribbon.crest,edge,alpha=(.06f+index*.025f+light*.12f)*decorationStrength,style=stroke)
                }
                drawPath(ribbon.crest,edge,alpha=(.32f+light)*decorationStrength,style=hairline)
            }
            // Fixed motes remain on the retained layer; no particle jobs.
            repeat(14) { index ->
                val x=size.width*(.05f+index*.069f+sin(time+index)*.014f+drift*.008f)
                val y=size.height*(.08f+index*.064f)+sin(time+index*.7f)*size.height*.016f
                drawCircle(Color(0xFFC2EDFF),radius=(.6f+index%3*.22f).dp.toPx(),center=Offset(x,y),alpha=(.11f+light*.15f)*decorationStrength)
            }
            drawRect(foregroundShade)
        }
    })
    if (scene != null) Ps4WallpaperFeedback(scene, Modifier.matchParentSize(), decorationStrength)
    }
}

@Composable
internal fun Ps4WallpaperFeedback(scene: Ps4WallpaperMotion, modifier: Modifier, strength: Float = 1f) {
    val media = LocalMediaAtmosphere.current
    Box(modifier.drawWithCache {
        val light = Brush.radialGradient(listOf(Color(0xFF64CFFF).copy(alpha=.12f * strength.coerceIn(0f, 1f)), Color.Transparent),
            center = Offset(size.width * .65f, size.height * .5f), radius = size.height * .48f)
        onDrawBehind {
            // Separate overlay: changing light never rebuilds ribbon paths or gradients.
            val amount = scene.amount
            if (scene.enabled) drawRect(media.color.value, alpha = media.presence.value * .10f * amount)
            val panel = if (scene.enabled) scene.panel?.value ?: 0f else 0f
            drawRect(Color.Black, alpha = maxOf(scene.search * .045f, panel * .08f) * amount)
            if (scene.light > .001f) withTransform({
                translate(top = (scene.lightY - .5f) * size.height)
            }) { drawRect(light, alpha = scene.light * amount) }
        }
    })
}

/** Fixed buffers and paths: no per-frame geometry collections or bitmap allocations. */
private class ConsoleRibbon(private val band: Int) {
    companion object { const val STEPS=32 }
    val fill=Path()
    val crest=Path()
    private val x=FloatArray(STEPS+1)
    private val y=FloatArray(STEPS+1)
    private val nx=FloatArray(STEPS+1)
    private val ny=FloatArray(STEPS+1)
    private val breadth=FloatArray(STEPS+1)
    private val t=FloatArray(STEPS+1) { it.toFloat()/STEPS }
    private val envelope=FloatArray(STEPS+1) { (.08f+sin(PI*t[it]).toFloat().coerceAtLeast(0f).pow(1.4f)) }

    fun update(w: Float,h: Float,time: Float,drift: Float) {
        val p=time+band*1.9f
        val sway=sin(p)*.065f
        val ripple=cos(time*2f+band)*.028f
        val startY=when(band) { 0->.48f; 1->.89f; else->1.06f }
        val endY=when(band) { 0->.16f; 1->.63f; else->.42f }
        val x0=-.18f*w; val x3=1.2f*w
        val x1=w*(.28f+cos(p)*.14f+drift*.035f)
        val x2=w*(.79f+sin(p)*.11f-drift*.025f)
        val y0=h*(startY+sway*.4f+drift*.022f)
        val y3=h*(endY-ripple+drift*.035f)
        val y1=h*(startY+.16f+sway)
        val y2=h*(endY-.09f-sway+ripple)
        val width=w*(.045f+band*.012f)*(1f+sin(p)*.22f)
        crest.reset()
        for(i in 0..STEPS) {
            val a=t[i]; val b=1f-a
            x[i]=b*b*b*x0+3f*b*b*a*x1+3f*b*a*a*x2+a*a*a*x3
            y[i]=b*b*b*y0+3f*b*b*a*y1+3f*b*a*a*y2+a*a*a*y3
            val dx=3f*b*b*(x1-x0)+6f*b*a*(x2-x1)+3f*a*a*(x3-x2)
            val dy=3f*b*b*(y1-y0)+6f*b*a*(y2-y1)+3f*a*a*(y3-y2)
            val length=sqrt(dx*dx+dy*dy).coerceAtLeast(1f)
            nx[i]=-dy/length; ny[i]=dx/length
            breadth[i]=width*envelope[i]
            if(i==0) crest.moveTo(x[i],y[i]) else crest.lineTo(x[i],y[i])
        }
    }
    fun surface(fraction: Float) {
        fill.reset()
        for(i in 0..STEPS) {
            val width=breadth[i]*fraction
            val px=x[i]+nx[i]*width; val py=y[i]+ny[i]*width
            if(i==0) fill.moveTo(px,py) else fill.lineTo(px,py)
        }
        for(i in STEPS downTo 0) {
            val width=breadth[i]*fraction
            fill.lineTo(x[i]-nx[i]*width,y[i]-ny[i]*width)
        }
        fill.close()
    }
}
