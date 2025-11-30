package co.edu.unicauca.proyectos.dto;

import lombok.Data;

@Data
public class AnteproyectoSubidoEvent {
    private Long idProyecto;
    private String titulo;
    private String jefeDepartamentoEmail;
    private String estudianteEmail;
    private String tutor1Email;
    private String tutor2Email;
}
