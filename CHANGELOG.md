# Historial de actualizaciones de Aura Launcher

Beta45 candidata (0.33.0/code45),29oct2026: seis entradas tras desbloqueo, selector/preview/intensidad y multimedia adaptativo de beta44. Compilación/lint final3m14s,0 errores/31 advertencias/3 avisos; revisión fuente completada. APK firmado/hash en docs/BETA45.md. NO instalada por firma incompatible; sin métricas físicas ni promoción a main. Preferencias unlock aún fuera del respaldo JSON.


Documento de texto principal para continuar el proyecto desde cualquier equipo. Orden: versión más reciente primero. Cada entrada distingue cambios, evidencia y límites. No se inventan fechas de entregas antiguas: se usa la versión y su documento fuente. El registro se reconstruyó el25 de octubre de2026.

## 0.31.0-beta42 · 28 de octubre de2026 · candidato local, NO instalado

Encabezado PS4 compacto con clima/fecha desplegables; Quick Menu lateral con audio; ficha de app con categoría Android y hasta5 shortcuts reales. Mantiene un toque para abrir y acceso TalkBack al bloqueo. Componentes delegados y revisión independiente, corrección de accesibilidad aplicada.

assembleBeta/lintBeta final correcto en1m18s;0 errores/30 advertencias/3 avisos. APK con firma original. Instalación detenida: el Motorola ya contiene beta43/code43 con certificado diferente. Esa fuente no está en esta rama ni apareció tras fetch. Recuperar código y firma de esa entrega antes de integrar/actualizar; no degradar ni borrar datos. [BETA42](docs/BETA42.md).

## 0.30.0-beta41 · 28 de octubre de2026 · fondo vivo PS4

Tres cintas luminosas independientes reemplazan las franjas paralelas. Parallax amortiguado ligado a la lista, energía durante scroll y pulso al cambiar foco. Clock en RESUMED, parada con ahorro/reduceMotion y geometría reutilizada. Skills PS4 Fidelity, UI/UX Pro Max, Deferring State Reads y Using Efficient Effects; revisión independiente sin hallazgos concretos.

Compilación/lint2m24s:0 errores/30 advertencias/3 avisos. Instalada con firma original, code41 y hash posterior verificados. Revisión visual acotada de movimiento en reposo y desplazamiento; sin FPS/batería medidos. Observación intermitente de encabezado en una captura pendiente de reproducción. [BETA41](docs/BETA41.md).

## 0.29.0-beta40 · 28 de octubre de 2026 · PS4 vertical

Referencia visual del usuario aplicada: iconos nativos a color sobre placas cuadradas, foco central blanco/cian con escala finita320ms, títulos Noto Sans Light y fondo propio de cintas azules animadas. Conserva lista vertical, búsqueda, A–Z, un toque, widgets, gestos, packs y reemplazos. Revisor independiente detectó pérdida de contenedor explícito AUTO PS4; corregida y confirmada. No se restauró el menú horizontal ni se simuló la beta38 cloud ausente.

Compilación/lint correctos en2m12s:0 errores/30 advertencias/3 avisos. Firma original e instalación code40 por actualización verificadas mediante APK posterior, versión y hash. Revisión visual limitada en Motorola confirmó foco al desplazar y sección A–Z; PiP cubrió parte del reproductor. Sin suites automáticas, FPS medidos ni equivalencia100% afirmada. [BETA40](docs/BETA40.md).

## Preparación local · 28 de octubre de 2026 · sin nueva versión

Instalación posterior completada: ADB vinculado, identidad Motorola autorizada verificada y certificados previo/candidato compatibles. `adb install -r` correcto; versión0.28.0-beta39/code39, firma original y hash del APK instalado verificados. Datos conservados mediante actualización, sin cambios de permisos ni ajustes; no se afirma aceptación visual o rendimiento.

Reutilizado checkout Windows existente y herramientas instaladas. Configuración de sesión y local.properties privado preparados; clave original conservada fuera de Git. Recompilación local beta39 con assembleBeta/lintBeta correcta en43s, lint0 errores/30 advertencias/3 avisos y certificado original verificado. Sin suites automáticas ni cambios de interfaz. Conexión ADB e instalación pendientes de vinculación; no hay validación visual nueva. La variante beta38 del chat cloud con Ps4Carousel.kt no aparece en las ramas GitHub consultadas y debe transferirse, sin recrearla de memoria. Ver [instrucciones locales](docs/LOCAL-DEVELOPMENT.md).

## 0.28.0-beta39 · 27 de octubre de2026 · rama de validación

