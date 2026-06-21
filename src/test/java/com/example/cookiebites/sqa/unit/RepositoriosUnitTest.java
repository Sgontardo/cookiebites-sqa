package com.example.cookiebites.sqa.unit;

import com.example.cookiebites.Back.Model.Pedido;
import com.example.cookiebites.Back.Model.Perfil;
import com.example.cookiebites.Back.Model.Producto;
import com.example.cookiebites.Back.Model.Venta;
import com.example.cookiebites.Back.Repository.ListaPedidos;
import com.example.cookiebites.Back.Repository.ListaProductos;
import com.example.cookiebites.Back.Repository.ListaUsuarios;
import com.example.cookiebites.Back.Repository.ListaVentas;
import com.example.cookiebites.sqa.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos Unitarios de Soporte Interno: CP-EF-025 .. CP-EF-028
 * (documento "Casos de prueba SQA v1").
 *
 * Las clases de repositorio se instancian directamente con `new` (sin
 * contexto de Spring) para cumplir con "Invocar método directamente". Los
 * campos privados configurados por @Value se asignan vía ReflectionTestUtils.
 */
class RepositoriosUnitTest {

    /**
     * CP-EF-025 (ListaProductos.consultaProducto): búsqueda lineal bajo carga
     * (dataset alto), tanto para nombre existente como inexistente, <= 60s.
     */
    @Test
    @DisplayName("CP-EF-025 - consultaProducto bajo carga retorna producto o null en tiempo límite")
    void cpEf025_consultaProducto_bajoCarga() {
        ListaProductos listaProductos = new ListaProductos();
        ArrayList<Producto> productos = TestDataUtils.generarProductos(TestDataUtils.DATASET_ALTO - 1);
        Producto objetivo = new Producto("Producto_Final_QA", 7.25, null, new ArrayList<>());
        productos.add(objetivo); // worst-case: al final de la lista
        ReflectionTestUtils.setField(listaProductos, "listaProductos", productos);

        long inicio = System.currentTimeMillis();
        Producto encontrado = listaProductos.consultaProducto("Producto_Final_QA");
        Producto noEncontrado = listaProductos.consultaProducto("No_Existe_Jamas_999");
        long duracion = System.currentTimeMillis() - inicio;

        assertNotNull(encontrado);
        assertEquals("Producto_Final_QA", encontrado.getNombre());
        assertNull(noEncontrado);
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-026 (ListaUsuarios.consultaPerfil): búsqueda de usuario bajo
     * carga (dataset alto), existente e inexistente, <= 60s.
     *
     * NOTA: consultaPerfil() nunca retorna null; si no encuentra coincidencia
     * retorna un Perfil "vacío" (todos los campos null), por lo que la
     * verificación de "no encontrado" se hace sobre nombreUsuario == null.
     */
    @Test
    @DisplayName("CP-EF-026 - consultaPerfil bajo carga retorna perfil o vacío en tiempo límite")
    void cpEf026_consultaPerfil_bajoCarga() {
        ListaUsuarios listaUsuarios = new ListaUsuarios();
        ArrayList<Perfil> perfiles = TestDataUtils.generarPerfiles(TestDataUtils.DATASET_ALTO - 1);
        Perfil objetivo = new Perfil("Final QA", "usuario_final_qa", "clave", "cliente", "f@correo.com");
        perfiles.add(objetivo); // worst-case: al final de la lista
        ReflectionTestUtils.setField(listaUsuarios, "listaPerfiles", perfiles);

        long inicio = System.currentTimeMillis();
        Perfil encontrado = listaUsuarios.consultaPerfil("usuario_final_qa");
        Perfil vacio = listaUsuarios.consultaPerfil("no_existe_jamas_999");
        long duracion = System.currentTimeMillis() - inicio;

        assertNotNull(encontrado);
        assertEquals("usuario_final_qa", encontrado.getNombreUsuario());
        assertNull(vacio.getNombreUsuario(), "Para usuarios no encontrados se retorna un Perfil vacío");
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-027 (ListaPedidos.findByName): verificar consulta de pedido bajo
     * carga (volumen medio/alto), <= 60s.
     *
     * NOTA SQA: se instancia ListaPedidos con `new` (sin pasar por
     * @PostConstruct init(), que normalmente agrega un Pedido() vacío con
     * nombreUsuario nulo). Esto evita el defecto de NullPointerException
     * descrito en CP-EF-018 y permite validar la lógica de búsqueda en
     * condiciones controladas, tal como exige un caso "unitario".
     */
    @Test
    @DisplayName("CP-EF-027 - findByName de pedidos bajo carga retorna pedido o null")
    void cpEf027_findByName_pedidos_bajoCarga() {
        ListaPedidos listaPedidos = new ListaPedidos();
        ArrayList<Pedido> pedidos = new ArrayList<>();
        for (int i = 0; i < TestDataUtils.DATASET_MEDIO; i++) {
            Pedido p = new Pedido();
            p.setNombreUsuario("cliente_" + i);
            pedidos.add(p);
        }
        ReflectionTestUtils.setField(listaPedidos, "listaPedidos", pedidos);

        long inicio = System.currentTimeMillis();
        Pedido encontrado = listaPedidos.findByName("cliente_" + (TestDataUtils.DATASET_MEDIO - 1));
        long duracion = System.currentTimeMillis() - inicio;

        assertNotNull(encontrado);
        assertEquals("cliente_" + (TestDataUtils.DATASET_MEDIO - 1), encontrado.getNombreUsuario());
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-027 (Edge Case adicional): documenta el defecto real de
     * ListaPedidos.findByName() cuando la lista contiene un registro con
     * nombreUsuario == null (como el insertado por @PostConstruct init() en
     * producción): se produce NullPointerException en vez de continuar la
     * búsqueda o retornar null de forma controlada.
     */
    @Test
    @DisplayName("CP-EF-027 (Edge) - findByName lanza NPE si existe un Pedido con nombreUsuario nulo (defecto)")
    void cpEf027_findByName_documentaDefectoConRegistroNulo() {
        ListaPedidos listaPedidos = new ListaPedidos();
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido()); // nombreUsuario == null, como en producción
        ReflectionTestUtils.setField(listaPedidos, "listaPedidos", pedidos);

        assertThrows(NullPointerException.class, () -> listaPedidos.findByName("cualquier_usuario"),
                "[SQA][DEFECTO] Se confirma NullPointerException en ListaPedidos.findByName() "
                        + "cuando existe un Pedido con nombreUsuario nulo en la lista.");
    }

    /**
     * CP-EF-028 (ListaVentas.save): verificar persistencia de venta con
     * volumen medio, sin superar 60s.
     */
    @Test
    @DisplayName("CP-EF-028 - ListaVentas.save persiste la venta en el JSON")
    void cpEf028_listaVentasSave_persisteSinSuperarTiempoLimite(@TempDir Path tempDir) throws IOException {
        ListaVentas listaVentas = new ListaVentas();
        String path = TestDataUtils.escribirVentas(tempDir, TestDataUtils.DATASET_MEDIO);
        ReflectionTestUtils.setField(listaVentas, "ventasJsonPath", path);
        ReflectionTestUtils.setField(listaVentas, "listaVentas", listaVentas.leerVentas());

        int tamanoInicial = listaVentas.findAll().size();
        assertEquals(TestDataUtils.DATASET_MEDIO, tamanoInicial);

        Venta nueva = new Venta("Banco_Unit_QA", 999999L, 111111L, 3009999999L);

        long inicio = System.currentTimeMillis();
        listaVentas.save(nueva);
        long duracion = System.currentTimeMillis() - inicio;

        assertEquals(tamanoInicial + 1, listaVentas.findAll().size());
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");

        // Verificamos que también haya quedado persistida en disco (no solo en memoria).
        ListaVentas relectura = new ListaVentas();
        ReflectionTestUtils.setField(relectura, "ventasJsonPath", path);
        ArrayList<Venta> desdeDisco = relectura.leerVentas();
        assertEquals(tamanoInicial + 1, desdeDisco.size());
    }
}
