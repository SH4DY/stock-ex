package ac.at.tuwien.sbc.marketdirectory.service;

import ac.at.tuwien.sbc.domain.service.IMarketDirectoryService;
import ac.at.tuwien.sbc.marketdirectory.MarketDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by dietl_ma on 16/06/15.
 */
public class MarketDirectoryService implements IMarketDirectoryService {

    @Autowired
    private MarketDirectory marketDirectory;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MarketDirectoryService.class);

    @Override
    public void addMarket(String market) {
        if (!marketDirectory.getDirectory().contains(market))
            marketDirectory.getDirectory().add(market);
        logger.info("added new market: " + market);
    }

    @Override
    public List<String> getMarkets() {
        return marketDirectory.getDirectory();
    }
}
