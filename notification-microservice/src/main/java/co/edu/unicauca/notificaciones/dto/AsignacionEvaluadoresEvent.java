package co.edu.unicauca.notificaciones.dto;

import lombok.Data;

@Data
public class AsignacionEvaluadoresEvent {
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
