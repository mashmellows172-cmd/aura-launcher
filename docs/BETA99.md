# Beta99 Local · Carátula de la ficha PS4

16 de noviembre de 2026. 0.68.1-beta99, código 99. Base beta98 (9f24d9e).

En el Motorola con beta97, al abrir desde el Centro rápido una sesión de música real, la carátula de la ficha quedaba sobre el control de posición y el contador. Se observó en dos pistas. Beta98 conserva el mismo código de la ficha.

La carátula animada pertenecía a la superficie del panel, pero su destino se medía en la capa externa que se desplaza al arrastrar la ficha. Ahora se dibuja directamente en esa capa. La carátula y su destino comparten coordenadas durante la transición y el arrastre.

Compilación y lint correctos en 9 min 15 s con 0 errores, 56 advertencias y 3 avisos. Pendientes la prueba visual de apertura, minimización y arrastre, la confirmación de búsqueda dentro de una pista y la medición de fluidez. La sesión de beta97 mostró el control de posición. Android no entregó el árbol de accesibilidad mientras la ficha se actualizaba y el teléfono perdió el primer plano durante la preparación de la medición.


## Firma y actualización

AAPT confirmó el paquete `dev.senda.launcher.local`, versión 0.68.1-beta99 y código 99. SHA-256 local `80c0013ca3a10da251d88a298b9904d3d683f3847fb1fb97176d33ae0a54e77c`. El certificado del candidato empieza por `2eb417c5` y el de beta97 instalada por `915f891c`. Los almacenes de desarrollo comprobados en este equipo no proporcionaron la firma de Aura Local. Beta97 sigue instalada. La validación de beta99 en el teléfono requiere compilar o firmar desde el entorno que actualizó beta97.
