package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Role;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.service.SesionService;
import com.ufps.seminario.service.UsuarioService;
import com.ufps.seminario.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InaccessibleObjectException;

@Controller
public class LoginController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @GetMapping("/")
    public String index(){

        return "index";
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
    @GetMapping("/Iniciar_Sesion")
    public String verLogin(){
        return "login.html";
    }
    @PostMapping("/Registrarse")
    public String registro(@ModelAttribute Usuario usuario){
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
            if(!userDetails.isEnabled()){
                usuario.setId(usuarioService.obtenerId(usuario.getUsername()));
                usuarioService.guardarUsuario(usuario);
                return "redirect:/Iniciar_Sesion?enlaceActivacion";
            }
            return "redirect:/Iniciar_Sesion?errorRegistro";

        } catch (UsernameNotFoundException e) {
            usuario.setEnabled(false);
            usuarioService.guardarUsuario(usuario);
            return "redirect:/Iniciar_Sesion?enlaceActivacion";
        }
    }
    @GetMapping("/logout")
    public String cerrarSesion(){
        return "redirect:/?logout";
    }

    @GetMapping("/Inicio")
    public String verPaginaInicio(Model model){
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "inicio";
    }
}


