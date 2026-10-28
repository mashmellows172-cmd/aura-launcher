# Aura Launcher

Launcher Android personal, implementado desde cero con APIs públicas. No está afiliado a Niagara.

## Compilar

Requiere Android Studio, JDK 17 y Android SDK Platform 37 / Build Tools.

```powershell
.\gradlew.bat assembleDebug
```

En este workspace, `./build-beta.ps1` usa el JDK y SDK existentes, ejecuta pruebas y lint, y firma con la clave de las betas anteriores. Evita desinstalar Aura para actualizar: usa `adb install -r` con el APK firmado.

Última beta instalada: **0.27.0-beta38**, variante optimizada con R8. [Entrega y límites](docs/BETA38.md). Los APK y claves de firma quedan fuera del repositorio.

[Historial completo por actualización](CHANGELOG.md) · [Trabajar desde otro equipo](docs/DEVELOPMENT.md) · [Estado para continuar](docs/CONTINUITY.md).

## Beta39 preparada

Favoritas sin estrellas repetidas, siete efectos de lista y siete despliegues por tema, fuentes refinadas y galería de fondos recuperada. Compilación/lint/firma verificados; instalación y revisión física pendientes. [Entrega y límites](docs/BETA39.md).

## Rama de validación beta38

Editor seguro con Undo/Redo y Room, revisión SIII y módulos de rendimiento. [Estado y límites](docs/BETA38.md). Rama `codex/phase1-theme-editor`; instalada, no fusionada a main. Medición física pendiente.

## Nuevo en beta34

- Corrección de fuentes en encabezado, tarjetas, audio y reloj Sense, implementada por OpenCode y revisada por Codex.
- [Prompts, resultados y validación de la delegación](docs/delegation/README.md).

## Nuevo en beta33

- 176 iconos PS4 derivados del inventario real, con placas azules y símbolos blancos/azul hielo.
- Tipografías propias de cada familia aplicadas en listas, buscador, menús y encabezados.
- [Muestra de iconos](artwork/ps4/app-icons/preview.png) y [muestra tipográfica/procedencia](artwork/theme-fonts/README.md).

## Nuevo en beta32

- PS4 vuelve a usar la navegación de Aura: lista vertical, favoritas, búsqueda y A–Z.
- Un toque abre cada app; gestos y widgets vuelven a la estructura común.
- Se retira la pantalla de consola de beta31 conservando tipografía, ondas y acabado azul.

## Nuevo en beta31

- Pantalla PS4 independiente: funciones, carrusel horizontal, Biblioteca con búsqueda y menú rápido lateral.
- Noto Sans, iconos de la guía Sony, selección ampliada y fondo azul-negro con ondas.
- [Procedencia y límites de fidelidad](artwork/ps4/README.md); los recursos oficiales disponibles no constituyen un kit HD.

## Nuevo en beta30

- Reconstrucción específica Galaxy SIII: 20 de176 apps con recursos preparados (9 iconos IA y11 equivalencias históricas por función);156 pendientes. El resto conserva su icono normal.
- Corrección del selector manual para usar el mismo recurso SIII de su vista previa.
- Tema PS4 introducido en beta29: fondo de ondas azules, favoritas horizontales y barra de funciones. La fidelidad de navegación continúa en desarrollo.

## Nuevo en beta28

- 176 apps del inventario real del Motorola, seis variantes por app:1.056 recursos, además del catálogo de32 funciones. Cobertura completa de esa captura; el acabado histórico individual continúa en revisión.
- SVG editables y WebP precalculado para evitar trazados pesados al desplazar la lista. [Muestra](artwork/installed-app-icons/preview.png).
- Galería por app y tamaño, selector A–Z accesible, superficies y fondos por tema, protección de lectura del wallpaper y comparación visual reversible. Respaldo11/importación1–11.
- Son reinterpretaciones derivadas de las identidades de las apps; no son1056 iconos oficiales históricos. Tus reemplazos y el modo Normales conservan prioridad.

## Nuevo en beta27

- 30 señales sonoras originales, configurables y desactivadas inicialmente; respetan silencio, No molestar y sonidos táctiles del sistema.
- Familia sonora automática o independiente, volumen y eventos; audio del índice opcional y limitado. Pool ligado a visibilidad de Inicio.
- Respaldo10/importación1–10. [Entrega intermedia y evidencia](docs/BETA27.md).

## Nuevo en beta26

- 192 variantes vectoriales originales:32 funciones por cada una de las seis familias de tema.
- Galería con tamaños32/42/60dp y fondo claro/oscuro. Selección manual por aplicación, además de asignación exacta para funciones del sistema.
- Los iconos normales, reemplazos personales, packs externos y colección retro conservan su prioridad. Sin permisos nuevos ni cambio del respaldo9.
- Maestros SVG y generador reproducible en [artwork/icon-families](artwork/icon-families/README.md).

