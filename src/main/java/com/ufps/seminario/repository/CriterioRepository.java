package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Criterio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Integer> {
}
