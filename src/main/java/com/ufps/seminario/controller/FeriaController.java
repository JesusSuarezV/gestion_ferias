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
@RequestMapping("/Ferias")
public class FeriaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    AreaService areaService;

    @Autowired
    FeriaService feriaService;


    @GetMapping("/")
    public String listarFeriasDisponibles(Model model,
                                  @RequestParam(name = "keyword", required = false) String keyword,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "5") int size) {
        String username = sesionService.getUsernameFromSession();

        List<Feria> ferias = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()).getMisFerias();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());

        Page<Feria> feriasPagina = feriaService.listarMisFerias(usuario, keyword, page, size);
        for(Feria feria:ferias){
            System.out.println(feria.getNombre());
        }

        for(Feria feria:feriasPagina.getContent()){
            System.out.println(feria.getNombre());
        }
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

        model.addAttribute("ferias", ferias);
        //model.addAttribute("keyword", keyword);

        return "verMisFerias";

    }

    @GetMapping("/Mis_Ferias")
    public String listarMisFerias(Model model,
                                  @RequestParam(name = "keyword", required = false) String keyword,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "5") int size) {
        String username = sesionService.getUsernameFromSession();

        List<Feria> ferias = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()).getMisFerias();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());
        Page<Feria> feriasPagina = feriaService.listarMisFerias(usuario, keyword, page, size);
        for(Feria feria:ferias){
            System.out.println(feria.getNombre());
        }

        for(Feria feria:feriasPagina.getContent()){
            System.out.println(feria.getNombre());
        }
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

        model.addAttribute("ferias", ferias);
        //model.addAttribute("keyword", keyword);

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
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("role", usuario.getRole().getNombre());

        Feria feria = feriaService.obtenerFeria(id);
        if(usuario.getId() != feria.getCreador().getId()){
            return "redirect:/Ferias/Mis_Ferias";
        }
        model.addAttribute("feria", feria);
        List<Area> areas = areaService.obtenerAreasPorFeria(feria);
        model.addAttribute("areas", areas);
        return "editarFeria";
    }

    @PostMapping("/Guardar_Feria")
    public String crearFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                                 @RequestParam("imagenFeria") MultipartFile imagen) {
        try {
            feria.setImagen(imagen.getBytes());
            feria.setFechaCreacion(LocalDate.now());
            if(requestParams.containsKey("username")){
                feria.setCreador(usuarioService.obtenerUsuarioPorUsername(requestParams.get("username")));
            }else{
                feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));
            }

            Feria feriaCreada = feriaService.guardarFeria(feria);
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String llave = entry.getKey();
                String nombreArea = entry.getValue();
                if (llave.length() > 4 && llave.startsWith("area") && nombreArea != null && !nombreArea.isEmpty()) {
                    Area area  = new Area();
                    area.setFeria(feriaCreada);
                    area.setEnabled(true);
                    area.setNombre(nombreArea);
                    areaService.guardarArea(area);
                }
            }

            return "redirect:/Ferias/Mis_Ferias?exito"; // Redirecciona a una página de éxito
        } catch (Exception e) {

            return "redirect:/Inicio?error";
        }
    }

    @PostMapping("/{id}/Actualizar_Feria")
    public String actualizarFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                                  @RequestParam("imagenFeria") MultipartFile imagen) {
        try {
            feria.setImagen(imagen.getBytes());
            feria.setFechaCreacion(LocalDate.now());
            if(requestParams.containsKey("username")){
                feria.setCreador(usuarioService.obtenerUsuarioPorUsername(requestParams.get("username")));
            }else{
                feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));
            }

            Feria feriaCreada = feriaService.guardarFeria(feria);
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String llave = entry.getKey();
                String nombreArea = entry.getValue();
                if (llave.length() > 4 && llave.startsWith("area") && nombreArea != null && !nombreArea.isEmpty()) {
                    Area area  = new Area();
                    area.setFeria(feriaCreada);
                    area.setEnabled(true);
                    area.setNombre(nombreArea);
                    areaService.guardarArea(area);
                }else if(llave.charAt(0) == '[' && llave.charAt(llave.length()-1) == '['){
                    int idArea = Integer.parseInt(llave.substring(1, llave.length()-1));
                    Area area = areaService.obtenerArea(idArea);
                    area.setNombre(nombreArea);
                    areaService.guardarArea(area);
                }
            }

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

    @GetMapping("/{id}")
    public String verFeria(Model model, @PathVariable int id){
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        model.addAttribute("feria", feriaService.obtenerFeria(id));
        return "verFeria";
    }

}
