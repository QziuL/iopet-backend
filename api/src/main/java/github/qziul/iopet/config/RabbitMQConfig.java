package github.qziul.iopet.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.telemetria:iopet.telemetria.queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.telemetria:amq.topic}")
    private String exchangeName;

    @Value("${rabbitmq.routingkey.telemetria:iopet.telemetria.#}")
    private String routingKey;

    @Bean
    public Queue telemetriaQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public TopicExchange telemetriaExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Binding telemetriaBinding(Queue telemetriaQueue, TopicExchange telemetriaExchange) {
        return BindingBuilder.bind(telemetriaQueue).to(telemetriaExchange).with(routingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}
