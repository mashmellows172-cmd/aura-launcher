package dev.senda.launcher.themeeditor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.asAndroidBitmap
import dev.senda.launcher.data.loadLocalImage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.UUID

class ThemeEditorViewModel(app:Application):AndroidViewModel(app) {
    private val dao=ThemeDatabase.get(app).themes()
    val variants=dao.variants().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
    private val _draft=MutableStateFlow<ThemeSnapshot?>(null)
    val draft=_draft.asStateFlow()
    private val _undo=MutableStateFlow(false); val canUndo=_undo.asStateFlow()
    private val _redo=MutableStateFlow(false); val canRedo=_redo.asStateFlow()
    private val _busy=MutableStateFlow(false); val busy=_busy.asStateFlow()
    private val _error=MutableStateFlow<String?>(null);val error=_error.asStateFlow()
    private var history:ThemeHistory?=null
    private var persistence:Job?=null
    private var opened=false
    fun open(initial:ThemeSnapshot) {
        if(opened)return;opened=true;_busy.value=true;_error.value=null
        viewModelScope.launch {
            try {
                val recovered=dao.draft()?.let { runCatching { ThemeSnapshot.parse(it.snapshotJson) }.getOrNull() }
                val value=recovered ?: initial.validated();history=ThemeHistory(value);_draft.value=value;flags()
            } catch(e:Exception){ if(e is CancellationException)throw e;_error.value=e.localizedMessage;history=ThemeHistory(initial);_draft.value=initial }
            finally {_busy.value=false}
        }
    }
    fun edit(next:ThemeSnapshot,commit:Boolean=true) {
        if(_busy.value)return
        _draft.value=next.validated();if(commit)history?.commit(next)
        flags();persistLater()
    }
    fun finishGesture(){_draft.value?.let { history?.commit(it) };flags();persistLater()}
    fun undo(){if(_busy.value)return;_draft.value=history?.undo();flags();persistLater()}
    fun redo(){if(_busy.value)return;_draft.value=history?.redo();flags();persistLater()}
    fun load(variant:ThemeVariantEntity){runCatching { ThemeSnapshot.parse(variant.snapshotJson) }.onSuccess { edit(it) }.onFailure { _error.value="Variante incompatible" }}
    private fun flags(){_undo.value=history?.canUndo==true;_redo.value=history?.canRedo==true}
    private fun persistLater(){persistence?.cancel();val value=_draft.value ?: return;persistence=viewModelScope.launch { delay(500);try{dao.saveDraft(ThemeDraftEntity(snapshotJson=value.json(),updatedAt=System.currentTimeMillis()))}catch(e:Exception){if(e is CancellationException)throw e;_error.value="No se pudo conservar el borrador"} }}
    fun cancel(done:()->Unit){if(_busy.value)return;_busy.value=true;viewModelScope.launch { try{persistence?.cancelAndJoin();dao.clearDraft();opened=false;_draft.value=null;history=null;done()}catch(e:Exception){if(e is CancellationException)throw e;_error.value="No se pudo cerrar el borrador"}finally{_busy.value=false} }}
    fun save(name:String,apply:Boolean,done:()->Unit) {
        val value=_draft.value ?: return;if(_busy.value)return
        _busy.value=true;_error.value=null
        viewModelScope.launch {
            try {
                persistence?.cancelAndJoin()
                val saved=withContext(Dispatchers.IO) {
                    val app=getApplication<Application>();val uri=value.wallpaper
                    if(uri==null)value else {
                        val directory=File(app.filesDir,"variant-wallpapers").apply{mkdirs()}
                        val source=Uri.parse(uri)
                        val owned=source.scheme=="file" && File(source.path.orEmpty()).canonicalFile.parentFile==directory.canonicalFile
                        if(owned){require(File(source.path.orEmpty()).isFile);value} else {
                            require(source.scheme in setOf("content","file"))
                            val bitmap=loadLocalImage(app,source,1920) ?: error("No se pudo leer el fondo")
                            val file=File(directory,"${UUID.randomUUID()}.png")
                            try { file.outputStream().use{check(bitmap.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it))}; value.copy(wallpaper=Uri.fromFile(file).toString()) }
                            catch(e:Exception){file.delete();throw e}
                        }
                    }
                }
                val entity=ThemeVariantEntity(UUID.randomUUID().toString(),name.trim().take(60).ifBlank{"Mi variante"},saved.theme,1,saved.json(),System.currentTimeMillis())
                if(apply)dao.saveAndActivate(entity) else {dao.save(entity);dao.clearDraft()}
                opened=false;_draft.value=null;history=null;done()
            } catch(e:Exception){if(e is CancellationException)throw e;_error.value=e.localizedMessage ?: "No se pudo guardar"}
            finally{_busy.value=false}
        }
    }
    fun useBase(done:()->Unit){if(_busy.value)return;_busy.value=true;viewModelScope.launch{try{persistence?.cancelAndJoin();dao.setActive(ActiveVariantEntity(variantId=null));dao.clearDraft();opened=false;_draft.value=null;history=null;done()}catch(e:Exception){if(e is CancellationException)throw e;_error.value="No se pudo restaurar el tema base"}finally{_busy.value=false}}}
}
