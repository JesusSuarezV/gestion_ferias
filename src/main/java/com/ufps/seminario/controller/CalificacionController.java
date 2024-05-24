package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calificaciones")
public class CalificacionController {

    @Autowired
    ProyectoService proyectoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    VersionService versionService;

    @Autowired
    IntegranteService integranteService;

    @GetMapping("/mis_calificaciones")
    public String verMisCalificaciones(Model model){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Proyecto> proyectos = proyectoService.obtenerProyectosPorCorreoOrdenadosPorFechaRegistro(username);
            model.addAttribute("proyectosCalificados", proyectos);
            return "misCalificaciones";
        }catch(Exception e){
            return "redirect:/";
        }
    }

    @GetMapping("/jurado")
    public String verVersionesJurado(Model model){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("role", usuario.getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesPorJurado(usuario);
            model.addAttribute("versiones_jurado", versiones);
            return "soyJurado";
        }catch(Exception e){
            return "redirect:/";
        }
    }

    @GetMapping("/jurado/version/{versionId}")
    public String verVersionJurado(Model model, @PathVariable int idVersion){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("role", usuario.getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            List<Proyecto> proyectos = new ArrayList<>();
            for(Proyecto proyecto: usuario.getProyectosCalificar()){
                if(proyecto.getVersion().getId() == version.getId()){
                    proyectos.add(proyecto);
                }
            }
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("version", version);
            return "version";
        }catch(Exception e){
            return "redirect:/version";
        }
    }

    @GetMapping("/jurado/proyecto/{proyectoId}")
    public String verProyectoJurado(Model model, @PathVariable int idProyecto){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("role", usuario.getRole().getNombre());
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            List<Integrante> integrantes = integranteService.obtenerIntegrantePorProyecto(proyecto);
            List<Usuario> jurados = proyecto.getJurados();
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("integrantes", integrantes);
            model.addAttribute("jurados", jurados);
            return "version";
        }catch(Exception e){
            return "redirect:/version";
        }
    }

}
