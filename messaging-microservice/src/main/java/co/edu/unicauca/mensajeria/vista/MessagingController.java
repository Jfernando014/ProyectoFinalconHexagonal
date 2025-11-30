package co.edu.unicauca.mensajeria.vista;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.mensajeria.models.MensajeInterno;
import co.edu.unicauca.mensajeria.dto.MensajeInternoRequest;
import co.edu.unicauca.mensajeria.dto.MensajeResponse;
import co.edu.unicauca.mensajeria.services.IMensajeInternoService;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajería Interna", description = "Gestión de mensajes entre estudiantes y docentes")
public class MessagingController {

    @Autowired
    private IMensajeInternoService mensajeService;
    
    @PostMapping("/enviar")
    public ResponseEntity<MensajeResponse> enviarMensaje(@RequestParam String remitenteEmail,
                                                         @RequestParam String destinatariosEmail,
                                                         @RequestParam String asunto,
                                                         @RequestParam String cuerpo,
                                                         @RequestParam(required = false) MultipartFile documentoAdjunto) {
        try {
            MensajeInternoRequest request = new MensajeInternoRequest();
            request.setRemitenteEmail(remitenteEmail);
            request.setDestinatariosEmail(destinatariosEmail);
            request.setAsunto(asunto);
            request.setCuerpo(cuerpo);
            request.setDocumentoAdjunto(documentoAdjunto);

            Long idMensaje = mensajeService.enviarMensaje(request);
            return ResponseEntity.ok(new MensajeResponse("Mensaje enviado con ID: " + idMensaje, "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeResponse("Error: " + e.getMessage(), "ERROR"));
        }
    }

    @Operation(
        summary = "Obtener mensajes enviados por un estudiante",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "email", description = "Email del estudiante", example = "estudiante@unicauca.edu.co")
        }
    )
    @GetMapping("/enviados/{email}")
    public ResponseEntity<List<MensajeInterno>> getMensajesEnviados(@PathVariable String email) {
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesEnviadosPorEstudiante(email);
        return ResponseEntity.ok(mensajes);
    }

    @Operation(
        summary = "Obtener mensajes recibidos por un docente",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "email", description = "Email del docente", example = "docente@unicauca.edu.co")
        }
    )
    @GetMapping("/recibidos/{email}")
    public ResponseEntity<List<MensajeInterno>> getMensajesRecibidos(@PathVariable String email) {
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesRecibidosPorDocente(email);
        return ResponseEntity.ok(mensajes);
    }
}