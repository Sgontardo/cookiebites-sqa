package com.example.cookiebites.Back.Model;

import org.springframework.stereotype.Component;
import java.lang.String;


@Component
public class Perfil {
    public String nombre;
    public String nombreUsuario;
    public String clave;
    public String rol;
    public String correo;

    public Perfil(){
    }

    public Perfil(String nombre, String nombreUsuario, String clave, String rol, String correo){
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rol = rol;
        this.correo = correo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
