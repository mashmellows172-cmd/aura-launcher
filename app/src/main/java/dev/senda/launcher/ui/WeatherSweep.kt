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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.widgets.WidgetWeather
import kotlinx.coroutines.awaitCancellation
import kotlin.math.*

/** Decorative only: no pointer input, no duplicate accessibility nodes, no polling or widget host. */
@Composable
internal fun WeatherSweep(theme:LauncherTheme,reading:WidgetWeather?,settings:WeatherMotionSettings,reduced:Boolean,modifier:Modifier=Modifier) {
    val motion=settings.bounded()
    val phase=remember { Animatable(1f) }
    var active by remember { mutableStateOf(false) }
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val accent=LocalSkinStyle.current.accent
    val treatment=weatherTreatment(theme)
    val condition=remember(motion.condition,reading?.conditionText) {
        if(motion.condition==WeatherCondition.AUTO)providerWeatherCondition(reading?.conditionText.orEmpty()) else motion.condition
    }
    LaunchedEffect(lifecycle,theme,motion,reduced,condition,reading!=null) {
        active=false
        phase.snapTo(1f)
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            try {
                if(motion.shouldAnimate(reduced,reading!=null,ValueAnimator.areAnimatorsEnabled())) {
                    active=true
                    do {
                        phase.snapTo(0f)
                        phase.animateTo(1f,tween(motion.durationMs,easing=LinearEasing))
                    } while(motion.mode==WeatherMotionMode.REPEAT && ValueAnimator.areAnimatorsEnabled())
                }
                awaitCancellation()
            } finally { active=false }
        }
    }
    Canvas(modifier.clipToBounds().clearAndSetSemantics {}) {
        val p=phase.value
        if(reduced||motion.mode==WeatherMotionMode.OFF||reading==null||!active)return@Canvas
        // Repeat scenes are periodic at both endpoints; ONCE uses a soft entrance and exit.
        val fade=if(motion.mode==WeatherMotionMode.REPEAT)1f else min(p/.16f,(1f-p)/.22f).coerceIn(0f,1f)
        drawWeatherScene(p,condition,treatment,accent,fade*motion.intensity/100f)
    }
}

