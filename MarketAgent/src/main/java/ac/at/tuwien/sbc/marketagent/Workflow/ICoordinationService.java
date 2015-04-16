package ac.at.tuwien.sbc.marketagent.workflow;

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
     */
    public ArrayList<ShareEntry> getShares();

    /**
     * Sets the share entry.
     *
     * @param se the new share entry
     */
    public void setShareEntry(ShareEntry se);

    /**
     * Gets the orders by properties.
     *
     * @param shareId the share id
     * @param type the type
     * @return the orders by properties
     */
    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type);
}
