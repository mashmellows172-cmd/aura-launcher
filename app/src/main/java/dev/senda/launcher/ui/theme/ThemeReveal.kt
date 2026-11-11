package dev.senda.launcher.ui.theme

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.LocalMotionSettings
import dev.senda.launcher.ui.LocalThemePreview
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.math.hypot
import kotlin.math.sin

/** Seven finite entrances. No wallpaper timer, bitmap capture or idle frame loop. */
enum class ThemeReveal(val label:String,val duration:Int) {
    DOTS("Puntos luminosos",320), FLIP("Pliegue Sense",340), RIPPLE("Gota Nature",380),
    CASCADE("Capas Optimus",330), WAVE("Onda Xperia",440), HINGE("Bisagra RAZR",300), FOCUS("Foco PS4",380)
}
fun themeReveal(theme:LauncherTheme)=when(theme) {
    LauncherTheme.SENSE->ThemeReveal.FLIP
    LauncherTheme.TOUCHWIZ->ThemeReveal.RIPPLE
    LauncherTheme.OPTIMUS->ThemeReveal.CASCADE
    LauncherTheme.XPERIA->ThemeReveal.WAVE
    LauncherTheme.RAZR->ThemeReveal.HINGE
    LauncherTheme.PS4->ThemeReveal.FOCUS
    else->ThemeReveal.DOTS
}

/** A changed key restarts a single reveal. Reads animation only in layer/draw. */
@Composable
fun Modifier.themeReveal(theme:LauncherTheme,key:Any?,reduced:Boolean=false):Modifier {
    val effect=themeReveal(theme)
    val settings=LocalMotionSettings.current
    val gentle=reduced||settings.reduceMotion
    val saving=rememberMotionPowerSaver()
    val disabled=LocalAppearance.current.highContrast||LocalThemePreview.current||!ValueAnimator.areAnimatorsEnabled()||saving
    val progress=remember(theme){Animatable(1f)}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val duration=if(gentle)150 else (effect.duration*settings.durationMs/360).coerceIn(160,800)
    val density=LocalDensity.current.density
    val ink=LocalSkinStyle.current.accent
    LaunchedEffect(theme,key,disabled,gentle,lifecycle,duration) {
        if(disabled){progress.snapTo(1f);return@LaunchedEffect}
        var played=false
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if(!played) {
                played=true
                try {
                    progress.snapTo(0f)
                    progress.animateTo(1f,tween(duration,easing=FastOutSlowInEasing))
                } finally {
                    withContext(NonCancellable){progress.snapTo(1f)}
                }
            }
            awaitCancellation()
        }
    }
    return this.graphicsLayer {
        val p=if(disabled)1f else progress.value
        val q=1f-p
        alpha=if(disabled)1f else .15f+.85f*p
        scaleX=1f;scaleY=1f;rotationX=0f;translationX=0f;translationY=0f
        if(gentle)return@graphicsLayer
        transformOrigin=when(effect){ThemeReveal.FLIP->TransformOrigin(.5f,0f);ThemeReveal.HINGE->TransformOrigin(.5f,.5f);else->TransformOrigin.Center}
        cameraDistance=18f*density
        when(effect) {
            ThemeReveal.DOTS->{translationY=q*3f*density}
            ThemeReveal.FLIP->{rotationX=-28f*q;translationY=-6f*q*density}
            ThemeReveal.RIPPLE->{scaleX=.94f+.06f*p;scaleY=scaleX}
            ThemeReveal.CASCADE->{translationY=18f*q*density}
            ThemeReveal.WAVE->{translationX=24f*q*density;translationY=sin(p*Math.PI).toFloat()*-4f*density}
            ThemeReveal.HINGE->{rotationX=36f*q;scaleY=.92f+.08f*p}
            ThemeReveal.FOCUS->{scaleX=.88f+.12f*p;scaleY=scaleX}
        }
    }.drawWithContent revealDraw@ {
        val p=if(disabled)1f else progress.value
        if(gentle||p>=.999f){drawContent();return@revealDraw}
        when(effect) {
            ThemeReveal.DOTS->{
                // Staggered pixel bands reveal actual content rather than a generic fade.
                val bands=8
                val path=Path()
                for(i in 0 until bands){val fill=(p*1.45f-i*.06f).coerceIn(0f,1f);path.addRect(Rect(0f,size.height*i/bands,size.width*fill,size.height*(i+1)/bands))}
                clipPath(path){this@revealDraw.drawContent()}
                for(i in 0 until 12){val x=size.width*(i+.5f)/12
                    if(theme==LauncherTheme.NOTHING) drawRect(ink.copy(alpha=(1f-p)*.45f),Offset(x,size.height*.5f),androidx.compose.ui.geometry.Size(2f*density,2f*density))
                    else drawCircle(ink.copy(alpha=(1f-p)*.45f),1.1f*density,Offset(x,size.height*.5f))}
            }
            ThemeReveal.RIPPLE->{
                val radius=hypot(size.width,size.height)*p
                val path=Path().apply{addOval(Rect(-radius,size.height*.5f-radius,radius,size.height*.5f+radius))}
                clipPath(path){this@revealDraw.drawContent()}
            }
            ThemeReveal.CASCADE->{clipRect(bottom=size.height*p){this@revealDraw.drawContent()}}
            else->drawContent()
        }
    }
}
