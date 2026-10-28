# Biblioteca retro personal

Aura permite importar una colección local de imágenes sin incluir recursos de fabricantes en su APK o código fuente. El código de Aura conserva su licencia MIT; las imágenes que añade el usuario conservan sus propios derechos.

## Editor desde beta39

En el editor abre **Fondo → Explorar fondos y colecciones**. Busca por nombre, tema o colección; usa **Todos** para revisar la biblioteca completa. Toca una miniatura para ampliarla y pulsa **Usar este fondo**. Guarda la variante para aplicar la selección. **Mis imágenes** abre el selector del sistema; **Importar colección de fondos (ZIP)** conserva la importación local.

La selección de un fondo importado no activa sus iconos. La imagen personal actual aparece aunque no pertenezca al catálogo. Si faltan recursos, se muestra un estado de error; no se inventan imágenes OEM. El predeterminado es el diseño procedural de Aura o el primer fondo de una colección ya activada, identificado como tal.

El lector admite ahora NOTHING, SENSE, TOUCHWIZ, OPTIMUS, XPERIA, RAZR y PS4; conserva formato `aura-retro-library`, versión1 y sus límites de seguridad. El preparador existente sigue usando las cuatro carpetas históricas documentadas abajo; para las otras familias puede prepararse un manifiesto compatible con imágenes propias. Un ZIP nuevo con familias adicionales requiere beta39 o posterior.

Respaldo de ajustes13 compatible con1–13. Las variantes Room y sus fondos privados todavía requieren una futura exportación de variantes; no confundirlas con ajustes/fondos guardados por el flujo anterior. La colección se conserva en su ZIP separado.

Las instrucciones y formatos anteriores de esta guía son referencia histórica del primer importador.

## Preparar la colección

Requiere Python 3 y Pillow. El preparador lee los originales sin modificarlos:

```powershell
python tools/prepare-retro-library.py --assets "C:\ruta\retro_phones_assets" --output "C:\ruta\Aura-retro-personal.zip"
```

La estructura esperada contiene `HTC_One_M7`, `Samsung_Galaxy_S3`, `LG_G2` y `Sony_Xperia_Z1`, cada carpeta con `icons/` y `wallpapers/`. Los nombres seleccionados se pueden revisar en el script. No se ejecutan scripts de la colección.

El script produce un ZIP y un informe adyacente con nombres de origen, tamaños y SHA-256 de cada imagen utilizada. Los fondos se optimizan a WebP, con un máximo de 1920 px por lado; los iconos conservan transparencia y proporciones en PNG, con un máximo de 256 px. No se incluyen sonidos ni botones internos que no sean iconos completos de aplicaciones.

## Importar y aplicar

1. Copia el ZIP al teléfono.
2. En Aura abre **Tema visual**, selecciona un tema retro y entra en **Acabado**.
3. Pulsa **Importar colección retro (ZIP)** y elige el archivo.
4. Activa **Recursos de mi colección** y revisa la vista previa.
5. En **Fondo**, recorre las miniaturas para elegir otra imagen. Un fondo personal previamente elegido tiene prioridad sobre el fondo predeterminado de la colección.
6. Pulsa **Aplicar tema**. La selección de recursos se recuerda por tema.

Importar actualiza la biblioteca local, pero no activa una colección por primera vez sin aplicar el tema. Actualizar una biblioteca ya activa sustituye sus recursos. Cancelar el editor descarta los cambios de selección del tema; conserva la biblioteca que se importó expresamente.

**Normales** sigue mostrando los iconos actuales de cada aplicación. **Temáticos** usa, por orden: reemplazo individual, paquete de iconos Android seleccionado o colección local y recursos de Aura. Un icono local conserva su contorno original con la forma predeterminada; una forma personalizada añade el contenedor que el usuario elija. Los iconos de aplicaciones siguen siendo decorativos junto al nombre accesible de la app.

## Revisión de la colección proporcionada

El README de la carpeta afirma que todos sus recursos pertenecen a modelos de 2012–2013. La inspección visual y los nombres de archivo no permiten confirmar esa afirmación. Por eso Aura muestra **Colección**, sin certificar firmware, fabricante ni año exactos.

| Carpeta | Fondos disponibles | Iconos utilizados | Criterio |
|---|---:|---:|---|
| HTC One M7 | 12 | 0 | Hay iconos AOSP/CyanogenMod y controles internos; no se presentan como Sense 5. |
| Samsung Galaxy S3 | 26 | 6 | Teléfono, cámara, vídeo, música, ajustes y tienda; se excluyen estados desactivados y botones. Los fondos incluyen generaciones posteriores. |
| LG G2 | 12 | 0 | La carpeta contiene controles internos, fondos de iconos y recursos de actividades; se conserva el arte de Aura. |
| Sony Xperia Z1 | 12 | 39 | Iconos completos de aplicaciones y equivalencias conocidas; la colección contiene diseños posteriores al Z1. |

Se conservan todos los fondos como opciones, incluidos duplicados o imágenes de generaciones posteriores, porque pertenecen a la colección elegida por el usuario. Los predeterminados son `HTC2`, `hd_01`, `LG9` y `Sony3`; esa selección visual no certifica su procedencia. El informe permite sustituir una selección cuando se disponga de recursos con procedencia comprobada.

## Almacenamiento, respaldo y límites

La biblioteca se guarda en almacenamiento privado. No requiere Internet, ubicación ni acceso general a la galería. La importación usa el selector de archivos de Android y acepta únicamente manifiesto e imágenes con rutas cerradas, límites por archivo y 64 MiB descomprimidos en total. Rechaza rutas relativas, entradas duplicadas, imágenes dañadas y referencias ausentes antes de cambiar la biblioteca activa. Mantiene una generación anterior para completar lecturas de imágenes en curso.

El respaldo de ajustes formato 6 conserva los interruptores por tema y admite importar formatos 1–6. La biblioteca se transfiere con su ZIP, por separado; un fondo seleccionado expresamente se copia como imagen personal y sí se incluye en el respaldo normal. Al restaurar ajustes en otro dispositivo sin la biblioteca, Aura utiliza sus recursos propios hasta importarla de nuevo.

El tema principal Nothing/CMF conserva su diseño y sus recursos propios. Los widgets externos siguen mostrando el contenido que publique cada aplicación.
