package dev.senda.launcher.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Own a new private file until settings commit. Never remove an already referenced resource. */
internal suspend fun persistLocalResource(
    directory: File,
    suffix: String,
    write: (File) -> Unit,
    commit: suspend (File) -> Unit,
): File {
    val file = File(directory, "${UUID.randomUUID()}$suffix")
    var committed = false
    try {
        withContext(Dispatchers.IO) {
            check(directory.isDirectory || directory.mkdirs()) { "No se pudo crear la carpeta de recursos" }
            write(file)
        }
        currentCoroutineContext().ensureActive()
        // Once persistence starts, finish it before cancellation can cause file rollback.
        withContext(NonCancellable) {
            commit(file)
            committed = true
        }
        return file
    } finally {
        if (!committed) withContext(NonCancellable + Dispatchers.IO) { file.delete() }
    }
}
