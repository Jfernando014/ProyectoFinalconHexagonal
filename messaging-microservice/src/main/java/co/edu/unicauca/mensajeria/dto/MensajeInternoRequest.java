package co.edu.unicauca.mensajeria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Datos para enviar un mensaje interno")
public class MensajeInternoRequest {
    @Schema(example = "estudiante@unicauca.edu.co", description = "Email del estudiante que envía", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remitenteEmail;

    @Schema(example = "docente1@unicauca.edu.co,docente2@unicauca.edu.co", description = "Emails de los docentes destinatarios", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destinatariosEmail;

    @Schema(example = "Propuesta de Proyecto de Grado", description = "Asunto del mensaje", requiredMode = Schema.RequiredMode.REQUIRED)
    private String asunto;

    @Schema(example = "Estimado profesor, adjunto mi propuesta inicial...", description = "Cuerpo del mensaje", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cuerpo;

    @Schema(description = "Archivo adjunto (opcional)")
    private MultipartFile documentoAdjunto;
}
