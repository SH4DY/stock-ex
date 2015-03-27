package ac.at.tuwien.sbc.domain.configuration;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("amqp")
public class CommonRabbitConfiguration {

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("exchange");
    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReplyTimeout(10000);
        return template;

    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        return jsonMessageConverter;
    }
/*
    @Bean
    Queue queue() {
        Queue queue = new Queue("stockqueue", false);
        BindingBuilder.bind(queue).to(exchange()).with("stockexchange");

        return queue;
    }
*/
 /*   @Bean
    public Queue replyQueue() {
        Queue queue = new Queue("reply.queue", true);
        BindingBuilder.bind(queue).to(exchange()).with("stockexchange");
        return queue;
    }
*/
/*
    @Bean
    public SimpleMessageListenerContainer replyListenerContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(replyQueue());
        container.setMessageListener(amqpTemplate);
        container.setMessageConverter(messageConverter);
        return container;
    }
*/


    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

}
