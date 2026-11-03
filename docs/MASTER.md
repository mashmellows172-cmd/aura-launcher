# Registro maestro de Aura Launcher

Checkpoint: **beta55,0.36.4/code55,3 de noviembre de2026**, instalada y hash
comprobado. Rama `codex/beta55-settled-focus`. Leer este archivo y CONTINUITY.md
al retomar desde cualquier equipo. El historial completo está en
[CHANGELOG](../CHANGELOG.md); cada BETA contiene evidencia y límites.

## Cambios entre versiones recientes

| Versión | Cambio respecto a anterior | Validación y entrega | Pendiente |
|---|---|---|---|
| [Beta50](BETA50.md) | Migración autorizada, foco Gboard, respaldo14 con desbloqueo/modo ligero | Build/lint, instalación; reexportación101 ajustes y5 imágenes contrastados | Reconexión voluntaria de permisos/widgets |
| [Beta51](BETA51.md) | Centro de estado, adaptación por batería, fondo pausado durante scroll, entradas accesibles, respaldo15 | Build/lint0 errores; instalación, versión/hash; inicio y centro comprobados | Comparación de rendimiento |
| [Beta52](BETA52.md) | Sustituye pausa/espera32ms por ritmo de frames; títulos estables, foco y fondo con menos pasadas, menú240ms/28dp | Build/lint0 errores; instalación y hash comprobados | Revisión visual final y muestra comparable: ADB desconectado después de instalar |

## Beta52: cambios concretos

- Ps4LiveWallpaper:15 pasadas de cintas por actualización frente a24; reloj
  alineado a frames con umbral16ms, aproximadamente60 actualizaciones/s en
  pantallas compatibles; conserva movimiento durante el gesto. Menos pasadas
  por frame NO demuestra menos trabajo total: aumenta la frecuencia respecto
  a beta51. La comparación física sigue pendiente.
- HomeScreen: quita las escalas simultáneas de fila, icono y título al cambiar
  foco. Títulos a tamaño del usuario+4sp, peso Regular y familia conservada.
- Ps4Theme: foco de7 a4 dibujos, halo único y contornos más finos cacheados.
- ThemeTypography: encabezados28/36sp Light; lectura18/26sp Regular; controles
  Medium. Noto existente, alternativa libre; no se presenta como SST oficial.
- Ps4QuickMenu: llegada240ms y recorrido28dp; conserva acciones y accesibilidad.
- FrameDiagnosticsDialog: explicación actualizada del modo adaptativo.
- No nuevos recursos gráficos ni dependencias dentro del APK.

## Delegación y consumo

Preferencia vigente: tareas pesadas locales mediante Ollama; supervisor revisa
alcance, diff, build/lint y resultado. No dar por buena una respuesta del modelo.
Modelos comprobados: deepseek-coder:6.7b y deepseek-r1:7b.

Para tareas locales: fuentes concretas, contexto corto, salida acotada y como
máximo una corrección antes de revisar estrategia. Usar
[runner](../tools/local-ai/README.md). Conservar originales; no comprimir de
forma destructiva código destinado a reemplazos exactos. Descargar modelo de
memoria al terminar cuando no haya más encargos. No prometer ahorro de cuota
sin medición; la supervisión y los agentes remotos también consumen cuota.

Beta52: agente de movimiento implementó fondo; agente de diseño implementó
tipografía/foco; revisor examinó diff combinado; supervisor integró menú,
validación e instalación. Agente de herramientas agotó cuota tras instalar;
se verificó localmente Headroom0.42.0. No volver a lanzarlo automáticamente.

Revisión breve adicional delegada a DeepSeek Coder por Ollama: contexto2048,
salida máxima200tokens, descarga inmediata del modelo al acabar. Respondió
100tokens rechazando indebidamente analizar animación Android en español.
Resultado descartado; sin reintento ni código aplicado. No se acredita ahorro.

## Skills y herramientas solicitadas

| Herramienta | Estado comprobado | Uso/límite |
|---|---|---|
| frontend-design, animate, accessible-animation, ponytail | Instaladas; aplicadas al diseño/revisión | PS4 fiel, continuidad, accesibilidad y cambios pequeños |
| playwright-skill | Instalada; Playwright1.62.1 y Chromium informados por agente | Navegador; no valida UI Compose. No se ejecutaron suites web |
| omni-compression, omni-budget | Instaladas y leídas | Requieren servicio OmniRoute/auth; puerto local20128 sin servicio. Sin enrutamiento global activado |
| Headroom | headroom-ai0.42.0 en entorno aislado Aura-tools/headroom-venv | Biblioteca, no SKILL.md. Compresión efectiva/ahorro aún no verificados |

