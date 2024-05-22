package com.ufps.seminario.service;

import com.ufps.seminario.entity.Usuario;

public interface UsuarioService {
    public boolean guardarUsuario(Usuario usuario);

    public int obtenerId(String correo);

    Usuario obtenerUsuarioPorUsername(String username); 
}
