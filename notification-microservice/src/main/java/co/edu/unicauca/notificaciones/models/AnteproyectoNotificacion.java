package co.edu.unicauca.notificaciones.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class AnteproyectoNotificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idProyecto;
    private String titulo;
    private String jefeDepartamentoEmail;
    private String estudianteEmail;
    private String tutor1Email;
    private String tutor2Email;
}