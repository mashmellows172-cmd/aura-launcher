# Aura — incidencias de beta14

## AURA-001 · S2 · Tema AMOLED no persiste
Pasos: seleccionar AMOLED; volver a leer `LauncherTheme.fromStoredValue("AMOLED")`.
Esperado: AMOLED. Antes: NOTHING. Evidencia: rama `null, "AMOLED" -> NOTHING` de beta12; regresión `everyExplicitThemeSurvivesReload`.
Corrección: solo la ausencia de preferencia selecciona el tema principal. Estado: corregido y comprobado por unidad y reinicio de proceso en el Motorola.

## AURA-002 · S3 · Editor descarta favoritas no visibles
Pasos: lista previa `[maps, hidden, mail, updating]`; editar las visibles a `[mail]`.
Esperado: conservar hidden y updating. Antes: el editor guardaba únicamente `[mail]`.
Evidencia: entrada/salida del guardado; regresión `editingFavoritesPreservesHiddenAndTemporarilyMissingPackages`.
Corrección: combinar edición con las favoritas no editables; eliminar limpieza destructiva basada en catálogo lanzable. Estado: corregido; pruebas de unidad, desactivación/reactivación y edición con favorita oculta pasaron en el Motorola.

## AURA-003 · S3 · Mayúsculas turcas rompen coincidencias
Pasos: normalizar İSTANBUL en idioma tr.
Esperado: istanbul. Antes: ıstanbul, por eliminar la marca antes de convertir a minúsculas.
Evidencia: regresión `searchHandlesTurkishCaseBeforeRemovingDiacritics`.
Corrección: convertir a minúsculas con el locale antes de quitar diacríticos. Estado: corregido y validado por unidad. La búsqueda sin tilde también se verificó en el Motorola.

## AURA-004 · S3 · Widget recién añadido queda fuera de la vista
Pasos: añadir Digital con la lista anclada en favoritas. Antes: el widget se insertaba por encima del ancla y solo se veía tras pulsar HOME.
Corrección: volver al principio cuando aumenta el número de widgets. Estado: verificado al añadir varios relojes en `widget_smoke.py`.

## AURA-005 · S3 · Retirar proveedor deja una superficie obsoleta
Pasos: añadir el widget de `dev.aura.qa.catalog`; desinstalar ese paquete manteniendo Aura visible.
Esperado: aviso recuperable y acción Quitar. Antes: la composición no observaba el cambio de proveedor.
Corrección: revisión observable de proveedores en las callbacks del host; invalidación del selector y de los widgets. Estado: `provider_smoke.py` reprodujo el fallo antes y pasó después sin reiniciar Aura.

## AURA-006 · S3 · Fallo del entorno gráfico durante una tanda de reinstalaciones
Evidencia: `work/qa-beta14/runtime.txt`, 13:27:08, `GraphicsEnvironment.queryAngleChoice`: Resources nulo durante `ActivityThread.handleBindApplication`, antes de entrar en código de Aura.
Estado: seguimiento del entorno Android/OEM; no se atribuye una causa confirmada a Aura. Un arranque controlado desde Settings fue COLD, 456 ms, sin nuevos errores AndroidRuntime. La relación con reinstalaciones rápidas es una hipótesis, no una corrección demostrada. Fuente de contexto: https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/java/android/os/GraphicsEnvironment.java

## Por comprobar
Proveedores adicionales de widgets con configuración propia; batería durante una jornada. El reinicio completo del Motorola pasó: HOME, tema y widget conservaron sus referencias. No se presentan como defectos reproducidos ni como pruebas completadas.

## Observación de QA: entrada táctil antes del reinicio
El menú dejó de responder a toques ADB tanto en debug como en la variante optimizada. Cerrar PiP y recrear el proceso no lo resolvieron. Tras reiniciar el dispositivo el menú volvió a responder. No se atribuye una causa confirmada ni se presenta como una corrección de código.
