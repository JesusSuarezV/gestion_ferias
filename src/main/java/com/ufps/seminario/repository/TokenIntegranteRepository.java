package com.ufps.seminario.repository;
import com.ufps.seminario.entity.TokenIntegrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenIntegranteRepository extends JpaRepository<TokenIntegrante, String> {
}
