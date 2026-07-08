package com.example.cookiebites.Back.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaInicioController {

    @GetMapping("/")
    public String redirigirAInicio() {
        return "redirect:/LogIn.html";
    }

    @GetMapping("/inicio")
    public String inicioAlternativo() {
        return "redirect:/LogIn.html";
    }
}