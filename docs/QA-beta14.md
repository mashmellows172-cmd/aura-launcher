# Aura — validación de uso diario y widgets

Fecha: 2026-10-21. Objetivo: beta14. Se prueba Aura; las animaciones siguen en pausa.

| Caso | Pasos y resultado esperado | Estado |
| --- | --- | --- |
| F01-H1 | Abrir favorita, regresar a HOME: Aura queda activa, búsqueda vacía. | Pasó en dispositivo (debug y catálogo optimizado) |
| F02-H1 | Tocar A/W/Y/Z con y sin widgets: ir a la sección correcta. | Pasó W/Y/Z con widget; cálculo por unidad |
| F02-E1 | Cambiar disponibilidad de un paquete: catálogo se actualiza sin perder favoritos/ocultas. | Pasó instalación, desactivación, reactivación y desinstalación |
| F03-H1 | Buscar app por nombre, abrirla; HOME limpia consulta. | Pasó en dispositivo |
| F03-E1 | Búsqueda vacía, acentos, emoji, idioma turco. | Unidad; búsqueda telefono en Motorola |
| F03-E2 | Sin coincidencias; limpiar consulta y cerrar teclado con Atrás. | Sin coincidencias y Atrás observados en catálogo |
| F04-H1 | Añadir, ordenar, quitar favoritas y guardar. | Pasó; orden original restaurado y comprobado contra respaldo |
| F04-E1 | Editar favoritas con una oculta/no disponible: conservar su preferencia. | Unidad y dispositivo: oculta/no disponible |
| F07-H1 | Elegir proveedor, aceptar vinculación, configurar si hace falta, ver widget real. | Pasó con Reloj Digital y proveedor QA |
| F07-E1 | Cancelar vinculación/configuración: liberar el ID y no añadir widget. | Pasó cancelación de permiso y configuración |
| F07-E2 | Redimensionar, ordenar, quitar; recrear proceso y conservar IDs/alturas. | Pasó resize, orden, eliminación y proceso nuevo |
| F07-E3 | Proveedor eliminado: mostrar estado no disponible con acción Quitar. | Pasó tras corregir AURA-005 |
| F07-E4 | Cuatro widgets: rechazar un quinto con mensaje. | Pasó límite cuatro |
| F08-H1 | Elegir tema AMOLED y recrear proceso: conservar AMOLED. | Pasó unidad y proceso nuevo en Motorola |
| F08-E1 | Imagen grande: decodificación acotada; URI revocada: fondo/icono alternativo. | Muestreo cubierto por unidad |
| F09-H1 | Hora abre alarmas; fecha abre calendario; clima abre paquete elegido. | Pasó: Reloj, Calendar y Prognoza |
| F10-H1 | Ocultar/restaurar app y verificar persistencia tras recreación. | Pasó ocultación/restauración con favorita |
| PERF-H1 | Medir inicio, memoria y frames con dumpsys, observar errores actuales. | Muestra debug COLD 456 ms; 19/336 frames con retraso; sin nuevos errores en esa muestra |
| REBOOT-H1 | Reiniciar teléfono; conservar HOME, tema, favoritas y widgets. | Pasó: boot ID cambió, HOME, Aura Pulse y widget con ID conservado. Orden de favoritas restaurado; widget QA eliminado; timeout original restaurado. |

Notificaciones integradas, carpetas, agenda con lectura de eventos y datos de uso no forman parte de este hito. Los permisos de widgets se solicitan mediante el sistema Android.


Última muestra optimizada, después del reinicio y sin PiP: arranque COLD de 169 ms; 397 frames, 14 con retraso (3,5 %); PSS 304.157 KB; sin nuevos errores AndroidRuntime de Aura. La carga y el estado del teléfono difieren de la muestra debug: no es una comparación causal ni una garantía sostenida.
