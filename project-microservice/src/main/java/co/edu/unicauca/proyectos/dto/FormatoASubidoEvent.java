package co.edu.unicauca.proyectos.dto;

import lombok.Data;

@Data
public class FormatoASubidoEvent {
    private Long idProyecto;
    private String titulo;
    private String coordinadorEmail;
}
