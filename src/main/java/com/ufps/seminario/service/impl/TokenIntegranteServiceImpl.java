package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.TokenIntegrante;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.repository.TokenIntegranteRepository;
import com.ufps.seminario.service.MailService;
import com.ufps.seminario.service.TokenIntegranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TokenIntegranteServiceImpl implements TokenIntegranteService {

    @Autowired
    TokenIntegranteRepository tokenIntegranteRepository;

    @Autowired
    IntegranteRepository integranteRepository;

    @Autowired
    MailService mailService;

    @Override
    public void guardarTokenIntegrante(TokenIntegrante tokenIntegrante) {
        tokenIntegranteRepository.save(tokenIntegrante);
    }
 
    @Override
    public void generarToken(Integrante integranteCreado) {
        try{
            String token = UUID.randomUUID().toString();
            TokenIntegrante tokenIntegrante = new TokenIntegrante(token, LocalDate.now(), integranteCreado, false);
            guardarTokenIntegrante(tokenIntegrante);
            String url = "https://gestionferias-production.up.railway.app/Confirmacion/integrante/"+token;
            String cuerpo = "Acaba de ser agregado al proyecto '"+ integranteCreado.getProyecto().getNombre() + "', confirme su registro en el siguiente enlace: <a href=\"" + url + "\">" + url + "</a>";
            mailService.enviarCorreo(integranteCreado.getCorreoRegistro(), "Invitación a Proyecto - FPA", cuerpo);
        }catch(Exception ignored){ 

        }
    }

    @Override
    public void aceptarInvitacion(String token) {
        TokenIntegrante tokenIntegrante = tokenIntegranteRepository.getReferenceById(token);
        tokenIntegrante.setEnabled(false);
        tokenIntegrante.getIntegrante().setEnabled(true);
        tokenIntegranteRepository.save(tokenIntegrante);
        integranteRepository.save(tokenIntegrante.getIntegrante());
    }
}
