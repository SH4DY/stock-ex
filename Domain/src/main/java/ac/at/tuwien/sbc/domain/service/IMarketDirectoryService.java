package ac.at.tuwien.sbc.domain.service;

import java.util.List;

/**
 * Created by dietl_ma on 16/06/15.
 */
public interface IMarketDirectoryService {
    public void addMarket(String market);
    public List<String> getMarkets();
}
