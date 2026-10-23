package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.widgets.WidgetWeather
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.roundToInt

/** Decorative only: no pointer input, no duplicate accessibility nodes, no polling or widget host. */
@Composable
internal fun WeatherSweep(theme:LauncherTheme,reading:WidgetWeather?,settings:WeatherMotionSettings,reduced:Boolean,modifier:Modifier=Modifier) {
    val motion=settings.bounded()
    val phase=remember { Animatable(1f) }
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val accent=LocalSkinStyle.current.accent
    val treatment=weatherTreatment(theme)
    LaunchedEffect(lifecycle,theme,motion,reduced,reading?.temperature,reading?.place,reading?.icon) {
        phase.snapTo(1f)
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            try {
                if(motion.shouldAnimate(reduced,reading!=null,ValueAnimator.areAnimatorsEnabled())) {
                    do {
                        phase.snapTo(0f)
                        phase.animateTo(1f,tween(motion.durationMs,easing=LinearEasing))
                        if(motion.mode!=WeatherMotionMode.REPEAT)break
                        delay(2500)
                    } while(ValueAnimator.areAnimatorsEnabled())
                }
                awaitCancellation()
            } finally { phase.snapTo(1f) }
        }
    }
    Canvas(modifier.clipToBounds().clearAndSetSemantics {}) {
        val p=phase.value
        if(reduced||motion.mode==WeatherMotionMode.OFF||reading==null||p>=1f)return@Canvas
        val envelope=sin(p*PI.toFloat()).coerceIn(0f,1f)*motion.intensity/100f
        val glyph=56.dp.toPx()
        val rawX=sweepX(p,size.width,glyph)
        val x=if(treatment==WeatherTreatment.LCD_SCAN)(rawX/4.dp.toPx()).roundToInt()*4.dp.toPx() else rawX
        val y=size.height*.5f
        val ink=accent.copy(alpha=.30f*envelope)
        when(treatment) {
            WeatherTreatment.DOT_MATRIX -> for(col in 0..10)for(row in 0..4) {
                val radius=if((col+row)%3==0)1.7.dp.toPx() else .8.dp.toPx()
                drawCircle(ink,radius,Offset(x-col*8.dp.toPx(),y+(row-2)*8.dp.toPx()))
            }
            WeatherTreatment.CLOUD_BANK -> {
                for(layer in 0..2)drawRoundRect(Color.White.copy(alpha=(.09f+layer*.035f)*envelope),
                    Offset(x-glyph*(1f+layer*.35f),y-glyph*.20f+layer*5.dp.toPx()),Size(glyph*2.2f,glyph*.55f),CornerRadius(glyph))
                drawCircle(accent.copy(alpha=.16f*envelope),glyph*.44f,Offset(x-glyph*.35f,y-glyph*.10f))
            }
            WeatherTreatment.WATER_GLASS -> for(ring in 1..4) {
                drawOval(ink,Offset(x-glyph*ring*.25f,y-glyph*ring*.10f),Size(glyph*ring*.65f,glyph*ring*.27f),style=Stroke((1f+ring*.2f).dp.toPx()))
                drawCircle(Color.White.copy(alpha=.20f*envelope),2.dp.toPx(),Offset(x-ring*13.dp.toPx(),y-ring*7.dp.toPx()))
            }
            WeatherTreatment.COLOR_RIBBON,WeatherTreatment.AURORA -> for(layer in 0..3) {
                val wave=Path().apply {
                    moveTo(x-glyph*2f,y+layer*4.dp.toPx())
                    cubicTo(x-glyph,y-glyph*.60f,x-glyph*.15f,y+glyph*.55f,x+glyph,y-glyph*.25f+layer*3.dp.toPx())
                }
                val tint=if(layer%2==0)accent else Color(0xFF7ACDE5)
                drawPath(wave,tint.copy(alpha=(if(treatment==WeatherTreatment.AURORA).23f else .15f)*envelope),
                    style=Stroke(if(treatment==WeatherTreatment.AURORA)(1f+layer).dp.toPx() else (6f+layer*2f).dp.toPx()))
            }
            WeatherTreatment.LCD_SCAN -> {
                drawRect(ink,Offset(x-glyph*1.6f,y-glyph*.40f),Size(glyph*1.8f,glyph*.8f))
                for(line in 0..7)drawLine(accent.copy(alpha=.30f*envelope),Offset(x-glyph*1.7f,y-glyph*.4f+line*6.dp.toPx()),Offset(x,y-glyph*.4f+line*6.dp.toPx()),1.dp.toPx())
            }
        }
        reading.icon?.let { bitmap ->
            drawImage(bitmap,srcSize=IntSize(bitmap.width,bitmap.height),dstOffset=IntOffset(x.roundToInt(),(y-glyph/2f).roundToInt()),
                dstSize=IntSize(glyph.roundToInt(),glyph.roundToInt()),alpha=.50f*envelope,
                colorFilter=if(treatment==WeatherTreatment.DOT_MATRIX||treatment==WeatherTreatment.LCD_SCAN)ColorFilter.tint(accent) else null)
        }
    }
}
