package com.ufps.seminario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Integrante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String correoRegistro;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private boolean enabled;
}
