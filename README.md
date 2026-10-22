# Aura Launcher

Launcher Android personal, implementado desde cero con APIs públicas. No está afiliado a Niagara.

## Compilar

Requiere Android Studio, JDK 17 y Android SDK Platform 37 / Build Tools.

```powershell
.\gradlew.bat assembleDebug
```

En este workspace, `./build-beta.ps1` usa el JDK y SDK existentes, ejecuta pruebas y lint, y firma con la clave de las betas anteriores. Evita desinstalar Aura para actualizar: usa `adb install -r` con el APK firmado.

Beta actual: [Aura-0.17.0-beta22.apk](beta/Aura-0.17.0-beta22.apk), variante optimizada con R8. Novedades, verificación y límites: [beta22](docs/BETA22.md). Consulta [BETA.md](BETA.md) para instalar y cambiar opciones.

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

Beta22: pruebas de lógica, compilación optimizada, lint y firma. Resultados y comprobaciones físicas pendientes: [informe beta22](docs/BETA22.md). Los datos de betas anteriores que siguen son históricos.

Compila contra Android API 37. Beta14 cuenta con 11 pruebas unitarias y análisis estático. Los pasos y la evidencia de dispositivo están en `../replica/test-plan.md`; las herramientas Android de regresión están en `e2e/`. Beta17 reemplaza el motor de movimiento anterior. El experimento beta13 permanece fuera de esta entrega, en `work/paused-motion-beta13` del workspace.

## Licencia

MIT; consulta [LICENSE](LICENSE).




Muestra histórica de beta14 optimizada, después del reinicio y sin PiP: arranque COLD de 169 ms; 397 frames, 14 con retraso (3,5 %); PSS 304.157 KB; sin nuevos errores AndroidRuntime de Aura. La carga y el estado del teléfono difieren de la muestra debug: no es una comparación causal ni una garantía sostenida.

Muestra beta16: arranque frío 253 ms; 1/500 frames con retraso; sin nuevos errores AndroidRuntime de Aura durante ocho desplazamientos. Dispositivo con PiP activo; alcance y límites en [QA beta16](docs/QA-beta16.md).

Muestra beta17 con Pliegue 3D y bloqueo activo: 4/596 frames con retraso durante ocho desplazamientos; sin nuevos errores AndroidRuntime de Aura. Bloqueo real y desbloqueo con huella comprobados. Alcance: [QA beta17](docs/QA-beta17.md).

## Recursos tipográficos

El código Aura mantiene su licencia MIT. Geist y Geist Mono tienen licencia SIL OFL1.1; sus avisos completos se incluyen en `app/src/main/assets/licenses/`. No se redistribuyen tipografías propietarias de Nothing.
