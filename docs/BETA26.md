# Aura Launcher 0.20.0-beta26

## Seis familias originales de iconos

32 funciones por tema,192 variantes vectoriales nativas. Nothing usa puntos; Sense objetos con relieve; TouchWiz objetos flotantes y reflejos; LG esmalte cálido; Xperia cristal fino; RAZR sprites LCD con bisel metálico. Cámara, teléfono, mensajes, música, calendario, ajustes y archivos tienen tratamiento propio por familia.

Son reinterpretaciones originales de época, no iconos extraídos de firmware ni192 aplicaciones distintas cubiertas. Los iconos de apps modernas sin coincidencia explícita conservan su identidad. La asignación de recursos de Aura deja de deducir por palabras sueltas del paquete: una app que contiene music no se convierte automáticamente en el reproductor del sistema.

## Comparación antes de aplicar

Abre **Tema visual → Iconos → Explorar familias de iconos**. La galería muestra los recursos reales y permite cambiar tema, tamaño32/42/60dp y fondo claro/oscuro. No reproduce animaciones ni cambia preferencias mientras exploras.

**Usar esta familia** prepara Temáticos y recursos de Aura en el borrador. **Aplicar tema** guarda. Cancelar conserva el inicio anterior. Los reemplazos individuales siguen teniendo prioridad. Normales conserva los iconos de Android; Monocromáticos mantiene el tratamiento existente. Si tienes activada tu colección personal o un paquete externo, sus recursos siguen apareciendo hasta elegir recursos de Aura.

## Elegir un dibujo para cualquier app

Pulsación larga en una aplicación → **Elegir icono** → **Dibujos de las familias Aura**. Elige un dibujo de cualquiera de las seis familias. Se guarda únicamente para esa app y el tema actual, usando la infraestructura de reemplazos existente. **Restablecer icono** elimina la elección; Normales continúa ignorando reemplazos. La opción de imagen de la galería sigue disponible.

La elección se guarda como un PNG local de128px fuera del hilo principal; puede incluirse en el respaldo9 existente. No añade preferencias ni requiere acceso a archivos externos. Los dibujos automáticos y la galería de familias usan vectores; las elecciones individuales usan la caché de imágenes acotada ya existente.

Las imágenes individuales conservan ahora el contorno completo, igual que los recursos importados, cuando no se pidió un contenedor personalizado.

## Fuente y rendimiento

Maestros SVG, manifiesto, generador reproducible y recursos Android incluidos en las fuentes. Sin librería SVG en ejecución, temporizadores o animación permanente por icono. Los vectores usan la carga y caché nativas de Compose. Esto describe la arquitectura; no implica una cifra medida de FPS o consumo.

Sin permisos ni cambios al formato9 de respaldo. Actualización del mismo paquete con la firma habitual, sin desinstalar. Evidencia de compilación, revisión visual y entrega en `QA-beta26.md`.
