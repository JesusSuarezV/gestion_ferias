package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ferias")
public class FeriaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    AreaService areaService;

    @Autowired
    FeriaService feriaService;

    @Autowired
    VersionService versionService;

    @Autowired
    AuspiciadorService auspiciadorService;

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

    @GetMapping("/mis_ferias")
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
        Collections.reverse(ferias);
        model.addAttribute("ferias", ferias);
        //model.addAttribute("keyword", keyword);

        return "verMisFerias";

    }

    @GetMapping("/crear")
    public String crearFeria(Model model) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "crearFeria";
    }

    @GetMapping("/{id}/editar")
    public String editarFeria(Model model, @PathVariable int id) {
        try{
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
        }catch (Exception e){
            return "redirect:/mis_ferias";
        }
    }

    @PostMapping("/crear")
    public String crearFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                                 @RequestParam("imagenFeria") MultipartFile imagen) {
        try {
            feria.setImagen(imagen.getBytes());
            feria.setFechaCreacion(LocalDate.now());
            feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));
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

    @PostMapping("/{id}/editar")
    public String editarFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                                  @RequestParam("imagenFeria") MultipartFile imagen) {
        try {
            feria.setImagen(imagen.getBytes());
            feria.setFechaCreacion(LocalDate.now()); //ESTA FECHA IMPORTA PARA ALGO?????
            feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));

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
                }else if(llave.startsWith("feria_area_delete")){
                    String pref = "feria_area_delete[";
                    int idArea = Integer.parseInt(llave.substring(pref.length(),llave.length()-1));
                    areaService.desactivarAreaPorId(idArea);
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

    @GetMapping("/{idFeria}/version")
    public String verVersionFeria(Model model, @PathVariable int idFeria){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesPorFeria(feriaService.obtenerFeria(idFeria));
            model.addAttribute("versiones", versiones);
            return "verFeria";
        }catch(Exception e){
            return "redirect:/version";
        }
    }

    @GetMapping("/{idFeria}/version/crear")
    public String crearVersionFeria(Model model, @PathVariable int idFeria){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            model.addAttribute("feria", feriaService.obtenerFeria(idFeria));
            return "verFeria";
        }catch(Exception e){
            return "redirect:/version";
        }
    }

    @PostMapping("/{idFeria}/version/crear")
    public String crearVersionFeria(Model model, @PathVariable int idFeria, @ModelAttribute Version version,
                                    @RequestParam Map<String, String> requestParams,
                                    @RequestParam("formatoVersion") MultipartFile formatoVersion){
        try{
            String username = sesionService.getUsernameFromSession();
            Feria feria = feriaService.obtenerFeria(idFeria);
            version.setArchivo(formatoVersion.getBytes());
            version.setFeria(feria);
            version.setEnabled(true);
            int cantidadDisponible= versionService.obtenerCantidadDisponiblePorFeriayFecha(feria, LocalDate.now());
            if(cantidadDisponible == 0){
                version.setNumero(versionService.obtenerVersionesPorFeria(feria).size()+1);
                Version versionCreada = versionService.guardarVersion(version);
                for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                    String llave = entry.getKey();
                    String nombre = entry.getValue();
                    if(llave != null && !llave.isEmpty() && nombre != null && !nombre.isEmpty()){
                        if(llave.startsWith("auspiciadores")){
                            Auspiciador auspiciador = new Auspiciador();
                            auspiciador.setVersion(versionCreada);
                            auspiciador.setEnabled(true);
                            auspiciador.setNombre(nombre);
                            auspiciadorService.guardarAuspiciador(auspiciador);
                        }
                    }
                }
            }else{
                return "error y pantalla versiones";
            }
            return "verFeria";
        }catch(Exception e){
            return "redirect:/version";
        }
    }
}
