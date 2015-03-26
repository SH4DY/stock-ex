package ac.at.tuwien.sbc.investor;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@Import({CommonRabbitConfiguration.class, CommonSpaceConfiguration.class})
public class InvestorApp {



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(InvestorApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InvestorApp.class)
                .headless(false)
                .web(false)
                .run(args);

        logger.info("HI IM AN INVESTOR");

       // ctx.registerShutdownHook();

    }
}
