package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig implements RabbitListenerConfigurer {
    // WhatsApp Queue Config
    public static final String QUEUE_NAME = "webhookEventQueue";
    public static final String EXCHANGE_NAME = "webhookEventExchange";
    public static final String ROUTING_KEY = "webhook.process";

    @Bean
    public Queue cadFileQueue() {
        return new Queue(QUEUE_NAME, true); // Durable queue
    }

    @Bean
    public TopicExchange cadFileExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue cadFileQueue, TopicExchange cadFileExchange) {
        return BindingBuilder.bind(cadFileQueue).to(cadFileExchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3); // Set initial concurrent consumers
        factory.setMaxConcurrentConsumers(5); // Set maximum concurrent consumers
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
    }

    // ✅ Media Processing Queue
    public static final String MEDIA_QUEUE_NAME = "mediaProcessingQueue";
    public static final String MEDIA_EXCHANGE_NAME = "mediaProcessingExchange";
    public static final String MEDIA_ROUTING_KEY = "media.process";

    // ✅ Media Queue Bean
    @Bean
    public Queue mediaQueue() {
        return new Queue(MEDIA_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange mediaExchange() {
        return new TopicExchange(MEDIA_EXCHANGE_NAME);
    }

    @Bean
    public Binding mediaBinding(Queue mediaQueue, TopicExchange mediaExchange) {
        return BindingBuilder.bind(mediaQueue).to(mediaExchange).with(MEDIA_ROUTING_KEY);
    }

}