Favoritas con indicador discreto en vez de estrellas repetidas; siete efectos de lista+Sin efecto; siete despliegues por tema para reloj/clima/reproductor/widgets. PS4 separa su clima/visualizador de Xperia. Pesos de fuentes corregidos y galería de fondos con filtros, búsqueda y ampliación; colecciones de siete familias. Respaldo13 compatible con1–13.

Compilación/lint correctos en3m34s:0 errores,30 advertencias,3 avisos. APK firmado con clave original y hash verificado; no instalado por ausencia del Motorola. Siete revisiones locales por tema tras bloqueo de cuota de Antigravity. Sin métricas físicas ni promoción a main. [BETA39](docs/BETA39.md).

## 0.27.0-beta38 · 26 de octubre de2026 · rama de validación

Editor de temas con Memento (50 pasos), Undo/Redo agrupado por gesto, variantes y borrador Room, copia privada de fondos y vista previa reutilizando componentes reales. Revisión SIII a tamaño Inicio:20 candidatos existentes/156 pendientes; no se generaron ni activaron iconos nuevos. Módulos Macrobenchmark/Baseline Profiles y protocolo de reposo añadidos.

Compilación beta/performance, módulos de medición y lint correctos:3m9s;0 errores,30 advertencias,3 avisos. APK firmado con certificado original. Instalada mediante actualización; versión38 y hashes verificados en Motorola y Descargas. **Sin mediciones físicas; perfiles propios pendientes de generar.** Rama codex/phase1-theme-editor, sin fusionar a main. Fase2 no iniciada. [BETA38](docs/BETA38.md).

## 0.26.2-beta37 · 26 de octubre de2026

Formas coherentes para iconos temáticos/nativos y paneles RAZR; foco en tarjetas, diálogos por tema, filas de altura adaptable y pesos Noto reales en PS4. Transiciones con asentamiento suave, origen reiniciado y vista previa que respeta movimiento reducido. Refinamiento de clave de caché validado tras auditoría OpenCode; recomendación incorrecta sobre foco rechazada.

Validación final: compilación/lint2m20s,0 errores/26 advertencias/3 avisos; firma, code37 e instalación/Descargas verificados por hash. Sin suites automáticas ni FPS medidos. Revisión física pendiente. [BETA37](docs/BETA37.md). Cierre del plan nocturno H1–H6 con sus límites documentados.

## 0.26.1-beta36 · 26 de octubre de2026

Nothing:176 iconos de apps y32 funciones sin contenedor circular añadido. Siluetas dot-matrix de mayor tamaño y trazos más completos; fallback sin placa y contraste semántico en capas monocromas. Conserva iconos normales, packs, reemplazos y contenedores elegidos explícitamente. Otros temas sin cambios de recursos.

Validación: lámina óptica de176 iconos y diff revisados; compilación/lint correctos,0 errores/26 advertencias/3 avisos. Firma, code36 y hashes de instalación/Descargas verificados. Sin suites ni FPS medidos; revisión visual física pendiente. [BETA36](docs/BETA36.md).

## 0.26.0-beta35 · 26 de octubre de2026

Cambios: siete efectos A–Z y opción sin efecto; apartado dedicado con vista previa interactiva, intensidad, respuesta, tamaño/permanencia de burbuja y vibración. Dos columnas con texto grande. Persistencia y respaldo12 implementados por OpenCode y revisados; UI por Codex. Conserva ergonomía y ajustes personales.

Validación: compilación/lint correctos;0 errores,26 advertencias,3 avisos. Instalación code35, firma y hashes local/transferencia/Descargas verificados. Sin suites ni medición FPS; revisión visual física pendiente. [Entrega](docs/BETA35.md).

## 0.25.1-beta34 · 26 de octubre de2026

Cambios: corrección implementada por OpenCode tras auditoría delegada y validada por Codex. El mensaje del encabezado, las etiquetas de tarjetas, el título del audio y el reloj flip Sense heredan la tipografía del tema, eliminando familias genéricas que se imponían a las fuentes de beta33. Los iconos y la navegación de Aura se mantienen.

Validación: diff revisado, compilación/lint correctos (0 errores,26 advertencias,3 avisos); firma, instalación code34 y hashes del APK/Descargas verificados. Sin suites automáticas ni afirmaciones de revisión visual de todos los temas. Informe del revisor contrastado; alerta de caché descartada por no corresponder al flujo real.

Fuente: [BETA34.md](docs/BETA34.md) y [trabajo delegado](docs/delegation/README.md).

