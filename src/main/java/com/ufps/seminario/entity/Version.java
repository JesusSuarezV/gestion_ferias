package com.ufps.seminario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "feria_id")
    private Feria feria;

    private int numero;

    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate fechaCierre;
    private byte[] archivo;
    private String archivoUrl;
    private String archivoConentType;
    private boolean cierre;
    private int aprobacion;
    private boolean enabled;

}
