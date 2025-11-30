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
public class NotificacionesClient {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:notificaciones.exchange}")
    private String exchange;

    // Fallbacks en cascada: primero keys nuevas, luego viejas, luego default literal
    @Value("${app.rabbitmq.routing.formatoA.subido:${app.rabbitmq.routing.formatoA:${app.rabbitmq.routing.subido:formatoA.subido}}}")
    private String rkFormatoASubido;

    @Value("${app.rabbitmq.routing.formatoA.evaluado:${app.rabbitmq.routing.evaluado:formatoA.evaluado}}")
    private String rkFormatoAEvaluado;

    @Value("${app.rabbitmq.routing.anteproyecto.subido:anteproyecto.subido}")
    private String rkAnteproyectoSubido;

    public void notificarFormatoASubido(FormatoASubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoASubido, ev);
    }

    public void notificarEvaluacion(EvaluacionFormatoAEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoAEvaluado, ev);
    }

    public void notificarAnteproyecto(AnteproyectoSubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkAnteproyectoSubido, ev);
    }
}
