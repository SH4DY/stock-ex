package ac.at.tuwien.sbc.domain.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.UUID;


// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("amqp")
public class CommonRabbitConfiguration {

    /** The Constant TOPIC_EXCHANGE. */
    public static final String TOPIC_EXCHANGE =  "stockTopicExchange";
    
    /** The Constant FANOUT_EXCHANGE. */
    public static final String FANOUT_EXCHANGE =  "stockFanoutExchange";

    /** The Constant INVESTOR_ENTRY_TOPIC. */
    public static final String INVESTOR_ENTRY_TOPIC =  "investorEntryTopic";
    
    /** The Constant SHARE_ENTRY_TOPIC. */
    public static final String SHARE_ENTRY_TOPIC =  "shareEntryTopic";
    
    /** The Constant ORDER_ENTRY_TOPIC. */
    public static final String ORDER_ENTRY_TOPIC =  "orderEntryTopic";
    
    /** The Constant TRANSACTION_ENTRY_TOPIC. */
    public static final String TRANSACTION_ENTRY_TOPIC =  "transactionEntryTopic";
    
    /** The Constant RELEASE_ENTRY_TOPIC. */
    public static final String RELEASE_ENTRY_TOPIC =  "releaseEntry";
    
    /** The Constant MARKET_RPC. */
    public static final String MARKET_RPC = "marketRPC";

    /** The uuid. */
    public static UUID uuid;

    /** The connection factory. */
    @Autowired
    ConnectionFactory connectionFactory;

    /** The rabbit admin. */
    @Autowired
    RabbitAdmin rabbitAdmin;

    /**
     * Topic exchange.
     *
     * @return the topic exchange
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /*@Bean
    public TopicExchange fanoutExchange() {
        return new TopicExchange(FANOUT_EXCHANGE);
    }*/

    /**
     * Rabbit transaction manager.
     *
     * @param connectionFactory the connection factory
     * @return the rabbit transaction manager
     */
    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    /**
     * Rabbit listener container factory.
     *
     * @param connectionFactory the connection factory
     * @return the simple rabbit listener container factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    /**
     * Amqp template.
     *
     * @param connectionFactory the connection factory
     * @return the rabbit template
     */
    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReplyTimeout(10000);
        return template;

    }

    /**
     * Json message converter.
     *
     * @return the message converter
     */
    @Bean
    public MessageConverter jsonMessageConverter(){
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.enableDefaultTyping();
        jsonMessageConverter.setJsonObjectMapper(jsonObjectMapper);
        return jsonMessageConverter;
    }

    //init investorEntry topic
    /**
     * Investor entry notification queue.
     *
     * @return the queue
     */
    @Bean
    public Queue investorEntryNotificationQueue() {
        Queue queue = new Queue(INVESTOR_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    /**
     * Investor entry notification queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding investorEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(investorEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), INVESTOR_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init shareEntry topic
    /**
     * Share entry notification queue.
     *
     * @return the queue
     */
    @Bean
    public Queue shareEntryNotificationQueue() {
        Queue queue = new Queue(SHARE_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    /**
     * Share entry notification queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding shareEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(shareEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), SHARE_ENTRY_TOPIC, new HashMap<String, Object>());
    }
    //init orderEntry topic
    /**
     * Order entry notification queue.
     *
     * @return the queue
     */
    @Bean
    public Queue orderEntryNotificationQueue() {
        Queue queue = new Queue(ORDER_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    /**
     * Order entry notification queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding orderEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(orderEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), ORDER_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init transactionEntry topic
    /**
     * Transaction entry notification queue.
     *
     * @return the queue
     */
    @Bean
    public Queue transactionEntryNotificationQueue() {
        Queue queue = new Queue(TRANSACTION_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    /**
     * Transaction entry notification queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding transactionEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(transactionEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), TRANSACTION_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init releaseEntry topic
    /**
     * Release entry notification queue.
     *
     * @return the queue
     */
    @Bean
    public Queue releaseEntryNotificationQueue() {
        Queue queue = new Queue(RELEASE_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }
    
    /**
     * Release entry notification queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding releaseEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(releaseEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), RELEASE_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init marketRPC queue
    /**
     * Market rpc queue.
     *
     * @return the queue
     */
    @Bean
    public Queue marketRPCQueue() {
        Queue queue = new Queue(MARKET_RPC);
        return queue;
    }
    
    /**
     * Market rpc queue binding.
     *
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding marketRPCQueueBinding(TopicExchange exchange) {
        return BindingBuilder.bind(marketRPCQueue()).to(exchange).with(marketRPCQueue().getName());
    }

    /**
     * Rabbit admin.
     *
     * @param connectionFactory the connection factory
     * @return the rabbit admin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Uuid.
     *
     * @return the string
     */
    @Bean
    public String uuid() {
        uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * On pre destroy.
     */
    @PreDestroy
    public void onPreDestroy() {
        //delete topic queues on shutdown
        rabbitAdmin.deleteQueue(INVESTOR_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(SHARE_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(ORDER_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(TRANSACTION_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(RELEASE_ENTRY_TOPIC + '_' + uuid);
    }


}
