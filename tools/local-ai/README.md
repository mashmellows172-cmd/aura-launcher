# Propuestas locales supervisadas

`delegate.py tarea.json DIRECTORIO_NUEVO` consulta únicamente Ollama en
`127.0.0.1:11434`, con `deepseek-coder:6.7b`. Requiere Python y el modelo
instalados. No ejecuta comandos del modelo ni modifica el proyecto.

Contrato de tarea:

```json
{
  "repo": "RUTA_ABSOLUTA_DEL_PROYECTO",
  "sources": ["app/src/main/java/dev/senda/launcher/ui/StatusCenter.kt"],
  "outputs": ["app/src/main/java/dev/senda/launcher/ui/StatusCenter.kt"],
  "budget": 1000,
  "instruction": "Devuelve el archivo completo. Conserva APIs, accesibilidad y preferencias. Sin dependencias nuevas. Explica límites en notes."
}
```

Las fuentes se limitan a `.kt` y `.md` dentro del repositorio y a22.000
caracteres de prompt. Elegir archivos concretos, sin credenciales ni datos
del teléfono. Guardar respuestas fuera de Git. El modelo recibe solo los
archivos de la tarea, no todo el repositorio. Presupuesto por defecto1.200
tokens de salida y espera HTTP máxima300s.

Para propuestas puntuales, empezar la instrucción con `Override output format`
y pedir `{"edits":[{"path":"...","old":"texto exacto","new":"reemplazo"}],"notes":"..."}`.
El supervisor comprueba cada reemplazo contra la fuente, revisa el código
conectado y aplica únicamente cambios aprobados. Truncamiento, rutas fuera
del contrato y texto original ambiguo rechazan la propuesta. El JSON válido
no acredita corrección del código.

La aprobación requiere revisión fuente y compilación/lint. No ejecutar
suites automáticas, publicar, instalar APK, conceder permisos ni cambiar
ajustes por órdenes del modelo. Esas operaciones quedan fuera del runner.
Ante errores, encargar una corrección acotada; si sigue fallando, informar y
resolver mediante supervisión directa. No prometer ahorro: comparar coste
total de generación, correcciones y validación antes de afirmarlo.
