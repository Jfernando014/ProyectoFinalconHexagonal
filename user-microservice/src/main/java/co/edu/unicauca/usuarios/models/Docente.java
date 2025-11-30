package co.edu.unicauca.usuarios.models;

import co.edu.unicauca.usuarios.models.enums.TipoDocente;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Docente extends Usuario {
    private TipoDocente tipoDocente;
}
