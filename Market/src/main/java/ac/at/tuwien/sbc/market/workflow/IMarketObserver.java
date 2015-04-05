package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;

/**
 * This interface shall be implemented by view layer components,
 * who want to display states of the market.
 */
public interface IMarketObserver {

    //To be defined in which format MarketAgent publishes stockprice
    public void onStockpriceChanged();

    public void onTransactionAdded(TransactionEntry transactionEntry);
    public void onOrderAdded(OrderEntry orderEntry);
    public void onShareAdded(ShareEntry shareEntry);
}
