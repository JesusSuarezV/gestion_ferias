package com.ufps.seminario.repository;
import com.ufps.seminario.entity.TokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenUsuarioRepository extends JpaRepository<TokenUsuario, String> {
}
