package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.TokenIntegrante;
import com.ufps.seminario.entity.TokenUsuario;
import com.ufps.seminario.repository.TokenUsuarioRepository;
import com.ufps.seminario.service.TokenService;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    TokenUsuarioRepository tokenUsuarioRepository;

    @Override
    public void guardarTokenUsuario(TokenUsuario tokenUsuario) {
        tokenUsuarioRepository.save(tokenUsuario);

    }

    @Override
    public boolean activarCuenta(String token) {
        Optional<TokenUsuario> optionalTokenUsuario = tokenUsuarioRepository.findById(token);
        if (optionalTokenUsuario.isPresent()) {
            TokenUsuario tokenUsuario = optionalTokenUsuario.get();
            if (tokenUsuario.isEnabled() && (LocalDate.now().toEpochDay() - tokenUsuario.getFecha().toEpochDay()) < 15) {
                tokenUsuario.getUsuario().setEnabled(true);
                tokenUsuario.setEnabled(false);
                guardarTokenUsuario(tokenUsuario);
                return true;
            }
        }
        return false;
    }
}
