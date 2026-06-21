package com.example.cookiebites.sqa.api;

import com.example.cookiebites.sqa.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * CP-EF-001 (Edge Case): Usuarios.json corrupto -> el servicio debe manejar
 * el error sin exceder el tiempo límite y, sobre todo, sin tumbar el servidor
 * (caída de servicio). Se ejecuta en un contexto Spring aparte porque el
 * archivo de datos se sustituye por uno corrupto desde el arranque.
 *
 * NOTA SQA: ListaUsuarios.leerPerfiles() captura IOException y degrada a una
 * lista vacía, por lo que el comportamiento esperado real es 200 OK con []
 * (no una caída del servidor), lo cual igualmente demuestra robustez frente
 * al archivo corrupto.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PerfilesCorruptedJsonApiTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registrarPropiedades(DynamicPropertyRegistry registry) {
        try {
            Path tempDir = java.nio.file.Files.createTempDirectory("cookiebites-sqa-");
            String path = TestDataUtils.escribirUsuariosCorrupto(tempDir);
            registry.add("usuarios.json.path", () -> path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("CP-EF-001 (Edge) - Usuarios.json corrupto no tumba el servidor y responde <= 60s")
    void cpEf001_archivoCorrupto_noCaeServidor() throws Exception {
        long inicio = System.currentTimeMillis();

        MvcResult resultado = mockMvc.perform(get("/perfil/todos")).andReturn();

        long duracion = System.currentTimeMillis() - inicio;
        int status = resultado.getResponse().getStatus();

        // El servidor debe seguir vivo y responder dentro del límite,
        // ya sea con 200 (lista vacía por degradación) o con un 5xx controlado.
        assertTrue(status == 200 || status >= 500,
                "Respuesta inesperada ante JSON corrupto: " + status);
        assertTrue(duracion <= TestDataUtils.LIMITE_MS,
                "RNF-EF-01 violado ante archivo corrupto: " + duracion + " ms");
    }
}
