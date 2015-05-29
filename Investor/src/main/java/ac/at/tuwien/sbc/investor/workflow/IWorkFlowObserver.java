package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 */
public interface IWorkFlowObserver {

    /**
     * This method is called when information about an IWorkFlow
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param ide the ide
     */
    public void onDepotEntryNotification(DepotEntry ide);
    
    /**
     * This method is called when information about an IWorkFlow
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param oe the oe
     */
    public void onOrderEntryNotification(OrderEntry oe);
    
    /**
     * This method is called when information about an IWorkFlow
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param se the se
     */
    public void onShareEntryNotification(ShareEntry se);
}
