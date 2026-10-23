# Aura 0.18.0-beta24 — clima atmosférico

El clima ocupa el fondo de la franja del reloj, sin mover controles ni cambiar los toques. Sustituye el bitmap viajero por partículas y formas originales: Nothing/CMF luminoso de puntos; Sense con bancos de nubes; TouchWiz con ondas de cristal; LG con cintas; Xperia con auroras finas; RAZR con líneas y segmentos LCD.

Sol, nubes, lluvia, nieve, tormenta y niebla cambian el dibujo. La condición automática usa únicamente la descripción accesible del icono que ya extrae el widget. Si falta o no se reconoce, utiliza atmósfera neutra. No analiza temperaturas ni imágenes para inventar condiciones. La escena manual es decorativa y no cambia el pronóstico.

En **Clima en el reloj → Animaciones del clima → Escena meteorológica**, elige proveedor o una condición manual. Conserva **Al entrar al inicio**, **Repetición**, **Desactivadas**, ciclo3–12s e intensidad20–100%. La repetición es opcional. Movimiento reducido, alto contraste y animaciones desactivadas del sistema impiden iniciar el efecto. No se añadieron permisos, servicios de red ni dependencias. Respaldo formato8, importación1–8; las versiones anteriores de Aura no admiten este nuevo respaldo.

## Verificación y límites

Revisión independiente del código: detectó un import ausente, un salto de partículas al repetir y una lectura demasiado amplia de los textos del widget; corregidos. También se elimina el frame vacío al reiniciar el ciclo y se corrigen textos y codificación. La validación visual, la legibilidad sobre cada fondo personal y el rendimiento sostenido en el Motorola siguen pendientes; no se promete equivalencia exacta con interfaces propietarias ni una apariencia impecable ya comprobada. No se añadieron ni ejecutaron pruebas automatizadas en esta revisión. El resultado de compilación/firma se registra al cerrar la entrega.

Las decisiones visuales usan UI/UX Pro Max: una sola escena, semántica decorativa, controles nativos y pausa por accesibilidad. La repetición decorativa es una excepción solicitada y puede elevar consumo. Se mantuvo el checkout y la distribución autorizados. Queda pendiente la prueba de rotación del editor documentada en QA-beta23.
Entrega: assembleBeta y lintBeta correctos; APK firmado y verificado. Nueva solicitud pendiente: diferenciar más los temas retro y añadir encabezados cambiantes con frases motivacionales latinas cercanas y semiformales. No se ha implementado este bloque adicional.
