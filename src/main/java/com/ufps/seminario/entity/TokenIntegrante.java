package com.ufps.seminario.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenIntegrante {
    @Id
    private String token;
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "integrante_id")
    private Integrante integrante;

    private boolean enabled;
}
