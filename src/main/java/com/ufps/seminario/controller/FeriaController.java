package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
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

    @Autowired
    FirebaseService firebaseService;

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

//        for(Feria feria:feriasPagina.getContent()){
//            System.out.println(feria.getNombre());
//        }
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
        List<Feria> real = new ArrayList<>();
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession());
        Page<Feria> feriasPagina = feriaService.listarMisFerias(usuario, keyword, page, size);
        for(Feria feria:ferias){
            if(feria.isEnabled()) real.add(feria);
        }

        for(Feria feria:feriasPagina.getContent()){
            System.out.println(feria.getNombre());
        }

        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        Collections.reverse(real);
        model.addAttribute("ferias", real);
        //model.addAttribute("keyword", keyword);

        return "feriasCreadas";

    }

    @GetMapping("/crear")
    public String crearFeria(Model model) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        return "crearFeria";
    }

    @GetMapping("/{id}/editar")
    public String editarFeria(Model model, @PathVariable int id, RedirectAttributes redirectAttributes) {
        try{
            String username = sesionService.getUsernameFromSession();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            model.addAttribute("username", username);
            model.addAttribute("role", usuario.getRole().getNombre());

            Feria feria = feriaService.obtenerFeria(id);
            if(usuario.getId() != feria.getCreador().getId()){
                redirectAttributes.addFlashAttribute("error", "Algo falló");
                return "redirect:/ferias/mis_ferias";
            }
            model.addAttribute("feria", feria);
            List<Area> areas = areaService.obtenerAreasPorFeria(feria);
            model.addAttribute("areas", areas);
            return "editarFeria";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló");
            return "redirect:/mis_ferias";
        }
    }

    @PostMapping("/crear")
    public String crearFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                             @RequestParam("imagenFeria") MultipartFile imagen, RedirectAttributes redirectAttributes) throws IOException {
        try {
            String imageUrl = firebaseService.uploadFile(imagen);
            System.out.println(imageUrl);
            feria.setImagenUrl(imageUrl);
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
            redirectAttributes.addFlashAttribute("exito", "Feria creada exitosamente");
            return "redirect:/ferias/mis_ferias";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Algo falló en la creación");
            return "redirect:/ferias/mis_ferias?error";
        }
    }

    @PostMapping("/{idFeria}/editar")
    public String editarFeria(@ModelAttribute Feria feria, @RequestParam Map<String, String> requestParams,
                                  @RequestParam("imagenFeria") MultipartFile imagen, @PathVariable int idFeria,
                              RedirectAttributes redirectAttributes) {
        try {
            if(imagen != null && imagen.getBytes().length > 0){
                String imageUrl = firebaseService.uploadFile(imagen);
                feria.setImagenUrl(imageUrl);
            }
            else {
                Feria oldFeria = feriaService.obtenerFeria(idFeria);
                feria.setImagenUrl(oldFeria.getImagenUrl());
            }
            feria.setFechaCreacion(LocalDate.now());
            feria.setCreador(usuarioService.obtenerUsuarioPorUsername(sesionService.getUsernameFromSession()));
            feria.setId(idFeria);
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
            redirectAttributes.addFlashAttribute("exito", "Feria editada exitosamente");
            return "redirect:/ferias/mis_ferias";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Algo falló en la edición");
            return "redirect:/ferias/mis_ferias";
        }
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
    public String verVersionFeria(Model model, @PathVariable int idFeria, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Feria feria = feriaService.obtenerFeria(idFeria);
            List<Version> versiones = versionService.obtenerVersionesPorFeria(feria);
            model.addAttribute("versiones", versiones);
            model.addAttribute("feria", feria);
            return "versionFeriasCreadas";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al obtener las versiones de esta feria");
            return "redirect:/ferias/mis_ferias";
        }
    }

    @GetMapping("/{idFeria}/version/crear")
    public String crearVersionFeria(Model model, @PathVariable int idFeria, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            model.addAttribute("feria", feriaService.obtenerFeria(idFeria));
            return "crearVersionFeria";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló, intentelo de nuevo más tarde");
            return "redirect:/ferias/"+idFeria+"/version";
        }
    }

    @PostMapping("/{idFeria}/version/crear")
    public String crearVersionFeria(Model model, @PathVariable int idFeria, @ModelAttribute Version version,
                                    @RequestParam Map<String, String> requestParams,
                                    @RequestParam("formatoVersion") MultipartFile formatoVersion,
                                    RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            Feria feria = feriaService.obtenerFeria(idFeria);

            String urlFile = firebaseService.uploadFile(formatoVersion);
            version.setArchivoUrl(urlFile);

            version.setArchivo(formatoVersion.getBytes());
            version.setFeria(feria);
            version.setEnabled(true);
            int cantidadDisponible= versionService.obtenerCantidadDisponiblePorFeriayFecha(feria, LocalDate.now());
            boolean fechasCorrectas =
                    !version.getFechaInicio().isAfter(version.getFechaLimite())
                && !version.getFechaLimite().isAfter(version.getFechaCierre());

            if(cantidadDisponible == 0 && fechasCorrectas){
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
                if(cantidadDisponible != 0) redirectAttributes.addFlashAttribute("error", "Algo falló: Asegurese de que no existen versiones de esta feria abiertas");
                else redirectAttributes.addFlashAttribute("error", "Fechas inconsistentes");
                return "redirect:/ferias/"+idFeria+"/version/crear";
            }
            redirectAttributes.addFlashAttribute("exito", "Versión creada exitosamente");
            return "redirect:/ferias/"+idFeria+"/version";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló en la creación");
            return "redirect:/ferias/"+idFeria+"/version/crear";
        }
    }

    @PostMapping("/{idFeria}/eliminar")
    public String eliminarFeria(Model model, @PathVariable int idFeria, RedirectAttributes redirectAttributes){
        try{
            feriaService.ocultarFeria(idFeria);
            List<Version> versiones = versionService.obtenerVersionesPorFeria(feriaService.obtenerFeria(idFeria));
            for(Version version: versiones){
                version.setEnabled(false);
                version.setCierre(true);
                versionService.guardarVersion(version);
            }
            redirectAttributes.addFlashAttribute("exito", "Feria eliminada exitosamente");
            return "redirect:/ferias/mis_ferias";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló en la eliminación");
            return "redirect:/ferias/mis_ferias";
        }
    }

    @GetMapping("/imagen/{idFeria}")
    public ResponseEntity<byte[]> obtenerImagenFeria(@PathVariable int idFeria){
        byte[] imageBytes = feriaService.obtenerFeria(idFeria).getImagen();

        if (imageBytes == null || imageBytes.length == 0) {
            imageBytes = obtenerImagenPredeterminada();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg"); // Cambia a "image/png" o el tipo adecuado si es necesario
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    private byte[] obtenerImagenPredeterminada() {
        try {
            Resource resource = new ClassPathResource("static/img/img1.jpeg"); // Ruta a tu imagen predeterminada
            return Files.readAllBytes(resource.getFile().toPath());
        } catch (Exception e) {
            return new byte[0];
        }
    }

}
