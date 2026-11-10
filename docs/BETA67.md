# Beta67 · pack meteorológico PS4 y listado de aplicaciones

10 de noviembre de 2026. 0.46.0-beta67, código 67. Base beta66 `69a70a4`.

- PS4 siempre usa un icono de Aura. El pack incluye 14 condiciones y un símbolo neutro para condiciones sin identificar. A los ocho eventos de beta66 se suman nubes parciales de día y de noche, llovizna, lluvia intensa, aguanieve y granizo.
- Los cambios de condición tienen un fundido de 340 ms. El relieve de las nubes recibe un reflejo localizado. Cada variante tiene su propio movimiento, con recorridos finitos y una versión estática para alto contraste, ahorro de energía y movimiento reducido. La muestra sigue disponible en **Menú rápido → Clima en el reloj → Ver iconos y animaciones PS4**.
- Prognoza 3.5.0 publica el símbolo de su widget sin descripción. Aura reconoce las familias por coincidencia con los recursos de Prognoza instalados, con la comparación fuera del hilo principal. Las lecturas ambiguas mantienen el símbolo neutro. El widget conserva la temperatura y ciudad publicadas. [Código del widget de Prognoza](https://github.com/davidtakac/prognoza/blob/3.5.0/androidApp/src/main/java/hr/dtakac/prognoza/widget/ForecastWidget.kt)
- Se eliminó el segundo filtro de habilitación de actividades. Android ya resuelve el estado efectivo de los alias para el usuario. El filtro extra descartaba alias activos como los de McDonald’s y Mercado Libre por el valor de habilitación de su manifiesto.

## Validación

- `assembleBeta` y `lintBeta` completados en 58 s. Lint registró 0 errores, 37 advertencias y 3 avisos. Firma del APK verificada.
- Instalación sobre beta66 por ADB inalámbrico con `install -r`. Android confirmó versión 0.46.0-beta67 y código 67. SHA-256 instalado y local coincidente `51fbc6ee03fc938f2e4bd8d2a9bf6b802e762905b8e1ec60b293ad7bd041185b`.
- En el Motorola, la cabecera PS4 identificó Soleado desde Prognoza y se observó el icono plateado. Ambas aplicaciones aparecieron en los resultados de búsqueda de Aura. Se limpió la búsqueda al terminar.
- Queda pendiente la revisión visual de las otras condiciones, los cambios de evento y las métricas de fluidez. La evidencia privada del teléfono permanece fuera del repositorio.
