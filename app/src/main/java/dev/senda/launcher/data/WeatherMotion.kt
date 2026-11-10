package dev.senda.launcher.data

enum class WeatherMotionMode(val label:String) {
    OFF("Desactivadas"), ONCE("Al entrar al inicio"), REPEAT("Repetición");
    companion object { fun fromStored(value:String?)=entries.firstOrNull { it.name==value } ?: ONCE }
}

data class WeatherMotionSettings(val mode:WeatherMotionMode=WeatherMotionMode.ONCE,val durationMs:Int=8000,val intensity:Int=60,val condition:WeatherCondition=WeatherCondition.AUTO) {
    fun bounded()=copy(durationMs=durationMs.coerceIn(3000,12000),intensity=intensity.coerceIn(20,100))
    fun shouldAnimate(reduced:Boolean,hasReading:Boolean,systemEnabled:Boolean)=mode!=WeatherMotionMode.OFF&&!reduced&&hasReading&&systemEnabled
}

internal enum class WeatherTreatment { DOT_MATRIX, CLOUD_BANK, WATER_GLASS, COLOR_RIBBON, AURORA, LCD_SCAN, CONSOLE_WAVE }
internal fun weatherTreatment(theme:LauncherTheme)=when(theme) {
    LauncherTheme.SENSE->WeatherTreatment.CLOUD_BANK
    LauncherTheme.TOUCHWIZ->WeatherTreatment.WATER_GLASS
    LauncherTheme.OPTIMUS->WeatherTreatment.COLOR_RIBBON
    LauncherTheme.XPERIA->WeatherTreatment.AURORA
    LauncherTheme.PS4->WeatherTreatment.CONSOLE_WAVE
    LauncherTheme.RAZR->WeatherTreatment.LCD_SCAN
    else->WeatherTreatment.DOT_MATRIX
}
internal fun sweepX(progress:Float,width:Float,glyph:Float)=-glyph+(width+glyph)*progress.coerceIn(0f,1f)


/** AUTO trusts only explicit provider text. Manual scenes are decoration, not forecasts. */
enum class WeatherCondition(val label:String) {
    AUTO("Según el proveedor"), AMBIENT("Atmósfera neutra"), SUN("Soleado"), CLOUDS("Nublado"),
    RAIN("Lluvia"), SNOW("Nieve"), STORM("Tormenta"), FOG("Niebla"),
    WIND("Viento"), NIGHT("Noche despejada"), PARTLY_CLOUDY("Parcialmente nublado"),
    CLOUDY_NIGHT("Noche con nubes"), DRIZZLE("Llovizna"), DOWNPOUR("Lluvia intensa"),
    SLEET("Aguanieve"), HAIL("Granizo");
    companion object { fun fromStored(value:String?)=entries.firstOrNull { it.name==value } ?: AUTO }
}

internal fun WeatherCondition.atmosphericScene() = when (this) {
    WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLOUDY_NIGHT -> WeatherCondition.CLOUDS
    WeatherCondition.DRIZZLE, WeatherCondition.DOWNPOUR -> WeatherCondition.RAIN
    WeatherCondition.SLEET, WeatherCondition.HAIL -> WeatherCondition.SNOW
    WeatherCondition.WIND, WeatherCondition.NIGHT -> WeatherCondition.AMBIENT
    else -> this
}

internal fun providerWeatherCondition(description:String):WeatherCondition {
    val text=java.text.Normalizer.normalize(description.lowercase(java.util.Locale.ROOT),java.text.Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"")
    fun has(words:String)=Regex("(?<![a-z])(?:$words)(?![a-z])").containsMatchIn(text)
    // A description containing forecasts/probabilities cannot safely identify the current condition.
    if ('%' in text || has("probabilidad|probability|chance|tomorrow|manana|forecast|later") || "sin lluvia" in text || "no rain" in text) return WeatherCondition.AMBIENT
    return when {
        has("thunderstorm|thunderstorms|thunder|tormenta|tormentas|burza|burze")->WeatherCondition.STORM
        has("hail|hailstones|granizo|grad")->WeatherCondition.HAIL
        has("sleet|wintry mix|aguanieve|agua nieve|deszcz ze sniegiem")->WeatherCondition.SLEET
        has("snow|snowy|snowfall|nieve|nevando|snieg|sniezne")->WeatherCondition.SNOW
        has("drizzle|llovizna|mzawka")->WeatherCondition.DRIZZLE
        has("heavy rain|heavy showers|downpour|lluvia intensa|lluvia fuerte|lluvia torrencial|ulewa")->WeatherCondition.DOWNPOUR
        has("rain|rainy|showers|lluvia|lluvias|deszcz|opady")->WeatherCondition.RAIN
        has("fog|foggy|mist|niebla|neblina|bruma|mgla")->WeatherCondition.FOG
        has("partly cloudy night|partly cloudy at night|cloudy night|noche con nubes|noche parcialmente nublada|noche nublada")->WeatherCondition.CLOUDY_NIGHT
        has("partly cloudy|partially cloudy|mostly sunny|parcialmente nublado|parcialmente nuboso|intervalos nubosos|czesciowe zachmurzenie")->WeatherCondition.PARTLY_CLOUDY
        has("cloudy|clouds|overcast|nublado|nublados|nuboso|nubosos|nubosidad|nubes|zachmurzenie|pochmurno")->WeatherCondition.CLOUDS
        has("clear night|clear at night|noche despejada|noche clara|despejado de noche|bezchmurna noc")->WeatherCondition.NIGHT
        text.trim() in setOf("wind", "viento", "wiatr") ||
            has("windy|strong winds|gusty|ventoso|viento fuerte|rafagas de viento|wietrznie|silny wiatr")->WeatherCondition.WIND
        has("sunny|clear|sunshine|soleado|despejado|slonecznie|bezchmurnie")->WeatherCondition.SUN
        else->WeatherCondition.AMBIENT
    }
}
