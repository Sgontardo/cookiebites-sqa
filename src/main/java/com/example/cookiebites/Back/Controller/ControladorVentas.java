package com.example.cookiebites.Back.Controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.cookiebites.Back.Model.Venta;
import com.example.cookiebites.Back.Repository.ListaVentas;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorVentas {

    @Autowired
    private ListaVentas listaVentas;

    public ControladorVentas(ListaVentas lista) {
        this.listaVentas = lista;
        System.out.println("creado el controlador");
    }

    @GetMapping("/ventas")
    ArrayList<Venta> findAll(){
        System.out.println(this.listaVentas);
        return this.listaVentas.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/ventas/registrar")
    public void agregarVenta(@RequestBody Venta ven){
        System.out.println(ven.toString());
        this.listaVentas.save(ven);
    }
}
