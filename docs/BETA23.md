# Aura 0.18.0-beta23

## Reloj y clima

El símbolo real del widget del clima recorre de izquierda a derecha la franja que une el reloj con la meteorología. La capa se dibuja detrás del contenido, sin captar toques ni mover el reloj, la fecha o sus botones. No deduce el tiempo a partir de la temperatura; los datos y el símbolo siguen perteneciendo al proveedor elegido.

| Tema | Tratamiento del barrido |
|---|---|
| Nothing / CMF | Matriz de puntos con el acento elegido. |
| Sense | Bancos suaves de luz y relieve atmosférico. |
| TouchWiz | Cristal, gotas decorativas y anillos de agua. |
| LG | Cintas anchas de color. |
| Xperia | Ondas luminosas finas. |
| RAZR | Escaneo LCD y desplazamiento escalonado. |

Las estelas son acabados decorativos del tema; el símbolo del proveedor representa la condición meteorológica. Se conserva la misma distribución en todos los temas.

En **Clima en el reloj → Animaciones del clima**:

- **Al entrar al inicio**: un recorrido al mostrarse Inicio o cambiar los datos/tema, después queda en reposo. Valor inicial.
- **Repetición**: ciclos con pausa, solo mientras Aura está visible; aumenta el trabajo de dibujo y el consumo.
- **Desactivadas**: no solicita frames para el efecto.
- Recorrido de **3–12 segundos**, intensidad **20–100%**. Los deslizadores guardan al soltarlos.

Movimiento reducido, alto contraste y las animaciones desactivadas de Android impiden el movimiento. No hay nuevos permisos, ubicación, llamadas de red ni segundo widget del clima. El respaldo usa formato7 y admite importar1–7.

## Inicio y editor

- Tarjetas de alarma y agenda con categoría, dato principal y glifo vectorial original. Nothing/CMF usa superficies neutras; los retro conservan su radio, gradiente y tipografía.
- Altura configurada como mínimo: el texto grande puede ampliar la tarjeta. La regla de media anchura es la misma en Inicio y vista previa.
- Selectores de bloque y deshacer aparecen al abrir el editor. La vista previa es opcional y muestra los mismos componentes con datos de ejemplo, sin crear widgets duplicados.
- Se conservan borrador, selección, deshacer y visibilidad de la vista previa al recrear la actividad. Guardar y cancelar mantienen su significado.

## Verificación

30 pruebas JVM correctas; compilación beta optimizada con R8 y lint sin errores. Una revisión independiente no detectó fallos críticos ni importantes en el código. Resultados nativos y límites de rendimiento se documentan en `QA-beta23.md` antes de cerrar la entrega.

Las fuentes distribuibles excluyen capturas privadas, respaldos, claves y recursos personales de fabricantes. La biblioteca retro sigue siendo una importación independiente.
