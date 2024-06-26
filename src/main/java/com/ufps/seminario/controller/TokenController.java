package com.ufps.seminario.controller;

import com.ufps.seminario.service.TokenIntegranteService;
import com.ufps.seminario.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TokenController {

    @Autowired
    TokenService tokenService;

    @Autowired
    TokenIntegranteService tokenIntegranteService;

    @GetMapping("/Confirmacion/{token}")
    public String confirmarCuenta(@PathVariable String token) {
        if (tokenService.activarCuenta(token)) {
            return "redirect:/?exitoRegistro";
        } else {
            return "redirect:/?errorToken";
        }
    }

    @GetMapping("/Confirmacion/integrante/{token}")
    public String confirmarIntegrante(@PathVariable String token) {
        tokenIntegranteService.aceptarInvitacion(token);
        return "redirect:/?exitoRegistro";
    }
}
