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


/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("amqp")
public class CommonRabbitConfiguration {

    public static final String TOPIC_EXCHANGE =  "stockTopicExchange";
    public static final String FANOUT_EXCHANGE =  "stockFanoutExchange";

    public static final String INVESTOR_ENTRY_TOPIC =  "investorEntryTopic";
    public static final String SHARE_ENTRY_TOPIC =  "shareEntryTopic";
    public static final String ORDER_ENTRY_TOPIC =  "orderEntryTopic";
    public static final String TRANSACTION_ENTRY_TOPIC =  "transactionEntryTopic";
    public static final String RELEASE_ENTRY_QUEUE =  "releaseEntry";
    public static final String MARKET_RPC = "marketRPC";

    public static UUID uuid;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /*@Bean
    public TopicExchange fanoutExchange() {
        return new TopicExchange(FANOUT_EXCHANGE);
    }*/

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

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.enableDefaultTyping();
        jsonMessageConverter.setJsonObjectMapper(jsonObjectMapper);
        return jsonMessageConverter;
    }

    //init investorEntry topic
    @Bean
    public Queue investorEntryNotificationQueue() {
        Queue queue = new Queue(INVESTOR_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    @Bean
    public Binding investorEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(investorEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), INVESTOR_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init shareEntry topic
    @Bean
    public Queue shareEntryNotificationQueue() {
        Queue queue = new Queue(SHARE_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    @Bean
    public Binding shareEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(shareEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), SHARE_ENTRY_TOPIC, new HashMap<String, Object>());
    }
    //init orderEntry topic
    @Bean
    public Queue orderEntryNotificationQueue() {
        Queue queue = new Queue(ORDER_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    @Bean
    public Binding orderEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(orderEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), ORDER_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init transactionEntry topic
    @Bean
    public Queue transactionEntryNotificationQueue() {
        Queue queue = new Queue(TRANSACTION_ENTRY_TOPIC + '_' + uuid(), false, false, true);
        return queue;
    }

    @Bean
    public Binding transactionEntryNotificationQueueBinding(TopicExchange exchange) {
        return new Binding(transactionEntryNotificationQueue().getName(), Binding.DestinationType.QUEUE, exchange.getName(), TRANSACTION_ENTRY_TOPIC, new HashMap<String, Object>());
    }

    //init releaseEntry queue
    @Bean
    public Queue releaseEntryQueue() {
        Queue queue = new Queue(RELEASE_ENTRY_QUEUE);
        return queue;
    }
    @Bean
    public Binding releaseEntryQueueBinding(TopicExchange exchange) {
        return BindingBuilder.bind(releaseEntryQueue()).to(exchange).with(releaseEntryQueue().getName());
    }

    //init marketRPC queue
    @Bean
    public Queue marketRPCQueue() {
        Queue queue = new Queue(MARKET_RPC);
        return queue;
    }
    @Bean
    public Binding marketRPCQueueBinding(TopicExchange exchange) {
        return BindingBuilder.bind(marketRPCQueue()).to(exchange).with(marketRPCQueue().getName());
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public String uuid() {
        uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @PreDestroy
    public void onPreDestroy() {
        //delete topic queues on shutdown
        rabbitAdmin.deleteQueue(INVESTOR_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(SHARE_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(ORDER_ENTRY_TOPIC + '_' + uuid);
        rabbitAdmin.deleteQueue(TRANSACTION_ENTRY_TOPIC + '_' + uuid);
    }


}
