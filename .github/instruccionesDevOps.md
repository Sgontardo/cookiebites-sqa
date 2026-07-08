# Objetivos Proyecto SQA

| ID | Objetivo | Justificación | Métrica asociada | Estándar |
|---|---|---|---|---|
| O-01 | Lograr que el 100% de las operaciones críticas de consulta (listar productos y consultar pedidos/ventas) respondan en menos de 1 minuto bajo un dataset de 10,000 registros durante las pruebas de rendimiento con JMeter. | El RNF-01 establece un tiempo de respuesta máximo de 1 minuto, pero no especifica qué operaciones. Nuestra inspección estática reveló que los métodos findAll() son los más vulnerables a la saturación. Este objetivo aterriza el RNF-01 a operaciones concretas y verificables. | Tiempo de respuesta promedio de las consultas findAll() medido con JMeter. (Debe ser < 60 segundos) | ISO/IEC 25010 (Eficiencia de Desempeño) |
| O-02 | Alcanzar una cobertura del pipeline de CI/CD del 100% en las etapas de build, análisis estático (SonarQube) y ejecución de pruebas unitarias (JUnit) | La cobertura completa del flujo automatizado. Mide el progreso de la implementación del ecosistema. | Porcentaje de etapas del pipeline configuradas y funcionando correctamente. | ISO/IEC/IEEE 12207 (Integración continua) |
| O-03 | Lograr que el 90% de los casos de prueba de sistema automatizados (Selenium) sean trazables hasta los requerimientos específicos del ERS y asociado a una historia de usuario del Sprint 1. | Se busca determinar qué porcentaje de nuestras pruebas tienen trazabilidad documentada. | Porcentaje de casos de prueba con trazabilidad documentada en la Matriz de Trazabilidad. (Debe ser >= 90%) | IEEE 730 (Trazabilidad) |
| O-04 | Identificar y documentar al menos 3 oportunidades de mejora en el proceso de SQA del equipo a partir de la auditoría interna, y cerrar el 100% de las no conformidades críticas encontradas | Se busca encontrar oportunidades de mejora y cerrar no conformidades críticas. | Número de oportunidades de mejora identificadas. Número de no conformidades críticas cerradas. | IEEE 730, IEEE 1012-2024 |

# Tareas DevOps

## Configuración del Baseline (Punto Cero de la Simulación)

Establecer el punto cero de SQA realizando el commit inicial con el código fuente oficial y las pruebas unitarias recibidas. El objetivo no es desarrollar software, sino evaluar la capacidad del proyecto para sostener un proceso de IC/DC conforme a IEEE 730.

- El repositorio contiene el código fuente y las pruebas unitarias en su estado original (sin modificaciones).
- Se ha documentado el estado inicial del código (baseline) como punto de partida de la simulación.

*IEEE 730-2014 (Cláusula 5.3: Planificación de SQA). El baseline es el inicio de la simulación, no del desarrollo. El objetivo es evaluar el proceso, no el producto.*

## Pruebas que quiere el equipo hacer (las resaltadas en amarillo)

- Identificador Único: CP-EF-004 (agregarPerfil)
- Identificador Único: CP-EF-006 (editarPerfil)
- Identificador Único: CP-EF-007 (buscarProducto)
- Identificador Único: CP-EF-010 (eliminarProducto)
- Identificador Único: CP-EF-021 (CarritoCompra.agregarCarrito)
- Identificador Único: CP-EF-027 (ListaPedidos.findByName)
- Identificador Único: CP-EF-028 (ListaVentas.save)

## Orquestación del Pipeline de CI/CD (Simulación de Integración)

Simular integraciones para evaluar el proceso de IC/DC. Configurar el pipeline en GitHub Actions para que ejecute build, pruebas JUnit y análisis SonarQube en cada commit. El fraccionamiento del código es una técnica para generar eventos de integración.

- El pipeline se ejecuta automáticamente en cada commit.
- Se generan reportes de SonarQube y cobertura de pruebas como artefactos.
- Se ha documentado la técnica de fraccionamiento utilizada y los eventos de integración generados.

*ISO/IEC/IEEE 12207 (Integración continua). La simulación es un artefacto pedagógico y de auditoría, no un pipeline productivo. El fraccionamiento del código es una técnica para generar eventos de integración.*

## Dockerización (Reproducibilidad del Entorno)

Crear el contenedor Docker para que el ambiente de pruebas sea reproducible y consistente para todo el equipo.

- El contenedor Docker está disponible y documentado.
- El entorno de pruebas es consistente y reproducible.

### Entrega para compartir

La imagen está preparada para que cualquier persona pueda correr el proyecto de dos formas:

```bash
docker build -t cookiebites-sqa:latest .
docker run --rm -p 8080:8080 cookiebites-sqa:latest
```

