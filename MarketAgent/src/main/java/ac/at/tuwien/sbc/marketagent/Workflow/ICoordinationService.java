package ac.at.tuwien.sbc.marketagent.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderType;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 30/03/15.
 */
public interface ICoordinationService {

    /**
     * Gets the shares.
     *
     * @return the shares
     * @param market
     */
    public ArrayList<ShareEntry> getShares(String market);

    /**
     * Sets the share entry.
     *
     * @param se the new share entry
     * @param market
     */
    public void setShareEntry(ShareEntry se, String market);

    /**
     * Gets the orders by properties.
     *
     * @param shareId the share id
     * @param type the type
     * @param market
     * @return the orders by properties
     */
    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type, String market);

    public DepotEntry getDepot(String depotId, String market);
}
