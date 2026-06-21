# Suite de Pruebas SQA – Cookiebites

Este paquete contiene el desarrollo de código de pruebas (JUnit 5 + Spring
Boot Test + MockMvc) correspondiente a la batería de **28 casos de prueba
(CP-EF-001 a CP-EF-028)** definida en `Casos de prueba SQA v1.pdf`, aplicada
sobre el código fuente real del proyecto `cookiebites-main`.

## Dónde está el código

```
src/test/java/com/example/cookiebites/sqa/
├── util/
│   └── TestDataUtils.java          Generador de datasets (1,000 / 10,000 registros),
│                                    inyección de registros "objetivo" al final del
│                                    archivo (peor caso lineal) y JSON corrupto.
├── api/
│   ├── PerfilesApiTest.java            CP-EF-001 .. CP-EF-006
│   ├── PerfilesCorruptedJsonApiTest.java  CP-EF-001 (Edge: JSON corrupto)
│   ├── ProductosApiTest.java           CP-EF-007 .. CP-EF-011
│   ├── CarritoApiTest.java             CP-EF-012 .. CP-EF-015
│   └── PedidosVentasApiTest.java       CP-EF-016 .. CP-EF-020
└── unit/
    ├── CarritoCompraUnitTest.java      CP-EF-021 .. CP-EF-024
    └── RepositoriosUnitTest.java       CP-EF-025 .. CP-EF-028
```

## Cómo ejecutar

```bash
./mvnw test
```

(o `mvn test` si tienes Maven instalado localmente). Requiere JDK 17, igual
que el proyecto original.

## Enfoque de diseño aplicado (alineado con el documento de diseño)

- **Pruebas de rendimiento/carga:** los módulos de Perfiles, Productos y
  Ventas se prueban contra datasets generados dinámicamente con 1,000
  (`DATASET_MEDIO`) y 10,000 (`DATASET_ALTO`) registros, sin tocar los
  archivos reales `Data/*.json` del proyecto (se usan rutas temporales vía
  `@DynamicPropertySource`, sobrescribiendo `usuarios.json.path`,
  `productos.json.path` y `ventas.json.path`).
- **Análisis de valores límite:** cada caso mide el tiempo de respuesta y
  afirma `duración <= 60_000 ms` (constante `TestDataUtils.LIMITE_MS`,
  trazable a RNF-EF-01).
- **Adivinación de errores / casos negativos:** búsquedas con el registro
  objetivo colocado al final del archivo (peor caso de búsqueda lineal),
  búsquedas de elementos inexistentes, credenciales incorrectas, JSON
  corrupto, cantidades negativas, etc.
- **Casos unitarios de soporte interno (CP-EF-021 a CP-EF-028):** las clases
  `CarritoCompra`, `ListaProductos`, `ListaUsuarios`, `ListaPedidos` y
  `ListaVentas` se instancian directamente con `new` (sin contexto de
  Spring), inyectando sus campos privados vía `ReflectionTestUtils`, tal
  como exige la especificación ("Invocar método directamente").

## Hallazgos de SQA documentados en el código (no son fallas de las pruebas)

Durante el desarrollo de las pruebas, alineado con el objetivo de un tester
de identificar no conformidades, se detectaron y documentaron en comentarios
`[SQA][DEFECTO]` / `[SQA][HALLAZGO]` los siguientes puntos del código fuente:

1. **`ControladorPerfiles.agregarPerfil`** no valida campos obligatorios
   nulos/vacíos: el caso `CP-EF-004 (Edge Case)` espera `400 Bad Request`
   pero la implementación actual siempre responde `201 Created`.
2. **`ListaPedidos.findByName`** lanza `NullPointerException` cuando la
   lista contiene un `Pedido` con `nombreUsuario == null` — situación que
   ocurre siempre en producción porque `@PostConstruct init()` inserta un
   `Pedido()` vacío al arrancar la aplicación. Esto afecta directamente al
   endpoint `GET /pedido/leer/{id}` (`CP-EF-018`). Se incluyó un test
   adicional (`cpEf027_findByName_documentaDefectoConRegistroNulo`) que
   confirma el `NullPointerException` de forma aislada y reproducible.
3. **`ControladorCarrito.agregarProducto`** no valida cantidades negativas
   (`CP-EF-012 Edge Case`), aceptándolas sin error — coincide con el riesgo
   `CP-EX-016` señalado en el documento v2 de priorización de riesgos.
4. **`ControladorProductos.agregarProducto`** escribe la imagen subida en
   una ruta absoluta fija dentro del propio repositorio
   (`src/main/java/.../Front/View/img/galletas/`) en lugar de una ruta
   configurable; los tests de este endpoint limpian el archivo generado en
   `@AfterEach` para no contaminar el repositorio, pero se recomienda
   externalizar esa ruta a `application.properties` igual que se hizo con
   los `*.json.path`.

Estos hallazgos no requieren modificar el código de producción para esta
entrega (alcance: desarrollo de pruebas), pero quedan documentados como
evidencia de la ejecución de las pruebas, como pide el documento de
Procedimiento de Prueba (sección 3 del PDF): "capturar los logs y marcar
como FAILED" cuando el resultado no coincide con el esperado.
