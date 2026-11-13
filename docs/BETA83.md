# Beta83 · cursor estable al buscar

13 de noviembre de 2026. 0.59.1-beta83, código 83.

- El campo de búsqueda guarda texto, selección y composición del teclado mediante TextFieldValue. Cada edición actualiza directamente el estado de Compose del campo.
- El filtro usa solo el texto normalizado. Borrar, Atrás y volver a Inicio reinician el campo mediante las acciones existentes.
- Validación visual pendiente en el Motorola. La comprobación de conexión inicial encontró la cortina de notificaciones abierta.

## Verificación

- El primer intento detectó una referencia a la consulta anterior en la vista previa. Se corrigió antes de la compilación final.
- assembleBeta y lintBeta correctos en 3m18s. Cero errores, 40 advertencias y 3 avisos.
- Moto Edge 50 Ultra actualizado de beta82 a beta83 por ADB inalámbrico con install -r. Firma coincidente y código 83 verificados.
- SHA-256 del APK instalado 2161D89CD3D380F40FF20516A11C92447C532DD4377CA739C1B5D9FD0CC4A684.
- El teléfono estaba en otra aplicación al finalizar. Queda pendiente comprobar escritura rápida, sugerencias del teclado y edición en mitad de una búsqueda.
