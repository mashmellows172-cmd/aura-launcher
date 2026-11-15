# Beta94 · Bordes suaves del acento PS4

14 de noviembre de 2026. Aura Local 0.65.2-beta94, código 94.

- El halo de la cabecera se dibuja como una elipse. La intensidad llega a cero antes de los bordes del área de dibujo.
- El Centro rápido aplica el acento al contorno curvo superior. El brillo de los laterales se disipa hacia abajo.
- El acento sigue el color de la app seleccionada. La geometría se dibuja en Compose y no usa un bucle de animación.

## Verificación

- `assembleBeta lintBeta` para la variante Local completados. Lint informó 0 errores, 56 advertencias y 3 avisos.
- Actualización inalámbrica con `adb install -r` sobre beta93 en el Motorola Edge 50 Ultra. Versión, certificado y hash del APK instalado verificados. SHA-256 `A37A341E0BC97A42E22EFB06B8B387CB0036C172B637E7F61EA629242217117F`.
- Revisión visual pendiente. Aura no estaba en primer plano y el teléfono estaba bloqueado o con pantalla apagada.
