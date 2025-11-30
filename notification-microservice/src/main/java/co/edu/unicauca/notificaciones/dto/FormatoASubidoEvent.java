package co.edu.unicauca.notificaciones.dto;

import lombok.Data;
@Data
public class FormatoASubidoEvent {
    private Long idProyecto;
    private String titulo;
    private String coordinadorEmail;
}