private fun DrawScope.drawWeatherScene(p:Float,condition:WeatherCondition,treatment:WeatherTreatment,accent:Color,strength:Float) {
    val dots=treatment==WeatherTreatment.DOT_MATRIX
    val lcd=treatment==WeatherTreatment.LCD_SCAN
    val unit=dp(1f)
    val tau=(PI*2).toFloat()
    fun wrap(v:Float)=v-floor(v)
    fun ink(color:Color,alpha:Float)=color.copy(alpha=(alpha*strength).coerceIn(0f,1f))
    fun particle(x:Float,y:Float,r:Float,color:Color,alpha:Float) {
        val c=Offset(x,y)
        if(dots) {
            // Nested circles give a luminous core without an offscreen blur pass.
            drawCircle(ink(color,alpha*.12f),r*3f,c)
            drawCircle(ink(color,alpha*.25f),r*1.8f,c)
            drawCircle(ink(color,alpha),r,c)
        } else if(lcd)drawRect(ink(color,alpha),Offset(x-r,y-r),Size(r*2,r*2))
        else drawCircle(ink(color,alpha),r,c)
    }
    fun cloud(cx:Float,cy:Float,r:Float,color:Color) {
        if(dots||lcd) {
            for(col in -8..8)for(row in -3..3) {
                val x=col/8f;val y=row/3f
                if(x*x+y*y<1f || (x+.25f).pow(2)+(y+.45f).pow(2)<.55f)
                    particle(cx+col*dp(4f),cy+row*dp(4f),dp(if(lcd)1.3f else 1f),color,.45f)
            }
        } else {
            val a=if(treatment==WeatherTreatment.CLOUD_BANK).24f else .12f
            for(layer in 0..2) {
                val offset=layer*r*.06f
                drawRoundRect(ink(color,a),Offset(cx-r+offset,cy-r*.15f+offset),Size(r*2,r*.55f),CornerRadius(r))
                drawCircle(ink(color,a),r*.43f,Offset(cx-r*.24f,cy-r*.13f+offset))
                drawCircle(ink(color,a),r*.32f,Offset(cx+r*.29f,cy-r*.03f+offset))
            }
        }
    }
    // The atmospheric direction and material belong to each theme, over the whole header.
    when(treatment) {
        WeatherTreatment.DOT_MATRIX -> for(i in 0..23) {
            val x=wrap(i*.618034f+p)*size.width
            val y=(.12f+wrap(i*.4142f)*.76f)*size.height
            val glow=.12f+.12f*(1f+sin(tau*(p+i*.19f)))
            particle(x,y,dp(.7f),accent,glow)
        }
        WeatherTreatment.CLOUD_BANK -> for(i in 0..2)cloud((wrap(p+i/3f)*1.6f-.3f)*size.width,size.height*(.22f+i*.22f),size.height*.36f,Color.White)
        WeatherTreatment.WATER_GLASS -> for(i in 0..5) {
            val t=wrap(p+i/6f);val r=dp(8f)+t*size.height*.45f
            drawOval(ink(accent,(1f-t)*.3f),Offset(size.width*(i+.5f)/6-r,size.height*.75f-r*.25f),Size(r*2,r*.5f),style=Stroke(dp(1f)))
        }
        WeatherTreatment.COLOR_RIBBON,WeatherTreatment.AURORA -> for(i in 0..3) {
            val y=size.height*(.20f+i*.18f)
            val swing=sin(tau*(p+i*.14f))*size.height*.22f
            val path=Path().apply {moveTo(-size.width*.1f,y);cubicTo(size.width*.25f,y+swing,size.width*.62f,y-swing,size.width*1.1f,y)}
            val color=if(i%2==0)accent else Color(0xFF75CDEB)
            drawPath(path,ink(color,if(treatment==WeatherTreatment.AURORA).22f else .16f),style=Stroke(dp(if(treatment==WeatherTreatment.AURORA)1.5f+i else 8f+i*3)))
        }
        WeatherTreatment.LCD_SCAN -> {
            for(i in 0..12)drawLine(ink(accent,.08f),Offset(0f,size.height*i/12),Offset(size.width,size.height*i/12),unit)
            for(i in 0..9)particle(wrap(p+i/10f)*size.width,size.height*(.2f+(i%3)*.3f),dp(1.5f),accent,.25f)
        }
    }
    val cool=if(dots||lcd)accent else Color(0xFFB9E7FF)
    val warm=if(dots||lcd)accent else Color(0xFFFFD785)
    when(condition) {
        WeatherCondition.SUN -> {
            val cx=size.width*.74f;val cy=size.height*.42f;val radius=size.height*.19f
            if(dots||lcd) {
                for(col in -6..6)for(row in -6..6) {
                    val d=col*col+row*row
                    if(d in 14..33)particle(cx+col*dp(3f),cy+row*dp(3f),dp(1f),warm,.75f)
                }
            } else {
                drawCircle(ink(warm,.05f),radius*2.4f,Offset(cx,cy))
                drawCircle(ink(warm,.13f),radius*1.7f,Offset(cx,cy))
                drawCircle(ink(warm,.55f),radius,Offset(cx,cy))
            }
            for(i in 0..11) {
                val a=i*tau/12+sin(tau*p)*.12f
                val r=radius*(1.5f+.12f*sin(tau*(p+i*.1f)))
                particle(cx+cos(a)*r,cy+sin(a)*r,dp(if(lcd)1.4f else 1.1f),warm,.65f)
            }
        }
        WeatherCondition.CLOUDS -> for(i in 0..1)cloud((wrap(p+i*.5f)*1.6f-.3f)*size.width,size.height*(.40f+i*.26f),size.height*.35f,cool)
        WeatherCondition.RAIN,WeatherCondition.SNOW,WeatherCondition.STORM -> {
            val snow=condition==WeatherCondition.SNOW
            val count=if(snow)28 else 40
            for(i in 0 until count) {
                val fall=wrap(p*(if(snow)1f else 2f)+i*.381966f)
                val x=wrap(i*.618034f+p)*size.width
                val y=fall*(size.height+dp(12f))-dp(6f)
                val a=(sin(fall*PI).toFloat()*.65f).coerceIn(0f,1f)
                if(dots||lcd||snow) {
                    val drift=if(snow)sin(tau*(p+i*.31f))*dp(5f) else 0f
                    particle(x+drift,y,dp(if(snow)1.4f else 1f),cool,a)
                    if(!snow)particle(x-dp(1f),y-dp(4f),dp(.75f),cool,a*.45f)
                } else drawLine(ink(cool,a),Offset(x,y),Offset(x+dp(2f),y+dp(if(treatment==WeatherTreatment.WATER_GLASS)10f else 6f)),dp(1.4f))
            }
            if(condition==WeatherCondition.STORM) {
                // Slow breathing bolt, never a full-field flash.
                val alpha=.22f+.25f*(.5f+.5f*sin(tau*p))
                val x=size.width*.77f;val y=size.height*.16f
                val bolt=Path().apply {moveTo(x,y);lineTo(x-dp(10f),y+dp(15f));lineTo(x+dp(2f),y+dp(13f));lineTo(x-dp(5f),y+dp(31f))}
                drawPath(bolt,ink(warm,alpha),style=Stroke(dp(if(lcd)2f else 1.6f)))
            }
        }
        WeatherCondition.FOG -> for(i in 0..4) {
            val x=(wrap(p+i*.2f)*1.7f-.35f)*size.width
            val y=size.height*(.2f+i*.15f)
            if(dots||lcd)for(j in 0..20)particle(x+j*dp(4f),y,dp(1f),cool,.32f)
            else drawRoundRect(ink(cool,.11f),Offset(x,y),Size(size.width*.45f,dp(5f)),CornerRadius(dp(8f)))
        }
        else -> Unit // Unknown condition remains neutral; never inferred from temperature.
    }
}

private fun DrawScope.dp(value:Float)=value.dp.toPx()
