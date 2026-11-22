package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.widgets.WidgetWeather
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.awaitCancellation
import kotlin.math.*

/** One bounded scene per visible entry or weather event; cancellation leaves no frame timer. */
@Composable
internal fun rememberWeatherScenePhase(event: String, theme: LauncherTheme, settings: WeatherMotionSettings,
    reduced: Boolean, ready: Boolean): State<Float> {
    val phase = remember { Animatable(1f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val blocked = reduced || settings.mode == WeatherMotionMode.OFF || LocalSystemPowerSaving.current ||
        LocalThemePreview.current || LocalAppearance.current.highContrast
    LaunchedEffect(lifecycle, event, theme, settings.bounded(), blocked, ready) {
        phase.snapTo(1f)
        if (blocked || !ready || !ValueAnimator.areAnimatorsEnabled()) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                phase.snapTo(0f)
                phase.animateTo(1f, tween(settings.bounded().durationMs, easing = LinearEasing))
                awaitCancellation()
            } finally { withContext(NonCancellable) { phase.snapTo(1f) } }
        }
    }
    return phase.asState()
}

@Composable
internal fun rememberPs4WeatherPhase(event: String, settings: WeatherMotionSettings, reduced: Boolean, ready: Boolean): State<Float> =
    rememberWeatherScenePhase(event, LauncherTheme.PS4, settings, reduced, ready)

/** A single ribbon carries the weather scene from the icon across its surface. */
@Composable
internal fun Ps4WeatherRibbon(condition: WeatherCondition, settings: WeatherMotionSettings, phase: State<Float>,
    modifier: Modifier = Modifier, fromRight: Boolean = true,
    showWeatherScene: Boolean = true, suppressedBy: State<Float>? = null,
) {
    if (settings.mode == WeatherMotionMode.OFF || LocalSystemPowerSaving.current ||
        LocalAppearance.current.highContrast || LocalThemePreview.current) return
    val intensity = settings.bounded().intensity / 100f
    val constrained = LocalDecorationConstrained.current
    val accent = LocalSkinStyle.current.accent
    Spacer(modifier.clearAndSetSemantics {}.drawWithCache {
        val ribbon = Path()
        val paths = List(5) { Path() }
        val cloudPixels = Path()
        val glow = Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF90DFFF), Color.Transparent))
        onDrawBehind {
            val p = phase.value
            if (p <= 0f || p >= 1f || (suppressedBy?.value ?: 1f) < 1f) return@onDrawBehind
            val width = size.width
            val height = size.height
            val intro = (p / .18f).coerceIn(0f, 1f)
            val fade = ((1f - p) / .28f).coerceIn(0f, 1f)
            val travel = androidx.compose.animation.core.FastOutSlowInEasing.transform(p)
            val head = (if (fromRight) 1f - travel else travel) * width
            val strength = intro * fade * intensity
            val y = height * .68f
            ribbon.reset()
            ribbon.moveTo(0f, y)
            ribbon.cubicTo(width * .27f, y + height * .23f, head, y - height * .5f, width, y - height * .19f)
            clipRect(max(0f, head - width * .42f), 0f, min(width, head + width * .42f), height) {
                if (!constrained.value) drawPath(ribbon, glow, alpha = strength * .32f, style = Stroke(5.dp.toPx()))
                drawPath(ribbon, glow, alpha = strength * .84f, style = Stroke(1.dp.toPx()))
                if (showWeatherScene && !constrained.value) drawWeatherScene(p, condition, WeatherTreatment.CONSOLE_WAVE,
                    accent, strength * .57f, cloudPixels, paths)
            }
        }
    })
}

