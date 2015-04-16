package ac.at.tuwien.sbc.marketagent;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@EnableScheduling
@Import({CommonRabbitConfiguration.class, CommonSpaceConfiguration.class})
public class MarketAgentApp {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MarketAgentApp.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MarketAgentApp.class)
                .headless(false)
                .web(false)
                .run(args);

        logger.info("HI IM A MARKETAGENT");
        ctx.registerShutdownHook();

    }
}