Fuentes: [Playwright](https://github.com/lackeyjb/playwright-skill),
[OmniRoute](https://github.com/cheaper-inference/OmniRoute/wiki/Skills),
[Headroom](https://github.com/headroomlabs-ai/headroom).

## Regla para la siguiente entrega

### Incidencia prioritaria abierta: lag durante swipe

Reporte del usuario tras beta52: animación/transición durante swipe todavía
se percibe con lag. El pulido visual NO resolvió la prioridad de fluidez.
No cerrar esta incidencia por compilación correcta o por reducir dibujos.

Hipótesis pendiente de aislar: beta52 aumentó la frecuencia del fondo y lo
mantiene activo durante scroll; menos pasadas por actualización no implica
menor carga por segundo. Revisadas además las transformaciones compartidas
de AuraMotion y carga/caché de iconos: no se ha demostrado una causa única.

Siguiente validación: mismo recorrido en Motorola, comparar fondo activo y
estático, luego efectos de lista activos e inactivos mediante controles
existentes; conservar/restaurar preferencias. Recoger frames y revisar el
gesto visualmente. Sin suites automáticas ni cambios de ajustes del sistema.
ADB y mDNS sin dispositivo al investigar este reporte; solicitada dirección
actual. No se creó beta53 ni se aplicó otro ajuste visual a ciegas.

Reconexión posterior por mDNS: beta52 confirmada. Primera secuencia de cuatro
swipes600ms:223frames,49janky(21,97%),mediana26ms,p9057ms. Se observó Cascada
seleccionada y se tocó Sin efecto para comparar. Segunda muestra280frames,
98janky(35%),mediana18ms,p9029ms; NO se considera comparación válida: al
intentar restaurar, la ventana activa era otra app y la extracción UI falló.
Gestos detenidos para evitar interferir; solicitada ventana de un minuto sin
interacción. Selección actual y restauración de Cascada pendientes de verificar.
No afirmar causalidad, ahorro ni arreglo con estas muestras. Evidencia privada
local; sin nueva versión ni cambios de código.

Comparación repetida con el usuario dejando el teléfono libre: selección
Sin efecto confirmada por UI; cuatro swipes600ms:234frames,42janky(17,95%),
mediana28ms,p9048ms. Cascada restaurada y confirmada por UI; mismo recorrido:
219frames,41janky(18,72%),mediana29ms,p9048ms. La diferencia pequeña no permite
atribuir el lag principal a Cascada. Se conserva Cascada al acabar. Muestras
cortas, sin control de frecuencia/temperatura/actividad de fondo; no son un
benchmark. Estado térmico global Android0 al consultar después. Pendiente
aislar fondo estático frente a animado y revisar tiempos de dibujo; no hay
corrección de código ni nueva versión acreditada en este diagnóstico.

Actualizar este registro, CHANGELOG, README, CONTINUITY y BETAxx en el mismo
commit de aplicación: fecha real, versión/código, archivos y cambios visibles,
agentes/modelos y qué se aceptó/rechazó, validaciones, tamaño/firma/hash si se
comprobaron, instalación real y pendientes. Nunca incluir secretos, claves,
SDK, APK, respaldos ni evidencia privada del teléfono. No ejecutar suites
automáticas ni cambiar ajustes/permisos del dispositivo sin autorización.

## Beta53: capa retenida y seguimiento de temas

Comparación manual completada con autorización: dos muestras de cuatro swipes
600ms. P90 beta53:34ms y27ms frente a48ms en muestra beta52 anterior;
mediana21ms y16ms frente a29ms. Jank moderno15,84% y3,18% (legacy92,08%
y69,36%); variabilidad alta, condiciones entre versiones no controladas.
Indicio favorable, sin paridad con otros temas ni porcentaje de mejora estable.
Captura revisada correcta; preferencias sin cambios. Ver BETA53.md.

Instalada, versión53 y hash del teléfono comprobados; ver BETA53.md. Capa opaca con geometría retenida; comparación física pendiente, sin paridad con otros temas acreditada. Ponytail ya estaba instalada y se usó. DeepSeek local revisó134tokens, conclusión sobre reducir FPS rechazada. Sin agentes remotos adicionales. Ver THEME-FIDELITY.md para siete identidades, sonidos AUTO existentes y pendientes reales; no se afirma reconstrucción completa. Cuota90% no observable.

## Beta54: prueba estática solicitada

Medición final: Aura visible comprobada. Estático primera pasada285frames,
14,74%jank,mediana20ms,p9034ms; segunda392frames,3,57%jank,mediana16ms,p9021ms.
Beta53 animado: primera p9034ms/15,84%jank; segunda p9027ms/3,18%jank.
Mejora de cola en segunda pasada, sin reducción clara de jank; no demuestra
que el fondo sea causa única. Se mantiene estático según prueba solicitada.
Resultados y límites completos en BETA54.md; preferencias sin cambios.

Fondo PS4 sin clock; demás efectos y preferencias conservados. Build/lint2m33s,0 errores/35 advertencias/3 avisos; instalada, versión54 y hash comprobados. Muestra inicial descartada: NotificationShade y cero frames. Usuario confirmó Aura visible; comparación completada. Sin mejora estable acreditada. Ver BETA54.md. Skills frontend-design y caveman aplicadas.

## Beta55: foco asentado, mejora no demostrada

Ver [BETA55](BETA55.md). Instalada, código/hash verificados. Prioridad absoluta: regresión de swipe PS4. No añadir nuevos efectos hasta aislar el coste. OpenCode revisó fuente; Ollama revisó velocidad; Antigravity recibió solicitud sin respuesta recuperada. No dar sus hipótesis por hechos.
