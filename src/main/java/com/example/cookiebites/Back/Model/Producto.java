package com.example.cookiebites.Back.Model;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import java.lang.String;

@Component
public class Producto {
    public String nombre;
    public double precio;
    public String imagen;
    public ArrayList<String> ingredientes = new ArrayList<>();


    public Producto(){}

    public Producto(String nombre, double precio, String imagen, ArrayList<String> ingredientes){
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.ingredientes = ingredientes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public ArrayList<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(ArrayList<String> ingredientes) {
        this.ingredientes = ingredientes;
    }
}
