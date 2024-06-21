package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Role;
import com.ufps.seminario.service.SesionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    SesionService sesionService;

    @GetMapping("/ver_usuarios")
    public String verUsuarios(Model model, RedirectAttributes redirectAttributes){
        List<Usuario> estudiantes = usuarioService.obtenerEstudiantes();
        List<Usuario> administradores = usuarioService.obtenerAdministradores();
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("administradores", administradores);

        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
        model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

        return "gestionUsuarios";
    } 

    @PostMapping("{idUsuario}/admin")
    public String promoverUsuario(@PathVariable int idUsuario, RedirectAttributes redirectAttributes){
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        usuario.setRole(new Role(2, "ADMIN_FERIA", true));
        usuarioService.actualizarUsuario(usuario);

        return "redirect:/usuario/ver_usuarios";
    }

    @PostMapping("{idUsuario}/estudiante")
    public String degradarUsuario(@PathVariable int idUsuario, RedirectAttributes redirectAttributes){
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        usuario.setRole(new Role(1, "ESTUDIANTE", true));
        usuarioService.actualizarUsuario(usuario);

        return "redirect:/usuario/ver_usuarios";
    }

    @PostMapping("{idUsuario}/eliminar")
    public String bloquearUsuario(@PathVariable int idUsuario, RedirectAttributes redirectAttributes){
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        usuario.setEnabled(false);
        usuarioService.actualizarUsuario(usuario);

        return "redirect:/usuario/ver_usuarios";
    }


}
