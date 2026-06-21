package com.example.cookiebites.Back.Controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.cookiebites.Back.Model.Perfil;
import com.example.cookiebites.Back.Repository.ListaUsuarios;

@RestController
public class ControladorPerfiles {

    @Autowired
    private ListaUsuarios listaUsuarios;

    
    public ControladorPerfiles(ListaUsuarios lista){
        this.listaUsuarios = lista;
        System.out.println("creado el controlador");
    }

    @GetMapping("/perfil/todos")
    ArrayList<Perfil> findAll(){
        System.out.println(this.listaUsuarios);
        return this.listaUsuarios.findAll();
    }
    @GetMapping("/perfil/{nombre}/clave/{clave}")
    ResponseEntity<Perfil> findByName(@PathVariable("nombre") String nombre, @PathVariable("clave") String clavesita){
        Perfil per = this.listaUsuarios.consultaPerfil(nombre);

        ResponseEntity<Perfil> retorno = (per.clave.equals(clavesita))? (new ResponseEntity<Perfil>(per, HttpStatus.OK)) : (new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return retorno;
    }

        @GetMapping("/perfil/{nombre}")
    ResponseEntity<Perfil> findByJustName(@PathVariable("nombre") String nombre){
        Perfil per = this.listaUsuarios.consultaPerfil(nombre);
        return new ResponseEntity<Perfil>(per, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("perfil/crear")
    public void agregarPerfil(@RequestBody Perfil per){
        System.out.println(per.toString());
        this.listaUsuarios.crearPerfil(per);
    }

    @DeleteMapping("/perfil/eliminar/{nombreUsuario}")
        public ResponseEntity<Void> eliminarPerfil(@PathVariable("nombreUsuario") String nombreUsuario) {
        listaUsuarios.eliminarPerfil(nombreUsuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/perfil/editar/{nombreUsuario}")
public ResponseEntity<Void> editarPerfil(@PathVariable("nombreUsuario") String nombreUsuario, @RequestBody Perfil perfilEditado) {
    ArrayList<Perfil> perfiles = listaUsuarios.findAll();
    for (Perfil usuario : perfiles) {
        if (usuario.nombreUsuario.equalsIgnoreCase(nombreUsuario)) {
            usuario.nombre = perfilEditado.nombre;
            usuario.nombreUsuario = perfilEditado.nombreUsuario;
            usuario.clave = perfilEditado.clave;
            usuario.rol = perfilEditado.rol;
            usuario.correo = perfilEditado.correo;
            listaUsuarios.agregarUsuario(); // Guarda los cambios en el JSON
            return ResponseEntity.noContent().build();
        }
    }
    return ResponseEntity.notFound().build();
}

}