/** Decorative only: no pointer input, no duplicate accessibility nodes, no polling or widget host. */
@Composable
internal fun WeatherSweep(
    theme: LauncherTheme,
    reading: WidgetWeather?,
    settings: WeatherMotionSettings,
    reduced: Boolean,
    modifier: Modifier = Modifier,
    foldProgress: State<Float>? = null,
    scenePhase: State<Float>? = null,
) {
    val motion = settings.bounded()
    val blocked = reduced || LocalSystemPowerSaving.current || LocalAppearance.current.highContrast || LocalThemePreview.current
    val accent=LocalSkinStyle.current.accent
    val treatment=weatherTreatment(theme)
    val density = LocalDensity.current.density
    val dynamicPaths = remember { List(5) { Path() } }
    val cloudPixels = remember(density, treatment) { Path().apply {
        for (col in -8..8) for (row in -3..3) {
            val x=col/8f; val y=row/3f
            if (x*x+y*y<1f || (x+.25f)*(x+.25f)+(y+.45f)*(y+.45f)<.55f) {
                val radius = density * if (treatment == WeatherTreatment.LCD_SCAN) 1.3f else 1f
                val bounds = Rect(col*4*density-radius, row*4*density-radius, col*4*density+radius, row*4*density+radius)
                if (treatment == WeatherTreatment.LCD_SCAN || treatment == WeatherTreatment.DOT_MATRIX) addRect(bounds) else addOval(bounds)
            }
        }
    } }
    val condition = remember(motion.condition, reading?.conditionText, reading?.symbolCondition) {
        if (motion.condition != WeatherCondition.AUTO) motion.condition
        else providerWeatherCondition(reading?.conditionText.orEmpty())
            .takeUnless { it == WeatherCondition.AMBIENT } ?: reading?.symbolCondition ?: WeatherCondition.AMBIENT
    }
    val phase = scenePhase ?: rememberWeatherScenePhase(
        "${condition.name}:${reading?.place}", theme, motion, blocked, ready = reading != null,
    )
    val constrained = LocalDecorationConstrained.current
    Canvas(modifier.clipToBounds().clearAndSetSemantics {}) {
        val p = phase.value
        if (blocked || !motion.shouldAnimate(false, reading != null, ValueAnimator.areAnimatorsEnabled()) ||
            p <= 0f || p >= 1f || constrained.value) return@Canvas
        val fold = (1f - (foldProgress?.value ?: 0f) / .45f).coerceIn(0f, 1f)
        val fade = min(p / .16f, (1f - p) / .22f).coerceIn(0f, 1f) * fold
        drawWeatherScene(p, condition, treatment, accent, fade * motion.intensity / 100f, cloudPixels, dynamicPaths)
    }
}

