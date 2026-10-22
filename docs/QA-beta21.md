# Revisión beta21 — 2026-10-22

## Compilación y artefacto

Beta `0.16.0-beta21`, paquete `dev.senda.launcher.debug`, API37, variante derivada de release con R8 y reducción de recursos. APK final: 2.996.226 bytes. El certificado SHA-256 coincide con las betas previas: `729385fce1929acc453ec13a8b13f8e6fa4d8a5693713a10c8b2c2cdb202a67c`.

La compilación optimizada, lint y 11 pruebas unitarias existentes finalizaron correctamente. Lint registra seis advertencias y tres sugerencias, sin errores. Las advertencias restantes incluyen versiones de herramientas, búsqueda de recursos en paquetes externos y conversiones Uri; no se declara una auditoría completa de accesibilidad.

## Observado en Motorola Edge 50 Ultra / Android17

- Importación del ZIP personal con 62 fondos y 45 iconos seleccionados.
- Activación y revisión visual de los cuatro temas, incluida conservación de proporciones/transparencia en TouchWiz y Xperia.
- Galerías de fondos accesibles desde el editor; un cambio de fondo en borrador fue cancelado antes de aplicar.
- La versión final optimizada se instaló sin desinstalar. Su pantalla de inicio y jerarquía de controles se capturaron después de terminar la animación breve del clima.
- La cabecera muestra datos publicados por el widget público de Prognoza. No se verificó una nueva ciudad ni una API meteorológica independiente.
- Se conservaron los widgets y la configuración existente durante la actualización; no se reinició el dispositivo.
- La accesibilidad de gestos quedó activa como antes de la revisión; los otros servicios coinciden. El tiempo temporal de pantalla se restauró a 60.000 ms.

## Límites y pendientes

La revisión visual de galerías ocurrió en la primera compilación beta21; la compilación final añade caché acotada, ciclo breve del clima, recogida de estado ligada al ciclo de vida y optimizaciones R8. Se comprobó su inicio, pero aún faltan regresiones dirigidas de rechazo de bibliotecas dañadas y recreación del proceso en el APK final.

El usuario cambió tema y avatar durante parte de la sesión. Los respaldos privados contienen fotografías personales y no se distribuyen. No se restauran ciegamente ajustes anteriores que puedan sobrescribir elecciones posteriores del usuario.

Los dos recorridos `gfxinfo` de ocho desplazamientos tuvieron condiciones distintas y la segunda muestra capturó muy pocos frames. No permiten afirmar una mejora o regresión de fluidez, ahorro de batería ni una tasa sostenida de refresco. La reducción del tamaño del APK sí es una diferencia directa de los artefactos: 10.215.636 a 2.996.226 bytes.
