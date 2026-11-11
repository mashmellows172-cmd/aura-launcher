package dev.senda.launcher.data

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.ZoneId
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.coroutineContext

internal data class ForecastPlace(
    val id: Int, val name: String, val region: String, val latitude: Double, val longitude: Double,
) {
    val label get() = listOf(name, region).filter { it.isNotBlank() }.joinToString(", ")
    fun json() = JSONObject().put("id", id).put("name", name).put("region", region)
        .put("latitude", latitude).put("longitude", longitude)

    companion object {
        fun parse(json: JSONObject): ForecastPlace {
            val latitude = json.getDouble("latitude")
            val longitude = json.getDouble("longitude")
            require(latitude.isFinite() && latitude in -90.0..90.0 && longitude.isFinite() && longitude in -180.0..180.0)
            return ForecastPlace(json.getInt("id"), json.getString("name").take(120),
                json.optString("region").take(180), latitude, longitude)
        }
    }
}

internal data class ForecastHour(
    val time: LocalDateTime, val temperature: Double, val precipitation: Int?, val condition: WeatherCondition,
)
internal data class ForecastReport(
    val hours: List<ForecastHour>, val sunrise: LocalDateTime?, val sunset: LocalDateTime?,
    val fetchedAt: Long, val offline: Boolean, val zone: String,
)

