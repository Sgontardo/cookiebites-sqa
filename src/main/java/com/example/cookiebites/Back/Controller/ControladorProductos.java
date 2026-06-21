package com.example.cookiebites.Back.Controller;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.cookiebites.Back.Model.Producto;
import com.example.cookiebites.Back.Repository.ListaProductos;
import java.io.File;


@RestController
public class ControladorProductos {
    @Autowired
    private ListaProductos listaProductos;

    
    public ControladorProductos(ListaProductos list){
        this.listaProductos = list;
        System.out.println("creado el controlador");
    }

     @GetMapping("productos/buscar/{nombre}")
    public ResponseEntity<Producto> buscarProducto(@PathVariable String nombre) {
        Producto producto = listaProductos.consultaProducto(nombre);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/productos")
    ArrayList<Producto> findAll(){
        System.out.println(this.listaProductos);
        return this.listaProductos.findAll();
    }

    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping("productos/crear")
    // public void agregarProducto(@RequestBody Producto prod){
    //     System.out.println(prod.toString());
    //     this.listaProductos.save(prod);
    // }

   @PostMapping(value = "productos/crear", consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public void agregarProducto(
        @RequestPart("producto") Producto prod,
        @RequestPart("imagen") MultipartFile imagenFile
    ) throws IOException {
        String nombreImagen = imagenFile.getOriginalFilename();

        // Obtén la ruta absoluta del proyecto
        String basePath = new File("").getAbsolutePath();
        String rutaDestino = basePath + "/src/main/java/com/example/cookiebites/Front/View/img/galletas/" + nombreImagen;

        File destino = new File(rutaDestino);
        destino.getParentFile().mkdirs(); // Crea la carpeta si no existe
        imagenFile.transferTo(destino);

        prod.setImagen("img/galletas/" + nombreImagen);
        this.listaProductos.save(prod);
    }
    
    @DeleteMapping("/productos/eliminar/{nombre}")
        public ResponseEntity<Void> eliminarProducto(@PathVariable String nombre) {
        listaProductos.eliminarProducto(nombre);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/productos/editar/{nombre}")
    public ResponseEntity<Void> editarProducto(@PathVariable String nombre, @RequestBody Producto productoEditado) {
        Producto productoExistente = listaProductos.consultaProducto(nombre);
        if (productoExistente != null) {
            productoExistente.setNombre(productoEditado.getNombre());
            productoExistente.setPrecio(productoEditado.getPrecio());
            productoExistente.setIngredientes(productoEditado.getIngredientes());
            productoExistente.setImagen(productoEditado.getImagen());
            listaProductos.actualizarProductos(); // Guarda los cambios en el JSON
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}