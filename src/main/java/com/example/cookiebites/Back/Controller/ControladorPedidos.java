package com.example.cookiebites.Back.Controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.cookiebites.Back.Model.Pedido;
import com.example.cookiebites.Back.Repository.ListaPedidos;

@RestController
public class ControladorPedidos {
    @Autowired
    private ListaPedidos lista;
    

    public ControladorPedidos(ListaPedidos lista){
        this.lista = lista;
    }

    @GetMapping("/pedidos")
    ArrayList<Pedido> findAll(){
        System.out.println(this.lista);
        return this.lista.findAll();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("pedido/crear")
    public void agregarProducto(@RequestBody Pedido ped){
        System.out.println(ped.toString());
        this.lista.save(ped);
    }

    @GetMapping("pedido/leer/{id}")
    public ResponseEntity<Pedido> verCarrito(@PathVariable("id") String nombre) {
        return ResponseEntity.ok(lista.findByName(nombre));   
    }

}
