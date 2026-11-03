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
Sin suites automáticas ni ajustes del sistema. Evidencia privada fuera de Git.
