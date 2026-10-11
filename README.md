# Contador simple

Escucha la música por el micrófono y cuenta del 1 al 8 sola. Sin cuentas,
sin base de datos, sin marcar canciones. Si el UNO cae corrido, se toca el
botón una vez justo en el UNO y se acomoda.

## Instalar en el teléfono

Desde el teléfono, abrir:

https://github.com/gabrieltrabajo909-create/contador-simple/releases/latest/download/contador-simple.apk

Android va a avisar que es una app de fuera de la tienda: hay que permitirlo
para el navegador con el que se bajó.

## Cómo está hecho

- `app/src/main/assets/index.html` es la página del contador (la misma que
  `simple.html` del proyecto principal).
- `MainActivity.java` es solo una ventana que la muestra y le da permiso
  para el micrófono.
- Cada cambio guardado en `main` arma un APK nuevo en GitHub
  (`.github/workflows/apk.yml`).
