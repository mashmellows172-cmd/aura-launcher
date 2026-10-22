package dev.senda.launcher

import android.app.role.RoleManager
import android.Manifest
import android.content.pm.ActivityInfo
import dev.senda.launcher.data.HomeRotation
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.LocalIconPack
import dev.senda.launcher.data.LocalRetroAssets
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowInsetsControllerCompat
import dev.senda.launcher.ui.HomeScreen
import dev.senda.launcher.ui.theme.AuraTheme
import dev.senda.launcher.ui.theme.isDarkSkin
import dev.senda.launcher.ui.theme.statusBarColor
import dev.senda.launcher.widgets.WidgetController

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()
    private var isDefaultHome by mutableStateOf(false)
    private lateinit var widgets: WidgetController

    private val calendarPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.setHeaderSettings(viewModel.uiState.value.headerSettings.copy(showCalendar = granted))
        viewModel.refreshHeader()
    }
    private val exportSettings = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> uri?.let(viewModel::exportBackup) }
    private val importSettings = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(viewModel::prepareRestore) }

    private val homeRoleRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        updateHomeState()
        viewModel.refreshApps()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgets = WidgetController(this)
        updateHomeState()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val systemIsDark = isSystemInDarkTheme()
            val darkTheme = uiState.theme.isDarkSkin(systemIsDark)
            SideEffect {
                val orientation = when (uiState.rotation) {
                    HomeRotation.SYSTEM -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    HomeRotation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                    HomeRotation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                }
                if (requestedOrientation != orientation) requestedOrientation = orientation
                val barColor = if (uiState.appearance.highContrast) { if (darkTheme) android.graphics.Color.BLACK else android.graphics.Color.WHITE } else uiState.theme.statusBarColor(systemIsDark)
                window.statusBarColor = barColor
                window.navigationBarColor = barColor
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            CompositionLocalProvider(dev.senda.launcher.ui.LocalMotionSettings provides uiState.visualSettings, LocalAppearance provides uiState.appearance, LocalIconPack provides uiState.iconMapping,
                LocalRetroAssets provides uiState.retroLibrary[uiState.theme.name]?.takeIf { uiState.retroEnabled[uiState.theme.name] == true }) {
            AuraTheme(uiState.theme, systemIsDark, highContrast = uiState.appearance.highContrast, finish = uiState.themeFinishes[uiState.theme.name] ?: dev.senda.launcher.data.ThemeFinish()) {
                HomeScreen(
                    viewModel = viewModel,
                    isDefaultHome = isDefaultHome,
                    onChooseHome = ::requestHomeRole,
                    widgetController = widgets,
                    onRequestCalendar = { calendarPermission.launch(Manifest.permission.READ_CALENDAR) },
                    onAppPermissions = { startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$packageName"))) },
                    onNotificationSettings = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                    onExportSettings = { exportSettings.launch("Aura-respaldo-${java.time.LocalDate.now()}.json") },
                    onImportSettings = { importSettings.launch(arrayOf("application/json", "application/octet-stream", "text/plain")) },
                )
            }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateHomeState()
        viewModel.refreshApps()
        viewModel.refreshHeader()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) viewModel.returnHome()
    }

    override fun onStart() { super.onStart(); widgets.start(); viewModel.setHomeVisible(true) }
    override fun onStop() { viewModel.setHomeVisible(false); widgets.stop(); super.onStop() }

    @Deprecated("Required by the public AppWidgetHost configuration API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        widgets.handleConfigurationResult(requestCode, resultCode)
    }

    private fun updateHomeState() {
        isDefaultHome = getSystemService(RoleManager::class.java)
            ?.isRoleHeld(RoleManager.ROLE_HOME) == true
    }
    private fun requestHomeRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true) {
            homeRoleRequest.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
        } else {
            homeRoleRequest.launch(Intent(Settings.ACTION_HOME_SETTINGS))
        }
    }
}







