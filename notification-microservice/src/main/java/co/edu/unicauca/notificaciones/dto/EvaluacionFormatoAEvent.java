package co.edu.unicauca.notificaciones.dto;

import lombok.Data;

@Data
public class EvaluacionFormatoAEvent {
    private Long idProyecto;
    private String titulo;
    private boolean aprobado;
    private String observaciones;
    private String estudianteEmail1;
    private String estudianteEmail2;
    private String directorEmail;
    private String codirectorEmail;
    private String coordinadorEmail;
}