package ac.at.tuwien.sbc.investor.configuration;

import ac.at.tuwien.sbc.investor.workflow.amqp.MessageHandler;
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

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 01/04/15.
 */
@Configuration
@Profile("amqp")
public class RabbitConfiguration {

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /** The message handler. */
    @Autowired
    private MessageHandler messageHandler;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    /**
     * Investor entry notification container.
     *
     * @param connectionFactory the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
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

    /**
     * Share entry notification container.
     *
     * @param connectionFactory the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
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

    /**
     * Order entry notification container.
     *
     * @param connectionFactory the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
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

}
