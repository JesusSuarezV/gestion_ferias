package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {
}
