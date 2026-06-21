package com.example.cookiebites.Back.Model;

import java.util.ArrayList;

import com.example.cookiebites.Back.Repository.ListaProductos;

public class CarritoCompra {

    private ArrayList<CarritoItem> carrito = new ArrayList<>();
    public String usuario; 

    public CarritoCompra(String Usuario){
        this.usuario = Usuario;
    }

    public void agregarCarrito(CarritoItem item) {
        boolean encontrado = false;
        for (CarritoItem existente : this.carrito) {
            if (existente.getNombreProducto().equals(item.getNombreProducto())) {
                existente.setCantidad(existente.getCantidad()+item.getCantidad());
                encontrado = true;
                break;
            }
        }
        if (!encontrado)
        this.carrito.add(item);
    }

    public double totalPagar(ListaProductos productos) {
        double total = 0;
        for (CarritoItem car : this.carrito) {
            Producto prod = productos.consultaProducto(car.getNombreProducto());
            if (prod != null) {
                total += prod.getPrecio() * car.getCantidad();
            }
        }
        return total;
    }

    public void eliminarProducto(String nombreProducto) {
        carrito.removeIf(item -> item.getNombreProducto().equalsIgnoreCase(nombreProducto));
    }

    public void editarCantidad(String nombreProducto, int nuevaCantidad) {
        for (CarritoItem item : carrito) {
            if (item.getNombreProducto().equalsIgnoreCase(nombreProducto)) {
                item.setCantidad(nuevaCantidad);
                break;
            }
        }
    }

    public ArrayList<CarritoItem> findAll(){
        return this.carrito;
    }   
}
