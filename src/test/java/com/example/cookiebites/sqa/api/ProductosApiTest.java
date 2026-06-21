package com.example.cookiebites.sqa.api;

import com.example.cookiebites.Back.Model.Producto;
import com.example.cookiebites.sqa.util.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Módulo: Productos
 * Casos: CP-EF-007 .. CP-EF-011 (documento "Casos de prueba SQA v1").
 *
 * Dataset: Productos.json con 10,000 registros (Dataset Alto), con un
 * producto objetivo al final del archivo para el caso de búsqueda inexistente
 * / peor caso lineal.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProductosApiTest {

    private static final String PRODUCTO_OBJETIVO = "Galleta_Objetivo_Final";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private File imagenCreadaParaLimpiar;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registrarPropiedades(DynamicPropertyRegistry registry) {
        try {
            Path tempDir = java.nio.file.Files.createTempDirectory("cookiebites-sqa-");
            String path = TestDataUtils.escribirProductosConObjetivoAlFinal(
                    tempDir, TestDataUtils.DATASET_ALTO, PRODUCTO_OBJETIVO);
            registry.add("productos.json.path", () -> path);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar Productos.json de prueba", e);
        }
    }

    @AfterEach
    void limpiarImagenGenerada() {
        if (imagenCreadaParaLimpiar != null && imagenCreadaParaLimpiar.exists()) {
            imagenCreadaParaLimpiar.delete();
        }
    }

    /**
     * CP-EF-007 (Happy Path): búsqueda de producto existente -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-007 - buscarProducto con nombre existente retorna 200")
    void cpEf007_buscarProducto_existente_retorna200() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/productos/buscar/{nombre}", PRODUCTO_OBJETIVO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(PRODUCTO_OBJETIVO));

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-007 (Edge Case): búsqueda de producto inexistente -> 404 <= 60s.
     */
    @Test
    @DisplayName("CP-EF-007 - buscarProducto inexistente retorna 404")
    void cpEf007_buscarProducto_inexistente_retorna404() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/productos/buscar/{nombre}", "Producto_Que_No_Existe_999"))
                .andExpect(status().isNotFound());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-008: listar catálogo completo bajo carga alta -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-008 - findAll de productos bajo dataset alto (10,000) <= 60s")
    void cpEf008_findAll_datasetAlto_respondeEnTiempoLimite() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(TestDataUtils.DATASET_ALTO));

        long duracion = System.currentTimeMillis() - inicio;
        assertTrue(duracion <= TestDataUtils.LIMITE_MS,
                "RNF-EF-01 violado: findAll de productos tardó " + duracion + " ms");
    }

    /**
     * CP-EF-009 (Happy Path): creación de producto vía multipart/form-data con
     * imagen -> 201 Created <= 60s. La imagen usada es liviana; el límite de
     * tamaño superior (Boundary) se ejerce en un caso separado si se requiere
     * con un archivo más pesado.
     */
    @Test
    @DisplayName("CP-EF-009 - agregarProducto con imagen (multipart) retorna 201")
    void cpEf009_agregarProducto_conImagen_retorna201() throws Exception {
        Producto producto = new Producto("Galleta_QA_Multipart", 5.0, null, new ArrayList<>());
        String productoJson = objectMapper.writeValueAsString(producto);

        MockMultipartFile productoPart = new MockMultipartFile(
                "producto", "producto.json", "application/json", productoJson.getBytes(StandardCharsets.UTF_8));
        byte[] contenidoImagenFicticia = "contenido-de-prueba-no-es-una-imagen-real".getBytes(StandardCharsets.UTF_8);
        String nombreArchivo = "qa_test_image_" + System.nanoTime() + ".png";
        MockMultipartFile imagenPart = new MockMultipartFile(
                "imagen", nombreArchivo, "image/png", contenidoImagenFicticia);

        // Registramos para limpieza, ya que el controlador escribe en una ruta
        // fija dentro del proyecto (src/main/.../img/galletas/<nombre>).
        String basePath = new File("").getAbsolutePath();
        imagenCreadaParaLimpiar = new File(basePath
                + "/src/main/java/com/example/cookiebites/Front/View/img/galletas/" + nombreArchivo);

        long inicio = System.currentTimeMillis();

        mockMvc.perform(multipart("/productos/crear")
                        .file(productoPart)
                        .file(imagenPart))
                .andExpect(status().isCreated());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
        assertTrue(imagenCreadaParaLimpiar.exists(), "La imagen debió persistirse en disco");
    }

    /**
     * CP-EF-010: eliminación de producto existente -> 204 No Content <= 60s.
     */
    @Test
    @DisplayName("CP-EF-010 - eliminarProducto existente retorna 204")
    void cpEf010_eliminarProducto_existente_retorna204() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(delete("/productos/eliminar/{nombre}", PRODUCTO_OBJETIVO))
                .andExpect(status().isNoContent());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        // Verificamos que ya no es localizable.
        mockMvc.perform(get("/productos/buscar/{nombre}", PRODUCTO_OBJETIVO))
                .andExpect(status().isNotFound());
    }

    /**
     * CP-EF-011: edición de producto existente -> 204 No Content <= 60s, con
     * verificación de persistencia de los cambios.
     * NOTA: este test crea su propio producto (no depende de PRODUCTO_OBJETIVO,
     * que pudo haber sido eliminado por CP-EF-010 si los tests corren en el
     * mismo contexto y orden no determinista).
     */
    @Test
    @DisplayName("CP-EF-011 - editarProducto existente retorna 204 y persiste cambios")
    void cpEf011_editarProducto_existente_retorna204YPersiste() throws Exception {
        String nombreProductoPropio = "Galleta_Editable_QA";
        ArrayList<String> ingredientesOriginales = new ArrayList<>();
        ingredientesOriginales.add("Harina");
        Producto original = new Producto(nombreProductoPropio, 2.0, "img/original.png", ingredientesOriginales);

        // Insertamos el producto directamente vía endpoint multipart minimal,
        // reutilizando agregarProducto para mantener consistencia con la app real.
        MockMultipartFile productoPart = new MockMultipartFile(
                "producto", "producto.json", "application/json",
                objectMapper.writeValueAsString(original).getBytes(StandardCharsets.UTF_8));
        String nombreImg = "qa_editable_" + System.nanoTime() + ".png";
        MockMultipartFile imagenPart = new MockMultipartFile(
                "imagen", nombreImg, "image/png", "img-falsa".getBytes(StandardCharsets.UTF_8));

        String basePath = new File("").getAbsolutePath();
        imagenCreadaParaLimpiar = new File(basePath
                + "/src/main/java/com/example/cookiebites/Front/View/img/galletas/" + nombreImg);

        mockMvc.perform(multipart("/productos/crear").file(productoPart).file(imagenPart))
                .andExpect(status().isCreated());

        ArrayList<String> ingredientesNuevos = new ArrayList<>();
        ingredientesNuevos.add("Avena");
        ingredientesNuevos.add("Canela");
        Producto actualizado = new Producto(nombreProductoPropio, 9.99, "img/nueva.png", ingredientesNuevos);

        long inicio = System.currentTimeMillis();
        mockMvc.perform(put("/productos/editar/{nombre}", nombreProductoPropio)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isNoContent());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        mockMvc.perform(get("/productos/buscar/{nombre}", nombreProductoPropio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(9.99));
    }
}
