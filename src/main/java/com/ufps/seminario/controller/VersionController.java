package com.ufps.seminario.controller;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.service.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/version")
public class VersionController {
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

    @Autowired
    AreaService areaService;

    @Autowired
    IntegranteService integranteService;

    @Autowired
    AuspiciadorService auspiciadorService;

    @Autowired
    CriterioService criterioService;

    @GetMapping("/{idVersion}")
    public String verVersion(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            model.addAttribute("version", version);
            List<Proyecto> proyectos = proyectoService.obtenerProyectosPorVersion(version);
            model.addAttribute("proyectos", proyectos);
            return "verInfoVersion";
        } catch (Exception e) {
            return "redirect:/version/ferias_disponibles";
        }
    }

    @GetMapping("/{idVersion}/registrar")
    public String registrarAVersion(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            model.addAttribute("version", version);
            return "version"; //Luego miro esto
        } catch (Exception e) {
            return "redirect:/version";
        }
    }

    @PostMapping("/{idVersion}/registrar")
    public String registrarAVersion(@ModelAttribute Proyecto proyecto, @PathVariable int idVersion,
                                    @RequestParam Map<String, String> requestParams,
                                    @RequestParam("archivoProyecto") MultipartFile archivoProyecto) {
        try {
            String username = sesionService.getUsernameFromSession();
            Usuario creadorProyecto = usuarioService.obtenerUsuarioPorUsername(username);
            Version version = versionService.obtenerVersion(idVersion);
            //Crear proyecto
            proyecto.setArchivo(archivoProyecto.getBytes());
            proyecto.setVersion(version);
            proyecto.setFechaRegistro(LocalDate.now());
            Proyecto proyectoCreado = proyectoService.guardarProyecto(proyecto);

            //Asignar areas y crear integrantes
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String llave = entry.getKey();
                String nombre = entry.getValue();
                if (nombre != null && !nombre.isEmpty()) {
                    if (llave.startsWith("integrante")) { //Crear Integrante
                        Integrante integrante = new Integrante();
                        integrante.setEnabled(true); //OJO esto toca mirarlo con TOKEN
                        integrante.setCorreoRegistro(nombre);
                        try {
                            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(nombre);
                            integrante.setUsuario(usuario);
                        } catch (Exception e) {
                            //Nada xd
                        }
                        integrante.setProyecto(proyectoCreado);
                        integranteService.guardarIntegrante(integrante);
                    } else if (llave.startsWith("area")) { //Asignar Area
                        int idArea = Integer.parseInt(llave.substring(5, llave.length() - 1));
                        Area area = areaService.obtenerArea(idArea);
                        proyectoCreado.getAreas().add(area);
                    }
                }
            }

            //Agregar creador como integrante
            Integrante integrante = new Integrante();
            integrante.setEnabled(true); //OJO esto toca mirarlo con TOKEN
            integrante.setCorreoRegistro(creadorProyecto.getUsername());
            integrante.setUsuario(creadorProyecto);
            integrante.setProyecto(proyectoCreado);
            integranteService.guardarIntegrante(integrante);

            //Actualizar proyecto
            proyectoService.guardarProyecto(proyectoCreado);

            return "version"; //Luego miro esto
        } catch (Exception e) {
            return "redirect:/version";
        }
    }


    @GetMapping("/ferias_disponibles")
    public String searchFeriasDisponibles(Model model) {
        return "redirect:/version/ferias_disponibles/search";
    }

    @GetMapping("/ferias_disponibles/search")
    public String searchFeriasDisponibles(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesDisponibles(keyword, LocalDate.now());
            model.addAttribute("versiones", versiones);
            return "catalogoDeFerias";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/ferias_inscritas")
    public String verFeriasInscritas(Model model) {
        return "redirect:/version/ferias_inscritas/search";
    }

    @GetMapping("/ferias_inscritas/search")
    public String searchFeriasInscritas(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesPorIntegranteYDisponibles(username, keyword, LocalDate.now());
            model.addAttribute("versiones", versiones);
            return "version"; //Luego miro esto
        } catch (Exception e) {
            return "redirect:/version";
        }
    }


    @GetMapping("/ferias_finalizadas")
    public String searchFeriasFinalizadas(Model model) {
        return "redirect:/version/ferias_finalizadas/search";
    }

    @GetMapping("/ferias_finalizadas/search")
    public String searchFeriasFinalizadas(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            List<Version> versiones = versionService.obtenerVersionesPorIntegranteYCerradas(username, keyword, LocalDate.now());
            model.addAttribute("versiones", versiones);
            return "feriasFinalizadas";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/{idVersion}/editar")
    public String editarVersion(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            Feria feria = version.getFeria();
            model.addAttribute("version", version);
            model.addAttribute("feria", feria);
            List<Auspiciador> auspiciadores = auspiciadorService.obtenerAuspiciadorPorVersion(version);
            model.addAttribute("auspiciadores", auspiciadores);
            return "editarVersionFeria"; //Luego miro esto
        } catch (Exception e) {
            return "redirect:/mis_ferias";
        }
    }

    @PostMapping("/{idVersion}/editar")
    public String editarVersionFeria(Model model, @PathVariable int idVersion, @ModelAttribute Version version,
                                    @RequestParam Map<String, String> requestParams,
                                    @RequestParam("formatoVersion") MultipartFile formatoVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            Feria feria = feriaService.obtenerFeriaByVersion(idVersion);
            version.setArchivo(formatoVersion.getBytes());
            version.setFeria(feria);
            version.setId(idVersion);
            int cantidadDisponible = versionService.obtenerCantidadDisponiblePorFeriayFecha(feria, version.getFechaInicio());
            boolean fechasCorrectas =
                    !version.getFechaInicio().isAfter(version.getFechaLimite())
                            && !version.getFechaLimite().isAfter(version.getFechaCierre());
            if (fechasCorrectas) {
                version.setNumero(versionService.obtenerVersionesPorFeria(feria).size());
                Version versionCreada = versionService.guardarVersion(version);
                for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                    String llave = entry.getKey();
                    String nombre = entry.getValue();
                    if (llave != null && !llave.isEmpty() && nombre != null && !nombre.isEmpty()) {
                        if (llave.startsWith("auspiciadores")) {
                            Auspiciador auspiciador = new Auspiciador();
                            auspiciador.setVersion(versionCreada);
                            auspiciador.setEnabled(true);
                            auspiciador.setNombre(nombre);
                            auspiciadorService.guardarAuspiciador(auspiciador);
                        } else if (llave.startsWith("version_auspiciadores_delete")) {
                            String pref = "version_auspiciadores_delete[";
                            int id = Integer.parseInt(llave.substring(pref.length(), llave.length() - 1));
                            auspiciadorService.eliminarAuspiciadorPorId(id);
                        }
                    }
                }
            } else {
                return "redirect:/version/"+idVersion+"/editar?error";
            }
            return "redirect:/ferias/"+feria.getId()+"/version";
        } catch (Exception e) {
            return "redirect:/version/"+idVersion+"/editar?error";
        }
    }

    @GetMapping("/{idVersion}/rubrica/editar")
    public String editarRubrica(Model model, @PathVariable int idVersion) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

            Version version = versionService.obtenerVersion(idVersion);
            List<Criterio> criterios = criterioService.obtenerCriterioPorVersion(version);
            model.addAttribute("criterios", criterios);
            return "version"; //revisar
        } catch (Exception e) {
            return "lament"; //revisar
        }
    }

    @PostMapping("/{idVersion}/rubrica/editar")
    public String editarRubrica(Model model, @PathVariable int idVersion,
                                @RequestParam Map<String, String> requestParams) {
        try {
            String username = sesionService.getUsernameFromSession();
            model.addAttribute("username", username);
            model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
            Version version = versionService.obtenerVersion(idVersion);
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String llave = entry.getKey();
                String valor = entry.getKey();
                if (llave != null && !llave.isEmpty()) {
                    String prefijoViejos = "criterio_item[";
                    String prefijoNuevos = "item[";

                    String prefNivel = "criterio_nivel[";


                    if (llave.startsWith(prefijoViejos)) {
                        int idCriterio = Integer.parseInt(llave.substring(prefijoViejos.length(), llave.length() - 1));

                        String llaveNivel = "criterio_nivel["
                                + Integer.toString(idCriterio) + "]";
                        int valorNivel = Integer.parseInt(requestParams.get(llaveNivel));

                        Criterio criterio = criterioService.obtenerCriterioPorId(idCriterio);
                        criterio.setNombre(valor);
                        criterio.setValor(valorNivel);
                        criterio.setEnabled(true);
                        criterio.setVersion(version);
                        criterioService.guardarCriterio(criterio);
                    } else if (llave.startsWith(prefijoNuevos)) {
                        int idCriterio = Integer.parseInt(llave.substring(prefijoNuevos.length(), llave.length() - 1));

                        String llaveNivel = "nivel["
                                + idCriterio + "]";
                        int valorNivel = Integer.parseInt(requestParams.get(llaveNivel));

                        Criterio criterio = new Criterio();
                        criterio.setNombre(valor);
                        criterio.setValor(valorNivel);
                        criterio.setEnabled(true);
                        criterio.setVersion(version);
                        criterioService.guardarCriterio(criterio);
                    }
                }
            }
            return "version"; //revisar
        } catch (Exception e) {
            return "lament"; //revisar
        }
    }

    @PostMapping("/{idVersion}/cerrar")
    public String cerrarVersion(Model model, @PathVariable int idVersion){
        try{
            Version version = versionService.obtenerVersion(idVersion);
            version.setCierre(true);
            versionService.guardarVersion(version);
            return "redirect:/ferias/"+version.getFeria().getId()+"/version";
        }catch(Exception e){
            return "redirect:/ferias/mis_ferias?error";
        }
    }

    @PostMapping("/{idVersion}/eliminar")
    public String eliminarVersion(Model model, @PathVariable int idVersion){
        try{
            Version version = versionService.obtenerVersion(idVersion);
            versionService.ocultarVersion(idVersion);
            return "redirect:/ferias/"+version.getFeria().getId()+"/version";
        }catch(Exception e){
            return "redirect:/ferias/mis_ferias?error";
        }
    }

}
