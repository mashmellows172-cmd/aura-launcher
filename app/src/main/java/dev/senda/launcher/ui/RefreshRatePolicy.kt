package dev.senda.launcher.ui

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Window

/** Main-thread window hint, released after interaction; never changes system settings. */
internal class RefreshRatePolicy(private val window: Window) {
    private val handler = Handler(Looper.getMainLooper())
    private var blocked = true
    private var resumed = false
    private var focused = false
    private var closed = false
    private val release = Runnable { apply(0f, 0) }

    fun configure(light: Boolean, saver: Boolean) {
        blocked = light || saver
        if (blocked) pauseHint()
    }

    fun resume() { resumed = true }
    fun focus(value: Boolean) {
        focused = value
        if (!value) pauseHint()
    }

    fun interaction() {
        if (closed || blocked || !resumed || !focused) return
        handler.removeCallbacks(release)
        if (Build.VERSION.SDK_INT >= 30) {
            apply(120f, 0)
        } else {
            val display = window.decorView.display
            val current = display?.mode
            val target = display?.supportedModes?.filter {
                current != null && it.physicalWidth == current.physicalWidth && it.physicalHeight == current.physicalHeight
            }?.maxByOrNull { it.refreshRate }
            apply(0f, target?.modeId ?: 0)
        }
        handler.postDelayed(release, 1500L)
    }

    fun pause() { resumed = false; pauseHint() }
    fun close() { closed = true; pause() }
    private fun pauseHint() { handler.removeCallbacks(release); apply(0f, 0) }

    private fun apply(rate: Float, mode: Int) {
        val attributes = window.attributes
        if (attributes.preferredRefreshRate == rate && attributes.preferredDisplayModeId == mode) return
        attributes.preferredRefreshRate = rate
        attributes.preferredDisplayModeId = mode
        window.attributes = attributes
    }
}
