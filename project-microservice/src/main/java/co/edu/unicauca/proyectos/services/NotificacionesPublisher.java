package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;
import co.edu.unicauca.proyectos.dto.EvaluacionFormatoAEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionesPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:notificaciones.exchange}")
    private String exchange;

    // Usa 'routing.*' y define defaults
    @Value("${app.rabbitmq.routing.formatoA.subido:formatoA.subido}")
    private String rkFormatoASubido;

    @Value("${app.rabbitmq.routing.formatoA.evaluado:formatoA.evaluado}")
    private String rkFormatoAEvaluado;

    @Value("${app.rabbitmq.routing.anteproyecto.subido:anteproyecto.subido}")
    private String rkAnteproyectoSubido;

    public void publicarFormatoASubido(FormatoASubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoASubido, ev);
    }

    public void publicarEvaluacionFormatoA(EvaluacionFormatoAEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoAEvaluado, ev);
    }

    public void publicarAnteproyectoSubido(AnteproyectoSubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkAnteproyectoSubido, ev);
    }
}
