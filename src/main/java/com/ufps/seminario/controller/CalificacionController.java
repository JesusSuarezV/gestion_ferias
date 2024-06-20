package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
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

    @Autowired
    CriterioService criterioService;

    @Autowired
    CalificacionService calificacionService;

    @GetMapping("/mis_calificaciones")
    public String verMisCalificaciones(Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Proyecto> proyectos = proyectoService.obtenerProyectosPorCorreoOrdenadosPorFechaRegistro(username);
            model.addAttribute("proyectosCalificados", proyectos);
            return "misCalificaciones";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/jurado")
    public String verVersionesJurado(Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("role", usuario.getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesPorJurado(usuario);
            model.addAttribute("versiones_jurado", versiones);
            return "soyJurado";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/jurado/version/{idVersion}")
    public String verVersionJurado(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("role", usuario.getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            List<Proyecto> proyectos = new ArrayList<>();
            for (Proyecto proyecto : usuario.getProyectosCalificar()) {
                if (!versionService.estaCerrado(proyecto.getVersion())
                        && proyecto.getVersion().getId() == version.getId()) {
                    proyectos.add(proyecto);
                }
            }
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("version", version);
            return "versionJurado";
        } catch (Exception e) {
            return "redirect:/verFeria";
        }
    }

    @GetMapping("/jurado/proyecto/{idProyecto}")
    public String verProyectoJurado(Model model, @PathVariable int idProyecto) {
        try {
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
            return "redirect:/calificaciones";
        } catch (Exception e) {
            return "redirect:/calificaciones";
        }
    }

    @GetMapping("/jurado/proyecto/{idProyecto}/calificar")
    public String calificarProyecto(Model model, @PathVariable int idProyecto) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
        if (proyecto.getJurados().contains(usuario)) {
            // obligatorio para el tema de los menus
            model.addAttribute("username", usuario.getUsername());
            model.addAttribute("role", usuario.getRole().getNombre());
            model.addAttribute("jurado", usuario);
            List<Criterio> criterios = criterioService.obtenerCriterioPorVersion(proyecto.getVersion());
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("criterios", criterios);
            model.addAttribute("service", calificacionService);
            return "calificarProyectos";
        } else {
            return "redirect:/calificaciones";
        }
    }

    @PostMapping("/jurado/proyecto/{idProyecto}/guardar_calificacion")
    public String guardarCalificacion(Model model, @PathVariable int idProyecto,
            @RequestParam("criterioId") List<Integer> criterioIds,
            @RequestParam("valor") List<Integer> valores) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
        // verificamos que el usuario en cuestion pueda calificar dicho proyecto
        if (proyecto.getJurados().contains(usuario)) {
            for (int i = 0; i < criterioIds.size(); i++) {
                Criterio criterio = criterioService.obtenerCriterioPorId(criterioIds.get(i));
                if (criterio != null) {
                    Calificacion calificacion = calificacionService.obtenerCalificacion(proyecto, usuario, criterio);
                    if (calificacion == null) {
                        calificacion = new Calificacion();
                        calificacion.setProyecto(proyecto);
                        calificacion.setJurado(usuario);
                        calificacion.setCriterio(criterio);
                    }
                    calificacion.setProyecto(proyecto);
                    calificacion.setJurado(usuario);
                    calificacion.setCriterio(criterio);
                    calificacion.setValor(valores.get(i));
                    calificacion.setEnabled(true);
                    calificacionService.guardarCalificacion(calificacion);
                }
            }
            // actualizamos la calificacion
            float valorCalificacion = 0.0f;
            for (Calificacion calificacion : calificacionService.obtenerCalificaciones(proyecto, usuario)) {
                valorCalificacion += (float) (calificacion.getValor() * calificacion.getCriterio().getValor()) / 5;
            }
            proyecto.setCalificacion(valorCalificacion / proyecto.getJurados().size());
            proyectoService.guardarProyecto(proyecto);
            return "redirect:/calificaciones/jurado/proyecto/{idProyecto}/calificar?exito";
        } else {
            return "redirect:/calificaciones?error";
        }
    }

}
