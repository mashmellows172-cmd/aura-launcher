package dev.senda.launcher.ui

import android.animation.ValueAnimator
import android.app.KeyguardManager
import android.content.*
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.rememberMotionPowerSaver
import kotlinx.coroutines.launch
import kotlin.math.*

enum class UnlockStyle(val title: String) {
    AUTO("Según el tema"), NONE("Sin efecto"), SLIDE("Slide clásico"), CRT("CRT suave"),
    FOLD("Fold expansivo"), ORBITAL("Orbital"), RIPPLE("Revelado circular"), RIBBON("Cinta PS4")
}

/** Observes authentication; never dismisses or replaces the system keyguard. */
class UnlockEvents(private val activity: ComponentActivity) : DefaultLifecycleObserver {
    var sequence by mutableIntStateOf(0); private set
    private var lockedObserved = false
    private var pendingAt = 0L
    private val keyguard get() = activity.getSystemService(KeyguardManager::class.java)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> { lockedObserved = true; pendingAt = 0 }
                Intent.ACTION_USER_PRESENT -> if (lockedObserved) {
                    pendingAt = SystemClock.elapsedRealtime(); consume()
                }
            }
        }
    }
    init {
        ContextCompat.registerReceiver(activity, receiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF); addAction(Intent.ACTION_USER_PRESENT)
        }, ContextCompat.RECEIVER_EXPORTED)
        activity.lifecycle.addObserver(this)
    }
    fun consume() {
        val guard = keyguard ?: return
        if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) || !activity.hasWindowFocus()) return
        if (guard.isDeviceLocked || guard.isKeyguardLocked) { lockedObserved = true; return }
        if (pendingAt != 0L) {
            if (SystemClock.elapsedRealtime() - pendingAt < 2000) sequence++
            pendingAt = 0; lockedObserved = false
        }
    }
    override fun onResume(owner: LifecycleOwner) { consume() }
    override fun onDestroy(owner: LifecycleOwner) {
        activity.unregisterReceiver(receiver); activity.lifecycle.removeObserver(this)
    }
}

private fun resolve(style: UnlockStyle, theme: LauncherTheme) = if (style != UnlockStyle.AUTO) style else when(theme) {
    LauncherTheme.PS4 -> UnlockStyle.RIBBON
    LauncherTheme.NOTHING -> UnlockStyle.RIPPLE
    LauncherTheme.RAZR -> UnlockStyle.CRT
    LauncherTheme.TOUCHWIZ -> UnlockStyle.ORBITAL
    LauncherTheme.SENSE -> UnlockStyle.SLIDE
    else -> UnlockStyle.FOLD
}

@Composable
fun UnlockEntry(sequence: Int, theme: LauncherTheme, reduced: Boolean, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("aura_unlock", Context.MODE_PRIVATE) }
    var revision by remember { mutableIntStateOf(0) }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> revision++ }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val selected = remember(revision) { runCatching { UnlockStyle.valueOf(prefs.getString("style", "AUTO")!!) }.getOrDefault(UnlockStyle.AUTO) }
    val duration = remember(revision) { prefs.getInt("duration", 420).coerceIn(200,650) }
    val intensity = remember(revision) { prefs.getFloat("intensity", .7f).let { if(it.isFinite()) it.coerceIn(0f,1f) else .7f } }
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    var resumed by remember { mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ -> resumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
        lifecycle.addObserver(observer); onDispose { lifecycle.removeObserver(observer) }
    }
    UnlockSurface(sequence, resolve(selected,theme), duration, intensity,
        reduced || rememberMotionPowerSaver() || !resumed || LocalThemePreview.current, content)
}

