package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Area;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.service.FeriaService;
import com.ufps.seminario.service.SesionService;
import com.ufps.seminario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ufps.seminario.service.AreaService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/views")
public class ViewsController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @GetMapping("/menu")
    public String verMenu(Model model) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "menu";
    }

    @GetMapping("/navbar")
    public String verNavbar(Model model) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "navbar";
    }


}
