package com.example.cookiebites.Back.Model;

import java.time.LocalDate;

import com.example.cookiebites.Back.Repository.ListaProductos;


//pedido
public class Pedido {
    public String nombreUsuario;
    public String direccion;
    public CarritoCompra carrito;
    public LocalDate fechaDeEntrega;
    public EstadoPedido estado;
    
    public Pedido(){}

    public Pedido(String nombreUsuario, String direccion,CarritoCompra carrito, LocalDate fechaDeEntrega, EstadoPedido estado){
        this.nombreUsuario = nombreUsuario;
        this.direccion = direccion;
        this.fechaDeEntrega = fechaDeEntrega;
        this.estado = estado;
        this.carrito = carrito;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public CarritoCompra getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoCompra carrito) {
        this.carrito = carrito;
    }


    public LocalDate getFechaDeEntrega() {
        return fechaDeEntrega;
    }

    public void setFechaDeEntrega(LocalDate fechaDeEntrega) {
        this.fechaDeEntrega = fechaDeEntrega;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public double getTotal(ListaProductos productos) {
        return carrito.totalPagar(productos);
    }
}
