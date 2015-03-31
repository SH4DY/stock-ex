package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface IWorkFlowObserver {

    public void onInvestorDepotEntryNotification(InvestorDepotEntry ide);
    public void onOrderEntryNotification(OrderEntry oe);
    public void onShareEntryNotification(ShareEntry se);
}