## Nuevo en beta25

- Seis identidades históricas diferenciadas en reloj, tarjetas, clima y controles; muestra interactiva del tema en el editor.
- Widget automático de reproducción con sesiones y controles opcionales, carátula acotada y modo básico sin acceso especial.
- 108 mensajes por tema y franja horaria; animaciones decorativas ligadas al ciclo de vida y ajustes de accesibilidad.
- Respaldo9 compatible con formatos1–9. [Detalles y procedencia de recursos](docs/BETA25.md).

## Nuevo en beta23

- Barrido del símbolo meteorológico detrás de toda la franja reloj/clima, con seis acabados por tema. Controles de modo, duración e intensidad; movimiento reducido y ciclo de vida respetados.
- Tarjetas de alarma/agenda con jerarquía y estilo del tema. Texto grande puede aumentar su altura mínima.
- Editor con selectores y deshacer accesibles primero; vista previa opcional con los mismos componentes y regla de anchura que Inicio.
- Respaldo7/importación1–7, 30 pruebas JVM y comprobación de contraste de las paletas reales.

## Nuevo en beta22

- Editor visual de inicio con cuatro zonas, selección de bloques, deshacer, restablecimiento reversible y guardado explícito. Conserva el borrador y su historial al recrear la actividad.
- Ordenar bloques afecta solo a su zona. Los cambios de un deslizador se deshacen como un gesto.
- Aura Alloy: tema original inspirado en Motorola RAZR V3, con reloj LCD azul, metal grabado e iconos propios. Nothing sigue como principal.
- Importación de fondos con limpieza de archivos parciales y protección frente a cancelaciones. Las imágenes anteriores desaparecen al cambiar su origen.
- Fuentes distribuibles: `python tools/package-source.py --output beta/Aura-source.zip` desde un checkout Git. Excluye compilaciones, claves, evidencia privada y recursos personales.

## Nuevo en beta21

- Biblioteca retro local opcional: [preparar e importar recursos](docs/RETRO-LIBRARY.md). El APK y el código fuente no incluyen imágenes de fabricantes.
- Fondos seleccionables por tema e iconos importados con proporciones y transparencia conservadas.
- Caché de iconos acotada, recogida de estado ligada a visibilidad, tipos de fila y ciclo breve del clima. [Alcance de rendimiento](docs/perf-audit-2026-10-22-app.md).
- Desarrollo de beta20 incluido: avatar, clima desde el widget público de Prognoza y distribución configurable de alarma, agenda y widgets; regresiones pendientes identificadas en QA.

## Nuevo en beta19

- Elección explícita por tema: iconos Temáticos, Normales o Monocromáticos; Normales muestra los originales sin máscara ni recolor de Aura.
- Doble toque disponible en los huecos del inicio al acotar los accesos del reloj, fecha, clima y filas de aplicaciones.
- Cuatro temas retro renovados: Sense con reloj de láminas; TouchWiz con naturaleza/agua; LG con esmalte y cintas; Xperia con formas angulares y ondas.
- Ilustraciones originales para 17 categorías de aplicaciones y fallback al icono de cada app; formas personalizadas también en esos dibujos.

## Incluido desde beta18

- Tema principal Aura · Nothing: negro AMOLED uniforme, Geist/Geist Mono locales con licencia OFL, reloj original de puntos e iconos monocromáticos.
- Acabado naranja CMF opcional, controles neutros, contadores cápsula y letra A–Z de señal.
- Estilo de iconos automático, monocromático o a color por tema; vista previa y restablecimiento del estilo principal antes de aplicar.
- Respaldo formato 4 compatible al importar formatos 1–3. No modifica favoritos ni permisos al aplicar el tema.

## Incluido desde beta17

- Doble toque para bloquear desde el logo o el fondo libre, configurable y con acceso de accesibilidad voluntario.
- Motor visual nuevo: Profundidad, Pliegue 3D, Onda, Órbita y Deslizamiento; recuperación gradual y opción Sin efecto.
- Índice A–Z magnético/con resorte, indicador de letra ampliado y háptica opcional.
- Transiciones de diálogos coherentes, vista previa finita y ajustes independientes de lista, índice y menús.
- Respaldo formato 3, compatible al importar formatos 1 y 2.

## Incluido desde beta16

- Próxima alarma y agenda opcional de siete días, con accesos a sus aplicaciones.
- Editor de temas con vista previa, acentos, formas, paquete de iconos y fondo recordados por tema.
- Gestos configurables sobre el logo o el fondo libre; acceso a notificaciones opcional mediante accesibilidad.
- Orientación configurable, pulsación larga en el fondo y respaldo versión 2 compatible con versión 1.

