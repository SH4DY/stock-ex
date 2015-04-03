package ac.at.tuwien.sbc.broker.configuration;

import ac.at.tuwien.sbc.broker.workflow.amqp.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
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

/**
 * Created by dietl_ma on 01/04/15.
 */
@Configuration
@Profile("amqp")
public class RabbitConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageHandler messageHandler;

    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Bean
    public SimpleMessageListenerContainer investorEntryNotificationContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(messageConverter);
        container.setQueues((Queue)applicationContext.getBean("investorEntryNotificationQueue"));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onInvestorEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer shareEntryNotificationContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(messageConverter);
        container.setQueues((Queue)applicationContext.getBean("shareEntryNotificationQueue"));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onShareEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer orderEntryNotificationContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(messageConverter);
        container.setQueues((Queue)applicationContext.getBean("orderEntryNotificationQueue"));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onOrderEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer releaseEntryListenerContainer(ConnectionFactory connectionFactory, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageConverter(messageConverter);
        container.setQueues((Queue)applicationContext.getBean("releaseEntryQueue"));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onReleaseEntryMessage");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

}
