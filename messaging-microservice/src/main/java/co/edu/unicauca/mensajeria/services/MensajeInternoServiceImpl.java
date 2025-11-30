package co.edu.unicauca.mensajeria.services;

import co.edu.unicauca.mensajeria.models.MensajeInterno;
import co.edu.unicauca.mensajeria.dto.MensajeInternoRequest;
import co.edu.unicauca.mensajeria.repository.MensajeInternoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeInternoServiceImpl implements IMensajeInternoService {

    @Autowired
    private MensajeInternoRepository mensajeRepository;

    @Override
    public Long enviarMensaje(MensajeInternoRequest request) throws Exception {
        if (request.getRemitenteEmail() == null || request.getDestinatariosEmail() == null ||
            request.getAsunto() == null || request.getCuerpo() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setRemitenteEmail(request.getRemitenteEmail());
        mensaje.setDestinatariosEmail(request.getDestinatariosEmail());
        mensaje.setAsunto(request.getAsunto());
        mensaje.setCuerpo(request.getCuerpo());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setEstado("ENVIADO");

        if (request.getDocumentoAdjunto() != null && !request.getDocumentoAdjunto().isEmpty()) {
            mensaje.setDocumentoAdjunto(request.getDocumentoAdjunto().getBytes());
            mensaje.setNombreArchivo(request.getDocumentoAdjunto().getOriginalFilename());
        }

        MensajeInterno guardado = mensajeRepository.save(mensaje);
        return guardado.getId();
    }

    @Override
    public List<MensajeInterno> obtenerMensajesEnviadosPorEstudiante(String emailEstudiante) {
        return mensajeRepository.findByRemitenteEmail(emailEstudiante);
    }

    @Override
    public List<MensajeInterno> obtenerMensajesRecibidosPorDocente(String emailDocente) {
        return mensajeRepository.findByDestinatariosEmailContaining(emailDocente);
    }

    @Override
    public boolean marcarComoLeido(Long idMensaje) {
        return mensajeRepository.findById(idMensaje)
                .map(mensaje -> {
                    mensaje.setEstado("LEIDO");
                    mensajeRepository.save(mensaje);
                    return true;
                })
                .orElse(false);
    }
}
