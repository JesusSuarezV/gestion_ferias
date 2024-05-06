package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.repository.UsuarioRepository;
import com.ufps.seminario.service.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.getUsuarioByUsername(username);

        if(usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new MyUserDetails(usuario);
    }
}
