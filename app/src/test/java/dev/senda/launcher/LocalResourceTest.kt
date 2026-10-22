package dev.senda.launcher

import dev.senda.launcher.data.persistLocalResource
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files

class LocalResourceTest {
    private fun directory() = Files.createTempDirectory("aura-resource").toFile()

    @Test fun failedWriteRemovesPartialFileAndPreservesPreviousResource() = runBlocking {
        val dir = directory()
        try {
            val previous = File(dir, "previous.png").apply { writeText("previous") }
            try {
                persistLocalResource(dir, ".png", { it.writeText("partial"); throw IOException("full") }, { fail("must not persist") })
                fail("expected failure")
            } catch (_: IOException) { }
            assertEquals(listOf("previous.png"), dir.list()!!.toList())
            assertEquals("previous", previous.readText())
        } finally { dir.deleteRecursively() }
    }

    @Test fun failedSettingsCommitRemovesOnlyNewFile() = runBlocking {
        val dir = directory()
        try {
            File(dir, "old.png").writeText("old")
            try {
                persistLocalResource(dir, ".png", { it.writeText("new") }, { throw IOException("settings unavailable") })
                fail("expected failure")
            } catch (_: IOException) { }
            assertEquals(listOf("old.png"), dir.list()!!.toList())
        } finally { dir.deleteRecursively() }
    }

    @Test fun cancellationDuringCommitCannotDeleteReferencedFile() = runBlocking {
        val dir = directory()
        try {
            val entered = CompletableDeferred<Unit>()
            val finish = CompletableDeferred<Unit>()
            var referenced: File? = null
            val job = launch {
                persistLocalResource(dir, ".png", { it.writeText("new") }, {
                    entered.complete(Unit)
                    finish.await()
                    referenced = it
                })
            }
            entered.await()
            job.cancel()
            finish.complete(Unit)
            job.join()
            assertEquals("new", referenced!!.readText())
            assertEquals(1, dir.list()!!.size)
        } finally { dir.deleteRecursively() }
    }
}
