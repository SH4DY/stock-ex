package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.core.MzsCoreException;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void setListener(ICoordinationServiceListener listener);

    public void getInvestor(Integer investorId, CoordinationListener clistener);

    public void registerInvestorNotification(Integer investorId, CoordinationListener clistener);

    public void registerOrderNotification(Integer investorId, CoordinationListener clistener);

    public void setInvestor(InvestorDepotEntry ide);

    public void addOrder(OrderEntry oe);

    public void getOrders(Integer investorId, CoordinationListener cListener);
}
