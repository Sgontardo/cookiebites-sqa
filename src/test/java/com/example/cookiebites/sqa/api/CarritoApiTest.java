package com.example.cookiebites.sqa.api;

import com.example.cookiebites.sqa.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Módulo: Carrito de Compras
 * Casos: CP-EF-012 .. CP-EF-015 (documento "Casos de prueba SQA v1").
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CarritoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registrarPropiedades(DynamicPropertyRegistry registry) {
        try {
            Path tempDir = java.nio.file.Files.createTempDirectory("cookiebites-sqa-");
            // El módulo Carrito no depende de Usuarios/Productos.json para sus
            // propios endpoints, pero ListaProductos/ListaUsuarios igual se
            // inicializan en el contexto; les damos archivos vacíos válidos
            // para evitar ruido de otros módulos en este contexto aislado.
            String productosPath = TestDataUtils.archivoVacioEscribible(tempDir, "Productos.json");
            String usuariosPath = TestDataUtils.archivoVacioEscribible(tempDir, "Usuarios.json");
            String ventasPath = TestDataUtils.archivoVacioEscribible(tempDir, "Ventas.json");
            registry.add("productos.json.path", () -> productosPath);
            registry.add("usuarios.json.path", () -> usuariosPath);
            registry.add("ventas.json.path", () -> ventasPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> cuerpoAgregar(String nombreProducto, int cantidad) {
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombreProducto);
        Map<String, Object> body = new HashMap<>();
        body.put("producto", producto);
        body.put("cantidad", cantidad);
        return body;
    }

    /**
     * CP-EF-012 (Happy Path): agregar producto válido y cantidad positiva al
     * carrito -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-012 - agregarProducto al carrito (cantidad válida) retorna 200")
    void cpEf012_agregarProducto_cantidadValida_retorna200() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(post("/carrito/agregar/{id}", "usuario_carrito_qa")
                        .contentType("application/json")
                        .content(toJson(cuerpoAgregar("Galleta_Choco", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProducto").value("Galleta_Choco"))
                .andExpect(jsonPath("$.cantidad").value(2));

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-012 (Edge Case): cantidad negativa. El documento de pruebas la
     * incluye como entrada de "Adivinación de errores"; el resultado esperado
     * general del caso es 200 OK (el sistema actual no valida cantidades
     * negativas). Se documenta como hallazgo de validación de negocio
     * pendiente (ver también CP-EX-016 del documento v2 de priorización).
     */
    @Test
    @DisplayName("CP-EF-012 (Edge) - agregarProducto con cantidad negativa")
    void cpEf012_agregarProducto_cantidadNegativa_documentaComportamiento() throws Exception {
        long inicio = System.currentTimeMillis();

        var resultado = mockMvc.perform(post("/carrito/agregar/{id}", "usuario_carrito_qa_edge")
                        .contentType("application/json")
                        .content(toJson(cuerpoAgregar("Galleta_Choco", -5))))
                .andReturn();

        int status = resultado.getResponse().getStatus();
        if (status == 200) {
            System.out.println("[SQA][HALLAZGO] CP-EF-012 Edge Case: el endpoint aceptó cantidad "
                    + "negativa (-5) sin validación de negocio. Se recomienda agregar validación.");
        }
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
        assertTrue(status == 200 || status == 400, "Status inesperado: " + status);
    }

    /**
     * CP-EF-013: lectura del carrito (existente o recién creado) -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-013 - verCarrito retorna 200 con el contenido del carrito")
    void cpEf013_verCarrito_retorna200() throws Exception {
        mockMvc.perform(post("/carrito/agregar/{id}", "usuario_ver_carrito")
                        .contentType("application/json")
                        .content(toJson(cuerpoAgregar("Galleta_Avena", 3))))
                .andExpect(status().isOk());

        long inicio = System.currentTimeMillis();
        mockMvc.perform(get("/carrito/leer/{id}", "usuario_ver_carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Galleta_Avena"));
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-014: eliminación de un item del carrito -> 204 No Content <= 60s.
     */
    @Test
    @DisplayName("CP-EF-014 - eliminarDelCarrito retorna 204 y el item ya no aparece")
    void cpEf014_eliminarDelCarrito_retorna204() throws Exception {
        mockMvc.perform(post("/carrito/agregar/{id}", "usuario_eliminar_item")
                        .contentType("application/json")
                        .content(toJson(cuerpoAgregar("Galleta_Vainilla", 1))))
                .andExpect(status().isOk());

        long inicio = System.currentTimeMillis();
        mockMvc.perform(delete("/carrito/eliminar/{usuario}/{producto}",
                        "usuario_eliminar_item", "Galleta_Vainilla"))
                .andExpect(status().isNoContent());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        mockMvc.perform(get("/carrito/leer/{id}", "usuario_eliminar_item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * CP-EF-015: actualización de cantidad de un item existente -> 204 No
     * Content <= 60s, con verificación de persistencia del nuevo valor.
     */
    @Test
    @DisplayName("CP-EF-015 - editarCantidad retorna 204 y actualiza el valor")
    void cpEf015_editarCantidad_retorna204YActualiza() throws Exception {
        mockMvc.perform(post("/carrito/agregar/{id}", "usuario_editar_cantidad")
                        .contentType("application/json")
                        .content(toJson(cuerpoAgregar("Galleta_Coco", 1))))
                .andExpect(status().isOk());

        Map<String, Integer> nuevaCantidad = new HashMap<>();
        nuevaCantidad.put("cantidad", 7);

        long inicio = System.currentTimeMillis();
        mockMvc.perform(put("/carrito/editar/{usuario}/{producto}", "usuario_editar_cantidad", "Galleta_Coco")
                        .contentType("application/json")
                        .content(toJson(nuevaCantidad)))
                .andExpect(status().isNoContent());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        mockMvc.perform(get("/carrito/leer/{id}", "usuario_editar_cantidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(7));
    }

    private String toJson(Object o) throws IOException {
        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(o);
    }
}
