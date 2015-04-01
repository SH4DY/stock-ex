package ac.at.tuwien.sbc.company;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by dietl_ma on 25/03/15.
 */

@SpringBootApplication
@Import({CommonRabbitConfiguration.class, CommonSpaceConfiguration.class})
public class CompanyApp {

    /** The Constant logger. */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CompanyApp.class);


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(CompanyApp.class)
                .headless(true)
                .web(false)
                .run(args);

        logger.info("COMPANY started successfully");

    }

}
