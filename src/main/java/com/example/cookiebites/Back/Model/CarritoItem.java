package com.example.cookiebites.Back.Model;

public class CarritoItem {
    private String nombreProducto;
    private int cantidad;

    public CarritoItem(){}

    public CarritoItem(String nombreProducto, int cant) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
    }

    // public double calcularTotal() {
    //      return this.nombreProducto.getPrecio()*this.cant;
    // }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
