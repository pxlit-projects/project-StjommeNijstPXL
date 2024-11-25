package be.pxl.services.service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue reviewQueue() {
        return new Queue("review-queue", true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("update-queue", false); // Voor terugkoppeling van de Review-service
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