## 0.25.0-beta33 · 26 de octubre de2026

Cambios: catálogo PS4 de176 apps inventariadas con placas azules e identidades blancas/azul hielo, recursos precalculados y prioridad de modos Normales/packs/reemplazos conservada. Tipografías locales distintas para las siete familias principales, aplicadas a los15 roles de interfaz. Droid Sans y Roboto de época; alternativas libres identificadas para LG, RAZR y PS4. La estructura y gestos de Aura de beta32 se mantienen.

Validación: revisión óptica de iconos/fuentes, compilación y lint correctos (0 errores,26 advertencias,3 avisos). Instalación code33 y hashes de APK/Descargas verificados. Revisión física de todos los temas y medición de FPS pendientes. Recursos modernos son interpretaciones, no pack oficial Sony ni certificación de fuentes propietarias idénticas.

Fuente: [BETA33.md](docs/BETA33.md).

## 0.24.1-beta32 · 26 de octubre de2026

Cambios: PS4 recupera la estructura común de Aura: favoritas y apps verticales, búsqueda permanente, A–Z y gestos. Se retiran el carrusel, la Biblioteca separada y las acciones PSN. Apertura con un toque, objetivos de toque más amplios y foco visible. Se conservan el acabado azul, las ondas y Noto Sans. Índices alfabéticos corregidos para contar cada favorita.

Validación: compilación/lint y entrega se detallan en [BETA32.md](docs/BETA32.md). Instalación posterior por ADB inalámbrico confirmada:Success, code32 y hashes de APK/Descargas coincidentes. Datos y preferencias conservados por diseño; no se afirma revisión nativa si el teléfono está ausente.

## 0.24.0-beta31 · 25 de octubre de2026

Cambios: pantalla PS4 independiente con funciones horizontales, carrusel con selección ampliada, Biblioteca y búsqueda, menú rápido lateral, perfil/hora, Noto Sans y recursos originales de la guía Sony. Fondo de ondas azul-negro y énfasis de350ms. Accesos reales; funciones PSN remiten a PlayStation App. Skill ps4-theme-fidelity instalada localmente.

Validación: compilación/lint correctos (0 errores,16 advertencias,3 avisos); firma, hash e instalación code31 comprobados. Revisión visual nativa pendiente de desbloqueo. No es una certificación de equivalencia exacta con firmware9.x; los PNG de Sony no son HD.

Fuente: [BETA31.md](docs/BETA31.md).

## Pendiente · próxima actualización

- PlayStation: revisar beta32 en el Motorola; el objetivo vigente es ergonomía de Aura con estética PS4, según la corrección del usuario.
- Nothing: iconos con silueta nativa, monocromáticos y sin contenedores circulares; conservar lectura dot-matrix.
- Moto Razr: pulido profundo, metal/LCD y guiños al formato plegable, sin mezclar arbitrariamente generaciones. El tema actual parte del RAZR V3; cualquier evolución moderna debe quedar documentada.
- SIII: completar156 apps pendientes con recursos de época comprobados o IA identificada; conservar evidencia por icono.
- Estos puntos están solicitados, todavía NO se presentan como implementados.

## 0.23.1-beta30

Cambios: Reconstrucción SIII20/176 (9 IA,11 equivalencias históricas);156 pendientes. Selector manual usa el recurso de la vista previa. Calendario y correo incorrectos descartados.

Validación y límites: Compilación/lint e instalación verificadas; revisión nativa visual completa pendiente.

Fuente: [BETA30.md](docs/BETA30.md).

## 0.23.0-beta29

Cambios: SIII sustituye fallback genérico por catálogo explícito:6/176 apps. Primer tema PS4: ondas azules, favoritas horizontales y barra de funciones.

Validación y límites: Compilación/lint e instalación documentadas; prototipo PS4 pendiente de pulido.

Fuente: [BETA29.md](docs/BETA29.md).

## 0.22.0-beta28

Cambios: Inventario de176 apps;1.056 interpretaciones de seis familias, SVG editables y WebP precalculados. Galería por app.

Validación y límites: Cobertura del inventario no equivale a fidelidad histórica. TouchWiz fue reemplazado posteriormente por reconstrucción SIII.

Fuente: [BETA28.md](docs/BETA28.md).

## 0.21.0-beta27

Cambios: 30 sonidos originales por tema; volumen y eventos configurables, desactivados inicialmente. Respeto de silencio/No molestar y reproducción.

Validación y límites: Entrega intermedia; límites en documento de versión.

Fuente: [BETA27.md](docs/BETA27.md).

