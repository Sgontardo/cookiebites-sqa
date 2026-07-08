# Guía de Selenium y JMeter para CookieBites SQA

Este documento sirve como referencia para el Tester. DevOps deja la base lista: repositorio, contenedor Docker, pipeline de GitHub Actions, SonarQube, JUnit, Selenium Grid, JMeter y notificación a ClickUp. El Tester toma esta base y agrega las pruebas dinámicas de interfaz y rendimiento sin modificar la lógica del proyecto.

## 1. Cómo compartir el proyecto al Tester

La forma correcta es compartir el repositorio GitHub completo.

Flujo recomendado:

1. El equipo mantiene el proyecto en el repositorio.
2. El Tester clona el repositorio en su equipo.
3. El Tester levanta el contenedor con Docker.
4. El Tester ejecuta Selenium y JMeter contra la aplicación en `http://localhost:8080`.

Comandos básicos para el Tester:

```bash
git clone <URL-del-repositorio>
cd cookiebites-sqa
docker compose up --build
```

Para incluir Selenium Grid (necesario para pruebas Selenium en Docker):

```bash
docker compose --profile selenium up --build -d
```

Si quiere correr solo la imagen de la app:

```bash
docker build -t cookiebites-sqa:latest .
docker run --rm -p 8080:8080 cookiebites-sqa:latest
```

## 2. Responsabilidades del Tester

El Tester se encarga de:

- Diseñar y ejecutar pruebas funcionales con Selenium.
- Diseñar y ejecutar pruebas de rendimiento con JMeter.
- Documentar trazabilidad entre casos de prueba, requerimientos y resultados.
- Entregar evidencias para el informe SQA.
- No alterar la lógica de negocio salvo que el proceso de SQA lo autorice explícitamente.

## 3. Base de ejecución

La aplicación debe estar arriba antes de correr Selenium o JMeter.

Punto de acceso:

- `http://localhost:8080`

Rutas útiles para validar pruebas:

- `/productos`
- `/productos/buscar/{nombre}`
- `/perfil` o rutas equivalentes según el formulario o controlador que se esté probando

## 4. Selenium: referencia de trabajo

Selenium debe usarse para validar flujos de usuario visibles en la interfaz web.

### Objetivo

Verificar que los formularios, botones y consultas respondan como espera el usuario final.

### Prueba demo incluida

Ya existe una prueba demo en `src/test/java/com/example/cookiebites/sqa/selenium/LoginPageTest.java` que:
- Abre la página de login (`LogIn.html`)
- Verifica que el título contenga "CookieBites"
- Verifica que los campos `usuario` y `contrasena` existan

### Cómo ejecutar Selenium localmente

Opción A — Con Selenium Grid en Docker (recomendado):

```bash
# Terminal 1: Iniciar app + Selenium Grid
docker compose --profile selenium up --build -d

# Terminal 2: Ejecutar pruebas Selenium
./mvnw test -P selenium-tests -DSELENIUM_REMOTE_URL=http://localhost:4444/wd/hub -DTARGET_URL=http://localhost:8080/LogIn.html
```

Opción B — Con ChromeDriver local (sin Docker):

```bash
# Asegúrate de tener Chrome instalado
./mvnw test -P selenium-tests -DTARGET_URL=http://localhost:8080/LogIn.html
```

### Casos sugeridos para el Tester

- Buscar producto existente.
- Buscar producto inexistente.
- Crear, editar o eliminar perfiles si existe interfaz para ello.
- Navegar entre vistas HTML.
- Validar mensajes, redirecciones o cambios visibles en pantalla.

### Recomendaciones de diseño

- Usar selectores estables: `id`, `name` o `data-testid` si existieran.
- Evitar selectores frágiles por texto o posición absoluta.
- Separar los tests por escenario, no mezclar varios flujos en una sola prueba.
- Guardar capturas o evidencias cuando falle una verificación.

### Estructura sugerida

- `src/test/java/.../selenium/` — clases de prueba Java con JUnit + Selenium
- `src/test/resources/selenium/` — recursos adicionales (drivers, config, etc.)

### Evidencias esperadas

- Capturas de pantalla si aplica.
- Log de ejecución del navegador.
- Resultado por caso: aprobado, fallido o bloqueado.

