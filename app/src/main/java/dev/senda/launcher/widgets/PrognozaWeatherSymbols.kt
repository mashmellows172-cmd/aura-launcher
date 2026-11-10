package dev.senda.launcher.widgets

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import dev.senda.launcher.data.WeatherCondition
import dev.senda.launcher.data.WeatherCondition.*

internal class PrognozaWeatherSymbols(context: Context) {
    private val symbols by lazy {
        val resources = runCatching {
            context.packageManager.getResourcesForApplication(PROGNOZA_PACKAGE)
        }.getOrNull() ?: return@lazy emptyMap()

        val matches = mutableMapOf<Long, WeatherCondition>()
        val ambiguous = mutableSetOf<Long>()
        for ((condition, names) in families) {
            for (prefix in listOf("light", "dark")) {
                for (name in names) {
                    val id = resources.getIdentifier("${prefix}_$name", "drawable", PROGNOZA_PACKAGE)
                    if (id == 0) continue
                    val bitmap = runCatching {
                        resources.getDrawable(id, null).toBitmap(WEATHER_ICON_SIZE, WEATHER_ICON_SIZE)
                    }.getOrNull() ?: continue
                    val key = fingerprint(bitmap)
                    val previous = matches.put(key, condition)
                    if (previous != null && previous != condition) ambiguous.add(key)
                }
            }
        }
        matches - ambiguous
    }

    fun identify(bitmap: Bitmap): WeatherCondition? = symbols[fingerprint(bitmap)]
}

internal const val PROGNOZA_PACKAGE = "hr.dtakac.prognoza"
internal const val WEATHER_ICON_SIZE = 96

// Prognoza 3.5.0 publishes the current symbol as a drawable, with no content description.
private val families = mapOf(
    SUN to listOf("01d"),
    NIGHT to listOf("01n", "01m"),
    PARTLY_CLOUDY to listOf("02d", "03d"),
    CLOUDY_NIGHT to listOf("02m", "02n", "03m", "03n"),
    CLOUDS to listOf("04"),
    DRIZZLE to (listOf("46") + dayVariants("40")),
    RAIN to (listOf("09") + dayVariants("05")),
    DOWNPOUR to (listOf("10") + dayVariants("41")),
    SLEET to (listOf("12", "47", "48") + dayVariants("07", "42", "43")),
    SNOW to (listOf("13", "49", "50") + dayVariants("08", "44", "45")),
    STORM to (listOf("11", "14", "22", "23", "30", "31", "32", "33", "34") +
        dayVariants("06", "20", "21", "24", "25", "26", "27", "28", "29")),
    FOG to listOf("15"),
)

private fun dayVariants(vararg numbers: String) = numbers.flatMap { number ->
    listOf("${number}d", "${number}m", "${number}n")
}

private fun fingerprint(bitmap: Bitmap): Long {
    val sample = Bitmap.createScaledBitmap(bitmap, 24, 24, true)
    val pixels = IntArray(sample.width * sample.height)
    sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
    var hash = 1125899906842597L
    for (pixel in pixels) hash = 31L * hash + pixel
    return hash
}
