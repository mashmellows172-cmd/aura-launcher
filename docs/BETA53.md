# Beta53 · fondo PS4 retenido

3nov2026,0.36.2/code53. Rama codex/beta53-retained-ps4 sobre beta52.

Ps4LiveWallpaper registra geometría, gradientes y trazos sin leer estado animado
en draw. La fase se lee en graphicsLayer para trasladar suavemente la capa
retenida. Capa opaca para evitar buffer de alfa a pantalla completa. Fondo base
separado cubre los bordes; escala1,04. Conserva reloj/lifecycle/reduceMotion y
preferencias. Cambia la deformación de ondas por desplazamiento suave del
material; requiere aceptación visual y medición. No equivale al fondo oficial.

Revisión local DeepSeek Coder:134tokens, contexto2048,salida máxima160,
keep_alive0. Propuso caché/capa, pero afirmó erróneamente que menos FPS mejora
fluidez. Esa conclusión se rechazó. Supervisor implementó y revisó la solución.
No agentes remotos adicionales; cuota90% no observable desde la sesión.

Ponytail comprobada instalada y aplicada. Frontend-design/animate y
accessible-animation guían identidad y movimiento reducido. Sonidos por tema
ya existen mediante AUTO; no se sobrescriben familias, silencio ni volumen.
Matriz de referencias y pendientes de las siete familias en THEME-FIDELITY.md.
No presentar todos los temas como profundizados en esta entrega.

Compilación/lint final correctos1m8s;0 errores/35 advertencias/3 avisos.
APK15.546.660bytes. SHA256
87d093a47653e12c4cc6fb0f2b290e7bd66e42c92ad177539fb7ea7a71f9a637.
Firma2eb417c5… compatible verificada. ADB install -r:Success.
Sin suites automáticas. No hay mejora porcentual ni paridad de fluidez
acreditada. Primera compilación exploratoria correcta2m10s; la versión final
se recompila tras incrementar código y fijar opacidad.

Versión53/0.36.2-beta53 y SHA256 del APK instalado coinciden.

Comparación manual autorizada, cuatro swipes600ms de ida/vuelta por pasada:

| Muestra | Frames | Jank Android | Mediana | P90 | P95 |
|---|---:|---:|---:|---:|---:|
| Beta52/Cascada previa | 219 | 18,72% | 29ms | 48ms | 57ms |
| Beta53 primera | 303 | 15,84% | 21ms | 34ms | 40ms |
| Beta53 segunda | 346 | 3,18% | 16ms | 27ms | 30ms |

Los resultados sugieren menor tiempo de frame, con variabilidad considerable;
no prueban una mejora porcentual estable ni igualdad con los otros temas.
El indicador legacy también sigue alto:92,08% y69,36% en beta53; no mezclarlo
con el indicador moderno. Sin control de frecuencia/temperatura/carga de fondo
entre versiones; muestras cortas. La primera extracción UI no logró idle;
ventana activa de Aura comprobada y captura posterior revisada. No se cambiaron
preferencias durante estas dos muestras. Captura confirma PS4, siete favoritas,
iconos a color, fondo continuo sin bordes vacíos, búsqueda y A–Z. Una captura
no acredita la fluidez dinámica. Evidencia privada fuera de Git.
