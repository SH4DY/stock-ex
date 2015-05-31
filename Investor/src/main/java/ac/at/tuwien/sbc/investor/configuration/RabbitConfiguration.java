package ac.at.tuwien.sbc.investor.configuration;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
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
    private ApplicationContext context;

    /** The message handler. */
    //@Autowired
    //private MessageHandler messageHandler;

    @Autowired
    private MarketArgsConfiguration marketArgs;

    @Autowired
    @Qualifier("queueMap")
    private HashMap<String, HashMap<String, Queue>> queueMap;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);


    @Bean
    public HashMap<String, MessageHandler> messageHandlerMap() {
        HashMap<String, MessageHandler> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            MessageHandler messageHandler =  new MessageHandler(market, context);
            ((ConfigurableApplicationContext)context).getBeanFactory().registerSingleton("messageHandler_" + market, messageHandler);
            map.put(market, messageHandler);
        }
        return map;
    }
    /**
     * Investor entry notification container.
     *
     * @param connectionFactoryMap the connection factory
     * @param amqpTemplate the amqp template
     * @param messageConverter the message converter
     * @return the simple message listener container
     */
    @Bean
    public HashMap<String, SimpleMessageListenerContainer> investorEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {

        HashMap<String, SimpleMessageListenerContainer> map = new HashMap<>();

        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactoryMap.get(market));
            container.setMessageConverter(messageConverter);
            container.setQueues(queueMap.get(market).get(CommonRabbitConfiguration.DEPOT_ENTRY_TOPIC));

            MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandlerMap().get(market), "onDepotEntryNotification");
            adapter.setMessageConverter(messageConverter);
            container.setMessageListener(adapter);
            map.put(market, container);
            ((ConfigurableApplicationContext)context).getBeanFactory().registerSingleton("depotEntryNotification_" + market, container);
        }
        return map;
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
    public HashMap<String, SimpleMessageListenerContainer> shareEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {

        HashMap<String, SimpleMessageListenerContainer> map = new HashMap<>();

        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactoryMap.get(market));
            container.setMessageConverter(messageConverter);
            container.setQueues(queueMap.get(market).get(CommonRabbitConfiguration.SHARE_ENTRY_TOPIC));

            MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandlerMap().get(market), "onShareEntryNotification");
            adapter.setMessageConverter(messageConverter);
            container.setMessageListener(adapter);
            map.put(market, container);
            ((ConfigurableApplicationContext)context).getBeanFactory().registerSingleton("shareEntryNotification_" + market, container);
        }
        return map;
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
    public HashMap<String, SimpleMessageListenerContainer> orderEntryNotificationContainer(HashMap<String,ConnectionFactory> connectionFactoryMap, RabbitTemplate amqpTemplate, MessageConverter messageConverter) {

        HashMap<String, SimpleMessageListenerContainer> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactoryMap.get(market));
            container.setMessageConverter(messageConverter);
            container.setQueues(queueMap.get(market).get(CommonRabbitConfiguration.ORDER_ENTRY_TOPIC));

            MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandlerMap().get(market), "onOrderEntryNotification");
            adapter.setMessageConverter(messageConverter);
            container.setMessageListener(adapter);
            map.put(market, container);
            ((ConfigurableApplicationContext)context).getBeanFactory().registerSingleton("orderEntryNotification_" + market, container);
        }
        return map;
    }

}
