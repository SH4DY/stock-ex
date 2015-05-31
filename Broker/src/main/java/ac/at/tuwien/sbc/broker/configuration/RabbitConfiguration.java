package ac.at.tuwien.sbc.broker.configuration;

import ac.at.tuwien.sbc.broker.workflow.amqp.MessageHandler;
import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;

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

    @Autowired
    private MarketArgsConfiguration marketArgs;

    @Autowired
    @Qualifier("queueMap")
    private HashMap<String, HashMap<String, Queue>> queueMap;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    /**
     * Investor entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer investorEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.DEPOT_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onInvestorEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Share entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer shareEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.SHARE_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onShareEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Order entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer orderEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.ORDER_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onOrderEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Release entry notifcation container.
     *
     * @param connectionFactoryMap the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer releaseEntryNotifcationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.RELEASE_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onReleaseEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

}
