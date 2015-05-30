package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;

import java.util.ArrayList;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    /**
     * Gets the investor.
     *
     * @param cListener the c listener
     * @param market
     *@param depotId the investor id  @return the investor
     */
    public void getDepot(CoordinationListener cListener, String market, String depotId);

    /**
     * Gets the shares.
     *
     * @param shareIds the share ids
     * @param market
     *@param cListener the c listener  @return the shares
     */
    public void getShares(ArrayList<String> shareIds, String market, CoordinationListener cListener);

    /**
     * Register investor notification.
     *
     * @param cListener the c listener
     * @param market
     */
    public void registerDepotNotification(CoordinationListener cListener, String market);

    /**
     * Register order notification.
     *
     * @param cListener the c listener
     * @param market
     */
    public void registerOrderNotification(CoordinationListener cListener, String market);

    /**
     * Register share notification.
     *
     * @param cListener the c listener
     * @param market
     */
    public void registerShareNotification(CoordinationListener cListener, String market);

    /**
     * Sets the investor.
     *  @param de the new investor
     * @param market
     */
    public void setDepot(DepotEntry de, String market);

    /**
     * Adds the order.
     *
     * @param oe the oe
     * @param market
     */
    public void addOrder(OrderEntry oe, String market);

    /**
     * Gets the orders.
     *  @param depotId the investor id
     * @param market
     * @param cListener the c listener  @return the orders
     */
    public void getOrders(String depotId, String market, CoordinationListener cListener);

    /**
     * Delete order.
     *
     * @param orderID the order id
     * @param market
     */
    public void deleteOrder(UUID orderID, String market);

    public void makeRelease(ReleaseEntry re, String market);
}
