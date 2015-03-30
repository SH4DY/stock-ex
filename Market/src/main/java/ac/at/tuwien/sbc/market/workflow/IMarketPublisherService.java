package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

/**
 * Created by shady on 28/03/15.
 */
public interface IMarketPublisherService {
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener);
    public void getOrders(CoordinationListener listener);
}
