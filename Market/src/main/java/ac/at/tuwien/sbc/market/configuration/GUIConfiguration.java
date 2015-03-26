package ac.at.tuwien.sbc.market.configuration;

import ac.at.tuwien.sbc.market.gui.MainGUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
public class GUIConfiguration {
    @Bean
    public MainGUI mainGUI() {
        return new MainGUI();
    }
}
