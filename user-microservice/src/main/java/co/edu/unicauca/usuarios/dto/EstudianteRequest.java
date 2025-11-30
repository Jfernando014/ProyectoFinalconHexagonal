package co.edu.unicauca.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un estudiante")
public class EstudianteRequest {
    @Schema(example = "ana.gomez@unicauca.edu.co", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(example = "Stud123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(example = "Ana María", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombres;
    @Schema(example = "Gómez López", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;
    @Schema(example = "3209876543")
    private String celular;
    @Schema(example = "INGENIERIA_SISTEMAS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String programa;
}