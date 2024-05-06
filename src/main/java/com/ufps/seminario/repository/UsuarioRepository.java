package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario getUsuarioByUsername(String username);
}
