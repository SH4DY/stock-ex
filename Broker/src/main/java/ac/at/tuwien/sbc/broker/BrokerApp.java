package ac.at.tuwien.sbc.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@ComponentScan({"ac.at.tuwien.sbc.domain.configuration", "ac.at.tuwien.sbc.broker"})
public class BrokerApp {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(BrokerApp.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(BrokerApp.class)
                .headless(false)
                .web(false)
                .run(args);


        ctx.registerShutdownHook();

    }


}
