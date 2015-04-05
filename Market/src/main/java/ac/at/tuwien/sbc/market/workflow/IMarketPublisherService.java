package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

/**
 * Created by shady on 28/03/15.
 */
public interface IMarketPublisherService {
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener);
    public void getOrders(CoordinationListener listener);

    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener);
    public void getTransactions(CoordinationListener listener);
}
