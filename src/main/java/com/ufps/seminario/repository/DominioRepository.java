package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Dominio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DominioRepository extends JpaRepository<Dominio,Integer> {
}
