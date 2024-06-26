package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
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
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
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
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
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
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
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
            model.addAttribute("username", usuario.getNombre());
            model.addAttribute("apellido", usuario.getApellido());
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
                //comprobamos si el usuario ya califico este criterio
                Criterio criterio = criterioService.obtenerCriterioPorId(criterioIds.get(i));
                Calificacion calificacion = calificacionService.obtenerCalificacion(proyecto, usuario, criterio);
                if (calificacion != null) {
                    calificacion.setValor(valores.get(i));
                    calificacionService.guardarCalificacion(calificacion);
                } else {
                    Calificacion calificacion1 = new Calificacion();
                    calificacion1.setProyecto(proyecto);
                    calificacion1.setJurado(usuario);
                    calificacion1.setCriterio(criterio);
                    calificacion1.setValor(valores.get(i));
                    calificacion1.setEnabled(true);
                    calificacionService.guardarCalificacion(calificacion1);
                }
            }

            // actualizamos la calificacion
            float valorCalificacion = 0.0f;
            List<Calificacion> calificaciones = calificacionService.obtenerCalificaciones(proyecto, usuario);
            for (Calificacion calificacion : calificaciones) {
                if (calificacion.isEnabled() && calificacion.getCriterio().isEnabled()) {
                    valorCalificacion += (float) (calificacion.getValor() * calificacion.getCriterio().getValor()) / 5;
                }
            }
            float puntaje = (valorCalificacion / ((float) calificaciones.size() / criterioIds.size()));
            proyecto.setCalificacion((float) (Math.round(puntaje * 100.0) / 100.0));
            proyectoService.guardarProyecto(proyecto);
            return "redirect:/calificaciones/jurado/version/" + proyecto.getVersion().getId()
                    ;
        } else {
            return "redirect:/calificaciones?error";
        }
    }

    @GetMapping("/jurado/proyecto/{idProyecto}/ver")
    public String verProyecto(Model model, @PathVariable int idProyecto, RedirectAttributes redirectAttributes) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);

            List<Integrante> integrantes = integranteService.obtenerIntegrantePorProyecto(proyecto);
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("jurados", proyecto.getJurados());
            model.addAttribute("areas", proyecto.getAreas());
            model.addAttribute("integrantes", integrantes);
            model.addAttribute("rolObj", usuarioService.obtenerUsuarioPorUsername(username).getRole());

            boolean creador = Objects.equals(
                    proyecto.getVersion().getFeria().getCreador().getUsername(),
                    sesionService.getUsernameFromSession());

            model.addAttribute("creador", creador);

            boolean ok = !proyecto.getVersion().isCierre() && proyecto.getVersion().isEnabled()
                    && !LocalDate.now().isAfter(proyecto.getVersion().getFechaCierre())
                    && proyecto.getVersion().getFeria().isEnabled();
            model.addAttribute("asignacion", ok);
            return "proyectosPorCalificar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Algo falló al cargar el proyecto, raro");
            System.out.println(e.getMessage());
            return "redirect:/proyecto/mis_proyectos";
        }
    }

}
