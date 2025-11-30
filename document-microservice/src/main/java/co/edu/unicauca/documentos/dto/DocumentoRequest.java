package co.edu.unicauca.documentos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Datos para subir un documento")
public class DocumentoRequest {
    @Schema(description = "ID del proyecto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idProyecto;

    @Schema(description = "Tipo de documento", example = "FORMATO_A", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipoDocumento;

    @Schema(description = "Archivo a subir", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile archivo;
}