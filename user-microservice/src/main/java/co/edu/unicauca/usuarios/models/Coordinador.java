package co.edu.unicauca.usuarios.models;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
public class Coordinador extends Usuario {
}