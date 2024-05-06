package com.ufps.seminario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;
    @ManyToOne
    @JoinColumn(name = "criterio_id")
    private Criterio criterio;
    private int valor;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario jurado;
    private boolean enabled;
}
