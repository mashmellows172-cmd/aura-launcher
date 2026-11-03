# Beta54 · comparación PS4 estático

3nov2026,0.36.3/code54. Rama codex/beta54-static-ps4.

Prueba solicitada por el usuario: detener únicamente el reloj del fondo PS4.
Geometría y aspecto de beta53 retenidos; sin cambiar Cascada, foco, sonidos,
iconos ni preferencias guardadas. No equivale a movimiento reducido global.
Ponytail/frontend-design/accessible-animation aplicadas a cambio mínimo,
conservación de identidad y respeto de controles existentes; caveman en mensajes.

Hipótesis: si el fondo activo consume el presupuesto de frames, la versión
estática debe mejorar el mismo recorrido. No darlo por probado sin muestras.
Primer intento de referencia beta53 inválido: NotificationShade activo,
cero frames y percentiles4950ms centinela. Descartado. Solicitado dejar Aura
visible. Compilación/lint correctos2m33s,0 errores/35 advertencias/3 avisos.
APK15.546.660bytes; firma2eb417c5… compatible verificada. Install -r:Success.
Versión54/0.36.3-beta54 y hash del APK instalado comprobados:
13779dbebf37127cd1318446e3098592fe1c75afbdd59af927f98730fd97a384.
Medición estática pendiente: panel NotificationShade cubre Aura y se espera
confirmación del usuario de pantalla disponible. No afirmar mejora.

Medición completada tras confirmación del usuario; Aura en primer plano al
inicio y final. Dos pasadas de cuatro swipes600ms idénticas a beta53:

| Fondo | Pasada | Frames | Jank moderno | Mediana | P90 | P95 |
|---|---|---:|---:|---:|---:|---:|
| Animado beta53 | 1 | 303 | 15,84% | 21ms | 34ms | 40ms |
| Animado beta53 | 2 | 346 | 3,18% | 16ms | 27ms | 30ms |
| Estático beta54 | 1 | 285 | 14,74% | 20ms | 34ms | 40ms |
| Estático beta54 | 2 | 392 | 3,57% | 16ms | 21ms | 25ms |

Resultado mixto: primera pasada equivalente; segunda reduce cola de tiempos
pero no el porcentaje de jank moderno. Legacy estático86,67%/72,19%, también
alto. Sin control de frecuencia/temperatura/carga y pocas muestras: no afirmar
causalidad ni mejora estable. El fondo no explica por sí solo todo el retraso.
Se deja beta54 estática instalada para valoración del usuario, sin modificar
preferencias de efectos. Evidencia cruda privada fuera de Git.
Sin suites automáticas ni ajustes del sistema. Evidencia privada fuera de Git.
