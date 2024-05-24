package com.ufps.seminario.service;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.TokenUsuario;

public interface TokenService {
    public void guardarTokenUsuario(TokenUsuario tokenUsuario);

    public boolean activarCuenta(String token);
}
