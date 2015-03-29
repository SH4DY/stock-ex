package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void getInvestor(Integer investorId, CoordinationListener clistener);

    public void setInvestor(InvestorDepotEntry ide);

    public void addOrder(OrderEntry oe, Object sharedTransaction);

    public void getOrders(Integer id, CoordinationListener cListener);

    public ReleaseEntry getReleaseEntry(Object sharedTransaction);

    public ShareEntry getShareEntry(String shareId, Object sharedTransaction);

    public void setShareEntry(ShareEntry se, Object sharedTransaction);

    public void registerReleaseNotification(CoordinationListener clistener);

    public Object createTransaction(long timeout);

    public void commitTransaction(Object sharedTransaction);

    public void rollbackTransaction(Object sharedTransaction);


}
