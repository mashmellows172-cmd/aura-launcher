# PS4 vertical · beta40

Referencia aprobada: captura adjunta del usuario, lista vertical con identidades a color, placas cuadradas, foco central azul/blanco, títulos ligeros y cintas de luz azules. La guía pública de Sony describe la consola horizontal; aquí se adapta su lenguaje visual a Aura, sin restaurar el menú horizontal rechazado. Fuente: https://manuals.playstation.net/document/gb/ps4/ . No afirmar equivalencia100% ni usar SST propietaria sin disponer de licencia: mantener Noto Sans Light incluida.

1. Conservar cambios documentales locales previos, firma y datos. Implementar en la rama de validación existente; main mantiene el gate físico.
2. HomeScreen/Ps4Theme: foco derivado del ítem de app más cercano al centro, transiciones finitas320ms leídas en draw/layer, placas cuadradas e iconos nativos a color en modo AUTO PS4. Respetar Normales, Monocromo, packs y reemplazos. No cambiar preferencias persistentes ni inventar categorías.
3. Ps4Wallpaper: cintas y líneas cian sobre azul profundo, con el reloj lifecycle existente, sin blur ni nuevos timers. Fondo personal y alto contraste conservados.
4. ThemeTypography/ThemeChrome: títulos PS4 Light, encabezados32sp adaptables y contadores discretos. Búsqueda, A–Z, widgets, clima, alarmas, gestos y un toque para abrir permanecen en su layout real.
5. Compilar assembleBeta/lintBeta exclusivamente. Verificar firma/version/hash; instalar solo mediante el guardián local autorizado si Motorola disponible. No cambiar ajustes ni conceder permisos. Inspección visual sin navegar otras apps si teléfono libre; ausencia de revisión se documenta.
6. Registrar BETA40/CHANGELOG/continuidad/README y límites. No regenerar packs automáticos ni recrear la beta38 cloud ausente.
