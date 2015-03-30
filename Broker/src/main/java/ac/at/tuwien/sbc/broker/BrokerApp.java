package ac.at.tuwien.sbc.broker;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@EnableScheduling
@Import({CommonRabbitConfiguration.class, CommonSpaceConfiguration.class})
public class BrokerApp {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(BrokerApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(BrokerApp.class)
                .headless(false)
                .web(false)
                .run(args);


        ctx.registerShutdownHook();

    }


}
