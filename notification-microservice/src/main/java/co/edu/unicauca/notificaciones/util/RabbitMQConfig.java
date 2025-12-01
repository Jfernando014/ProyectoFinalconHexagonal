package co.edu.unicauca.notificaciones.util;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue.formatoA.subido}")
    private String qFormatoASubido;

    @Value("${app.rabbitmq.queue.formatoA.evaluado}")
    private String qFormatoAEvaluado;

    @Value("${app.rabbitmq.queue.anteproyecto.subido}")
    private String qAnteproyectoSubido;

    @Value("${app.rabbitmq.rk.formatoA.subido}")
    private String rkFormatoASubido;

    @Value("${app.rabbitmq.rk.formatoA.evaluado}")
    private String rkFormatoAEvaluado;

    @Value("${app.rabbitmq.rk.anteproyecto.subido}")
    private String rkAnteproyectoSubido;

    @Value("${app.rabbitmq.queue.evaluadores.asignados}")
    private String evaluadoresAsignadosQueueName;

    @Value("${app.rabbitmq.rk.evaluadores.asignados}")
    private String evaluadoresAsignadosRoutingKey;

    // 1) Exchange DIRECT (coincide con el que ya existe en el broker)
    @Bean
    public DirectExchange notificacionesExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // 2) Queues (durables)
    @Bean
    public Queue queueFormatoASubido() {
        return QueueBuilder.durable(qFormatoASubido).build();
    }

    @Bean
    public Queue queueFormatoAEvaluado() {
        return QueueBuilder.durable(qFormatoAEvaluado).build();
    }

    @Bean
    public Queue queueAnteproyectoSubido() {
        return QueueBuilder.durable(qAnteproyectoSubido).build();
    }

    // 3) Bindings
    @Bean
    public Binding bindingFormatoASubido(Queue queueFormatoASubido, DirectExchange notificacionesExchange) {
        return BindingBuilder.bind(queueFormatoASubido).to(notificacionesExchange).with(rkFormatoASubido);
    }

    @Bean
    public Binding bindingFormatoAEvaluado(Queue queueFormatoAEvaluado, DirectExchange notificacionesExchange) {
        return BindingBuilder.bind(queueFormatoAEvaluado).to(notificacionesExchange).with(rkFormatoAEvaluado);
    }

    @Bean
    public Binding bindingAnteproyectoSubido(Queue queueAnteproyectoSubido, DirectExchange notificacionesExchange) {
        return BindingBuilder.bind(queueAnteproyectoSubido).to(notificacionesExchange).with(rkAnteproyectoSubido);
    }

    // 4) Admin que declara todo al arrancar
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory cf) {
        RabbitAdmin admin = new RabbitAdmin(cf);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, MessageConverter mc) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(mc);
        return f;
    }

    @Bean
    public Queue evaluadoresAsignadosQueue() {
        return QueueBuilder.durable(evaluadoresAsignadosQueueName).build();
    }

    @Bean
    public Binding evaluadoresAsignadosBinding(DirectExchange notificacionesExchange,
                                               Queue evaluadoresAsignadosQueue) {
        return BindingBuilder
                .bind(evaluadoresAsignadosQueue)
                .to(notificacionesExchange)
                .with(evaluadoresAsignadosRoutingKey);
    }

}
