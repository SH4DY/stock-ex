package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

// TODO: Auto-generated Javadoc
/**
 * Created by shady on 28/03/15.
 */
public interface IMarketPublisherService {
    
    /**
     * Register order observer.
     *
     * @param coordinationListener the coordination listener
     */
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener);
    
    /**
     * Gets the orders.
     *
     * @param listener the listener
     * @return the orders
     */
    public void getOrders(CoordinationListener listener);

    /**
     * Register transaction observer.
     *
     * @param coordinationListener the coordination listener
     */
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener);
    
    /**
     * Gets the transactions.
     *
     * @param listener the listener
     * @return the transactions
     */
    public void getTransactions(CoordinationListener listener);

    /**
     * Register share observer.
     *
     * @param coordinationListener the coordination listener
     */
    public void registerShareObserver(CoordinationListener<ShareEntry> coordinationListener);
    
    /**
     * Gets the shares.
     *
     * @param listener the listener
     * @return the shares
     */
    public void getShares(CoordinationListener listener);
}
