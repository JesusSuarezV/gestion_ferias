package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario getUsuarioByUsernameIgnoreCase(String username);

    @Query("SELECT u FROM Usuario u WHERE u.role.id = 1 AND u.enabled = true ORDER BY u.username")
    List<Usuario> findEstudiantes();

    @Query("SELECT u FROM Usuario u WHERE u.role.id = 2 AND u.enabled = true ORDER BY u.username")
    List<Usuario> findAdmins();


}
