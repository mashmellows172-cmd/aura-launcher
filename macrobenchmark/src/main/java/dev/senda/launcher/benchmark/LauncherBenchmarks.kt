package dev.senda.launcher.benchmark
import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PACKAGE="dev.senda.launcher.benchmark"
private fun homeIntent()=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setComponent(ComponentName(PACKAGE,"dev.senda.launcher.MainActivity"))
@RunWith(AndroidJUnit4::class)
class LauncherBenchmarks {
    @get:Rule val rule=MacrobenchmarkRule()
    @Test fun coldStartupUncompiled()=startup(CompilationMode.None())
    @Test fun coldStartupProfileRequired()=startup(CompilationMode.Partial(BaselineProfileMode.Require))
    private fun startup(mode:CompilationMode) {
        rule.measureRepeated(packageName=PACKAGE,metrics=listOf(StartupTimingMetric()),compilationMode=mode,startupMode=StartupMode.COLD,iterations=10,
            setupBlock={pressHome()}) {
            startActivityAndWait(homeIntent())
            check(device.wait(Until.hasObject(By.res("aura-home-ready")),15_000)){"Home data not ready"}
        }
    }
    @Test fun alphabetAndScroll() {
        rule.measureRepeated(packageName=PACKAGE,metrics=listOf(FrameTimingMetric()),compilationMode=CompilationMode.Partial(),iterations=10,
            setupBlock={startActivityAndWait(homeIntent());check(device.wait(Until.hasObject(By.res("aura-home-ready")),15_000))}) {
            val rail=checkNotNull(device.findObject(By.res("aura-alphabet-rail"))){"Alphabet missing"}.visibleBounds
            device.swipe(rail.centerX(),rail.top+8,rail.centerX(),rail.bottom-8,60)
            device.swipe(rail.centerX(),rail.bottom-8,rail.centerX(),rail.top+8,60)
            val list=checkNotNull(device.findObject(By.res("aura-app-list"))){"List missing"}.visibleBounds
            repeat(3){device.swipe(list.centerX(),list.bottom-20,list.centerX(),list.top+20,20)}
            device.waitForIdle()
        }
    }
}
