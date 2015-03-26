package ac.at.tuwien.sbc.broker.configuration;

import ac.at.tuwien.sbc.broker.MyRabbitListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMessageListener(new MessageListenerAdapter(new MyRabbitListener(container), jsonMessageConverter));
        container.setQueues(new Queue("GEORESOLVER"));

        return container;
    }


}
