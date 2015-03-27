package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void getInvestor(Integer investorId, CoordinationListener clistener);

    public void setInvestor(InvestorDepotEntry ide);

    public void addOrder(OrderEntry oe);

    public void getOrders(Integer id, CoordinationListener cListener);
}
