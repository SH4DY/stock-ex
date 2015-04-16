package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    /**
     * Get investor by id.
     *
     * @param investorId the investor id
     * @param sharedTransaction the shared transaction
     * @return the investor
     */
    public InvestorDepotEntry getInvestor(Integer investorId, Object sharedTransaction);

    /**
     * Set/add investor.
     *
     * @param ide the ide
     * @param sharedTransaction the shared transaction
     * @param isRollbackAction the is rollback action
     * @throws CoordinationServiceException the coordination service exception
     */
    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException;

    /**
     * Add order.
     *
     * @param oe the oe
     * @param sharedTransaction the shared transaction
     * @param isRollbackAction the is rollback action
     * @throws CoordinationServiceException the coordination service exception
     */
    public void addOrder(OrderEntry oe, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException;

    /**
     * Get/take order by template.
     *
     * @param shareId the share id
     * @param type the type
     * @param status the status
     * @param price the price
     * @param sharedTransaction the shared transaction
     * @return the order by properties
     */

    /**
     * Get/take order by properties
     * @param shareId
     * @param type
     * @param status
     * @param price
     * @param sharedTransaction
     * @return
     */
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Double price, Object sharedTransaction);

    /**
     * Read/get all shares.
     *
     * @return the array list
     */
    public ArrayList<ShareEntry> readShares();

    /**
     * Get/take on release entry.
     *
     * @param sharedTransaction the shared transaction
     * @return the release entry
     */
    public ReleaseEntry getReleaseEntry(Object sharedTransaction);

    /**
     * Get share by id.
     *
     * @param shareId the share id
     * @param sharedTransaction the shared transaction
     * @return the share entry
     */
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction);

    /**
     * Set share entry.
     *
     * @param se the se
     * @param sharedTransaction the shared transaction
     * @throws CoordinationServiceException the coordination service exception
     */
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException;

    /**
     * Register notifications on release entries.
     *
     * @param cListener the c listener
     */
    public void registerReleaseNotification(CoordinationListener cListener);

    /**
     * Register notifications on order entries.
     *
     * @param cListener the c listener
     */
    public void registerOrderNotification(CoordinationListener cListener);

    /**
     * Register notifications on share entries.
     *
     * @param cListener the c listener
     */
    public void registerShareNotification(CoordinationListener cListener);

    /**
     * Add transaction entry.
     *
     * @param te the te
     * @param sharedTransaction the shared transaction
     * @throws CoordinationServiceException the coordination service exception
     */
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException;

    /**
     * Create coordination transaction.
     *
     * @param timeout the timeout
     * @return the object
     */
    public Object createTransaction(long timeout);

    /**
     * Commit coordination transaction.
     *
     * @param sharedTransaction the shared transaction
     */
    public void commitTransaction(Object sharedTransaction);

    /**
     * Rollback coordination transaction.
     *
     * @param sharedTransaction the shared transaction
     */
    public void rollbackTransaction(Object sharedTransaction);

}
