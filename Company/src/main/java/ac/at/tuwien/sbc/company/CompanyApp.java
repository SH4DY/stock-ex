package ac.at.tuwien.sbc.company;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@Import({CommonRabbitConfiguration.class, CommonSpaceConfiguration.class})
public class CompanyApp {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CompanyApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(CompanyApp.class)
                .headless(false)
                .web(false)
                .run(args);

        logger.info("HI IM A COMPANY");
        ctx.registerShutdownHook();

    }

}
