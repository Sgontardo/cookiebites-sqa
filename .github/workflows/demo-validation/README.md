# Demo de Validacion Fallida

Este directorio contiene dos artefactos de prueba pensados para validar, de forma controlada, el flujo de errores hacia ClickUp y la estabilidad del entorno:

- `selenium_failure.py`: abre la interfaz web y falla de forma intencional.
- `jmeter_failure.jmx`: ejecuta una carga simple y falla por una asercion imposible.

Estos artefactos no deben integrarse al pipeline principal. Sirven para pruebas manuales o para un workflow de validacion separado.

## Prerrequisitos

- El contenedor de CookieBites debe estar levantado.
- Para Selenium, debe estar disponible un Selenium Grid o un contenedor `selenium/standalone-chrome`.
- Para JMeter, basta con Docker y acceso a la app en `http://localhost:8080`.

## Ejecucion sugerida

1. Levantar la app con `docker compose up --build`.
2. Ejecutar el demo de Selenium.
3. Ejecutar el plan de JMeter.
4. Confirmar que ambos fallan y que el evento se registra en ClickUp si se enlaza al flujo de notificacion.
