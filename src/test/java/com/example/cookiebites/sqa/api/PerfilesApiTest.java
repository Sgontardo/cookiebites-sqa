package com.example.cookiebites.sqa.api;

import com.example.cookiebites.Back.Model.Perfil;
import com.example.cookiebites.sqa.util.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Módulo: Perfiles
 * Casos: CP-EF-001 .. CP-EF-006 (documento "Casos de prueba SQA v1").
 *
 * Dataset: Usuarios.json con 10,000 registros (Dataset Alto), con un registro
 * objetivo "user_objetivo_10000" colocado al final del archivo para forzar el
 * peor caso de búsqueda lineal (Worst-Case Scenario).
 *
 * RNF-EF-01: toda operación debe responder en <= 60,000 ms.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PerfilesApiTest {

    private static final String USUARIO_OBJETIVO = "user_objetivo_10000";
    private static final String CLAVE_OBJETIVO = "clave_objetivo_10000";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DynamicPropertySource
    static void registrarPropiedades(DynamicPropertyRegistry registry) {
        try {
            Path tempDir = java.nio.file.Files.createTempDirectory("cookiebites-sqa-");
            String path = TestDataUtils.escribirUsuariosConObjetivoAlFinal(
                    tempDir, TestDataUtils.DATASET_ALTO, USUARIO_OBJETIVO, CLAVE_OBJETIVO);
            registry.add("usuarios.json.path", () -> path);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar Usuarios.json de prueba", e);
        }
    }

    /**
     * CP-EF-001 (Happy Path): listar perfiles con dataset alto (10,000)
     * responde 200 OK con la lista completa en <= 60 s.
     */
    @Test
    @DisplayName("CP-EF-001 - findAll de perfiles bajo dataset alto (10,000) <= 60s")
    void cpEf001_findAll_datasetAlto_respondeEnTiempoLimite() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/perfil/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(TestDataUtils.DATASET_ALTO));

        long duracion = System.currentTimeMillis() - inicio;
        assertTrue(duracion <= TestDataUtils.LIMITE_MS,
                "RNF-EF-01 violado: findAll tardó " + duracion + " ms (límite " + TestDataUtils.LIMITE_MS + " ms)");
    }

    /**
     * CP-EF-002 (Happy Path): nombre válido + clave correcta -> 200 OK con el perfil.
     */
    @Test
    @DisplayName("CP-EF-002 - findByName con credenciales correctas")
    void cpEf002_findByName_credencialesCorrectas_retorna200() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/perfil/{nombre}/clave/{clave}", USUARIO_OBJETIVO, CLAVE_OBJETIVO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value(USUARIO_OBJETIVO));

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-002 (Edge Case): nombre válido + clave incorrecta -> 401, <= 60s.
     */
    @Test
    @DisplayName("CP-EF-002 - findByName con clave incorrecta retorna 401")
    void cpEf002_findByName_claveIncorrecta_retorna401() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/perfil/{nombre}/clave/{clave}", USUARIO_OBJETIVO, "clave-equivocada"))
                .andExpect(status().isUnauthorized());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-003: búsqueda por nombre únicamente, registro ubicado al final del
     * JSON (peor caso de búsqueda lineal) -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-003 - findByJustName peor caso (registro al final del archivo)")
    void cpEf003_findByJustName_peorCasoLineal_retorna200() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/perfil/{nombre}", USUARIO_OBJETIVO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value(USUARIO_OBJETIVO));

        long duracion = System.currentTimeMillis() - inicio;
        assertTrue(duracion <= TestDataUtils.LIMITE_MS,
                "RNF-EF-01 violado: búsqueda peor caso tardó " + duracion + " ms");
    }

    /**
     * CP-EF-004 (Happy Path): creación de un perfil completo -> 201 Created <= 60s.
     */
    @Test
    @DisplayName("CP-EF-004 - agregarPerfil con objeto completo retorna 201")
    void cpEf004_agregarPerfil_objetoCompleto_retorna201() throws Exception {
        Perfil nuevo = new Perfil("Nuevo Cliente", "nuevo_cliente_qa", "clave123", "cliente",
                "nuevo@correo.com");
        long inicio = System.currentTimeMillis();

        mockMvc.perform(post("/perfil/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        // Confirmamos que efectivamente quedó persistido y es consultable.
        mockMvc.perform(get("/perfil/{nombre}", "nuevo_cliente_qa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("nuevo_cliente_qa"));
    }

    /**
     * CP-EF-004 (Edge Case): objeto Perfil sin campos obligatorios.
     * Resultado esperado en la especificación: 400 Bad Request en <= 60s.
     * NOTA SQA: el controlador actual (ControladorPerfiles.agregarPerfil) no
     * valida campos obligatorios y siempre responde 201 Created; este caso
     * documenta una posible NO conformidad frente al RNF-EF-01 de validación.
     */
    @Test
    @DisplayName("CP-EF-004 - agregarPerfil sin campos obligatorios debería retornar 400 (defecto conocido)")
    void cpEf004_agregarPerfil_sinCamposObligatorios_esperaBadRequest() throws Exception {
        Perfil incompleto = new Perfil(); // todos los campos nulos

        var resultado = mockMvc.perform(post("/perfil/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(incompleto)))
                .andReturn();

        int status = resultado.getResponse().getStatus();
        if (status != 400) {
            System.out.println("[SQA][DEFECTO] CP-EF-004 Edge Case: se esperaba 400 Bad Request "
                    + "por falta de validación de campos obligatorios, pero el servicio respondió "
                    + status + ". El controlador no valida campos nulos/vacíos antes de persistir.");
        }
        assertEquals(400, status,
                "Se esperaba 400 Bad Request ante un Perfil sin campos obligatorios (ver nota SQA arriba)");
    }

    /**
     * CP-EF-005: borrado de perfil existente -> 204 No Content <= 60s.
     */
    @Test
    @DisplayName("CP-EF-005 - eliminarPerfil de un perfil existente retorna 204")
    void cpEf005_eliminarPerfil_existente_retorna204() throws Exception {
        // Creamos un perfil propio para este caso y luego lo eliminamos.
        Perfil aBorrar = new Perfil("Borrame", "usuario_a_borrar", "clave1", "cliente", "b@correo.com");
        mockMvc.perform(post("/perfil/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(aBorrar)))
                .andExpect(status().isCreated());

        long inicio = System.currentTimeMillis();
        mockMvc.perform(delete("/perfil/eliminar/{nombreUsuario}", "usuario_a_borrar"))
                .andExpect(status().isNoContent());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-006: actualización de perfil existente -> 204 No Content <= 60s,
     * con verificación de que los datos quedaron efectivamente actualizados.
     */
    @Test
    @DisplayName("CP-EF-006 - editarPerfil de un perfil existente retorna 204 y persiste cambios")
    void cpEf006_editarPerfil_existente_retorna204YPersiste() throws Exception {
        Perfil original = new Perfil("Editable", "usuario_editable", "claveVieja", "cliente", "e@correo.com");
        mockMvc.perform(post("/perfil/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(original)))
                .andExpect(status().isCreated());

        Perfil actualizado = new Perfil("Editable Actualizado", "usuario_editable", "claveNueva",
                "admin", "nuevo_correo@correo.com");

        long inicio = System.currentTimeMillis();
        mockMvc.perform(put("/perfil/editar/{nombreUsuario}", "usuario_editable")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isNoContent());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        mockMvc.perform(get("/perfil/{nombre}", "usuario_editable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("admin"))
                .andExpect(jsonPath("$.nombre").value("Editable Actualizado"));
    }
}
