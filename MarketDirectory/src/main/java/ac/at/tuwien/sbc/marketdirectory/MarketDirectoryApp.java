package ac.at.tuwien.sbc.marketdirectory;

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
@ComponentScan({"ac.at.tuwien.sbc.marketdirectory"})
public class MarketDirectoryApp {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MarketDirectoryApp.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MarketDirectoryApp.class)
                .headless(false)
                .web(false)
                .run(args);

        logger.info("HI IM A MARKET DIRECTORY");
//        ctx.registerShutdownHook();

    }
}
