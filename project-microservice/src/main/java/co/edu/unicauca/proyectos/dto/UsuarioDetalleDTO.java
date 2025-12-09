package co.edu.unicauca.proyectos.dto;

import lombok.Data;

@Data
public class UsuarioDetalleDTO {

    private Long id;         // mismo nombre de user-microservice
    private String nombres;
    private String apellidos;
    private String email;
    private String celular;
    private String rol;      // ESTUDIANTE, DOCENTE, COORDINADOR, JEFE_DEPARTAMENTO
    private String programa; // puede venir null si no aplica
}
