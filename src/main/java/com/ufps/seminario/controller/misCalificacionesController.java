package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/ferias_calificables")
public class misCalificacionesController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    FeriaService feriaService;

    @Autowired
    VersionService versionService;

    @Autowired
    ProyectoService proyectoService;


    @GetMapping
    public String listarMisFerias(Model model,
                                  @RequestParam(name = "keyword", required = false) String keyword,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "5") int size) {

        String username = sesionService.getUsernameFromSession();

        List<Feria> ferias = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()).getMisFerias();
        List<Feria> real = new ArrayList<>();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());
        Page<Feria> feriasPagina = feriaService.listarMisFerias(usuario, keyword, page, size);
        for(Feria feria:ferias){
            if(feria.isEnabled()) real.add(feria);
        }

        for(Feria feria:feriasPagina.getContent()){
            System.out.println(feria.getNombre());
        }

        model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
        model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        Collections.reverse(real);
        model.addAttribute("ferias", real);
        //model.addAttribute("keyword", keyword);

        return "feriasCalificacion";

    }

    @GetMapping("/{idFeria}/version") 
    public String verVersionFeria(Model model, @PathVariable int idFeria, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Feria feria = feriaService.obtenerFeria(idFeria);
            List<Version> versiones = versionService.obtenerVersionesPorFeria(feria);
            model.addAttribute("versiones", versiones);
            model.addAttribute("feria", feria);
            return "versionesCalificacion";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al obtener las versiones de esta feria");
            return "redirect:/ferias_calificables";
        }
    }

    @GetMapping("/{idFeria}/version/{idVersion}")
    public String verCalificacionesVersionFeria(Model model,@PathVariable int idFeria, @PathVariable int idVersion, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
            model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            model.addAttribute("version", version);

            List<Proyecto> aprobados = proyectoService.obtenerProyectosAprobados(version);
            List<Proyecto> reprobados = proyectoService.obtenerProyectosReprobados(version);

            if(aprobados.isEmpty()) System.out.println("aprobados nao hay");
            if(reprobados.isEmpty()) System.out.println("reprobados nao hay");

            model.addAttribute("aprobados", aprobados);
            model.addAttribute("reprobados", reprobados);



            return "aprobarProyectos";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al obtener los proyectos de esta version de feria");
            return "redirect:/ferias_calificables/{idFeria}/version";
        }
    }

    @PostMapping("/{idFeria}/version/{idVersion}/aprobar/{idProyecto}")
    public String aprobarProyecto(Model model, @PathVariable int idFeria, @PathVariable int idVersion, @PathVariable int idProyecto, RedirectAttributes redirectAttributes){
        try{
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            proyecto.setEstado(1);
            proyectoService.guardarProyecto(proyecto);

            return "redirect:/ferias_calificables/{idFeria}/version/{idVersion}";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al aprobar este proyecto");
            return "redirect:/ferias_calificables/{idFeria}/version";
        }
    }

    @PostMapping("/{idFeria}/version/{idVersion}/reprobar/{idProyecto}")
    public String reprobarProyecto(@PathVariable int idFeria, @PathVariable int idVersion, @PathVariable int idProyecto, RedirectAttributes redirectAttributes){
        try{
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            proyecto.setEstado(2);
            proyectoService.guardarProyecto(proyecto);

            return "redirect:/ferias_calificables/{idFeria}/version/{idVersion}";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al reprobar este proyecto");
            return "redirect:/ferias_calificables/{idFeria}/version";
        }
    }

}