## Incluido desde beta15

- Menú **Personalización y respaldo**: tamaño de texto e iconos, separación y alto contraste.
- Paquetes de iconos appfilter por tema, con prioridad de los PNG individuales y fallback a Android.
- Exportar/importar JSON local mediante el selector de archivos, imágenes embebidas y resumen antes de restaurar. Los widgets no se transfieren entre dispositivos.

## Funciones incluidas

- Catálogo de apps lanzables, búsqueda local sin distinguir acentos y apertura de apps. Los iconos se decodifican fuera del hilo de interfaz y se mantienen en una caché LRU acotada para reducir trabajo al desplazarse.
- Índice alfabético fijo a la derecha: toque directo o arrastre vertical para recorrer iniciales.
- Favoritos locales: añadir/quitar desde estrellas o pulsación larga, reordenar y quitar desde el editor.
- Organización local: ocultar una app desde su menú de pulsación larga y restaurarla desde **Apps ocultas**.
- Pieles persistentes: **Aura · Nothing** (principal, negro AMOLED, Geist e iconos monocromáticos), Aura Bloom (homenaje a HTC Sense), Aura Prism (homenaje a Samsung TouchWiz), Aura Flow (homenaje a LG), Aura Horizon (homenaje a Xperia) y Aura Alloy (homenaje al RAZR V3). Además conserva AMOLED puro, Oscuro, Claro y Seguir el sistema.
- Cada piel aplica un fondo abstracto original, color de acento, forma animada del soporte de iconos y su propio ritmo de transición. La distribución, el catálogo y la navegación permanecen iguales. Las referencias a fabricantes son homenajes; no se incluyen recursos ni marcas de ellos.
- Cabecera interactiva con accesos a alarmas, calendario y app de clima elegida por el usuario.
- Widgets Android reales: añadir desde el menú, autorizar con Android, configurar con su proveedor, cambiar altura, ordenar y quitar. Hasta cuatro, guardados localmente por ID. Escuchan actualizaciones mientras Aura está visible.
- Catálogo actualizado al instalar, desinstalar o activar/desactivar aplicaciones; favoritos y ocultas se conservan cuando un paquete deja de estar disponible temporalmente.
- Motor Aura Motion reescrito en beta17 con perfiles de profundidad, pliegue 3D, onda, órbita y deslizamiento; transformaciones ligadas a la velocidad, recuperación gradual, halo de búsqueda y controles de intensidad y movimiento reducido.
- Puedes asignar un fondo de tu galería a cada piel y elegir un PNG de icono para cada app desde su menú de pulsación larga. Los reemplazos se guardan por tema, localmente, y son reversibles.

Aura no necesita conexión, no recopila datos y no incluye analítica, publicidad ni pagos. Los temas, favoritos y apps ocultas se guardan localmente con DataStore.

## Verificación

Beta25: compilación optimizada, lint, revisión de código y firma. Consulta [QA beta25](docs/QA-beta25.md) para evidencia de instalación y límites. No se ejecutaron suites automáticas nuevas en esta entrega. Los resultados de betas anteriores son históricos.

Compila contra Android API 37. Beta14 cuenta con 11 pruebas unitarias y análisis estático. Los pasos y la evidencia de dispositivo están en `../replica/test-plan.md`; las herramientas Android de regresión están en `e2e/`. Beta17 reemplaza el motor de movimiento anterior. El experimento beta13 permanece fuera de esta entrega, en `work/paused-motion-beta13` del workspace.

## Licencia

MIT; consulta [LICENSE](LICENSE).




Muestra histórica de beta14 optimizada, después del reinicio y sin PiP: arranque COLD de 169 ms; 397 frames, 14 con retraso (3,5 %); PSS 304.157 KB; sin nuevos errores AndroidRuntime de Aura. La carga y el estado del teléfono difieren de la muestra debug: no es una comparación causal ni una garantía sostenida.

Muestra beta16: arranque frío 253 ms; 1/500 frames con retraso; sin nuevos errores AndroidRuntime de Aura durante ocho desplazamientos. Dispositivo con PiP activo; alcance y límites en [QA beta16](docs/QA-beta16.md).

Muestra beta17 con Pliegue 3D y bloqueo activo: 4/596 frames con retraso durante ocho desplazamientos; sin nuevos errores AndroidRuntime de Aura. Bloqueo real y desbloqueo con huella comprobados. Alcance: [QA beta17](docs/QA-beta17.md).

## Recursos tipográficos

El código Aura mantiene su licencia MIT. Geist y Geist Mono tienen licencia SIL OFL1.1; sus avisos completos se incluyen en `app/src/main/assets/licenses/`. No se redistribuyen tipografías propietarias de Nothing.
