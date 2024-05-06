package com.ufps.seminario.controller;

import com.ufps.seminario.service.VersionService;
import org.apache.commons.io.FilenameUtils;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.service.FeriaService;
import com.ufps.seminario.service.SesionService;
import com.ufps.seminario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/Ferias/{idFeria}/Versiones")
public class VersionController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    SesionService sesionService;

    @Autowired
    FeriaService feriaService;

    @Autowired
    VersionService versionService;

    @GetMapping()
    public String verVersiones(Model model, @PathVariable int idFeria, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "1") int page) {
        String username = sesionService.getUsernameFromSession();
        Feria feria = feriaService.obtenerFeria(idFeria);

        Page<Version> versiones = versionService.listarVersiones(feria, PageRequest.of(page - 1, 3));

        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        model.addAttribute("feria", feria);
        model.addAttribute("versiones", versiones);

        return "verVersiones";

    }

    @GetMapping("/Crear_Version")
    public String crearVersion(Model model, @PathVariable int idFeria) {


        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());
        model.addAttribute(feriaService.obtenerFeria(idFeria));
        return "crearVersion";
    }

    @GetMapping("/{id}/Editar_Version")
    public String editarFeria(Model model, @PathVariable int id) {
        String username = sesionService.getUsernameFromSession();
        model.addAttribute("username", username);
        model.addAttribute("role", usuarioService.obtenerUsuarioPorUsername(username).getRole().getNombre());

        model.addAttribute("version", versionService.obtenerVersion(id));
        return "editarVersion";
    }

    @PostMapping("/Guardar_Version")
    public String guardarVersion(@PathVariable int idFeria, @RequestParam("versionEvento") int versionEvento,
                                 @RequestParam("puntaje") int puntaje,
                                 @RequestParam("fechaInicio") LocalDate fechaInicio,
                                 @RequestParam("fechaLimite") LocalDate fechaLimite,
                                 @RequestParam("fechaCierre") LocalDate fechaCierre,
                                 @RequestParam("formatoProyecto") MultipartFile formatoProyecto) {
        try {
            Version version = new Version();

            version.setFeria(feriaService.obtenerFeria(idFeria));
            version.setNumero(versionEvento);
            version.setAprobacion(puntaje);
            version.setArchivoConentType(FilenameUtils.getExtension(formatoProyecto.getOriginalFilename()));
            version.setArchivo(formatoProyecto.getBytes());
            version.setFechaInicio(fechaInicio);
            version.setFechaCierre(fechaLimite);
            version.setFechaLimite(fechaCierre);
            version.setCierre(false);
            versionService.guardarVersion(version);


            return "redirect:/Ferias/{idFeria}/Versiones";
        } catch (Exception e) {

            return "redirect:/Inicio?error";
        }


    }

    @PostMapping("/{id}/Actualizar_Version")
    public String actualizarVersion(@PathVariable int id, @RequestParam("versionEvento") int versionEvento,
                                  @RequestParam("puntaje") int puntaje,
                                  @RequestParam("fechaInicio") LocalDate fechaInicio,
                                  @RequestParam("fechaLimite") LocalDate fechaLimite,
                                  @RequestParam("fechaCierre") LocalDate fechaCierre,
                                  @RequestParam("formatoProyecto") MultipartFile formatoProyecto) {
        try {
            Version version = versionService.obtenerVersion(id);
            version.setNumero(versionEvento);
            version.setAprobacion(puntaje);
            version.setFechaInicio(fechaInicio);
            version.setFechaLimite(fechaLimite);
            version.setFechaCierre(fechaCierre);


            if (formatoProyecto != null && !formatoProyecto.isEmpty()) {

                version.setArchivoConentType(FilenameUtils.getExtension(formatoProyecto.getOriginalFilename()));
                version.setArchivo(formatoProyecto.getBytes());
            }

            versionService.guardarVersion(version);

            return "redirect:/Ferias/{idFeria}/Versiones";
        } catch (Exception e) {

            return "redirect:/Inicio?error";
        }
    }

    @GetMapping("/{id}/Eliminar_Version")
    public String eliminarVersion(@PathVariable int id){
        versionService.ocultarVersion(id);
        return "redirect:/Ferias/{idFeria}/Versiones";
    }
}
