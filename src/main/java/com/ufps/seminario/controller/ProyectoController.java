package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/proyecto")
public class ProyectoController {

    @Autowired
    SesionService sesionService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ProyectoService proyectoService;

    @Autowired
    IntegranteService integranteService;

    @Autowired
    AreaService areaService;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    TokenIntegranteService tokenIntegranteService;

    @GetMapping("/mis_proyectos")
    public String verMisProyectos(Model model, RedirectAttributes redirectAttributes) {
        try {
            String username = sesionService.getUsernameFromSession();
            List<Proyecto> proyectosActuales = proyectoService.obtenerProyectosActualesPorCorreo(username);
            List<Proyecto> proyectosPasados = proyectoService.obtenerProyectosPasadosPorCorreo(username);

            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

            model.addAttribute("proyectosActuales", proyectosActuales);
            model.addAttribute("proyectosPasados", proyectosPasados);

            return "misProyectos";
        }catch (Exception e){
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Algo falló en la consulta de proyectos");
            return "redirect:/";
        }
    }

    @GetMapping("/{idProyecto}/editar")
    public String editarProyecto(Model model, @PathVariable int idProyecto, RedirectAttributes redirectAttributes){
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);

            if(!integranteService.esIntegrante(proyecto, username)){
                throw new RuntimeException("No es integrante del proyecto");
            }

            model.addAttribute("proyecto", proyecto);
            List<Integrante> integrantes = integranteService.obtenerIntegrantePorProyecto(proyecto);
            List<Area> areas = proyecto.getAreas();
            List<Area> areasVersion = areaService.obtenerAreasPorFeria(proyecto.getVersion().getFeria());
            model.addAttribute("integrantes", integrantes);
            model.addAttribute("areas", areas);
            model.addAttribute("areasVersion", areasVersion);

