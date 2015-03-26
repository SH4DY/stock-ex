package ac.at.tuwien.sbc.investor.configuration;

import ac.at.tuwien.sbc.investor.gui.MainGUI;
import ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
public class GUIConfiguration {
    @Bean(name = "mainGUI")
    public IWorkFlowObserver mainGUI() {
        return new MainGUI();
    }
}
