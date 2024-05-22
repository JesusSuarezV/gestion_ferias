package com.ufps.seminario.controller;

import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.service.CriterioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/criterio")
public class CriterioController {
    @Autowired
    CriterioService criterioService;

    @PostMapping("/{idCriterio}/eliminar")
    public String eliminarCriterio(Model model, @PathVariable int idCriterio){
        try{
            Criterio criterio = criterioService.obtenerCriterioPorId(idCriterio);
            criterio.setEnabled(false);
            criterioService.guardarCriterio(criterio);
            return "fire";
        }catch(Exception e){
            return "flowerOfIron";
        }
    }
}
