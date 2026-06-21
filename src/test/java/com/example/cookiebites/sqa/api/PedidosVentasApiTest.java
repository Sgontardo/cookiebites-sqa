package com.example.cookiebites.sqa.api;

import com.example.cookiebites.Back.Model.Venta;
import com.example.cookiebites.sqa.util.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Módulos: Pedidos y Ventas.
 * Casos: CP-EF-016 .. CP-EF-020 (documento "Casos de prueba SQA v1").
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PedidosVentasApiTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registrarPropiedades(DynamicPropertyRegistry registry) {
        try {
            Path tempDir = java.nio.file.Files.createTempDirectory("cookiebites-sqa-");
            String ventasPath = TestDataUtils.escribirVentas(tempDir, TestDataUtils.DATASET_ALTO);
            String productosPath = TestDataUtils.archivoVacioEscribible(tempDir, "Productos.json");
            String usuariosPath = TestDataUtils.archivoVacioEscribible(tempDir, "Usuarios.json");
            registry.add("ventas.json.path", () -> ventasPath);
            registry.add("productos.json.path", () -> productosPath);
            registry.add("usuarios.json.path", () -> usuariosPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CP-EF-016: listado de pedidos -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-016 - findAll de pedidos retorna 200")
    void cpEf016_findAllPedidos_retorna200() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-017: registro de pedido -> 201 Created <= 60s.
     * Se envía el pedido sin el sub-objeto "carrito" para evitar el problema
     * de deserialización conocido (CarritoCompra no expone constructor por
     * defecto), lo cual no afecta el objetivo del caso (validar la creación).
     */
    @Test
    @DisplayName("CP-EF-017 - agregarProducto (pedido/crear) retorna 201")
    void cpEf017_crearPedido_retorna201() throws Exception {
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("nombreUsuario", "usuario_pedido_qa");
        pedido.put("direccion", "Calle Falsa 123");
        pedido.put("fechaDeEntrega", "2026-07-01");
        pedido.put("estado", "ORDEN_RECIBIDA");

        long inicio = System.currentTimeMillis();

        mockMvc.perform(post("/pedido/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated());

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-018: consulta de pedido por usuario -> 200 OK <= 60s.
     *
     * NOTA SQA (defecto detectado): ListaPedidos.init() inserta un Pedido()
     * vacío (nombreUsuario == null) como primer elemento de la lista al
     * arrancar la aplicación. ListaPedidos.findByName() itera con
     * "pro.nombreUsuario.equalsIgnoreCase(name)", lo cual lanza
     * NullPointerException en cuanto evalúa ese primer registro nulo,
     * independientemente del nombre buscado. Este caso documenta dicho
     * hallazgo: se espera 200 OK por especificación, pero la implementación
     * actual puede responder 500 debido a la NPE descrita.
     */
    @Test
    @DisplayName("CP-EF-018 - verCarrito (pedido/leer) retorna 200 con el pedido del usuario")
    void cpEf018_leerPedidoPorUsuario_retorna200() throws Exception {
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("nombreUsuario", "usuario_lectura_pedido_qa");
        pedido.put("direccion", "Avenida Siempre Viva 742");
        pedido.put("fechaDeEntrega", "2026-08-15");
        pedido.put("estado", "EN_PROCESO");

        mockMvc.perform(post("/pedido/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated());

        long inicio = System.currentTimeMillis();
        MvcResult resultado = mockMvc.perform(get("/pedido/leer/{id}", "usuario_lectura_pedido_qa"))
                .andReturn();
        long duracion = System.currentTimeMillis() - inicio;

        int status = resultado.getResponse().getStatus();
        if (status != 200) {
            System.out.println("[SQA][DEFECTO] CP-EF-018: se esperaba 200 OK pero se obtuvo " + status
                    + ". Posible causa: NullPointerException en ListaPedidos.findByName() debido al "
                    + "Pedido() vacío insertado por @PostConstruct init().");
        }
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
        assertTrue(status == 200, "Se esperaba 200 OK (ver nota SQA arriba si fallo)");
    }

    /**
     * CP-EF-019: listado de ventas bajo dataset alto (10,000) -> 200 OK <= 60s.
     */
    @Test
    @DisplayName("CP-EF-019 - findAll de ventas bajo dataset alto (10,000) <= 60s")
    void cpEf019_findAllVentas_datasetAlto_respondeEnTiempoLimite() throws Exception {
        long inicio = System.currentTimeMillis();

        mockMvc.perform(get("/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(TestDataUtils.DATASET_ALTO));

        long duracion = System.currentTimeMillis() - inicio;
        assertTrue(duracion <= TestDataUtils.LIMITE_MS,
                "RNF-EF-01 violado: findAll de ventas tardó " + duracion + " ms");
    }

    /**
     * CP-EF-020: registro de venta -> 201 Created <= 60s, con verificación de
     * que el tamaño de la lista de ventas creció en uno.
     */
    @Test
    @DisplayName("CP-EF-020 - agregarVenta retorna 201 y persiste el registro")
    void cpEf020_agregarVenta_retorna201YPersiste() throws Exception {
        Venta venta = new Venta("Banco_QA", 12345678L, 998877L, 3001234567L);

        long inicio = System.currentTimeMillis();
        mockMvc.perform(post("/ventas/registrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated());
        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        mockMvc.perform(get("/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(TestDataUtils.DATASET_ALTO + 1));
    }
}
