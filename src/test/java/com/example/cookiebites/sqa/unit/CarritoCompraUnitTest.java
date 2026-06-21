package com.example.cookiebites.sqa.unit;

import com.example.cookiebites.Back.Model.CarritoCompra;
import com.example.cookiebites.Back.Model.CarritoItem;
import com.example.cookiebites.Back.Model.Producto;
import com.example.cookiebites.Back.Repository.ListaProductos;
import com.example.cookiebites.sqa.util.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos Unitarios de Soporte Interno: CP-EF-021 .. CP-EF-024
 * (documento "Casos de prueba SQA v1").
 *
 * Estas pruebas instancian las clases de modelo directamente (sin contexto de
 * Spring) tal como exige el campo "Pasos de Ejecución: Invocar método
 * directamente" de la especificación.
 */
class CarritoCompraUnitTest {

    /**
     * CP-EF-021 (CarritoCompra.agregarCarrito): verificar inserción o
     * acumulación sin duplicar items, en <= 60s.
     */
    @Test
    @DisplayName("CP-EF-021 - agregarCarrito inserta y luego acumula sin duplicar")
    void cpEf021_agregarCarrito_insertaYAcumulaSinDuplicar() {
        long inicio = System.currentTimeMillis();

        CarritoCompra carrito = new CarritoCompra("usuario_unit_test");
        CarritoItem item1 = new CarritoItem();
        item1.setNombreProducto("Galleta_Choco");
        item1.setCantidad(2);

        // Carrito vacío: debe insertar.
        carrito.agregarCarrito(item1);
        assertEquals(1, carrito.findAll().size());
        assertEquals(2, carrito.findAll().get(0).getCantidad());

        // Mismo producto repetido: debe acumular cantidad, no duplicar item.
        CarritoItem item1Repetido = new CarritoItem();
        item1Repetido.setNombreProducto("Galleta_Choco");
        item1Repetido.setCantidad(3);
        carrito.agregarCarrito(item1Repetido);

        assertEquals(1, carrito.findAll().size(), "No debe duplicar el item, debe acumular");
        assertEquals(5, carrito.findAll().get(0).getCantidad(), "La cantidad debe acumularse (2+3=5)");

        assertTrue(System.currentTimeMillis() - inicio <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-022 (CarritoCompra.totalPagar): verificar cálculo de total con
     * catálogo grande (dataset alto), en <= 60s.
     */
    @Test
    @DisplayName("CP-EF-022 - totalPagar calcula correctamente con catálogo grande")
    void cpEf022_totalPagar_calculaCorrectamente_catalogoGrande() {
        ListaProductos listaProductos = new ListaProductos();
        ArrayList<Producto> productos = TestDataUtils.generarProductos(TestDataUtils.DATASET_ALTO);
        // Producto conocido para validar el cálculo exacto.
        Producto conocido = new Producto("Producto_Conocido_QA", 4.5, null, new ArrayList<>());
        productos.add(conocido);
        ReflectionTestUtils.setField(listaProductos, "listaProductos", productos);

        CarritoCompra carrito = new CarritoCompra("usuario_total_qa");
        CarritoItem item = new CarritoItem();
        item.setNombreProducto("Producto_Conocido_QA");
        item.setCantidad(4);
        carrito.agregarCarrito(item);

        long inicio = System.currentTimeMillis();
        double total = carrito.totalPagar(listaProductos);
        long duracion = System.currentTimeMillis() - inicio;

        assertEquals(18.0, total, 0.0001, "4 unidades x 4.5 = 18.0");
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-023 (CarritoCompra.eliminarProducto interna): verificar
     * eliminación interna rápida, en <= 60s.
     */
    @Test
    @DisplayName("CP-EF-023 - eliminarProducto elimina el item solicitado")
    void cpEf023_eliminarProducto_eliminaItem() {
        CarritoCompra carrito = new CarritoCompra("usuario_elim_qa");
        CarritoItem itemA = new CarritoItem();
        itemA.setNombreProducto("Galleta_A");
        itemA.setCantidad(1);
        CarritoItem itemB = new CarritoItem();
        itemB.setNombreProducto("Galleta_B");
        itemB.setCantidad(2);
        carrito.agregarCarrito(itemA);
        carrito.agregarCarrito(itemB);

        long inicio = System.currentTimeMillis();
        carrito.eliminarProducto("Galleta_A");
        long duracion = System.currentTimeMillis() - inicio;

        assertEquals(1, carrito.findAll().size());
        assertEquals("Galleta_B", carrito.findAll().get(0).getNombreProducto());
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }

    /**
     * CP-EF-024 (CarritoCompra.editarCantidad interna): verificar edición
     * interna rápida, en <= 60s.
     */
    @Test
    @DisplayName("CP-EF-024 - editarCantidad actualiza la cantidad del item")
    void cpEf024_editarCantidad_actualizaCantidad() {
        CarritoCompra carrito = new CarritoCompra("usuario_editar_qa");
        CarritoItem item = new CarritoItem();
        item.setNombreProducto("Galleta_Editable");
        item.setCantidad(1);
        carrito.agregarCarrito(item);

        long inicio = System.currentTimeMillis();
        carrito.editarCantidad("Galleta_Editable", 99);
        long duracion = System.currentTimeMillis() - inicio;

        assertEquals(99, carrito.findAll().get(0).getCantidad());
        assertTrue(duracion <= TestDataUtils.LIMITE_MS, "RNF-EF-01 violado");
    }
}