## 5. JMeter: referencia de trabajo

JMeter debe usarse para medir tiempos de respuesta y comportamiento bajo carga.

### Objetivo

Validar que las operaciones críticas no superen el límite de respuesta definido para el proyecto.

### Prueba demo incluida

Ya existe un plan de prueba smoke en `src/test/jmeter/cookiebites-smoke.jmx` que:
- Hace `GET /productos`
- Valida que el código de respuesta sea 200

### Cómo ejecutar JMeter localmente

```bash
./mvnw verify -P jmeter-tests -DskipTests -D"jmeter.host=localhost" -D"jmeter.port=8080" -D"jmeter.protocol=http"
```

El plugin descarga JMeter automáticamente, ejecuta los `.jmx` de `src/test/jmeter/` y genera reportes en `target/jmeter/reports/`.

### Casos sugeridos para el Tester

- `GET /productos`
- `GET /productos/buscar/{nombre}`
- Consultas de pedidos o ventas si existen endpoints públicos para ello

### Parámetros recomendados

- Hilo inicial moderado y luego aumento gradual.
- Ramp-up controlado.
- Duración suficiente para observar estabilidad.
- Datos de prueba consistentes con el escenario de 10,000 registros cuando corresponda.

### Métricas a reportar

- Tiempo promedio de respuesta.
- Percentil 90 y 95.
- Throughput.
- Tasa de error.
- Consumo de recursos si el entorno lo permite.

### Archivos útiles

- Plan de prueba `.jmx` en `src/test/jmeter/`
- Reporte HTML de JMeter en `target/jmeter/reports/`
- CSV de resultados en `target/jmeter/results/`

## 6. Integración con el pipeline

El pipeline de GitHub Actions (`.github/workflows/ci.yml`) ejecuta automáticamente en cada push a `main`:

| Etapa | Qué hace | Reporte a ClickUp |
|---|---|---|
| JUnit | Build + pruebas unitarias + cobertura JaCoCo | Crea tarea con tags `junit`, `prueba-unitaria`, `cobertura` |
| Docker | Construye imagen + smoke test | — |
| Selenium Grid | Inicia contenedor Chrome standalone | — |
| Selenium | Ejecuta `./mvnw test -P selenium-tests` | Crea tarea con tags `selenium`, `prueba-sistema` |
| JMeter | Ejecuta `./mvnw verify -P jmeter-tests` | Crea tarea con tags `jmeter`, `prueba-rendimiento` |
| SonarQube | Análisis estático de código | Crea tarea con tags `sonarqube`, `analisis-estatico` |

Cada tarea en ClickUp incluye:
- Estado: ✅ Pasó / ❌ Falló
- Commit y branch
- Enlace al run de GitHub Actions
- Enlace a los artefactos generados (reportes JUnit, JaCoCo, Selenium, JMeter)

### Secrets requeridos en GitHub

| Secret | Propósito |
|---|---|
| `SONAR_TOKEN` | Token de autenticación para SonarQube |
| `SONAR_HOST_URL` | URL del servidor SonarQube |
| `CLICKUP_API_TOKEN` | Token de la API de ClickUp |
| `CLICKUP_LIST_ID` | ID de la Lista B (Hallazgos) en ClickUp |

## 7. Trazabilidad mínima recomendada

Cada prueba del Tester debe indicar:

- ID del caso.
- Requerimiento asociado.
- Escenario.
- Datos de entrada.
- Resultado esperado.
- Resultado real.
- Evidencia.

## 8. Flujo sugerido de trabajo

1. Clonar el repositorio.
2. Verificar que el contenedor esté corriendo (`docker compose up --build`).
3. Ejecutar Selenium sobre la interfaz web.
4. Ejecutar JMeter contra los endpoints críticos.
5. Registrar resultados y evidencias.
6. Entregar hallazgos al equipo para trazabilidad y auditoría.
7. Hacer push de los cambios (incluyendo nuevos tests) para que el pipeline los ejecute y genere reportes en ClickUp.

## 9. Regla práctica

Si el cambio afecta navegación, formularios o validaciones visibles, va a Selenium.
Si el cambio afecta tiempos de respuesta, concurrencia o carga, va a JMeter.
