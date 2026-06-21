package com.example.cookiebites.Back.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.example.cookiebites.Back.Model.Perfil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Repository
public class ListaUsuarios {

    @Value("${usuarios.json.path}")
    private String usuariosJsonPath;
    private ArrayList<Perfil> listaPerfiles = new ArrayList<Perfil>();
    
    @PostConstruct
    private void init(){
        this.listaPerfiles = leerPerfiles();
    }

    public void agregarUsuario() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(usuariosJsonPath);
            objectMapper.writeValue(archivo, listaPerfiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Perfil> leerPerfiles() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File archivo = new File(usuariosJsonPath);
            listaPerfiles = new ArrayList<>(objectMapper.readValue(
                archivo,
                new TypeReference<ArrayList<Perfil>>() {}
            ));
        } catch (IOException e) {
            e.printStackTrace();
            listaPerfiles = new ArrayList<>();
        }
        return listaPerfiles;
    }

    public void crearPerfil(Perfil per) {
        boolean existe = false;
        for (Perfil usuario : this.listaPerfiles) {
            if (usuario.nombreUsuario.equals(per.nombreUsuario)) {
                //EL USUARIO YA EXISTE
                System.err.println("El usuario ya existe");
                existe = true;
            }
        }
        if (!existe) save(per);
    }

    public Perfil consultaPerfil(String dato) {
        Perfil usuarioEncontrado = new Perfil();
        for (Perfil usuario : listaPerfiles) {
            if (usuario.nombreUsuario.equalsIgnoreCase(dato)) {
                usuarioEncontrado = usuario;
            }
        }
        return usuarioEncontrado;
    }

    public void eliminarPerfil(String dato) {
        for (Perfil usuario : listaPerfiles) {
            if (usuario.nombreUsuario.equalsIgnoreCase(dato)) {
                listaPerfiles.remove(usuario);
                agregarUsuario();
            }
        }
    }

    public ArrayList<Perfil> findAll(){
        return this.listaPerfiles;
    }

    public void save(Perfil per){
        this.listaPerfiles.add(per);
        agregarUsuario();
    }
}
