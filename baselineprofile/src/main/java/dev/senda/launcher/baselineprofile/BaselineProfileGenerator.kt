package dev.senda.launcher.baselineprofile
import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule val rule=BaselineProfileRule()
    @Test fun generate() {
        // Explicit isolated package; generation of other variants must be selected deliberately.
        val pkg=InstrumentationRegistry.getArguments().getString("targetPackage") ?: "dev.senda.launcher.benchmark"
        require(pkg=="dev.senda.launcher.benchmark"){"Daily launcher profile generation is prohibited"}
        rule.collect(packageName=pkg,includeInStartupProfile=true) {
            pressHome()
            startActivityAndWait(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setComponent(ComponentName(pkg,"dev.senda.launcher.MainActivity")))
            check(device.wait(Until.hasObject(By.res("aura-home-ready")),15_000))
            val rail=checkNotNull(device.findObject(By.res("aura-alphabet-rail"))).visibleBounds
            device.swipe(rail.centerX(),rail.top+8,rail.centerX(),rail.bottom-8,60)
            val list=checkNotNull(device.findObject(By.res("aura-app-list"))).visibleBounds
            repeat(3){device.swipe(list.centerX(),list.bottom-20,list.centerX(),list.top+20,25)}
            device.waitForIdle()
        }
    }
}
