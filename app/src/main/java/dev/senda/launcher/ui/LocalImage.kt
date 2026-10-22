package dev.senda.launcher.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import dev.senda.launcher.data.loadLocalImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** A changed source gets fresh state immediately, including the frame before its loader starts. */
@Composable
internal fun rememberLocalImage(uri: String?, maxEdge: Int): ImageBitmap? = key(uri, maxEdge) {
    val context = LocalContext.current
    val image by produceState<ImageBitmap?>(null) {
        value = uri?.let { withContext(Dispatchers.IO) { loadLocalImage(context, it.toUri(), maxEdge) } }
    }
    image
}
