package co.edu.unicauca.proyectos.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO ligero para exponer la información del Formato A
 * asociado a un proyecto y un estudiante.
 */
@Data
public class FormatoAInfoDTO {

    private Long idProyecto;
    private String tituloProyecto;
    private String estudiante1Email;
    private String estudiante2Email;   // puede ir null
    private String formatoAToken;      // ID del documento en document-microservice
    private LocalDate fechaFormatoA;   // fecha en que se subió el Formato A
}
