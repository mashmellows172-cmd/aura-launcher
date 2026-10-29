package dev.senda.launcher.ui

import android.animation.ValueAnimator
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.LocalAppearance
import kotlin.math.*

@Stable
private class ConsoleScene {
    var phase by mutableFloatStateOf(.7f)
    var parallax by mutableFloatStateOf(0f)
    var energy by mutableFloatStateOf(0f)
    var focusLight by mutableFloatStateOf(0f)
}

/** Original console light-wave scene; lifecycle/frame-clock driven, state read only in draw. */
@Composable
internal fun Ps4Wallpaper(reduced: Boolean, modifier: Modifier = Modifier, listState: LazyListState? = null) {
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
    val scene = remember { ConsoleScene() }
    val disabled = reduced || saver || LocalAppearance.current.highContrast || LocalThemePreview.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle, listState, disabled) {
        if (disabled) { scene.parallax=0f; scene.energy=0f; scene.focusLight=0f; return@LaunchedEffect }
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var previous = 0L
            var previousFocus: Any? = null
            while (ValueAnimator.areAnimatorsEnabled()) {
                withFrameNanos { now ->
                    val dt = if(previous==0L) 0f else ((now-previous)/1_000_000_000f).coerceIn(0f,.05f)
                    previous=now
                    val info = listState?.layoutInfo
                    val first = info?.visibleItemsInfo?.firstOrNull()
                    val cursor = if(listState==null) 0f else listState.firstVisibleItemIndex +
                        listState.firstVisibleItemScrollOffset.toFloat()/(first?.size?.coerceAtLeast(1) ?: 1)
                    val target = sin(cursor*.14f)
                    scene.parallax += (target-scene.parallax)*(1f-exp(-dt*5f))
                    val moving = if(listState?.isScrollInProgress==true) 1f else 0f
                    scene.energy += (moving-scene.energy)*(1f-exp(-dt*4f))
                    var focus: Any? = null
                    var nearest = Float.MAX_VALUE
                    if(info!=null) {
                        val center=info.viewportStartOffset+(info.viewportEndOffset-info.viewportStartOffset)*.42f
                        for(item in info.visibleItemsInfo) {
                            if(item.contentType!="app") continue
                            val distance=abs(item.offset+item.size*.5f-center)
                            if(distance<nearest) { nearest=distance; focus=item.key }
                        }
                    }
                    if(focus!=null && previousFocus!=null && focus!=previousFocus) scene.focusLight=1f
                    previousFocus=focus
                    scene.focusLight *= exp(-dt*3.6f)
                    scene.phase=(scene.phase+dt*(2f*PI.toFloat()/24f)*(1f+scene.energy*.28f))%(2f*PI.toFloat())
                }
            }
        }
    }
    Box(modifier.drawWithCache {
        val backdrop=Brush.linearGradient(listOf(Color(0xFF003FA6),Color(0xFF001D62),Color(0xFF000B2B)),end=Offset(size.width*.8f,size.height))
        val atmosphere=Brush.radialGradient(listOf(Color(0xFF0070D1).copy(alpha=.42f),Color.Transparent),center=Offset(size.width,size.height*.18f),radius=size.height*.7f)
        val ribbons=List(3) { ConsoleRibbon(it) }
        val glass=Brush.linearGradient(listOf(Color(0xFF0055D9).copy(alpha=.04f),Color(0xFF137EFF).copy(alpha=.12f),Color(0xFF2FCBFF).copy(alpha=.23f),Color(0xFF0070D1).copy(alpha=.04f)),start=Offset(0f,size.height*.9f),end=Offset(size.width,size.height*.1f))
        val edge=Brush.horizontalGradient(listOf(Color.Transparent,Color(0xFF008CFF),Color(0xFF9DEEFF),Color(0xFF0070D1).copy(alpha=.1f)))
        val glowStrokes=listOf(Stroke(20.dp.toPx()),Stroke(9.dp.toPx()),Stroke(3.dp.toPx()))
        val hairline=Stroke(.65.dp.toPx())
        val foregroundShade=Brush.horizontalGradient(listOf(Color(0xFF000E30).copy(alpha=.24f),Color.Transparent))
        onDrawBehind {
            drawRect(backdrop)
            drawRect(atmosphere)
            val time=scene.phase
            val drift=scene.parallax
            val light=scene.energy*.16f+scene.focusLight*.14f
            ribbons.forEach { ribbon ->
                ribbon.update(size.width,size.height,time,drift)
                // Nested translucent surfaces approximate a soft cross-section without blur.
                repeat(8) { layer ->
                    ribbon.surface(1f-layer*.105f)
                    drawPath(ribbon.fill,glass,alpha=.5f+light)
                }
                glowStrokes.forEachIndexed { index, stroke ->
                    drawPath(ribbon.crest,edge,alpha=(.035f+index*.025f+light*.12f),style=stroke)
                }
                drawPath(ribbon.crest,edge,alpha=.32f+light,style=hairline)
            }
            // Tiny light motes use the scene clock; no independent particle jobs.
            repeat(14) { index ->
                val x=size.width*(.05f+index*.069f+sin(time+index)*.014f+drift*.008f)
                val y=size.height*(.08f+index*.064f)+sin(time+index*.7f)*size.height*.016f
                drawCircle(Color(0xFFC2EDFF),radius=(.6f+index%3*.22f).dp.toPx(),center=Offset(x,y),alpha=.11f+light*.15f)
            }
            drawRect(foregroundShade)
        }
    })
}

/** Fixed buffers and paths: no per-frame geometry collections or bitmap allocations. */
private class ConsoleRibbon(private val band: Int) {
    companion object { const val STEPS=48 }
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
