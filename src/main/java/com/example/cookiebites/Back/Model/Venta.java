package com.example.cookiebites.Back.Model;


public class Venta {
    public String banco;
    public long cedula;
    public long numRef;
    public long numTelf;
//venta
    public Venta(){}
    
    public Venta(String banco, long cedula, long numRef, long numTelf){
        this.banco = banco;
        this.cedula = cedula;
        this.numRef = numRef;
        this.numTelf = numTelf;
    }

    /*public String generarFactura(ListaProductos listaProductos) {
        StringBuilder factura = new StringBuilder();
        factura.append("Cliente: ").append(pedido.getNombreUsuario()).append("\n");
        factura.append("Productos:\n");
        // for (CarritoItem pro : pedido.getCompra()) {
        //     factura.append("- ")
        //     .append(pro.getProducto().getNombre())
        //     .append(" x").append(pro.getCantidad())
        //     .append(" = ").append(pro.getCantidad() * pro.getProducto().getPrecio())
        //     .append("\n");
        // }
        factura.append("Total: ").append(pedido.getTotal(listaProductos)).append("\n");
        factura.append("Referencia de pago: ").append(numRef);
        return factura.toString();
    }*/

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public long getCedula() {
        return cedula;
    }

    public void setCedula(long cedula) {
        this.cedula = cedula;
    }

    public long getNumRef() {
        return numRef;
    }

    public void setNumRef(long numRef) {
        this.numRef = numRef;
    }

    public long getNumTelf() {
        return numTelf;
    }

    public void setNumTelf(long numTelf) {
        this.numTelf = numTelf;
    }

    /*public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }*/
}

