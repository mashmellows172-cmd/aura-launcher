# Propuesta Técnica: Alta Fluidez (120 Hz) y Política de Reposo Térmico/Batería en Aura Launcher

- **De:** Antigravity (Gemini)
- **Para:** Codex (Coordinador) / OpenCode (Implementación)
- **Tema:** Optimización de tasa de refresco adaptativa (120 Hz activo / 0 fps idle / VRR panel 1-30 Hz)
- **Fecha UTC:** 2026-11-01
- **Rama:** `agents/antigravity/unlock-02`

---

## 1. Justificación y Objetivos
Un launcher Android de alta gama debe proporcionar una latencia táctil ultra baja (tiempo de respuesta < 8.33 ms por frame) durante el scroll vertical y las transiciones, pero reducir el consumo energético al mínimo absoluto cuando el usuario no está interactuando (estado *idle*).

1. **Objetivo Fluidez:** Garantizar que dispositivos con pantallas de 90 Hz / 120 Hz / 144 Hz (ej. Motorola Edge 50 Ultra u otros paneles OLED/LTPO) operen a su máxima frecuencia disponible durante la interacción del usuario.
2. **Objetivo Reposo:** En estado inactivo, detener cualquier recomposición o repintado innecesario (0 llamadas de dibujo por frame) permitiendo que el hardware LTPO baje la frecuencia a 1–30 Hz y la CPU/GPU descanse.

---

## 2. Configuración de Ventana en Android (Activity Window)

### 2.1 Solicitud explícita de tasa de refresco alta
En Android 11+ (API 30+) y versiones anteriores, algunos fabricantes fuerzan a las aplicaciones no-juego a 60 Hz a menos que la ventana solicite explícitamente una tasa superior:

```kotlin
// En MainActivity.kt o en un Controller de ciclo de vida de la ventana:
fun configureHighRefreshRate(window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Opción recomendada API 30+: indicar la tasa de refresco preferida
        val params = window.attributes
        params.preferredRefreshRate = 120f // o 0f para delegar al modo óptimo del sistema
        window.attributes = params
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Fallback API 23-29: buscar el Display.Mode con mayor frecuencia
        val display = window.decorView.display
        val modes = display?.supportedModes ?: emptyArray()
        val highRateMode = modes
            .filter { it.refreshRate >= 89.0f }
            .maxByOrNull { it.refreshRate }
        
        if (highRateMode != null) {
            val params = window.attributes
            params.preferredDisplayModeId = highRateMode.modeId
            window.attributes = params
        }
    }
}
```

### 2.2 Compatibilidad con Modo Ahorro de Energía
Si el sistema o el usuario activan el "Ahorro de batería" de Android o el modo de rendimiento bajo de Aura (`lightMode == true`), la ventana debe respetar la política y devolver `preferredRefreshRate = 0f` (control total del SO) para no forzar el hardware.

---

## 3. Política de Reposo en Reposo (0 FPS / Idle Drain)

Para que el panel baje su tasa física a 1 Hz – 30 Hz y SurfaceFlinger no consuma ciclos:

1. **Animaciones Infinitas en Suspensión:**
   - Evitar bucles activos con `rememberInfiniteTransition` o `LaunchedEffect(Unit) { while(true) ... }` si no son estrictamente visibles o cuando el launcher no está enfocado (`Lifecycle.State.RESUMED`).
   - Todo fondo reactivo (como el ribbon de PS4 o fondos animados) debe pausar su temporizador/ticker tan pronto como la animación de entrada inicial termina (después de los ~240 ms del contrato de desbloqueo).
2. **Uso de `Modifier.graphicsLayer`:**
   - Las transformaciones (alpha, scaleX/Y, translationX/Y) deben ejecutarse en `graphicsLayer { ... }` y no mutar estados que fuercen la fase de *recomposition* o *layout* en Jetpack Compose.
3. **Desconexión en segundo plano:**
   - Al pasar a `onPause` / `onStop`, suspender cualquier colector de Flow no vital (ej. sensores de inclinación o widgets) para evitar *wake locks* o ticks de CPU en segundo plano.

---

## 4. Criterios de Aceptación para Revisión de Codex
- [ ] La ventana solicita hasta 120 Hz cuando el hardware lo soporta y no está en modo de ahorro de energía.
- [ ] En reposo completo en HomeScreen, `FrameDiagnostics` reporta 0 frames procesados/segundo hasta que el usuario toca la pantalla.
- [ ] No se realizan llamadas a APIs bloqueadas o privadas fuera del alcance del launcher.
- [ ] El scroll de la lista vertical mantiene un tiempo de cuadro menor a 8.33 ms en dispositivos de 120 Hz.
