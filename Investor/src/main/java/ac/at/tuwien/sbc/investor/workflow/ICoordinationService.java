package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.core.MzsCoreException;

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
     * @param investorId the investor id
     * @param cListener the c listener
     * @return the investor
     */
    public void getInvestor(Integer investorId, CoordinationListener cListener);

    /**
     * Gets the shares.
     *
     * @param shareIds the share ids
     * @param cListener the c listener
     * @return the shares
     */
    public void getShares(ArrayList<String> shareIds, CoordinationListener cListener);

    /**
     * Register investor notification.
     *
     * @param cListener the c listener
     */
    public void registerInvestorNotification(CoordinationListener cListener);

    /**
     * Register order notification.
     *
     * @param cListener the c listener
     */
    public void registerOrderNotification(CoordinationListener cListener);

    /**
     * Register share notification.
     *
     * @param cListener the c listener
     */
    public void registerShareNotification(CoordinationListener cListener);

    /**
     * Sets the investor.
     *
     * @param ide the new investor
     */
    public void setInvestor(InvestorDepotEntry ide);

    /**
     * Adds the order.
     *
     * @param oe the oe
     */
    public void addOrder(OrderEntry oe);

    /**
     * Gets the orders.
     *
     * @param investorId the investor id
     * @param cListener the c listener
     * @return the orders
     */
    public void getOrders(Integer investorId, CoordinationListener cListener);

    /**
     * Delete order.
     *
     * @param orderID the order id
     */
    public void deleteOrder(UUID orderID);
}
