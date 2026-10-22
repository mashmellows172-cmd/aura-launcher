package dev.senda.launcher.data

import java.text.Normalizer
import java.util.Locale

internal fun normalizeSearchText(value: String, locale: Locale = Locale.getDefault()): String = Normalizer
    .normalize(value.trim().lowercase(locale), Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")

internal fun imageSampleSize(width: Int, height: Int, maxEdge: Int): Int {
    require(maxEdge > 0)
    var sample = 1
    while (maxOf(width, height) / sample > maxEdge && sample < (1 shl 29)) sample *= 2
    return sample
}

internal fun mergeEditedFavorites(previous: List<String>, edited: List<String>, editable: Set<String>): List<String> =
    (edited.filter { it in editable } + previous.filterNot { it in editable }).distinct()

internal fun sectionStartIndices(letters: List<String>, counts: Map<String, Int>, favoriteCount: Int, hasWidgets: Boolean): Map<String, Int> {
    var index = (if (favoriteCount > 0) favoriteCount + 2 else 1) + if (hasWidgets) 1 else 0
    return letters.associateWith { letter -> index.also { index += 1 + (counts[letter] ?: 0) } }
}

internal fun sectionLetterFor(label: String, locale: Locale = Locale.getDefault()): String {
    val first = label.trim().firstOrNull() ?: return "#"
    return if (first.isLetter()) first.toString().uppercase(locale) else "#"
}

internal fun <T> moveListItem(items: List<T>, fromIndex: Int, toIndex: Int): List<T> {
    require(fromIndex in items.indices && toIndex in items.indices) { "Move indices must point to existing items." }
    if (fromIndex == toIndex) return items
    return items.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
}
