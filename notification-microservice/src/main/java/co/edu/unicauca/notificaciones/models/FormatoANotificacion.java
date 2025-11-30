package co.edu.unicauca.notificaciones.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class FormatoANotificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idProyecto;
    private String titulo;
    private String coordinadorEmail;
}