package dev.senda.launcher.data

import kotlin.math.ceil

internal fun dashboardHalfWidth(block: HomeBlock, style: BlockStyle, widthDp: Float, fontScale: Float): Boolean =
    style.halfWidth && block != HomeBlock.WIDGETS && widthDp >= 280f && fontScale <= 1.5f

/** User height is a minimum. Reserve one category line and one value line at the actual font scale. */
internal fun informationMinHeight(style: BlockStyle, fontScale: Float): Int =
    maxOf(style.height, ceil((12f + style.textSize) * fontScale * 1.3f + 16f).toInt(), 48)
