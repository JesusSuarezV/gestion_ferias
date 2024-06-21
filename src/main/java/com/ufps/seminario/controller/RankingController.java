package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    SesionService sesionService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    VersionService versionService;

    @Autowired
    ProyectoService proyectoService;

    @GetMapping("/{idVersion}")
    public String verRanking(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", usuarioService.obtenerUsuarioPorUsername(username).getNombre());
                model.addAttribute("apellido", usuarioService.obtenerUsuarioPorUsername(username).getApellido());
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            model.addAttribute("version", version);
            List<Proyecto> proyectos = proyectoService.obtenerProyectosPorVersion(version);
            Collections.sort(proyectos, new Comparator<Proyecto>() {
                @Override
                public int compare(Proyecto p1, Proyecto p2) {
                    return Float.compare(p2.getCalificacion(), p1.getCalificacion());
                }
            });
            model.addAttribute("proyectos", proyectos);
            return "rankingVersionFeria";
        } catch (Exception e) {
            return "redirect:/version/ferias_disponibles";
        }
    }
}
