package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Feria;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Ferias")
public class FeriaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    FeriaService feriaService;

    @GetMapping("/Mis_Ferias")
    public String listarMisFerias(Model model, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "1") int page) {
        String username = sesionService.getUsernameFromSession();

        List<Feria> ferias = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()).getMisFerias();
        Page<Feria> ferias2 = feriaService.listarMisFerias( usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()), keyword, PageRequest.of(page - 1, 3));
        for(Feria feria:ferias){
            System.out.println(feria.getNombre());
        }

        for(Feria feria:ferias2.getContent()){
            System.out.println(feria.getNombre());
        }
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

        model.addAttribute("ferias", ferias2);

        return "verMisFerias";

    }

    @GetMapping("/Crear_Feria")
    public String crearFeria(Model model) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "crearFeria";
    }

    @GetMapping("/{id}/Editar_Feria")
    public String editarFeria(Model model, @PathVariable int id) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        model.addAttribute("feria", feriaService.obtenerFeria(id));
        return "editarFeria";
    }

    @PostMapping("/Guardar_Feria")
    public String crearFeria(@RequestParam("nombreEvento") String nombreEvento,
                             @RequestParam("descripcionEvento") String descripcionEvento,
                             @RequestParam("imagenEvento") MultipartFile imagenEvento) {
        try {
            Feria feria = new Feria();
            feria.setNombre(nombreEvento);
            feria.setDescripcion(descripcionEvento);
            feria.setImagen(imagenEvento.getBytes());
            feria.setFechaCreacion(LocalDate.now());
            feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));

            feriaService.guardarFeria(feria);

            return "redirect:/Ferias/Mis_Ferias?exito"; // Redirecciona a una página de éxito
        } catch (Exception e) {

            return "redirect:/Inicio?error";
        }
    }

    @PostMapping("/{id}/Actualizar_Feria")
    public String actualizarFeria(@PathVariable int id, @RequestParam("nombreEvento") String nombreEvento,
                                  @RequestParam("descripcionEvento") String descripcionEvento,
                                  @RequestParam("imagenEvento") MultipartFile imagenEvento) {
        try {
            Feria feria = feriaService.obtenerFeria(id);
            feria.setNombre(nombreEvento);
            feria.setDescripcion(descripcionEvento);
            if (imagenEvento != null && !imagenEvento.isEmpty()) {
                feria.setImagen(imagenEvento.getBytes());
            }

            feriaService.guardarFeria(feria);

            return "redirect:/Ferias/Mis_Ferias?exito"; // Redirecciona a una página de éxito
        } catch (Exception e) {

            return "redirect:/Inicio?error";
        }
    }

    @GetMapping("/{id}/Elminar_Feria")
    public String eliminarFeria(@PathVariable int id){
        feriaService.ocultarFeria(id);
        return "redirect:/Ferias/Mis_Ferias";
    }



}
