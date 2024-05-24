package com.ufps.seminario.service;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.TokenIntegrante;

public interface TokenIntegranteService {
    public void guardarTokenIntegrante(TokenIntegrante tokenIntegrante);

    void generarToken(Integrante integranteCreado);

    public void aceptarInvitacion(String token);
}
