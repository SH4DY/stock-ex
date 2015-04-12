package ac.at.tuwien.sbc.marketagent.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderType;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 30/03/15.
 */
public interface ICoordinationService {

    public ArrayList<ShareEntry> getShares();

    public void setShareEntry(ShareEntry se);

    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type);
}
