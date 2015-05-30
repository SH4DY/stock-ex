package ac.at.tuwien.sbc.apprunner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * Created by dietl_ma on 14/04/15.
 */
@SpringBootApplication
@EnableScheduling
public class AppRunner {

    public static void main(String[] args) throws IOException {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(AppRunner.class)
                .headless(false)
                .web(false)
                .run(args);


        ctx.registerShutdownHook();
        /*Process p = Runtime.getRuntime().exec("java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=amqp  --id=567 --budget=10000");
        Process p2 = Runtime.getRuntime().exec("java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=amqp  --id=567 --budget=10000");

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        */
    }
}
