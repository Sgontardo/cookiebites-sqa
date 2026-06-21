package com.example.cookiebites.sqa.util;

import com.example.cookiebites.Back.Model.Perfil;
import com.example.cookiebites.Back.Model.Producto;
import com.example.cookiebites.Back.Model.Venta;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Utilidades de generación de datos de prueba (datasets medio/alto) usados por
 * las baterías CP-EF-001 .. CP-EF-028 del documento "Casos de prueba SQA v1".
 *
 * Genera archivos JSON planos equivalentes a Usuarios.json / Productos.json /
 * Ventas.json con N registros, permitiendo inyectar un registro "objetivo"
 * conocido en una posición concreta (p.ej. el último, para forzar el peor
 * caso de búsqueda lineal exigido por las técnicas de Análisis de Valores
 * Límite y Adivinación de Errores descritas en la especificación de diseño.
 */
public final class TestDataUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataUtils() {
    }

    /** Dataset medio según el documento de pruebas. */
    public static final int DATASET_MEDIO = 1000;
    /** Dataset alto según el documento de pruebas. */
    public static final int DATASET_ALTO = 10000;

    /** Límite no funcional crítico RNF-EF-01: 60 segundos en milisegundos. */
    public static final long LIMITE_MS = 60_000L;

    // ---------------------------------------------------------------- Perfiles

    public static ArrayList<Perfil> generarPerfiles(int cantidad) {
        ArrayList<Perfil> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(new Perfil(
                    "Usuario " + i,
                    "user_" + i,
                    "clave_" + i,
                    "cliente",
                    "user_" + i + "@correo.com"));
        }
        return lista;
    }

    /**
     * Genera N perfiles e inserta uno conocido en la última posición del
     * archivo, simulando el peor escenario (Worst-Case Scenario) para una
     * búsqueda lineal iterativa, tal como exige la técnica de Adivinación de
     * Errores del documento de diseño de pruebas.
     */
    public static String escribirUsuariosConObjetivoAlFinal(Path dir, int cantidad,
                                                              String nombreUsuarioObjetivo,
                                                              String claveObjetivo) throws IOException {
        ArrayList<Perfil> lista = generarPerfiles(cantidad - 1);
        lista.add(new Perfil("Usuario Objetivo", nombreUsuarioObjetivo, claveObjetivo,
                "cliente", nombreUsuarioObjetivo + "@correo.com"));
        return escribirJson(dir, "Usuarios.json", lista);
    }

    public static String escribirUsuarios(Path dir, int cantidad) throws IOException {
        return escribirJson(dir, "Usuarios.json", generarPerfiles(cantidad));
    }

    /** Escribe un JSON deliberadamente malformado para el caso CP-EF-001 (Edge Case). */
    public static String escribirUsuariosCorrupto(Path dir) throws IOException {
        Path archivo = dir.resolve("UsuariosCorrupto.json");
        Files.writeString(archivo,
                "[{\"nombre\":\"Roto\",\"nombreUsuario\":\"roto1\",\"clave\":\"x\"" /* falta cierre */);
        return archivo.toAbsolutePath().toString();
    }

    // --------------------------------------------------------------- Productos

    public static ArrayList<Producto> generarProductos(int cantidad) {
        ArrayList<Producto> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            ArrayList<String> ingredientes = new ArrayList<>();
            ingredientes.add("Harina");
            ingredientes.add("Azúcar");
            lista.add(new Producto("Galleta_" + i, 3.0 + (i % 10), "img/galletas/galleta_" + i + ".png",
                    ingredientes));
        }
        return lista;
    }

    public static String escribirProductosConObjetivoAlFinal(Path dir, int cantidad,
                                                               String nombreProductoObjetivo) throws IOException {
        ArrayList<Producto> lista = generarProductos(cantidad - 1);
        ArrayList<String> ingredientes = new ArrayList<>();
        ingredientes.add("Harina");
        ingredientes.add("Chocolate");
        lista.add(new Producto(nombreProductoObjetivo, 4.5, "img/galletas/objetivo.png", ingredientes));
        return escribirJson(dir, "Productos.json", lista);
    }

    public static String escribirProductos(Path dir, int cantidad) throws IOException {
        return escribirJson(dir, "Productos.json", generarProductos(cantidad));
    }

    // ------------------------------------------------------------------ Ventas

    public static ArrayList<Venta> generarVentas(int cantidad) {
        ArrayList<Venta> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(new Venta("Banco_" + (i % 5), 1000000L + i, 5000000L + i, 3000000000L + i));
        }
        return lista;
    }

    public static String escribirVentas(Path dir, int cantidad) throws IOException {
        return escribirJson(dir, "Ventas.json", generarVentas(cantidad));
    }

    // ------------------------------------------------------------------- Misc

    private static String escribirJson(Path dir, String nombreArchivo, Object contenido) throws IOException {
        File archivo = dir.resolve(nombreArchivo).toFile();
        MAPPER.writeValue(archivo, contenido);
        return archivo.getAbsolutePath();
    }

    public static String archivoVacioEscribible(Path dir, String nombreArchivo) throws IOException {
        File archivo = dir.resolve(nombreArchivo).toFile();
        MAPPER.writeValue(archivo, new ArrayList<>());
        return archivo.getAbsolutePath();
    }
}
