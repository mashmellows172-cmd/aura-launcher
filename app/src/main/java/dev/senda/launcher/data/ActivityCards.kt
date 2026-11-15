package dev.senda.launcher.data

import android.content.Intent
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.util.UUID

internal data class ActivityCard(val id: String, val title: String, val link: String, val cover: Int) {
    val host: String get() = Uri.parse(link).host.orEmpty()

    companion object {
        const val MAX_CARDS = 24
        const val COVER_COUNT = 4
        const val MAX_TITLE = 64
        const val MAX_LINK = 4096
        private val sharedLink = Regex("""https?://[^\s<>"“”]+""", RegexOption.IGNORE_CASE)

        fun normalizeLink(text: String): String? {
            val raw = text.trim()
            if (raw.isEmpty() || raw.length > MAX_LINK ||
                raw.any { it.isWhitespace() || it.isISOControl() || it == '\\' }) return null
            val uri = Uri.parse(raw).normalizeScheme()
            if (uri.scheme != "http" && uri.scheme != "https") return null
            val parsed = runCatching { URI(uri.toString()) }.getOrNull() ?: return null
            if (parsed.host.isNullOrBlank() || parsed.rawUserInfo != null) return null
            return uri.toString()
        }

        fun fromShare(intent: Intent): ActivityCard? {
            if (intent.action != Intent.ACTION_SEND || intent.type != "text/plain") return null
            val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.take(16_384)?.toString().orEmpty()
            val link = normalizeLink(text) ?: sharedLink.findAll(text).mapNotNull {
                normalizeLink(it.value)
            }.firstOrNull().orEmpty()
            val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)?.trim()?.take(MAX_TITLE).orEmpty()
            return draft(title, link)
        }

        fun draft(title: String = "", link: String = ""): ActivityCard {
            val id = UUID.randomUUID()
            return ActivityCard(id.toString(), title, link, Math.floorMod(id.hashCode(), COVER_COUNT))
        }

        fun decode(raw: String): List<ActivityCard> {
            val array = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
            return (0 until minOf(array.length(), MAX_CARDS)).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val link = normalizeLink(item.optString("link")) ?: return@mapNotNull null
                val id = item.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                ActivityCard(id, item.optString("title").take(MAX_TITLE), link,
                    item.optInt("cover").coerceIn(0, COVER_COUNT - 1))
            }.distinctBy(ActivityCard::id)
        }

        fun encode(cards: List<ActivityCard>): String = JSONArray().apply {
            cards.forEach {
                put(JSONObject().put("id", it.id).put("title", it.title)
                    .put("link", it.link).put("cover", it.cover))
            }
        }.toString()
    }
}
