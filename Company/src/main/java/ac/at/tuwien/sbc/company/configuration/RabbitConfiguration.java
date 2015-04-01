package ac.at.tuwien.sbc.company.configuration;

import ac.at.tuwien.sbc.company.workflow.amqp.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * Created by dietl_ma on 31/03/15.
 */
@Configuration
@Profile("amqp")
public class RabbitConfiguration {

    @Autowired
    private ApplicationContext ctx;

    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(messageConverter);
        container.setQueues((Queue)ctx.getBean("shareEntryNotificationQueue"));

        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageHandler(), messageConverter);
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

}