O bien con Compose:

```bash
docker compose up --build
```

La aplicación expone el puerto `8080` y conserva los datos locales en el volumen/archivo montado según la configuración del contenedor.

*ISO/IEC/IEEE 12207 (Gestión de infraestructura). Un entorno reproducible es clave para la validez de las pruebas.*

## Automatización de ClickUp (Gobernanza de Datos)

Configurar webhooks para que los fallos en el pipeline abran automáticamente tareas en la Lista B (Hallazgos) de ClickUp, garantizando la gobernanza de los datos generados.

- Los fallos en el pipeline crean automáticamente tareas en ClickUp.
- Se ha documentado el flujo de datos desde el pipeline hasta ClickUp.

### Integración real usada

La automatización queda conectada con la API de ClickUp mediante los secretos del workflow de GitHub Actions. Cuando falla una etapa, el pipeline consulta el run, detecta cada paso con fallo o cancelación y crea una tarea individual por cada evento.

*ISO/IEC/IEEE 15289 (Gestión de información del ciclo de vida). La gobernanza de datos asegura la trazabilidad y la fiabilidad de las conclusiones.*

# Tareas del DevOps (Ana García)

**Rol:** Arquitecto de Automatización y Simulación de Integración Continua

**Filosofía:** El DevOps no es un desarrollador que corrige código, sino un arquitecto que construye un ecosistema de automatización para evaluar la madurez del proceso de SQA. El objetivo es simular integraciones continuas para medir estabilidad, tiempos de respuesta, detección de fallos y trazabilidad.

## Tabla de Tareas del DevOps (Formato para ClickUp)

| ID | Tarea | Descripción | Criterios de Aceptación | Prioridad | Commit Asociado |
|---|---|---|---|---|---|
| DV-01 | Configuración del Baseline (Punto Cero de la Simulación) | Establecer el punto cero del experimento SQA realizando el commit inicial con el código fuente oficial de CookieBites y las pruebas unitarias JUnit recibidas (las resaltadas en amarillo). El objetivo no es desarrollar software, sino evaluar la capacidad del proyecto para sostener un proceso de IC/DC conforme a IEEE 730. | - El repositorio contiene el código fuente y las pruebas unitarias en su estado original (sin modificaciones). - Se ha documentado el estado inicial del código (baseline) como punto de partida de la simulación. - El commit está etiquetado como "Baseline". | Alta | Commit de Baseline |
| DV-02 | Dockerización (Reproducibilidad del Entorno) | Crear y mantener los archivos de configuración (Dockerfile, docker-compose.yml) para empaquetar la aplicación CookieBites y sus dependencias en un contenedor. Esto garantiza que el Tester ejecute las pruebas en un entorno idéntico al de producción, eliminando discrepancias técnicas. | - El contenedor Docker se construye sin errores. - La aplicación arranca correctamente dentro del contenedor. - El entorno de pruebas es reproducible y consistente para todo el equipo. - Los archivos están versionados en el repositorio. | Alta | Commit de Infraestructura |
| DV-03 | Orquestación del Pipeline de CI/CD (Simulación de Integración) | Configurar el pipeline en GitHub Actions para que ejecute un flujo automático en cada commit. El pipeline debe: Compilar (Build), Ejecutar Pruebas Unitarias (JUnit), y Ejecutar Análisis Estático (SonarQube). El fraccionamiento del código es una técnica para generar eventos de integración. | - El pipeline se ejecuta automáticamente en cada push a la rama main. - Se genera un artefacto ejecutable (.jar). - Las pruebas JUnit se ejecutan y generan un reporte. - SonarQube se ejecuta y genera un reporte de deuda técnica. - Se ha documentado la técnica de fraccionamiento utilizada y los eventos de integración generados. | Crítica | Commit de Infraestructura y Orquestación |
| DV-04 | Configuración de Pruebas de Humo (Smoke Tests) | Incluir verificaciones rápidas al inicio del pipeline para confirmar la estabilidad básica del sistema antes de permitir que el Tester proceda con pruebas más profundas. | - El pipeline ejecuta una prueba de humo que verifica que el contexto de Spring Boot carga correctamente. - Si la prueba de humo falla, el pipeline se detiene y no ejecuta las etapas siguientes. | Alta | Commit de Infraestructura y Orquestación |
| DV-05 | Integración de Pruebas Dinámicas (Selenium y JMeter) | Una vez que el pipeline valida la estructura técnica, integrar en el flujo los scripts de Selenium (pruebas funcionales) y JMeter (pruebas de rendimiento) diseñados por el Tester. | - Los scripts de Selenium se ejecutan automáticamente en el pipeline. - Los scripts de JMeter se ejecutan automáticamente en el pipeline. - Se generan reportes de ejecución de Selenium y JMeter como artefactos. - Los scripts están versionados en el repositorio. | Alta | Commit de Artefactos de Prueba |
| DV-06 | Automatización del Ecosistema ClickUp (Gobernanza de Datos) | Configurar el pipeline para que, si una etapa falla (JUnit, SonarQube, Selenium o JMeter), se abra automáticamente un ticket o tarea en la "Lista B" (Hallazgos) de ClickUp, garantizando la gobernanza de los datos generados. | - Los fallos en el pipeline crean automáticamente tareas en ClickUp con la descripción del error. - Las tareas incluyen el enlace al log del pipeline y la etapa que falló. - Se ha documentado el flujo de datos desde el pipeline hasta ClickUp. | Media | Commit de Integración de Herramientas |
| DV-07 | Habilitación de Métricas en Tiempo Real | Asegurar que los datos generados por las herramientas (logs de Selenium, reportes de SonarQube, resultados de JMeter) fluyan automáticamente hacia los dashboards que el Líder de Métricas presentará. | - Los reportes de SonarQube, JUnit, Selenium y JMeter están disponibles como artefactos descargables. - El Líder de Métricas tiene acceso a los datos generados por el pipeline. - Se ha documentado el flujo de datos hacia los dashboards. | Media | Commit de Integración de Herramientas |

