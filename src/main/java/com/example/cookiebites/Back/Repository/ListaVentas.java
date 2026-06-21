package com.example.cookiebites.Back.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.example.cookiebites.Back.Model.Venta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

@Repository
public class ListaVentas {

    @Value("${ventas.json.path}")
    private String ventasJsonPath;
    private ArrayList<Venta> listaVentas = new ArrayList<>();

    @PostConstruct
    private void init(){
        this.listaVentas = leerVentas();
        System.out.println("Lista Ventas creada");
    }

    public void registrarVenta() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(ventasJsonPath);
            objectMapper.writeValue(archivo, listaVentas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Venta> leerVentas() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(ventasJsonPath);
            listaVentas = new ArrayList<>(objectMapper.readValue(
                archivo,
                new TypeReference<ArrayList<Venta>>() {}
            ));
        } catch (IOException e) {
            e.printStackTrace();
            listaVentas = new ArrayList<>();
        }
        return listaVentas;
    }

    public ArrayList<Venta> findAll(){
        return this.listaVentas;
    }

    public void save(Venta venta){
        this.listaVentas.add(venta);
        registrarVenta();
    }
    
}
