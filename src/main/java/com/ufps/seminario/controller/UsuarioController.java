package com.ufps.seminario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.service.UsuarioService;

import org.springframework.ui.Model;
import java.util.List;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
     
    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/ver_usuarios")
    public String verUsuarios(Model model, RedirectAttributes redirectAttributes){
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "gestionUsuarios";
    }
}