@Composable
private fun UnlockSurface(sequence: Int, style: UnlockStyle, duration: Int, intensity: Float, reduced: Boolean, content: @Composable () -> Unit) {
    val progress = remember { Animatable(1f) }
    var consumedSequence by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val circle = remember { Path() }
    val wave = remember { Path() }
    LaunchedEffect(sequence, style, duration, intensity, reduced) {
        progress.snapTo(1f)
        if (sequence == 0 || sequence == consumedSequence) return@LaunchedEffect
        consumedSequence = sequence
        if (reduced || intensity <= 0f || style == UnlockStyle.NONE || !ValueAnimator.areAnimatorsEnabled()) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, tween(duration, easing = FastOutSlowInEasing))
    }
    Box(Modifier.fillMaxSize().pointerInput(Unit) {
        awaitEachGesture {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.changes.any { it.pressed } && progress.value < 1f) scope.launch { progress.snapTo(1f) }
            // Observe only: the original down event reaches the app unchanged.
            do { val next = awaitPointerEvent() } while(next.changes.any { it.pressed })
        }
    }) {
        Box(Modifier.fillMaxSize().graphicsLayer {
            val p = progress.value; val remaining = 1f-p
            alpha = 1f - remaining * .35f * intensity
            when(style) {
                UnlockStyle.SLIDE -> translationX = -size.width * .12f * remaining * intensity
                UnlockStyle.CRT -> { scaleY = 1f-(1f-max(.025f,p))*intensity; scaleX = 1f-.04f*(1f-p)*intensity }
                UnlockStyle.FOLD -> { scaleX = 1f-.35f*(1f-p)*intensity; rotationY = remaining * -14f * intensity; cameraDistance = 24*density }
                UnlockStyle.ORBITAL -> { scaleX=1f+.18f*remaining*intensity; scaleY=scaleX }
                UnlockStyle.RIBBON -> translationY = size.height*.025f*remaining*intensity
                else -> Unit
            }
        }.then(if(style == UnlockStyle.RIPPLE) Modifier.drawWithContent {
            val p = progress.value
            if(p>=1f) drawContent() else {
                circle.reset()
                val radius = hypot(size.width,size.height)*(1f-(1f-p)*intensity)
                circle.addOval(androidx.compose.ui.geometry.Rect(Offset(size.width*.5f,size.height*.85f),radius))
                clipPath(circle) { this@drawWithContent.drawContent() }
            }
        } else Modifier)) { content() }
        if(style == UnlockStyle.RIBBON || style == UnlockStyle.CRT) Canvas(Modifier.matchParentSize()) {
            val p=progress.value
            if(p<1f) {
                val y=size.height*p
                wave.reset(); wave.moveTo(0f,y)
                wave.cubicTo(size.width*.3f,y-size.height*.08f,size.width*.7f,y+size.height*.08f,size.width,y)
                drawPath(wave,Color(0xFF8FDFFF).copy(alpha=sin(p*PI.toFloat()).coerceIn(0f,1f)*.65f*intensity),style=androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx()))
            }
        }
    }
}

@Composable
internal fun UnlockSettingsDialog(theme: LauncherTheme, onDismiss: () -> Unit) {
    val context=LocalContext.current
    val prefs=remember(context) { context.getSharedPreferences("aura_unlock",Context.MODE_PRIVATE) }
    var selected by remember { mutableStateOf(runCatching { UnlockStyle.valueOf(prefs.getString("style","AUTO")!!) }.getOrDefault(UnlockStyle.AUTO)) }
    var duration by remember { mutableFloatStateOf(prefs.getInt("duration",420).coerceIn(200,650).toFloat()) }
    var intensity by remember { mutableFloatStateOf(prefs.getFloat("intensity",.7f).let { if(it.isFinite()) it.coerceIn(0f,1f) else .7f }) }
    var preview by remember { mutableIntStateOf(0) }
    val reduced=LocalMotionSettings.current.reduceMotion || dev.senda.launcher.data.LocalAppearance.current.highContrast || rememberMotionPowerSaver()
    AuraAlertDialog(onDismissRequest=onDismiss, title={Text("Entrada tras desbloquear")}, text={
        Column(Modifier.heightIn(max=500.dp).verticalScroll(rememberScrollState())) {
            Text("Se aplica después de la autenticación de Android. Tocar cancela el movimiento.")
            UnlockStyle.entries.forEach { style ->
                TextButton(onClick={selected=style},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) { Text((if(selected==style) "✓ " else "")+style.title) }
            }
            Text("Duración: ${duration.toInt()} ms")
            Slider(duration,{duration=it},valueRange=200f..650f)
            Text("Intensidad: ${(intensity*100).toInt()}%")
            Slider(intensity,{intensity=it})
            Box(Modifier.fillMaxWidth().height(100.dp)) {
                UnlockSurface(preview,resolve(selected,theme),duration.toInt(),intensity,reduced) {
                    Surface(color=Color(0xFF003087),modifier=Modifier.fillMaxSize()) { Text("Aura · Vista previa",Modifier.padding(24.dp),color=Color.White) }
                }
            }
            TextButton(onClick={preview++}) { Text("Reproducir vista previa") }
            Text("Ahorro y movimiento reducido muestran el estado final. Esta preferencia es local y aún no está incluida en el respaldo JSON.",style=MaterialTheme.typography.bodySmall)
        }
    },confirmButton={TextButton(onClick={prefs.edit().putString("style",selected.name).putInt("duration",duration.toInt()).putFloat("intensity",intensity).apply();onDismiss()}) {Text("Aplicar")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}
