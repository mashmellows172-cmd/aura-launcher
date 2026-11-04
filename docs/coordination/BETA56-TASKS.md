# Beta56: delegación supervisada

Base obligatoria: e8b5419 (beta55). Fecha: 3 de noviembre de 2026.

Ollama / qwen2.5-coder:7b: producir controlador Kotlin de refresco de ventana. API30+ solicita120f solo con interacción reciente, Activity RESUMED y foco. Liberar preferencia a0f tras1500ms, al pausar, perder foco, ahorro o modo ligero. API29 seleccionar modo de mayor Hz con misma resolución; liberar modeId0. Sin bucles, red, permisos ni polling. Controlador recibe estado externo light/power y ofrece interaction/resume/pause/close. No editar MainActivity; supervisor integra.

OpenCode / Ollama local: proponer parche mínimo de Ps4LiveWallpaper.kt para rasterizar solo el fondo estático beta55 una vez por tamaño/densidad, nunca por frame ni UI thread. Mantener composición visual, fondo estático, iconos y efectos existentes. No modificar otras rutas. Entregar diff unificado, sin aplicar, sin shell, sin builds, sin instalar. Si no es demostrablemente seguro, entregar hallazgo concreto y no parche.

DeepSeek Harness: revisión de solo lectura de los dos resultados. Comprobar lifecycle, asignaciones bitmap, recursos, compatibilidadAPI29, liberación de preferencias, regresión de modo ligero. No comandos, archivos externos, secretos, Git, red, cambios ni suites. Informe máximo250palabras con problemas concretos; distinguir propuestas de resultados medidos.

Todos: no afirmar120FPS, panelLTPO1Hz ni mejora porcentual. No escribir en main ni publicar. Una respuesta y como máximo una corrección. Codex verifica diff, compila/lint y compara métricas antes de entregar. Preservar rama local con borrador beta50.
