package co.edu.unicauca.proyectos.dto;

import lombok.Data;

@Data
public class ProyectoEvaluadoEvent {
    private Long idProyecto;
    private boolean aprobado;
    private String observaciones;
    private String[] destinatarios;
}