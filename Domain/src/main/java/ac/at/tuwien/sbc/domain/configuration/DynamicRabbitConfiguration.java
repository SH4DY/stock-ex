package ac.at.tuwien.sbc.domain.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dietl_ma on 31/05/15.
 */
@Configuration
@Profile("amqp")
public class DynamicRabbitConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    HashMap<String,ConnectionFactory> connectionFactoryMap;

    @Autowired
    HashMap<String, RabbitAdmin> rabbitAdminMap;

    @Autowired
    @Qualifier("exchangeKeyMap")
    HashMap<String, String> exchangeKeyMap;

    @Autowired
    MarketArgsConfiguration marketArgs;

    private HashMap<String, HashMap<String, Queue>> queueMap;



    @PostConstruct
    public void onPostConstruct() throws MalformedURLException {
        queueMap = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {

            RabbitAdmin rabbitAdmin = rabbitAdminMap.get(market);
            //init exchange
            TopicExchange exchange = new TopicExchange(exchangeKeyMap.get(market));
            rabbitAdmin.declareExchange(exchange);

            //init topics
            String[] queueNames = new String[]{CommonRabbitConfiguration.DEPOT_ENTRY_TOPIC,
                    CommonRabbitConfiguration.SHARE_ENTRY_TOPIC,
                    CommonRabbitConfiguration.ORDER_ENTRY_TOPIC,
                    CommonRabbitConfiguration.TRANSACTION_ENTRY_TOPIC,
                    CommonRabbitConfiguration.RELEASE_ENTRY_TOPIC};

            String currentIdentifier = exchangeKeyMap.get(market) + "_" + CommonRabbitConfiguration.uuid();

            HashMap<String, Queue> exchangeQueueMap = new HashMap<>();
            for (String queueName : queueNames) {
                Queue topic = new Queue(queueName + currentIdentifier, false, false, true);
                exchangeQueueMap.put(queueName, topic);

                rabbitAdmin.declareQueue(topic);
                rabbitAdmin.declareBinding(new Binding(queueName + currentIdentifier,
                        Binding.DestinationType.QUEUE,
                        exchange.getName(), queueName,
                        new HashMap<String, Object>()));

            }

            //init market rpc queue
            Queue marketRPCQueue = new Queue(CommonRabbitConfiguration.MARKET_RPC + exchangeKeyMap.get(market));
            exchangeQueueMap.put(CommonRabbitConfiguration.MARKET_RPC, marketRPCQueue);

            rabbitAdmin.declareQueue(marketRPCQueue);
            rabbitAdmin.declareBinding(BindingBuilder.bind(marketRPCQueue).to(exchange).with(CommonRabbitConfiguration.MARKET_RPC));

            queueMap.put(market, exchangeQueueMap);

        }
        ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton("queueMap", queueMap);
        //((ConfigurableApplicationContext) context).refresh();
    }

    /**
     * On pre destroy.
     */
    @PreDestroy
    public void onPreDestroy() {
        //delete topic queues on shutdown
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {

            for (Queue queue : queueMap.get(market).values()) {

                if (!queue.getName().equals(CommonRabbitConfiguration.MARKET_RPC + exchangeKeyMap.get(market)))
                    rabbitAdminMap.get(market).deleteQueue(queue.getName());
            }
        }
    }
}
