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
public class TokenUsuario {
    @Id
    private String token;
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private boolean enabled;
}
