# Guía de Selenium y JMeter para CookieBites SQA

Este documento sirve como referencia para el Tester. DevOps deja la base lista: repositorio, contenedor Docker, pipeline de GitHub Actions, SonarQube, JUnit y notificación a ClickUp. El Tester toma esta base y agrega las pruebas dinámicas de interfaz y rendimiento sin modificar la lógica del proyecto.

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

Si quiere correr solo la imagen:

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

### Casos sugeridos

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

- `src/test/java/.../selenium/`
- `src/test/resources/selenium/`

### Evidencias esperadas

- Capturas de pantalla si aplica.
- Log de ejecución del navegador.
- Resultado por caso: aprobado, fallido o bloqueado.

## 5. JMeter: referencia de trabajo

JMeter debe usarse para medir tiempos de respuesta y comportamiento bajo carga.

### Objetivo

Validar que las operaciones críticas no superen el límite de respuesta definido para el proyecto.

### Casos sugeridos

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

- Plan de prueba `.jmx`.
- Reporte HTML de JMeter.
- CSV de resultados sin procesar.

## 6. Integración con el pipeline

DevOps ya dejó el pipeline base listo para:

- Ejecutar build y pruebas unitarias.
- Generar cobertura con JaCoCo.
- Construir la imagen Docker.
- Hacer smoke test.
- Enviar fallos a ClickUp.

Cuando el Tester entregue Selenium y JMeter, se recomienda agregar etapas separadas en GitHub Actions para correrlos y publicar sus reportes como artefactos.

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

1. Verificar que el contenedor esté corriendo.
2. Ejecutar Selenium sobre la interfaz web.
3. Ejecutar JMeter contra los endpoints críticos.
4. Registrar resultados y evidencias.
5. Entregar hallazgos al equipo para trazabilidad y auditoría.

## 9. Regla práctica

Si el cambio afecta navegación, formularios o validaciones visibles, va a Selenium.
Si el cambio afecta tiempos de respuesta, concurrencia o carga, va a JMeter.