## Resumen de Commits Clave (Para el Repositorio)

| # | Nombre del Commit | Contenido | Propósito |
|---|---|---|---|
| 1 | Commit de Baseline | Código original de CookieBites + pruebas unitarias JUnit (resaltadas en amarillo). | Establecer el punto cero de la simulación. Sin este commit, no hay nada que auditar. |
| 2 | Commit de Infraestructura y Orquestación | Archivos de configuración: Dockerfile, docker-compose.yml, .github/workflows/ci.yml. | Construir la "fábrica" de automatización. Es el corazón del pipeline. |
| 3 | Commit de Integración de Herramientas | Archivos de configuración de herramientas: sonar-project.properties, configuración de webhooks de ClickUp. | Conectar el cerebro del ecosistema. Configurar cómo se comunican las herramientas. |
| 4 | Commit de Artefactos de Prueba | Scripts de Selenium y JMeter desarrollados por el Tester. | Integrar la carga de trabajo de pruebas en el pipeline. |

## Herramientas a Utilizar

| Herramienta | Propósito | Dónde se usa |
|---|---|---|
| GitHub | Repositorio de código y control de versiones | Almacenar el código, las configuraciones y los scripts. |
| GitHub Actions | Orquestación del pipeline de CI/CD | Ejecutar build, pruebas JUnit, SonarQube, Selenium y JMeter automáticamente. |
| Docker | Contenedores para entorno reproducible | Empaquetar la aplicación y sus dependencias para pruebas consistentes. |
| JUnit | Pruebas unitarias | Validar la lógica de negocio de CookieBites. |
| SonarQube | Análisis estático de código | Inspeccionar deuda técnica, complejidad y vulnerabilidades. |
| Selenium | Pruebas funcionales automatizadas | Validar flujos de usuario desde la interfaz web. |
| JMeter | Pruebas de rendimiento | Medir tiempos de respuesta y validar el RNF-01 (eficiencia). |
| ClickUp | Gestión de hallazgos y trazabilidad | Crear tareas automáticamente cuando el pipeline falla (Lista B). |

## Flujo de Trabajo del DevOps

1. **Preparación:** Instalar herramientas (Git, Docker, Java, Maven, etc.).
2. **Baseline:** Subir el código original y las pruebas JUnit al repositorio.
3. **Pipeline:** Configurar GitHub Actions con build, JUnit y SonarQube.
4. **Docker:** Crear el Dockerfile y docker-compose.yml para entorno reproducible.
5. **Pruebas de Humo:** Añadir una prueba rápida al inicio del pipeline.
6. **Integración de Pruebas Dinámicas:** Subir scripts de Selenium y JMeter al repositorio y configurar el pipeline para ejecutarlos.
7. **ClickUp:** Configurar webhooks para que los fallos del pipeline creen tareas automáticamente.
8. **Métricas:** Asegurar que los reportes generados estén disponibles para el Líder de Métricas.

## Rol del Tester

El Tester se encarga de Selenium y JMeter. DevOps deja la infraestructura lista y solo integra los artefactos de prueba cuando ya existan. El contenedor y el pipeline deben servir como base común para que el Tester valide comportamiento funcional y rendimiento sin rehacer el entorno.

## Mensaje Clave para el DevOps

> No estás desarrollando software. Estás simulando un proceso de integración continua para evaluar la madurez del equipo y el ecosistema. Cada commit, cada fallo y cada reporte es un dato que alimenta la auditoría y las métricas. El éxito no es que el pipeline pase, sino que capture y reporte cada evento de forma trazable.
