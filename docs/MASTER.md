# Registro maestro de Aura Launcher

Checkpoint: **beta52,0.36.1/code52,3 de noviembre de2026**, instalada y hash
comprobado. Rama `codex/beta52-ps4-fluidity`. Leer este archivo y CONTINUITY.md
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

Actualizar este registro, CHANGELOG, README, CONTINUITY y BETAxx en el mismo
commit de aplicación: fecha real, versión/código, archivos y cambios visibles,
agentes/modelos y qué se aceptó/rechazó, validaciones, tamaño/firma/hash si se
comprobaron, instalación real y pendientes. Nunca incluir secretos, claves,
SDK, APK, respaldos ni evidencia privada del teléfono. No ejecutar suites
automáticas ni cambiar ajustes/permisos del dispositivo sin autorización.
