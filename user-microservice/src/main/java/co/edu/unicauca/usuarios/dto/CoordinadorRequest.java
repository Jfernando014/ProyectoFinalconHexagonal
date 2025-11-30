package co.edu.unicauca.usuarios.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un Coordinador")
public class CoordinadorRequest {

    @Schema(example = "juan.perez@unicauca.edu.co", description = "Email institucional", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "Pass123!", description = "Contraseña fuerte", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(example = "Juan Carlos", description = "Nombres", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombres;

    @Schema(example = "Pérez Gómez", description = "Apellidos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;

    @Schema(example = "3101234567", description = "Celular (opcional)")
    private String celular;

    @Schema(example = "INGENIERIA_SISTEMAS", description = "Programa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String programa;
}
