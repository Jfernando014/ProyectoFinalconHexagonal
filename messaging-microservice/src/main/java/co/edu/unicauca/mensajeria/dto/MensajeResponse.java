package co.edu.unicauca.mensajeria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta estándar del servicio de mensajes")
public class MensajeResponse {
    @Schema(example = "Mensaje enviado exitosamente", description = "Mensaje descriptivo del resultado")
    private String mensaje;
    @Schema(example = "SUCCESS", allowableValues = {"SUCCESS", "ERROR"}, description = "Estado de la operación")
    private String estado;

    public MensajeResponse(String mensaje, String estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }
}
