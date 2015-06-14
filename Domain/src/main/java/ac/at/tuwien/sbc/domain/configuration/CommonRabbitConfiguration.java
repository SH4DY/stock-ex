package ac.at.tuwien.sbc.domain.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    @Deprecated
    public static final String TOPIC_EXCHANGE =  "stockTopicExchange";
    
    /** The Constant FANOUT_EXCHANGE. */
    @Deprecated
    public static final String FANOUT_EXCHANGE =  "stockFanoutExchange";

    /** The Constant DEPOT_ENTRY_TOPIC. */
    public static final String DEPOT_ENTRY_TOPIC =  "depotEntryTopic";
    
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

    @Autowired
    MarketArgsConfiguration marketArgs;


    @Bean(name = "exchangeKeyMap")
    public HashMap<String, String> exchangeKeyMap() throws MalformedURLException {
        HashMap<String,String> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            map.put(market, parseMarketUrl(market).getPath());
        }
        return map;
    }

    @Bean(name = "exchangeKey")
    public String singleExchangeKey (HashMap<String, String> exchangeKeyMap) {
        return exchangeKeyMap.entrySet().iterator().next().getValue();
    }


    @Bean
    public HashMap<String,ConnectionFactory> connectionFactoryMap() throws MalformedURLException {

        HashMap<String,ConnectionFactory> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {

            URL url = parseMarketUrl(market);
            com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = new com.rabbitmq.client.ConnectionFactory();
            rabbitConnectionFactory.setHost(url.getHost());
            rabbitConnectionFactory.setPort(url.getPort());

            map.put(market, new CachingConnectionFactory(rabbitConnectionFactory));
        }

        return map;
    }


    @Bean
    public HashMap<String, RabbitAdmin> rabbitAdminMap(HashMap<String,ConnectionFactory> connectionFactoryMap) {
        HashMap<String,RabbitAdmin> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            map.put(market, new RabbitAdmin(connectionFactoryMap.get(market)));
        }

        return map;
    }

    /**
     *
     * @param connectionFactoryMap
     * @return
     */
    @Bean
    public HashMap<String,RabbitTemplate> amqpTemplateMap(HashMap<String,ConnectionFactory> connectionFactoryMap) {

        HashMap<String,RabbitTemplate> map = new HashMap<>();

        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            RabbitTemplate template = new RabbitTemplate(connectionFactoryMap.get(market));
            template.setMessageConverter(jsonMessageConverter());
            template.setReplyTimeout(10000);
            map.put(market, template);
        }
        return map;
    }

    @Bean
    public RabbitTemplate singleAmqpTemplate (HashMap<String,RabbitTemplate> amqpTemplateMap) {
        return amqpTemplateMap.entrySet().iterator().next().getValue();
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


    /**
     * Uuid.
     *
     * @return the string
     */
    public static String uuid() {
        uuid = UUID.randomUUID();
        return uuid.toString();
    }


    /**
     *
     * @param market
     * @return
     * @throws MalformedURLException
     */
    private URL parseMarketUrl(String market) throws MalformedURLException {
        return new URL("http://" + market);
    }


}
