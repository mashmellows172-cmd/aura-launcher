package dev.senda.launcher.ui

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.FrameMetrics
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FrameReport(
    val running: Boolean = false,
    val frames: Int = 0,
    val slowFrames: Int = 0,
    val averageMs: Double = 0.0,
    val worstMs: Double = 0.0,
    val refreshHz: Float = 60f,
    val droppedReports: Int = 0,
)

val LocalFrameDiagnostics = staticCompositionLocalOf<FrameDiagnostics?> { null }

/**
 * Opt-in, in-memory sample of this Activity window only (not separate Dialog windows).
 * Slow means TOTAL_DURATION exceeds DEADLINE on API31+, with display interval
 * fallback on API29/30 or an unavailable deadline. This is diagnostic timing, not
 * a benchmark, compositor FPS, battery measurement or Android's official jank metric.
 * Android may drop metric callbacks; only received, valid samples are counted.
 */
class FrameDiagnostics(private val activity: ComponentActivity) : DefaultLifecycleObserver {
    private val main = Handler(Looper.getMainLooper())
    private val lock = Any()
    private val mutableReport = MutableStateFlow(FrameReport())
    val report: StateFlow<FrameReport> = mutableReport.asStateFlow()
    private var thread: HandlerThread? = null
    private var listener: Window.OnFrameMetricsAvailableListener? = null
    private var closed = false
    private var generation = 0L
    private var running = false
    private var frames = 0
    private var slow = 0
    private var dropped = 0
    private var totalNanos = 0.0
    private var worstNanos = 0L
    private var refreshHz = 60f

    init { activity.lifecycle.addObserver(this) }

    private fun onMain(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) action() else main.post { action() }
    }

    private fun displayRate(): Float = activity.window.decorView.display?.refreshRate
        ?.takeIf { it.isFinite() && it in 20f..1000f } ?: 60f

    private fun snapshot() = FrameReport(running, frames, slow,
        if (frames == 0) 0.0 else totalNanos / frames / 1_000_000.0,
        worstNanos / 1_000_000.0, refreshHz, dropped)

    private val publish = object : Runnable {
        override fun run() {
            synchronized(lock) {
                if (!running) return
                refreshHz = displayRate()
                mutableReport.value = snapshot()
            }
            main.postDelayed(this, 1000L)
        }
    }
    private val timeout = Runnable { stop() }

    fun start(): Unit = onMain {
        if (closed || !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return@onMain
        synchronized(lock) { if (running) return@onMain }
        val token = synchronized(lock) {
            generation++
            frames = 0; slow = 0; dropped = 0; totalNanos = 0.0; worstNanos = 0L
            refreshHz = displayRate(); running = true
            mutableReport.value = snapshot()
            generation
        }
        val worker = HandlerThread("AuraFrameDiagnostics").apply { start() }
        thread = worker
        val callback = Window.OnFrameMetricsAvailableListener { _, metrics, dropCount ->
            // Android reuses FrameMetrics: consume primitive values within this callback.
            val duration = metrics.getMetric(FrameMetrics.TOTAL_DURATION)
            val deadline = if (Build.VERSION.SDK_INT >= 31) metrics.getMetric(FrameMetrics.DEADLINE) else -1L
            synchronized(lock) {
                if (running && generation == token) {
                    dropped += dropCount.coerceAtLeast(0)
                    if (duration < 0L) return@synchronized
                    frames++
                    totalNanos += duration.toDouble()
                    if (duration > worstNanos) worstNanos = duration
                    val budget = if (deadline > 0L) deadline.toDouble() else 1_000_000_000.0 / refreshHz
                    if (duration.toDouble() > budget) slow++
                }
            }
        }
        listener = callback
        try {
            activity.window.addOnFrameMetricsAvailableListener(callback, Handler(worker.looper))
            main.postDelayed(publish, 1000L)
            main.postDelayed(timeout, 60_000L)
        } catch (_: RuntimeException) {
            stop()
        }
    }

    fun stop(): Unit = onMain {
        main.removeCallbacks(publish)
        main.removeCallbacks(timeout)
        synchronized(lock) {
            running = false
            generation++ // Ignore callbacks already queued by the old window subscription.
            mutableReport.value = snapshot()
        }
        listener?.let { runCatching { activity.window.removeOnFrameMetricsAvailableListener(it) } }
        listener = null
        thread?.quitSafely()
        thread = null
    }

    fun close(): Unit = onMain {
        if (closed) return@onMain
        closed = true
        stop()
        activity.lifecycle.removeObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) { stop() }
    override fun onDestroy(owner: LifecycleOwner) { close() }
}
