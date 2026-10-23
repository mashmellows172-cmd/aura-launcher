package dev.senda.launcher.data

enum class WeatherMotionMode(val label:String) {
    OFF("Desactivadas"), ONCE("Al entrar al inicio"), REPEAT("Repetición");
    companion object { fun fromStored(value:String?)=entries.firstOrNull { it.name==value } ?: ONCE }
}

data class WeatherMotionSettings(val mode:WeatherMotionMode=WeatherMotionMode.ONCE,val durationMs:Int=8000,val intensity:Int=60) {
    fun bounded()=copy(durationMs=durationMs.coerceIn(3000,12000),intensity=intensity.coerceIn(20,100))
    fun shouldAnimate(reduced:Boolean,hasReading:Boolean,systemEnabled:Boolean)=mode!=WeatherMotionMode.OFF&&!reduced&&hasReading&&systemEnabled
}

internal enum class WeatherTreatment { DOT_MATRIX, CLOUD_BANK, WATER_GLASS, COLOR_RIBBON, AURORA, LCD_SCAN }
internal fun weatherTreatment(theme:LauncherTheme)=when(theme) {
    LauncherTheme.SENSE->WeatherTreatment.CLOUD_BANK
    LauncherTheme.TOUCHWIZ->WeatherTreatment.WATER_GLASS
    LauncherTheme.OPTIMUS->WeatherTreatment.COLOR_RIBBON
    LauncherTheme.XPERIA->WeatherTreatment.AURORA
    LauncherTheme.RAZR->WeatherTreatment.LCD_SCAN
    else->WeatherTreatment.DOT_MATRIX
}
internal fun sweepX(progress:Float,width:Float,glyph:Float)=-glyph+(width+glyph)*progress.coerceIn(0f,1f)
