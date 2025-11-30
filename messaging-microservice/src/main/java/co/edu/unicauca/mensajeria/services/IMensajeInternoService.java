package co.edu.unicauca.mensajeria.services;

import co.edu.unicauca.mensajeria.models.MensajeInterno;
import co.edu.unicauca.mensajeria.dto.MensajeInternoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMensajeInternoService {
    Long enviarMensaje(MensajeInternoRequest request) throws Exception;
    List<MensajeInterno> obtenerMensajesEnviadosPorEstudiante(String emailEstudiante);
    List<MensajeInterno> obtenerMensajesRecibidosPorDocente(String emailDocente);
    boolean marcarComoLeido(Long idMensaje);
}