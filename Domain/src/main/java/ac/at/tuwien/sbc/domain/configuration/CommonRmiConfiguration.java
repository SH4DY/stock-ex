package ac.at.tuwien.sbc.domain.configuration;

import ac.at.tuwien.sbc.domain.service.IMarketDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * Created by dietl_ma on 16/06/15.
 */
@Configuration
public class CommonRmiConfiguration {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CommonRmiConfiguration.class);

    @Bean
    public IMarketDirectoryService marketDirectoryService() {
        IMarketDirectoryService marketDirectoryService = null;

        try {
            RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
            rmiProxyFactoryBean.setServiceInterface(IMarketDirectoryService.class);
            rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1199/MarketDirectoryService");
            rmiProxyFactoryBean.afterPropertiesSet();
            marketDirectoryService = (IMarketDirectoryService) rmiProxyFactoryBean.getObject();
        }
        catch (Exception e) {
            logger.info("MarketDirectoryService is not available");
        }

        return marketDirectoryService;
    }

}
