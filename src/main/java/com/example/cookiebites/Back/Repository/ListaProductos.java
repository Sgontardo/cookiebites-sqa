package com.example.cookiebites.Back.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.example.cookiebites.Back.Model.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;


@Repository
public class ListaProductos {

    @Value("${productos.json.path}")
    private String productosJsonPath;
    private ArrayList<Producto> listaProductos = new ArrayList<>();
    
    @PostConstruct
    public  void init(){
        this.listaProductos = leerProductos();
        System.out.println("Lista Productos creada");
    }

    public ArrayList<Producto> leerProductos() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(productosJsonPath);
            listaProductos = new ArrayList<>(objectMapper.readValue(
                archivo,
                new TypeReference<ArrayList<Producto>>() {}
            ));
        } catch (IOException e) {
            e.printStackTrace();
            listaProductos = new ArrayList<>();
        }
        return listaProductos;
    }

    public void actualizarProductos() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(productosJsonPath);
            objectMapper.writeValue(archivo, listaProductos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Producto consultaProducto(String nombre) {
        for (Producto p : listaProductos) {
            if (p.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                return p;
            }
        }
        return null;
    }

    public void eliminarProducto(String nombre) {
        for (Producto p : listaProductos) {
            if (p.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                listaProductos.remove(p);
                actualizarProductos();
            }
        }
    }

    public ArrayList<Producto> findAll(){
        return this.listaProductos;
    }

    public void save(Producto prod){
        this.listaProductos.add(prod);
        actualizarProductos();
    }
}
