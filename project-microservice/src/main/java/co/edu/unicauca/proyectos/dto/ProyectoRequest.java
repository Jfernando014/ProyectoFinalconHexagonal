package co.edu.unicauca.proyectos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos para crear un proyecto de grado")
public class ProyectoRequest {
    @Schema(example = "Sistema de Gestión", requiredMode = Schema.RequiredMode.REQUIRED)
    private String titulo;
    @Schema(example = "INVESTIGACION", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modalidad;
    @Schema(example = "director@unicauca.edu.co", requiredMode = Schema.RequiredMode.REQUIRED)
    private String directorEmail;
    private String codirectorEmail;
    @Schema(example = "estudiante1@unicauca.edu.co", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estudiante1Email;
    private String estudiante2Email;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String objetivoGeneral;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String objetivosEspecificos;
}