## 0.20.0-beta26

Cambios: 32 funciones por seis familias:192 variantes. Selección reversible por app y galería de tamaños.

Validación y límites: Son funciones e interpretaciones; no192 apps históricas.

Fuente: [BETA26.md](docs/BETA26.md).

## 0.19.0-beta25

Cambios: Contratos visuales de seis temas; media con sesiones opcionales y controles; mensajes por tema/franja horaria.

Validación y límites: Detalles y verificación en documento de versión.

Fuente: [BETA25.md](docs/BETA25.md).

## beta24

Cambios: Escenas atmosféricas por condición y tema sustituyen el bitmap viajero: puntos, nubes, ondas, cintas, auroras y segmentos LCD.

Validación y límites: La condición depende del texto accesible del proveedor; fallback neutro. Véase documento de versión.

Fuente: [BETA24.md](docs/BETA24.md).

## 0.18.0-beta23

Cambios: Barrido meteorológico detrás del reloj; tarjetas y editor con jerarquía temática y alturas accesibles.

Validación y límites: Este efecto fue sustituido después; no describe el clima actual.

Fuente: [BETA23.md](docs/BETA23.md).

## 0.17.0-beta22

Cambios: Editor de cuatro zonas; deshacer/restablecer; borrador persistente; fondos con transacción local; tema Alloy inspirado en RAZR V3.

Validación y límites: Checkpoint local documentado; consultar límites de validación.

Fuente: [BETA22.md](docs/BETA22.md).

## 0.16.0-beta21

Cambios: Biblioteca retro ZIP opcional; fondos e iconos de colección personal, caché acotada y prioridad de reemplazos.

Validación y límites: Importación validada; recursos personales se transfieren por separado.

Fuente: [BETA21.md](docs/BETA21.md).

## 0.14.0-beta19

Cambios: Modos Temáticos/Normales/Monocromáticos; bloqueo en huecos; cuatro temas retro e ilustraciones de categorías.

Validación y límites: Los iconos normales conservan su identidad.

Fuente: [BETA19.md](docs/BETA19.md).

## 0.13.0-beta18

Cambios: Lenguaje Nothing/CMF, AMOLED, Geist/Geist Mono y reloj de puntos; acento naranja opcional.

Validación y límites: Tratamiento circular original sujeto a la nueva petición: retirar contenedores circulares.

Fuente: [BETA18.md](docs/BETA18.md).

## 0.12.0-beta17

Cambios: Doble toque para bloquear; accesibilidad voluntaria; motor visual de lista/índice/menús y movimiento reducido.

Validación y límites: Bloqueo real y desbloqueo por huella registrados en conversación.

Fuente: [BETA17.md](docs/BETA17.md).

## 0.11.0-beta16

Cambios: Próxima alarma, agenda opcional y gestos de inicio con accesos independientes.

Validación y límites: Permisos opcionales y consultas solo mientras Inicio es visible.

Fuente: [BETA16.md](docs/BETA16.md).

## 0.10.0-beta15

Cambios: Tamaños de texto/iconos/espacios, alto contraste, paquetes Android y personalización/respaldo.

Validación y límites: Consultar documento de versión para alcance.

Fuente: [BETA15.md](docs/BETA15.md).

## 0.9.0-beta14

Cambios: Uso diario y widgets, temas y accesos de reloj, calendario y clima.

Validación y límites: La validación histórica está en el documento original; no se repitió al generar esta cronología.

Fuente: [BETA14.md](docs/BETA14.md).

## Versiones sin ficha independiente en este índice

La evolución anterior a beta14 y beta20 no se reconstruye como entrega independiente sin evidencia suficiente. Avatar, proveedor Prognoza y distribución del dashboard aparecen recogidos en las notas posteriores. Las notas y los commits originales se conservan; la ausencia de entrada no significa ausencia de trabajo.

## Cómo registrar la siguiente entrega

Antes de publicar una versión, añadir arriba una entrada con versión/código, fecha real, cambios visibles, correcciones, validación ejecutada, límites conocidos y enlace a su ficha. Si se instala, anotar resultado y hash del APK; si no se instala, decirlo. Mover aquí únicamente cambios completados desde Pendiente. Hacer commit de código, recursos y documentación juntos. No registrar tokens, claves, cuentas ni respaldos del teléfono.

## Checkpoint29oct: beta44 sin entregar
Widget multimedia activo/compacto y optimizaciones en fuente; validación final pendiente. Parada cuota89%. Leer docs/BETA44.md antes de continuar. No usar APK del proceso previo como entrega validada.
