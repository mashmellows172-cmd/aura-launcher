# Aura 0.13.0-beta18 — sistema visual Nothing

## Incluido

Tema principal Aura · Nothing, basado en el lenguaje visual industrial de Nothing/CMF: negro AMOLED real; superficies neutras; Geist y Geist Mono locales; reloj original de puntos, accesos independientes a alarmas, calendario y clima; iconos circulares monocromáticos; indicadores propios de favoritas, contadores cápsula, búsqueda y diálogos coherentes. El naranja CMF es un acento opcional. No introduce nuevas animaciones.

En Tema visual → Acabado se puede restablecer el estilo principal y elegir Naranja CMF. En Iconos se puede escoger Del tema, Monocromáticos o A color. Cada elección se guarda por tema. El borrador solo se confirma con Aplicar tema. No se fuerza la sustitución de preferencias durante una actualización.

El tema principal monocromatiza el recurso de cada app; cuando Android proporciona un icono monocromático (API33+) lo utiliza. Si no existe, desatura el icono disponible. No redistribuye el paquete de iconos de Nothing ni promete dibujos idénticos para aplicaciones que carezcan de ese recurso. Los widgets externos conservan la apariencia de su proveedor.

## Datos y compatibilidad

Formato de respaldo4; importa1–4. Añade la opción validada skin_icons por tema, incluida en la aplicación atómica del acabado. Favoritas, gestos, agenda, animaciones, permisos y otros temas se conservan. La actualización mantiene packageId y certificado de las betas anteriores; instalar con adb install -r, nunca desinstalar para actualizar.

## Recursos y licencia

Código Aura MIT. Geist y Geist Mono SIL OFL1.1, avisos completos en app/src/main/assets/licenses. Fuentes obtenidas de los directorios ofl/geist y ofl/geistmono del repositorio oficial google/fonts. Reloj y marca de puntos originales de Aura. Referencias: https://nothing.tech/nothing-os y https://checkout-eu.nothing.tech/pages/cmf-phone-1, consultadas21/10/2026. Sin afiliación con Nothing.

## Comprobaciones

- assembleBeta y lintBeta correctos: 0 errores, 7 advertencias y 2 sugerencias.
- Certificado idéntico a las betas anteriores; instalación final con install-r en el Motorola Edge50 Ultra Android17.
- 17 pares de color del sistema comprobados con el script de contraste de replica-design: cero fallos AA. Esto verifica la paleta especificada; no certifica la accesibilidad completa de widgets externos ni de colores personalizados.
- Revisión visual real de inicio, buscador, menús, ajustes y vista previa; acabado CMF aplicado y regreso a Nothing. Iconos adaptativos monocromáticos y fallback visibles en la lista.
- Respaldo final formato4: frente al respaldo original solo cambian skin_accent_NOTHING de BLUE a DEFAULT y la nueva opción skin_icons_NOTHING=AUTO. Los demás ajustes exportables se conservan.
- No se ejecutaron nuevas suites de pruebas, benchmarks ni mediciones de consumo. Capturas y respaldos privados fuera de los entregables.
- Resultado final del acceso de gestos y tiempo de pantalla: véase QA-beta18.md.
