# Aura0.24.0-beta31 · PS4 Dynamic Menu

Fecha del registro:25 de octubre de2026. Estado de entrega: compilada, firmada e instalada (code31), conservando datos. Revisión visual física pendiente de desbloqueo del Motorola.

## Cambios

El tema PS4 tiene una pantalla propia: cabecera con fecha/hora/perfil, fila horizontal de funciones, carrusel de contenido con ajuste al terminar de desplazar, tarjeta seleccionada ampliada y panel de información. Un toque selecciona otra app; Iniciar o un segundo toque abre la selección. Biblioteca muestra todas las aplicaciones visibles con búsqueda y cuadrícula adaptativa. Las16 primeras entradas del carrusel priorizan favoritas sin reordenar preferencias.

Menú rápido lateral para biblioteca, audio, música, notificaciones, personalización, tema, widgets y bloqueo. Iconos originales publicados por Sony para funciones del sistema; tipografía libre Noto Sans. Azul#003087/#0070D1, fondo azul-negro con ondas/partículas nativas y foco de350ms. Se respeta movimiento reducido y alto contraste; los fondos personales continúan teniendo prioridad. El área de contenido usa gestos horizontales propios; el doble toque en fondo libre permanece.

Las tarjetas de widgets y reproducción existentes siguen disponibles debajo del panel de contenido. Notificaciones abre la cortina Android, Eventos el calendario, Música la sesión activa o selector de app musical, Ajustes la personalización de Aura. Amigos/Mensajes/Party/Trofeos explican su dependencia de PlayStation App antes de abrirla. Store abre el sitio oficial. No se simula una cuenta PSN, trofeos ni mensajes.

## Recursos y límites

13PNG oficiales pequeños de la guía Sony, con hashes y dimensiones en artwork/ps4/official-assets.json. No son un kit HD ni se afirma equivalencia píxel por píxel con firmware9.x. La guía pública actual corresponde a13.50+ y sus imágenes sirven como referencia estructural. Fuente Noto Sans con licencia OFL; SST no está incluida. Ver artwork/ps4/README.md.

No se cambiaron los otros temas ni el catálogo SIII20/176. Los nuevos diseños Nothing/Razr y el resto de iconos SIII continúan pendientes.

## Skill

Se instaló ps4-theme-fidelity desde la carpeta scratch indicada por el usuario, conservando el original. Sus sugerencias se contrastan con Sony: las capturas tienen prioridad sobre afirmaciones no verificadas (firmware9.x como último estable, engranaje de Ajustes, etc.). Se mantiene350ms por petición directa del usuario.

## Verificación

No se añadieron ni ejecutaron suites automáticas. assembleBeta/lintBeta: BUILD SUCCESSFUL en2m21s;0 errores,16 advertencias,3 avisos. Se corrigió el uso no observable del idioma en fecha. Certificado idéntico a betas anteriores. Transferencia con SHA256 verificado; instalación devolvió Success y PackageManager confirmó0.24.0-beta31/code31. APK SHA256: `06b6bf259f303a3ffe2c9939aa8b19e206de1abd28d2cdebf71dd3b04aabfb45`. Revisión visual/foco nativos y medición de FPS pendientes; no se afirma60/120fps medidos. Las nuevas advertencias corresponden a2 recursos de referencia sin uso y2 sugerencias KTX, además de12 previas.
