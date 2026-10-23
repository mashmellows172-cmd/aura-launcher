package dev.senda.launcher

import dev.senda.launcher.data.*
import org.junit.Assert.*
import org.junit.Test

class WeatherMotionTest {
    @Test fun movementRequiresDataAndRespectsAllOffSwitches() {
        val settings=WeatherMotionSettings()
        assertTrue(settings.shouldAnimate(false,true,true))
        assertFalse(settings.shouldAnimate(true,true,true))
        assertFalse(settings.shouldAnimate(false,false,true))
        assertFalse(settings.shouldAnimate(false,true,false))
        assertFalse(settings.copy(mode=WeatherMotionMode.OFF).shouldAnimate(false,true,true))
        assertEquals(WeatherMotionMode.ONCE,settings.mode)
    }
    @Test fun settingsAreBoundedAndLegacyValuesFallback() {
        assertEquals(3000,WeatherMotionSettings(durationMs=-1,intensity=0).bounded().durationMs)
        assertEquals(20,WeatherMotionSettings(durationMs=-1,intensity=0).bounded().intensity)
        assertEquals(12000,WeatherMotionSettings(durationMs=90000,intensity=999).bounded().durationMs)
        assertEquals(100,WeatherMotionSettings(durationMs=90000,intensity=999).bounded().intensity)
        assertEquals(WeatherMotionMode.ONCE,WeatherMotionMode.fromStored("unknown"))
    }
    @Test fun sweepCrossesWholeClockWeatherSpanWithoutJumpingBack() {
        assertEquals(-40f,sweepX(0f,300f,40f),.001f)
        assertEquals(300f,sweepX(1f,300f,40f),.001f)
        val positions=(0..10).map { sweepX(it/10f,300f,40f) }
        assertTrue(positions.zipWithNext().all { (a,b)->b>a })
        assertEquals(300f,sweepX(2f,300f,40f),.001f)
    }
    @Test fun mainAndEveryRetroHaveTheirOwnTreatment() {
        val themes=listOf(LauncherTheme.NOTHING,LauncherTheme.SENSE,LauncherTheme.TOUCHWIZ,LauncherTheme.OPTIMUS,LauncherTheme.XPERIA,LauncherTheme.RAZR)
        assertEquals(6,themes.map(::weatherTreatment).toSet().size)
    }
}