            return "editarProyecto";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Feria eliminada exitosamente");
            return "redirect:/mis_proyectos";
        }
    }

    @PostMapping("/{idProyecto}/editar")
    public String editarProyecto(@ModelAttribute Proyecto proyecto, @PathVariable int idProyecto,
                                 @RequestParam Map<String, String> requestParams,
                                 @RequestParam("archivoProyecto") MultipartFile archivoProyecto,
                                 RedirectAttributes redirectAttributes){
        try {

            String username = sesionService.getUsernameFromSession();
            Usuario creadorProyecto = usuarioService.obtenerUsuarioPorUsername(username);
            Proyecto proyectoOriginal = proyectoService.obtenerProyectoPorId(idProyecto);

            if(!integranteService.esIntegrante(proyectoOriginal, username)){
                throw new RuntimeException("No es integrante del proyecto");
            }

            proyecto.setVersion(proyectoOriginal.getVersion());
            if(archivoProyecto != null && archivoProyecto.getBytes().length>0){
                String fileUrl = firebaseService.uploadFile(archivoProyecto);
                proyecto.setArchivoUrl(fileUrl);
            }
            else{
                proyecto.setArchivoUrl(proyectoOriginal.getArchivoUrl());
            }
            proyecto.setId(idProyecto);
            proyecto.setFechaRegistro(proyectoOriginal.getFechaRegistro());
            proyecto.setJurados(proyectoOriginal.getJurados());
            proyecto.setAreas(proyectoOriginal.getAreas());
            proyecto.setCalificacion(proyectoOriginal.getCalificacion());
            proyecto.setEnabled(proyectoOriginal.isEnabled());
            proyecto.setEstado(proyectoOriginal.isEstado());
            proyecto = proyectoService.guardarProyecto(proyecto);

            //Asignar areas y crear integrantes
            List<Integrante> integrantesEliminados = new ArrayList<>();
            List<Area> areasEliminadas = new ArrayList<>();
            List<Integrante> integrantesNuevos = new ArrayList<>();
            List<Area> areasNuevas = new ArrayList<>();
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String llave = entry.getKey();
                String nombre = entry.getValue();
                if (nombre != null && !nombre.isEmpty()) {
                    if(llave.startsWith("integrante")){ //Crear Integrante
                        Integrante integrante = new Integrante();
                        integrante.setEnabled(false);
                        integrante.setCorreoRegistro(nombre);
                        try{
                            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(nombre);
                            integrante.setUsuario(usuario);
                        }catch(Exception e){
                            //Nada xd
                        }
                        integrante.setProyecto(proyecto);
                        integrantesNuevos.add(integrante);
                    }else if(llave.startsWith("area")){ //Asignar Area
                        String pref = "area[";
                        int idArea = Integer.parseInt(llave.substring(pref.length(), llave.length()-1));
                        Area area = areaService.obtenerArea(idArea);
                        areasNuevas.add(area);
                    }else {
                        if(llave.startsWith("proyecto_area_delete")){
                            String pref = "proyecto_area_delete[";
                            int i = Integer.parseInt(llave.substring(pref.length(), llave.length() - 1));
                            Area area = areaService.obtenerArea(i);
                            areasEliminadas.add(area);
                        }else if(llave.startsWith("proyecto_integrante_delete")){
                            String pref = "proyecto_integrante_delete[";
                            int i = Integer.parseInt(llave.substring(pref.length(), llave.length() - 1));
                            Integrante integrante = integranteService.obtenerIntegrantePorId(i);
                            integrantesEliminados.add(integrante);
                        }
                    }
                }
            }

            //Eliminar areas e integrantes
            for(Area area: areasEliminadas){
                proyecto.getAreas().remove(area);
            }
            for(Integrante integrante: integrantesEliminados){
                if(!creadorProyecto.getUsername().equals(integrante.getCorreoRegistro())){
                    integranteService.eliminarIntegrante(integrante);
                }
            }

            //Agregar areas e integrantes
            for(Area area: areasNuevas){
                proyecto.getAreas().add(area);
            }
            for(Integrante integrante: integrantesNuevos){
                tokenIntegranteService.generarToken(integranteService.guardarIntegrante(integrante));
            }

            //Actualizar proyecto
            proyectoService.guardarProyecto(proyecto);
            redirectAttributes.addFlashAttribute("exito", "Proyecto editado :D");
            return "redirect:/proyecto/mis_proyectos";
        }catch (Exception e){
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Hubo un error al editar proyecto");
            return "redirect:/proyecto/mis_proyectos";
        }
    }

    @PostMapping("/{idProyecto}/eliminar")
    public String eliminarProyecto(Model model, @PathVariable int idProyecto, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);

            if(!integranteService.esIntegrante(proyecto, username)){
                throw new RuntimeException("No es integrante del proyecto");
            }

            proyecto.setEnabled(false);
            proyectoService.guardarProyecto(proyecto);
            redirectAttributes.addFlashAttribute("exito", "Proyecto eliminado");
            return "redirect:/proyecto/mis_proyectos";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Proyecto no pudo ser eliminado");
            return "redirect:/proyecto/mis_proyectos";
        }
    }

    @GetMapping("/{idProyecto}")
    public String verProyecto(Model model, @PathVariable int idProyecto, RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
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
                    sesionService.getUsernameFromSession()
            );


            model.addAttribute("creador", creador);

            boolean ok = !proyecto.getVersion().isCierre() && proyecto.getVersion().isEnabled()
            && !LocalDate.now().isAfter(proyecto.getVersion().getFechaCierre()) && proyecto.getVersion().getFeria().isEnabled();
            model.addAttribute("asignacion", ok);
            return "verInformacionProyecto";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al cargar el proyecto, raro");
            System.out.println(e.getMessage());
            return "redirect:/proyecto/mis_proyectos";
        }
    }

    @PostMapping("/{idProyecto}/jurado")
    public String agregarJurado(Model model, @PathVariable int idProyecto,
                                @RequestParam Map<String, String> reqParams,
                                RedirectAttributes redirectAttributes){
        try{
            Usuario jurado = usuarioService.obtenerUsuarioPorUsername(reqParams.get("juradoCorreo"));
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);

            boolean contains = proyecto.getIntegrantes().stream()
                    .anyMatch(integrante -> integrante.getUsuario() == jurado);

            if (contains) {
                redirectAttributes.addFlashAttribute("error", "Un miembro del proyecto NO puede ser asignado como jurado");
                return "redirect:/proyecto/"+idProyecto+"?error";
            } else {
                proyecto.getJurados().add(jurado);
                proyectoService.guardarProyecto(proyecto);
                redirectAttributes.addFlashAttribute("exito", "Jurado agregado exitosamente");
                return "redirect:/proyecto/"+idProyecto+"?exito";
            }



        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "No se pudó agregar al jurado, tal vez no esta registrado en el sistema");
            return "redirect:/proyecto/"+idProyecto+"?error";
        }

    }

    @PostMapping("/{idProyecto}/jurado/eliminar")
    public String eliminarJurado(Model model, @PathVariable int idProyecto,
                                @RequestParam Map<String, String> reqParams,
                                 RedirectAttributes redirectAttributes){
        try{
            String username = sesionService.getUsernameFromSession();

            Usuario jurado = usuarioService.obtenerUsuarioPorUsername(reqParams.get("juradoCorreo"));
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);

            if(!proyecto.getVersion().getFeria().getCreador().getUsername().equals(username)){
                throw new RuntimeException("No es creador de esta feria");
            }
            proyecto.getJurados().remove(jurado);
            proyectoService.guardarProyecto(proyecto);
            redirectAttributes.addFlashAttribute("exito", "Jurado eliminado exitosamente");
            return "redirect:/proyecto/"+idProyecto+"?exito";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("error", "Algo falló al eliminar el jurado");
            return "redirect:/proyecto/"+idProyecto+"?error";
        }
    }

    @GetMapping("/archivo/{idProyecto}")
    public ResponseEntity<ByteArrayResource> descargarPdf(@PathVariable int idProyecto) {
        byte[] pdfData = proyectoService.obtenerProyectoPorId(idProyecto).getArchivo();

        if (pdfData == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(pdfData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=document.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfData.length)
                .body(resource);
    }
}