private fun DrawScope.drawWeatherScene(p:Float,condition:WeatherCondition,treatment:WeatherTreatment,accent:Color,strength:Float,cloudPixels:Path,paths:List<Path>) {
    val dots=treatment==WeatherTreatment.DOT_MATRIX
    val lcd=treatment==WeatherTreatment.LCD_SCAN
    val unit=dp(1f)
    val tau=(PI*2).toFloat()
    fun wrap(v:Float)=v-floor(v)
    fun ink(color:Color,alpha:Float)=color.copy(alpha=(alpha*strength).coerceIn(0f,1f))
    fun particle(x:Float,y:Float,r:Float,color:Color,alpha:Float) {
        val c=Offset(x,y)
        if(dots) {
            // Nothing uses discrete ink segments, without circular glow plates.
            drawRect(ink(color,alpha), Offset(x-r*1.4f,y-r*.55f), Size(r*2.8f,r*1.1f))
        } else if(lcd)drawRect(ink(color,alpha),Offset(x-r,y-r),Size(r*2,r*2))
        else drawCircle(ink(color,alpha),r,c)
    }
    fun cloud(cx:Float,cy:Float,r:Float,color:Color) {
        if(dots||lcd) {
            withTransform({ translate(cx,cy); scale(r/(32f*unit),r/(32f*unit),Offset.Zero) }) {
                if(dots)drawPath(cloudPixels,ink(color,.12f),style=Stroke(dp(3f)))
                drawPath(cloudPixels,ink(color,.60f))
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
    val wet = condition in wetConditions
    // Material follows the actual condition, so a clear sky never receives rain ripples.
    when(treatment) {
        WeatherTreatment.DOT_MATRIX -> Unit
        WeatherTreatment.CLOUD_BANK -> if (condition != WeatherCondition.AMBIENT && condition != WeatherCondition.AUTO) for(i in 0..2) {
            val center=Offset((wrap(p+i/3f)*1.6f-.3f)*size.width,size.height*(.22f+i*.22f))
            val daylight = condition == WeatherCondition.SUN || condition == WeatherCondition.PARTLY_CLOUDY
            for(layer in 1..3)drawCircle(ink(if(daylight) Color(0xFFFFE1AE) else Color(0xFFB9E7FF),.025f),size.height*(.18f+layer*.12f),center)
        }
        WeatherTreatment.WATER_GLASS -> if (wet) for(i in 0..5) {
            val t=wrap(p+i/6f);val r=dp(8f)+t*size.height*.45f
            drawOval(ink(accent,(1f-t)*.3f*min(t/.12f,1f)),Offset(size.width*(i+.5f)/6-r,size.height*.75f-r*.25f),Size(r*2,r*.5f),style=Stroke(dp(1f)))
        }
        WeatherTreatment.COLOR_RIBBON -> for(i in 0..15) {
            val x=wrap(p+i*.618034f)*size.width
            val y=(.15f+wrap(i*.381966f)*.7f)*size.height
            val tile=dp(3f+(i%3)*2f)
            val light=.08f+.10f*(.5f+.5f*sin(tau*(p+i*.13f)))
            drawRoundRect(ink(if(i%2==0)accent else Color(0xFFEBD4A6),light),Offset(x,y),Size(tile,tile),CornerRadius(dp(.7f)))
        }
        WeatherTreatment.AURORA -> for(i in 0..3) {
            val y=size.height*(.20f+i*.18f)
            val swing=sin(tau*(p+i*.14f))*size.height*.22f
            val path=paths[i].apply {reset();moveTo(-size.width*.1f,y);cubicTo(size.width*.25f,y+swing,size.width*.62f,y-swing,size.width*1.1f,y)}
            val color=if(i%2==0)accent else Color(0xFF75CDEB)
            drawPath(path,ink(color,.22f),style=Stroke(dp(1.5f+i)))
        }
        WeatherTreatment.CONSOLE_WAVE -> {
            for(i in 0..2) {
                val y=size.height*(.28f+i*.18f)
                val swing=sin(tau*(p*.55f+i*.22f))*size.height*.15f
                val path=paths[i].apply{reset();moveTo(0f,y);cubicTo(size.width*.30f,y+swing,size.width*.68f,y-swing,size.width,y)}
                drawPath(path,ink(Color(0xFFB8E7FF),.30f),style=Stroke(dp(.8f+i*.3f)))
            }
            for(i in 0..3) {
                val x=wrap(p*.55f+i*.25f)*size.width;val y=size.height*(.18f+i*.19f)
                drawRect(ink(Color.White,.20f),Offset(x,y),Size(dp(2f),dp(2f)),style=Stroke(dp(.7f)))
            }
        }
        WeatherTreatment.LCD_SCAN -> {
            for(i in 0..12)drawLine(ink(accent,.08f),Offset(0f,size.height*i/12),Offset(size.width,size.height*i/12),unit)
            for(i in 0..9)particle(wrap(p+i/10f)*size.width,size.height*(.2f+(i%3)*.3f),dp(1.5f),accent,.25f)
        }
    }
    val cool=if(dots||lcd)accent else Color(0xFFB9E7FF)
    val warm=if(dots||lcd)accent else Color(0xFFFFD785)
    when(condition) {
        WeatherCondition.SUN, WeatherCondition.PARTLY_CLOUDY -> {
            val cx=size.width*.74f;val cy=size.height*.42f;val radius=size.height*.19f
            if(dots) {
                // A segmented cross of daylight rather than a circular badge.
                for (i in -4..4) {
                    particle(cx+i*dp(3f),cy,dp(1f),warm,.7f)
                    if(i != 0) particle(cx,cy+i*dp(3f),dp(1f),warm,.7f)
                }
                for (x in -1..1 step 2) for (y in -1..1 step 2)
                    particle(cx+x*dp(7f),cy+y*dp(7f),dp(1.1f),warm,.55f)
            } else if(lcd) {
                for(col in -6..6)for(row in -6..6) {
                    val d=col*col+row*row
                    if(d in 14..33)particle(cx+col*dp(3f),cy+row*dp(3f),dp(1f),warm,.75f)
                }
            } else {
                drawCircle(ink(warm,.05f),radius*2.4f,Offset(cx,cy))
                drawCircle(ink(warm,.13f),radius*1.7f,Offset(cx,cy))
                drawCircle(ink(warm,.55f),radius,Offset(cx,cy))
            }
            if(!dots) for(i in 0..11) {
                val a=i*tau/12+sin(tau*p)*.12f
                val r=radius*(1.5f+.12f*sin(tau*(p+i*.1f)))
                particle(cx+cos(a)*r,cy+sin(a)*r,dp(if(lcd)1.4f else 1.1f),warm,.65f)
            }
        }
        WeatherCondition.NIGHT, WeatherCondition.CLOUDY_NIGHT -> {
            val center = Offset(size.width*.76f,size.height*.35f)
            if (dots || lcd) {
                for (i in 0..7) particle(center.x + dp(i*2f),center.y + dp(abs(i-3)*2f),dp(1f),cool,.6f)
            } else drawArc(ink(cool,.6f),65f,230f,false,
                center-Offset(dp(10f),dp(12f)),Size(dp(20f),dp(24f)),style=Stroke(dp(2f)))
            for (i in 0..5) particle(size.width*(.15f+i*.13f),size.height*(.18f+(i%3)*.18f),dp(.8f),cool,.45f)
        }
        WeatherCondition.CLOUDS -> Unit // The shared bank below also layers partly cloudy and night scenes.
        WeatherCondition.DRIZZLE, WeatherCondition.DOWNPOUR, WeatherCondition.SLEET, WeatherCondition.HAIL,
        WeatherCondition.RAIN,WeatherCondition.SNOW,WeatherCondition.STORM -> {
            val snow=condition==WeatherCondition.SNOW || condition==WeatherCondition.SLEET
            val count = when { condition == WeatherCondition.DRIZZLE -> 8; condition == WeatherCondition.DOWNPOUR -> 28;
                treatment == WeatherTreatment.CONSOLE_WAVE -> if (snow) 10 else 14; snow -> 22; else -> 28 }
            for(i in 0 until count) {
                val fall=wrap(p*(if(snow)1f else 2f)+i*.381966f)
                val x=wrap(i*.618034f+p)*size.width
                val y=fall*(size.height+dp(12f))-dp(6f)
                val a=(sin(fall*PI).toFloat()*.65f).coerceIn(0f,1f)
                if(dots||lcd||snow) {
                    val drift=if(snow)sin(tau*(p+i*.31f))*dp(5f) else 0f
                    particle(x+drift,y,dp(if(snow)1.4f else 1f),cool,a)
                    if(!snow)particle(x-dp(1f),y-dp(4f),dp(.75f),cool,a*.45f)
                } else if (condition == WeatherCondition.HAIL) {
                    drawRect(ink(cool,a),Offset(x,y),Size(dp(2.5f),dp(2.5f)))
                } else if (treatment == WeatherTreatment.CONSOLE_WAVE) {
                    // Water catches the PS4 glass: short trail, bead and a fine reflected edge.
                    drawLine(ink(cool,a*.45f),Offset(x,y-dp(9f)),Offset(x,y),dp(1f))
                    drawOval(ink(cool,a*.48f),Offset(x-dp(1.8f),y),Size(dp(3.6f),dp(6f)),style=Stroke(dp(.7f)))
                    drawLine(ink(Color.White,a*.7f),Offset(x-dp(.6f),y+dp(1f)),Offset(x-dp(.6f),y+dp(3f)),dp(.6f))
                } else drawLine(ink(cool,a),Offset(x,y),Offset(x+dp(2f),y+dp(if(treatment==WeatherTreatment.WATER_GLASS)10f else 6f)),dp(1.4f))
            }
            if(condition==WeatherCondition.STORM) {
                // Slow breathing bolt, never a full-field flash.
                val alpha=.22f+.25f*(.5f+.5f*sin(tau*p))
                val x=size.width*.77f;val y=size.height*.16f
                if(dots) {
                    for(row in stormPixels.indices) for(col in stormPixels[row].indices) if(stormPixels[row][col]=='1')
                        particle(x+(col-3)*dp(3f),y+row*dp(3f),dp(1f),warm,alpha)
                } else {
                    val bolt=paths[4].apply {reset();moveTo(x,y);lineTo(x-dp(10f),y+dp(15f));lineTo(x+dp(2f),y+dp(13f));lineTo(x-dp(5f),y+dp(31f))}
                    drawPath(bolt,ink(warm,alpha),style=Stroke(dp(if(lcd)2f else 1.6f)))
                }
            }
        }
        WeatherCondition.FOG -> for(i in 0..4) {
            val x=(wrap(p+i*.2f)*1.7f-.35f)*size.width
            val y=size.height*(.2f+i*.15f)
            if(dots||lcd)for(j in 0..20)particle(x+j*dp(4f),y,dp(1f),cool,.32f)
            else drawRoundRect(ink(cool,.11f),Offset(x,y),Size(size.width*.45f,dp(5f)),CornerRadius(dp(8f)))
        }
        WeatherCondition.WIND -> for(i in 0..3) {
            val x=(wrap(p+i*.25f)*1.5f-.25f)*size.width
            val y=size.height*(.25f+i*.17f)
            if(dots||lcd) for(j in 0..8) particle(x+j*dp(4f),y,dp(1f),cool,.45f)
            else drawLine(ink(cool,.32f),Offset(x,y),Offset(x+size.width*.22f,y-dp(3f)),dp(1f))
        }
        WeatherCondition.AUTO, WeatherCondition.AMBIENT -> Unit // Unknown remains neutral.
    }
    if (condition in cloudyConditions ||
        (wet && treatment == WeatherTreatment.CLOUD_BANK)) {
        val partly = condition == WeatherCondition.PARTLY_CLOUDY || condition == WeatherCondition.CLOUDY_NIGHT
        for (i in 0..(if(partly) 0 else 1)) {
            val x=(.42f+i*.32f+sin(p*PI).toFloat()*.16f)*size.width
            cloud(x,size.height*(.40f+i*.26f),size.height*(if(partly) .27f else .35f),cool)
        }
    }
}

private fun DrawScope.dp(value:Float)=value.dp.toPx()
private val stormPixels = listOf("0001100", "0011100", "0111000", "1111110", "0001100", "0011000", "0110000")

private val wetConditions = setOf(WeatherCondition.RAIN, WeatherCondition.DRIZZLE, WeatherCondition.DOWNPOUR,
    WeatherCondition.STORM, WeatherCondition.SLEET, WeatherCondition.HAIL)
private val cloudyConditions = setOf(WeatherCondition.CLOUDS, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLOUDY_NIGHT)
