package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;

// TODO: Auto-generated Javadoc
/**
 * This interface shall be implemented by view layer components,
 * who want to display states of the market.
 */
public interface IMarketObserver {

    //To be defined in which format MarketAgent publishes stockprice
    /**
     * This method is called when information about an IMarket
     * which was previously requested using an asynchronous
     * interface becomes available.
     */
    public void onStockpriceChanged();

    /**
     * This method is called when information about an IMarket
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param transactionEntry the transaction entry
     */
    public void onTransactionAdded(TransactionEntry transactionEntry);
    
    /**
     * This method is called when information about an IMarket
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param orderEntry the order entry
     */
    public void onOrderAdded(OrderEntry orderEntry);
    
    /**
     * This method is called when information about an IMarket
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param shareEntry the share entry
     */
    public void onShareAdded(ShareEntry shareEntry);
}
