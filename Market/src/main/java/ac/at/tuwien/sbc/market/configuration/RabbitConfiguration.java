package ac.at.tuwien.sbc.market.configuration;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.market.workflow.amqp.AmqpMessageHandler;
import ac.at.tuwien.sbc.market.workflow.amqp.RPCMessageHandler;
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

    /** The RPC message handler. */
    @Autowired
    private RPCMessageHandler RPCMessageHandler;

    /** The message handler. */
    @Autowired
    private AmqpMessageHandler messageHandler;

    @Autowired
    @Qualifier("queueMap")
    private HashMap<String, HashMap<String, Queue>> queueMap;

    @Autowired
    private MarketArgsConfiguration marketArgs;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    /**
     * Market rpc container.
     *
     * @param connectionFactoryMap the connection factory
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer marketRPCContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        //container.setQueues((Queue)applicationContext.getBean("marketRPCQueue"));
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.MARKET_RPC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(RPCMessageHandler, messageConverter);
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Order entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer orderEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        //container.setQueues((Queue)applicationContext.getBean("orderEntryNotificationQueue"));
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.ORDER_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onOrderEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Share entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer shareEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        //container.setQueues((Queue)applicationContext.getBean("shareEntryNotificationQueue"));
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.SHARE_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onShareEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

    /**
     * Transaction entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public SimpleMessageListenerContainer transactionEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMap.get(marketArgs.getMarkets().get(0)));
        container.setMessageConverter(messageConverter);
        //container.setQueues((Queue)applicationContext.getBean("transactionEntryNotificationQueue"));
        container.setQueues(queueMap.get(marketArgs.getMarkets().get(0)).get(CommonRabbitConfiguration.TRANSACTION_ENTRY_TOPIC));

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler, "onTransactionEntryNotification");
        adapter.setMessageConverter(messageConverter);
        container.setMessageListener(adapter);
        return container;
    }

}