/** Forecast data is fetched only from the panel, with a bounded response and an atomic cache. */
internal class WeatherForecast(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("aura_forecast", Context.MODE_PRIVATE)
    private val cache = AtomicFile(File(context.filesDir, "weather-forecast.json"))
    companion object { private val cacheMutex = Mutex() }

    fun selectedFor(sourceCity: String): ForecastPlace? = runCatching {
        val saved = JSONObject(preferences.getString("selection", null) ?: return null)
        if (saved.getString("source") != sourceCity) return null
        ForecastPlace.parse(saved.getJSONObject("place"))
    }.getOrNull()

    fun select(sourceCity: String, place: ForecastPlace) {
        preferences.edit().putString("selection",
            JSONObject().put("source", sourceCity).put("place", place.json()).toString()).apply()
    }

    fun clearSelection() { preferences.edit().remove("selection").apply() }

    suspend fun search(query: String): List<ForecastPlace> = withContext(Dispatchers.IO) {
        val term = query.trim().take(120)
        require(term.length >= 2)
        val json = request("https://geocoding-api.open-meteo.com/v1/search?name=" +
            URLEncoder.encode(term, "UTF-8") + "&count=6&language=es&format=json")
        val results = json.optJSONArray("results") ?: return@withContext emptyList()
        (0 until minOf(results.length(), 6)).mapNotNull { index ->
            runCatching {
                val row = results.getJSONObject(index)
                ForecastPlace.parse(row.put("region",
                    listOf(row.optString("admin1"), row.optString("country")).filter { it.isNotBlank() }.distinct().joinToString(", ")))
            }.getOrNull()
        }
    }

    suspend fun load(place: ForecastPlace, refresh: Boolean = false): ForecastReport = withContext(Dispatchers.IO) {
        cacheMutex.withLock {
            val saved = runCatching {
                val json = cache.openRead().use { JSONObject(it.bufferedReader().readText()) }
                require(ForecastPlace.parse(json.getJSONObject("place")) == place)
                json
            }.getOrNull()
            val age = saved?.optLong("fetchedAt")?.let { System.currentTimeMillis() - it }
            if (!refresh && saved != null && age != null && age in 0..1_800_000) {
                runCatching { decode(saved, false) }.getOrNull()?.let { return@withLock it }
            }
            try {
                val data = request("https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}" +
                    "&longitude=${place.longitude}&hourly=temperature_2m,precipitation_probability,weather_code,is_day" +
                    "&daily=sunrise,sunset&timezone=auto&forecast_days=2")
                val json = JSONObject().put("place", place.json()).put("fetchedAt", System.currentTimeMillis()).put("data", data)
                val report = decode(json, false)
                runCatching {
                    val stream = cache.startWrite()
                    try {
                        stream.write(json.toString().toByteArray(Charsets.UTF_8))
                        cache.finishWrite(stream)
                    } catch (error: Exception) {
                        cache.failWrite(stream)
                        throw error
                    }
                }
                report
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (saved != null && age != null && age in 0..21_600_000) {
                    runCatching { decode(saved, true) }.getOrNull()?.let { return@withLock it }
                }
                throw IOException("No se pudo obtener un pronóstico válido.", error)
            }
        }
    }

    private suspend fun request(address: String): JSONObject {
        val connection = URL(address).openConnection() as HttpsURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.instanceFollowRedirects = false
        try {
            if (connection.responseCode != 200) throw IOException("El servicio no está disponible.")
            val text = connection.inputStream.bufferedReader().use { reader ->
                val result = StringBuilder()
                val buffer = CharArray(4096)
                while (true) {
                    coroutineContext.ensureActive()
                    val count = reader.read(buffer)
                    if (count < 0) break
                    if (result.length + count > 262_144) throw IOException("La respuesta es demasiado grande.")
                    result.append(buffer, 0, count)
                }
                result.toString()
            }
            return try { JSONObject(text) } catch (error: org.json.JSONException) {
                throw IOException("El servicio devolvió datos incompletos.", error)
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun decode(saved: JSONObject, offline: Boolean): ForecastReport {
        val json = saved.getJSONObject("data")
        val now = LocalDateTime.now(ZoneId.of(json.getString("timezone")))
        val hourly = json.getJSONObject("hourly")
        val times = hourly.getJSONArray("time")
        val temperatures = hourly.getJSONArray("temperature_2m")
        val codes = hourly.getJSONArray("weather_code")
        val days = hourly.getJSONArray("is_day")
        val rain = hourly.optJSONArray("precipitation_probability")
        val hours = (0 until minOf(times.length(), temperatures.length(), codes.length(), days.length(), 48)).mapNotNull { i ->
            val time = LocalDateTime.parse(times.getString(i))
            val temperature = temperatures.optDouble(i, Double.NaN)
            if (time.isBefore(now.withMinute(0).withSecond(0).withNano(0)) || !temperature.isFinite()) null
            else ForecastHour(time, temperature, rain?.optInt(i, -1)?.takeIf { it in 0..100 },
                forecastCondition(codes.optInt(i, -1), days.optInt(i, 1) == 1))
        }.take(24)
        if (hours.isEmpty()) throw IOException("No hay pronóstico por horas disponible.")
        val daily = json.optJSONObject("daily")
        fun solar(key: String): LocalDateTime? = daily?.optJSONArray(key)?.let { values ->
            (0 until values.length()).firstNotNullOfOrNull { index ->
                runCatching { LocalDateTime.parse(values.getString(index)) }.getOrNull()
                    ?.takeIf { it.toLocalDate() == now.toLocalDate() }
            }
        }
        return ForecastReport(hours, solar("sunrise"), solar("sunset"), saved.getLong("fetchedAt"), offline,
            json.getString("timezone"))
    }
}

internal fun forecastCondition(code: Int, day: Boolean): WeatherCondition = when (code) {
    0 -> if (day) WeatherCondition.SUN else WeatherCondition.NIGHT
    1, 2 -> if (day) WeatherCondition.PARTLY_CLOUDY else WeatherCondition.CLOUDY_NIGHT
    3 -> WeatherCondition.CLOUDS
    45, 48 -> WeatherCondition.FOG
    51, 53, 55 -> WeatherCondition.DRIZZLE
    56, 57, 66, 67 -> WeatherCondition.SLEET
    61, 63, 80, 81 -> WeatherCondition.RAIN
    65, 82 -> WeatherCondition.DOWNPOUR
    71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
    95 -> WeatherCondition.STORM
    96, 99 -> WeatherCondition.HAIL
    else -> WeatherCondition.AMBIENT
}
