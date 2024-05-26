package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/eventos")
public class EventosController {

    @Autowired
    VersionService versionService;

    @Autowired
    ProyectoService proyectoService;

    @Autowired
    SesionService sesionService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    IntegranteService integranteService;

    @GetMapping("/")
    public String verEventos(){
        return "redirect:/eventos/search";
    }

    @GetMapping("/search")
    public String buscarEventos(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            try{
                String username = sesionService.getUsernameFromSession();
                model.addAttribute("username", username);
                model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            }catch(Exception e){
                model.addAttribute("role", 0);
            }
            List<Version> versiones = versionService.obtenerVersionesDisponibles(keyword, LocalDate.now());
            model.addAttribute("versiones", versiones);
            return "verEventosIndex";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/{idVersion}/proyectos")
    public String verProyectos(@PathVariable int idVersion){
        return "redirect:/eventos/"+idVersion+"/proyectos/search";
    }

    @GetMapping("/{idVersion}/proyectos/search")
    public String buscarProyecto(@PathVariable int idVersion, @RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            try{
                String username = sesionService.getUsernameFromSession();
                model.addAttribute("username", username);
                model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            }catch(Exception e){
                model.addAttribute("role", 0);
            }
            Version version = versionService.obtenerVersion(idVersion);
            List<Proyecto> proyectos = proyectoService.obtenerProyectosPorVersionYPalabra(version, keyword);
            model.addAttribute("version", version);
            model.addAttribute("proyectos", proyectos);
            return "verProyectosIndex";
        } catch (Exception e) {
            return "redirect:/eventos/search";
        }
    }

    @GetMapping("/{idVersion}/proyectos/{idProyecto}")
    public String verProyecto(@PathVariable int idVersion, @PathVariable int idProyecto, Model model){
        try {
            try{
                String username = sesionService.getUsernameFromSession();
                model.addAttribute("username", username);
                model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            }catch(Exception e){
                model.addAttribute("role", 0);
            }
            Version version = versionService.obtenerVersion(idVersion);
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            List<Integrante> integrantes = integranteService.obtenerIntegrantePorProyecto(proyecto);
            model.addAttribute("version", version);
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("integrantes", integrantes);
            return "verInformacionProyectoIndex";
        } catch (Exception e) {
            return "redirect:/eventos/"+idVersion+"/proyectos";
        }
    }

}
