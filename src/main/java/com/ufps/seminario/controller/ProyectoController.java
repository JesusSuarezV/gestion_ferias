package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/mis_proyectos")
    public String verVersion(Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            List<Proyecto> proyectosActuales = proyectoService.obtenerProyectosActualesPorCorreo(username);
            List<Proyecto> proyectosPasados = proyectoService.obtenerProyectosPasadosPorCorreo(username);

            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

            model.addAttribute("proyectosActuales", proyectosActuales);
            model.addAttribute("proyectosPasados", proyectosPasados);

            return "version"; //Luego miro esto
        }catch (Exception e){
            return "redirect:/version";
        }
    }

    @GetMapping("/{idProyecto}/editar")
    public String editarProyecto(Model model, @PathVariable int idProyecto){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            model.addAttribute("proyecto", proyecto);
            List<Integrante> integrantes = integranteService.obtenerIntegrantePorProyecto(proyecto);
            List<Area> areas = proyecto.getAreas();
            model.addAttribute("integrantes", integrantes);
            model.addAttribute("areas", areas);
            return "version";
        }catch(Exception e){
            return "redirect/mis_proyectos";
        }
    }

    @PostMapping("/{idProyecto}/editar")
    public String editarProyecto(@ModelAttribute Proyecto proyecto, @PathVariable int idProyecto,
                                 @RequestParam Map<String, String> requestParams,
                                 @RequestParam("archivoProyecto") MultipartFile archivoProyecto){
        try {

            String username = sesionService.getUsernameFromSession();
            Usuario creadorProyecto = usuarioService.obtenerUsuarioPorUsername(username);
            proyecto.setArchivo(archivoProyecto.getBytes());
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
                        integrante.setEnabled(true); //OJO esto toca mirarlo con TOKEN
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
                        int idArea = Integer.parseInt(llave.substring(5, llave.length()-1));
                        Area area = areaService.obtenerArea(idArea);
                        areasNuevas.add(area);
                    }else {
                        int i = Integer.parseInt(llave.substring(llave.length() - 3, llave.length() - 1));
                        if(llave.startsWith("proyecto_area_delete")){
                            Area area = areaService.obtenerArea(i);
                            areasEliminadas.add(area);
                        }else if(llave.startsWith("proyecto_integrante_delete")){
                            Integrante integrante = integranteService.obtenerIntegrantePorId(i);
                            integrantesEliminados.add(integrante);
                        }
                    }
                }
            }

            //Eliminar ares e integrantes
            for(Area area: areasEliminadas){
                proyecto.getAreas().remove(area); //ESTO NECESITA EL EQUALS NO?
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
                integranteService.guardarIntegrante(integrante);
            }

            //Actualizar proyecto
            proyectoService.guardarProyecto(proyecto);

            return "version"; //Luego miro esto
        }catch (Exception e){
            return "redirect:/version";
        }
    }

    @GetMapping("/{idProyecto}")
    public String verProyecto(Model model, @PathVariable int idProyecto){
        try{
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("jurados", proyecto.getJurados());
            return "version";
        }catch(Exception e){
            return "redirect/mis_proyectos";
        }
    }

    @PostMapping("/{idProyecto}/jurado")
    public String agregarJurado(Model model, @PathVariable int idProyecto,
                                @RequestParam Map<String, String> reqParams){
        try{
            Usuario jurado = usuarioService.obtenerUsuarioPorUsername(reqParams.get("juradoCorreo"));
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
            proyecto.getJurados().add(jurado);
            proyectoService.guardarProyecto(proyecto);
            return "takeMyHand";
        }catch(Exception e){
            return "exposingTheAngels";
        }

    }
}
