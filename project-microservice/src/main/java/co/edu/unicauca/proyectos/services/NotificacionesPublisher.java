package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;
import co.edu.unicauca.proyectos.dto.EvaluacionFormatoAEvent;
import co.edu.unicauca.proyectos.dto.AsignacionEvaluadoresEvent;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestructura que implementa el puerto de salida
 * INotificacionesClient usando RabbitMQ.
 */
@Component
@RequiredArgsConstructor
public class NotificacionesPublisher implements INotificacionesClient {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing.formatoA.subido}")  // Changed: rk → routing
    private String rkFormatoASubido;

    @Value("${app.rabbitmq.routing.formatoA.evaluado}") // Changed: rk → routing
    private String rkFormatoAEvaluado;

    @Value("${app.rabbitmq.routing.anteproyecto.subido}") // Changed: rk → routing
    private String rkAnteproyectoSubido;

    @Value("${app.rabbitmq.routing.evaluadores.asignados}") // Added this line
    private String rkEvaluadoresAsignados;

    @Override
    public void notificarFormatoASubido(FormatoASubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoASubido, ev);
    }

    @Override
    public void notificarEvaluacionFormatoA(EvaluacionFormatoAEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoAEvaluado, ev);
    }

    @Override
    public void notificarAnteproyectoSubido(AnteproyectoSubidoEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkAnteproyectoSubido, ev);
    }

    @Override
    public void notificarAsignacionEvaluadores(AsignacionEvaluadoresEvent ev) {
        rabbitTemplate.convertAndSend(exchange, rkEvaluadoresAsignados, ev);
    }
}
