package com.example.cookiebites.Back.Repository;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import com.example.cookiebites.Back.Model.CarritoCompra;


import jakarta.annotation.PostConstruct;

@Repository
public class ListaCarritos {

    private ArrayList<CarritoCompra> listaCarros = new ArrayList<CarritoCompra>();

    @PostConstruct
    private void init(){
        System.err.println("Lista pedidos creada");
    }

    public ArrayList<CarritoCompra> findAll(){
        return this.listaCarros;
    }

    public void save(CarritoCompra car){
        this.listaCarros.add(car);
    }

    public CarritoCompra findByName(String name){
        for (CarritoCompra pro : listaCarros) {
            if (pro.usuario.equalsIgnoreCase(name)) {
                return pro;
            }
        }
        return null;
    }


}
