package ac.at.tuwien.sbc.apprunner.configuration;

import ac.at.tuwien.sbc.apprunner.gui.MainGUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
public class GUIConfiguration {
    
    /**
     * Main gui.
     *
     * @return the main gui
     */
    @Bean
    public MainGUI mainGUI() {
        return new MainGUI();
    }
}
