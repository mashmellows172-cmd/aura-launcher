package dev.senda.launcher.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.senda.launcher.NotificationShadeService
import dev.senda.launcher.data.LauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun QuickCenterActions(onSearch: () -> Unit, onSettings: (() -> Unit) -> Unit) {
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(CameraManager::class.java) }
    val scope = rememberCoroutineScope()
    val cameraId by produceState<String?>(null, manager) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                manager?.cameraIdList?.firstOrNull { id ->
                    val camera = manager.getCameraCharacteristics(id)
                    camera.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                        camera.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
                }
            }.getOrNull()
        }
    }
    var torch by remember(cameraId) { mutableStateOf<Boolean?>(null) }
    var permitted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var configuring by rememberSaveable { mutableStateOf(false) }
    val preferences = remember(context) { context.getSharedPreferences("quick_center_shortcuts", 0) }
    var showTorch by remember { mutableStateOf(preferences.getBoolean("torch", true)) }
    var showSearch by remember { mutableStateOf(preferences.getBoolean("search", true)) }
    var showSettings by remember { mutableStateOf(preferences.getBoolean("settings", true)) }
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    fun toggleTorch(enabled: Boolean) {
        val id = cameraId ?: return
        val service = manager ?: return
        scope.launch {
            val changed = withContext(Dispatchers.IO) { runCatching { service.setTorchMode(id, enabled) }.isSuccess }
            if (!changed) toast("La linterna no está disponible en este momento")
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permitted = granted
        if (granted) toggleTorch(torch != true) else toast("Concede acceso a la cámara para usar el flash")
    }
    DisposableEffect(manager, cameraId, permitted, showTorch) {
        val callback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(id: String, enabled: Boolean) {
                if (id == cameraId) torch = enabled
            }
            override fun onTorchModeUnavailable(id: String) {
                if (id == cameraId) torch = null
            }
        }
        val registered = manager != null && cameraId != null && showTorch &&
            runCatching { manager.registerTorchCallback(context.mainExecutor, callback) }.isSuccess
        onDispose { if (registered) manager?.unregisterTorchCallback(callback) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (showTorch) QuickShortcut("torch", "Linterna", Modifier.weight(1f), enabled = cameraId != null && (!permitted || torch != null), active = torch) {
            if (permitted) toggleTorch(torch != true) else permission.launch(Manifest.permission.CAMERA)
        }
        if (showSearch) QuickShortcut("search", "Buscar apps", Modifier.weight(1f), onClick = onSearch)
        if (showSettings) QuickShortcut("settings", "Ajustes rápidos", Modifier.weight(1f)) {
            onSettings {
                if (!NotificationShadeService.openQuickSettings()) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        .onFailure { toast("No se pudieron abrir los ajustes") }
                }
            }
        }
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { configuring = true }) { Text("Personalizar accesos") }
        DropdownMenu(expanded = configuring, onDismissRequest = { configuring = false }) {
            QuickShortcutToggle("Linterna", showTorch) {
                showTorch = it
                preferences.edit().putBoolean("torch", it).apply()
            }
            QuickShortcutToggle("Buscar apps", showSearch) {
                showSearch = it
                preferences.edit().putBoolean("search", it).apply()
            }
            QuickShortcutToggle("Ajustes rápidos", showSettings) {
                showSettings = it
                preferences.edit().putBoolean("settings", it).apply()
            }
        }
    }
}

@Composable
private fun QuickShortcutToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.widthIn(min = 220.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(checked, onChange)
    }
}

@Composable
private fun QuickShortcut(icon: String, label: String, modifier: Modifier, enabled: Boolean = true,
    active: Boolean? = null, onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    TextButton(onClick = onClick, enabled = enabled, shape = shape, contentPadding = PaddingValues(6.dp),
        modifier = modifier.heightIn(min = 64.dp).ps4ControlGlass(shape, translucent = active != true)
            .semantics { if (active != null) stateDescription = if (active) "Encendida" else "Apagada" }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ThemeSystemIcon(icon, LauncherTheme.PS4)
            Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
        }
    }
}
