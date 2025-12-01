package co.edu.unicauca.notificaciones.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class EvaluadoresAsignadosNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProyecto;
    private String titulo;

    private String jefeDepartamentoEmail;
    private String evaluador1Email;
    private String evaluador2Email;

    private String estudianteEmail1;
    private String estudianteEmail2;
    private String directorEmail;
    private String codirectorEmail;
}
