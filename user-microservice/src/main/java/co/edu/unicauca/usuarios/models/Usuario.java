package co.edu.unicauca.usuarios.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class Usuario {
    @Id
    @Column(unique = true, nullable = false)
    private String email; 

    private String password;
    private String nombres;
    private String apellidos;
    private String celular;
    private String programa;
}