package co.edu.unicauca.usuarios.dto;

import co.edu.unicauca.usuarios.models.enums.TipoDocente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un docente")
public class DocenteRequest {
    @Schema(example = "juan.perez@unicauca.edu.co", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(example = "Pass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombres;
    @Schema(example = "Pérez Gómez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;
    @Schema(example = "3101234567")
    private String celular;
    @Schema(example = "INGENIERIA_SISTEMAS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String programa;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoDocente tipoDocente;
}