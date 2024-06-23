package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.service.CriterioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/criterio")
public class CriterioController {

    @Autowired
    CriterioService criterioService;

    @PostMapping("/{idCriterio}/eliminar")
    public String eliminarCriterio(Model model, @PathVariable int idCriterio, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Formulario enviado exitosamente!");
        String referer = request.getHeader("Referer");
        try {
            Criterio criterio = criterioService.obtenerCriterioPorId(idCriterio);
            criterio.setEnabled(false);
            criterioService.guardarCriterio(criterio);
            redirectAttributes.addFlashAttribute("exito", "Criterio eliminado exitosamente");
            return "redirect:" + referer;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error algo fallo al eliminar");
            return "redirect:" + referer;
        }
    }
}
