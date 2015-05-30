package ac.at.tuwien.sbc.investor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@ComponentScan({"ac.at.tuwien.sbc.domain.configuration", "ac.at.tuwien.sbc.investor"})
public class InvestorApp {



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(InvestorApp.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InvestorApp.class)
                .headless(false)
                .web(false)
                .run(args);

        for (int i = 0; i < args.length ; i++) {
            logger.info("ARG " + String.valueOf(i) + ": " +args[i]);
        }
        logger.info("HI IM AN INVESTOR");

       // ctx.registerShutdownHook();

    }


}
