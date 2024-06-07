package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Role;
import com.ufps.seminario.entity.TokenUsuario;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.repository.TokenUsuarioRepository;
import com.ufps.seminario.repository.UsuarioRepository;
import com.ufps.seminario.service.MailService;
import com.ufps.seminario.service.TokenService;
import com.ufps.seminario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public boolean guardarUsuario(Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setRole(new Role(1, "ESTUDIANTE", true));
            usuarioRepository.save(usuario);

            String token = UUID.randomUUID().toString();
            TokenUsuario tokenUsuario = new TokenUsuario(token, LocalDate.now(), usuario, true);
            tokenService.guardarTokenUsuario(tokenUsuario);

            String url = "https://gestionferias-production.up.railway.app/Confirmacion/" + token;
            String cuerpo = "Por favor, confirme su cuenta en el siguiente enlace: <a href=\"" + url + "\">" + url + "</a>";
            mailService.enviarCorreo(usuario.getUsername(), "ENLACE DE ACTIVACIÓN FPA", cuerpo);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int obtenerId(String correo) {
        return usuarioRepository.getUsuarioByUsername(correo).getId();
    }

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.getUsuarioByUsername(username);
    }

}
