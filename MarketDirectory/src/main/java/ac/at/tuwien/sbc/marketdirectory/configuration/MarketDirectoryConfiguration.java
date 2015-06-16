package ac.at.tuwien.sbc.marketdirectory.configuration;

import ac.at.tuwien.sbc.domain.service.IMarketDirectoryService;
import ac.at.tuwien.sbc.marketdirectory.service.MarketDirectoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
/**
 * Created by dietl_ma on 16/06/15.
 */
@Configuration
public class MarketDirectoryConfiguration {

    @Bean
    IMarketDirectoryService marketDirectoryService() {
        System.out.println("LKJHLKAJSHLKJAHSKLJAHKLJAHSKLJHASKJHAKLJHSKLJAHS");
        return new MarketDirectoryService();
    }

    @Bean
    RmiServiceExporter serviceExporter(IMarketDirectoryService marketDirectoryService) {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("MarketDirectoryService");
        rmiServiceExporter.setServiceInterface(IMarketDirectoryService.class);
        rmiServiceExporter.setService(marketDirectoryService);
        rmiServiceExporter.setRegistryPort(1199);
        return rmiServiceExporter;
    }
}
