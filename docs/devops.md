# Guía DevOps para CookieBites SQA

## Alcance del rol

El rol de DevOps en esta simulación no corrige la lógica de negocio. Su trabajo es montar y operar la infraestructura de verificación para evaluar el proceso de SQA:

- Ejecutar build y pruebas unitarias con JUnit.
- Generar cobertura con JaCoCo.
- Analizar el código con SonarQube.
- Empaquetar y ejecutar la app con Docker.
- Notificar fallos hacia ClickUp.

## Lo que no cubre DevOps

Selenium y JMeter son parte del flujo del Tester. DevOps puede dejar la infraestructura lista para ejecutarlos, pero no debe inventar ni corregir los scripts de prueba funcional o rendimiento.

## Requisitos de entorno

- `JAVA_VERSION=17`
- `SONAR_TOKEN` y `SONAR_HOST_URL` para el escaneo de SonarQube.
- `CLICKUP_API_TOKEN` para autenticar llamadas a la API de ClickUp.
- `CLICKUP_LIST_ID` para crear tareas en la Lista B de hallazgos.

## Integración con ClickUp

El workflow crea una tarea por cada paso fallido detectado en la ejecución de GitHub Actions. Cada tarea incluye:

- Nombre del workflow.
- Branch y commit.
- URL directa del run.
- Detalle estructurado del paso que falló.

Los secretos deben configurarse en GitHub en `Settings > Secrets and variables > Actions`.

## Ejecución local con Docker

```bash
docker compose up --build
```

La aplicación queda expuesta en `http://localhost:8080`.

## Pipeline

El workflow de GitHub Actions ejecuta:

1. `./mvnw -B clean verify`
2. Publicación de reportes de JUnit y JaCoCo como artefactos.
3. Construcción de la imagen Docker.
4. Smoke test contra `GET /productos` dentro del contenedor.
5. Escaneo SonarQube si existen las credenciales.
6. Notificación a ClickUp cuando el pipeline falla.