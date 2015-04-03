package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.core.MzsCoreException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void getInvestor(Integer investorId, CoordinationListener cListener);

    public void getShares(ArrayList<String> shareIds, CoordinationListener cListener);

    public void registerInvestorNotification(CoordinationListener cListener);

    public void registerOrderNotification(CoordinationListener cListener);

    public void registerShareNotification(CoordinationListener cListener);

    public void setInvestor(InvestorDepotEntry ide);

    public void addOrder(OrderEntry oe);

    public void getOrders(Integer investorId, CoordinationListener cListener);

    public void deleteOrder(UUID orderID);
}
