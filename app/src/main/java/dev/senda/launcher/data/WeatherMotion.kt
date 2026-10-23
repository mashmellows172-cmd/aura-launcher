package dev.senda.launcher.data

enum class WeatherMotionMode(val label:String) {
    OFF("Desactivadas"), ONCE("Al entrar al inicio"), REPEAT("Repetición");
    companion object { fun fromStored(value:String?)=entries.firstOrNull { it.name==value } ?: ONCE }
}

data class WeatherMotionSettings(val mode:WeatherMotionMode=WeatherMotionMode.ONCE,val durationMs:Int=8000,val intensity:Int=60,val condition:WeatherCondition=WeatherCondition.AUTO) {
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


/** AUTO trusts only explicit provider text. Manual scenes are decoration, not forecasts. */
enum class WeatherCondition(val label:String) {
    AUTO("Según el proveedor"), AMBIENT("Atmósfera neutra"), SUN("Soleado"), CLOUDS("Nublado"),
    RAIN("Lluvia"), SNOW("Nieve"), STORM("Tormenta"), FOG("Niebla");
    companion object { fun fromStored(value:String?)=entries.firstOrNull { it.name==value } ?: AUTO }
}
internal fun providerWeatherCondition(description:String):WeatherCondition {
    val text=java.text.Normalizer.normalize(description.lowercase(java.util.Locale.ROOT),java.text.Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"")
    fun has(words:String)=Regex("(?<![a-z])(?:$words)(?![a-z])").containsMatchIn(text)
    return when {
        has("thunderstorm|thunderstorms|thunder|tormenta|tormentas|burza|burze")->WeatherCondition.STORM
        has("snow|snowy|snowfall|nieve|nevando|snieg|sniezne")->WeatherCondition.SNOW
        has("rain|rainy|showers|drizzle|lluvia|lluvias|llovizna|deszcz|opady")->WeatherCondition.RAIN
        has("fog|foggy|mist|niebla|neblina|mgla")->WeatherCondition.FOG
        has("cloudy|clouds|overcast|nublado|nuboso|nubes|zachmurzenie|pochmurno")->WeatherCondition.CLOUDS
        has("sunny|clear sky|soleado|despejado|slonecznie|bezchmurnie")->WeatherCondition.SUN
        else->WeatherCondition.AMBIENT
    }
}
