package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    /**
     * Get investor by id
     * @param investorId
     * @param sharedTransaction
     * @return
     */
    public InvestorDepotEntry getInvestor(Integer investorId, Object sharedTransaction);

    /**
     * Set/add investor
     * @param ide
     * @param sharedTransaction
     * @throws CoordinationServiceException
     */
    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException;

    /**
     * Add order
     * @param oe
     * @param sharedTransaction
     * @throws CoordinationServiceException
     */
    public void addOrder(OrderEntry oe, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException;

    /**
     * Get/take order by template
     * @param oe
     * @param sharedTransaction
     * @return
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
     * Read/get all shares
     * @return
     */
    public ArrayList<ShareEntry> readShares();

    /**
     * Get/take on release entry
     * @param sharedTransaction
     * @return
     */
    public ReleaseEntry getReleaseEntry(Object sharedTransaction);

    /**
     * Get share by id
     * @param shareId
     * @param sharedTransaction
     * @return
     */
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction);

    /**
     * Set share entyr
     * @param se
     * @param sharedTransaction
     * @throws CoordinationServiceException
     */
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException;

    /**
     * Register notifications on release entries
     * @param cListener
     */
    public void registerReleaseNotification(CoordinationListener cListener);

    /**
     * Register notifications on order entries
     * @param cListener
     */
    public void registerOrderNotification(CoordinationListener cListener);

    /**
     * Register notifications on share entries
     * @param cListener
     */
    public void registerShareNotification(CoordinationListener cListener);

    /**
     * Add transaction entry
     * @param te
     * @param sharedTransaction
     * @throws CoordinationServiceException
     */
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException;

    /**
     * Create coordination transaction
     * @param timeout
     * @return
     */
    public Object createTransaction(long timeout);

    /**
     * Commit coordination transaction
     * @param sharedTransaction
     */
    public void commitTransaction(Object sharedTransaction);

    /**
     * Rollback coordination transaction
     * @param sharedTransaction
     */
    public void rollbackTransaction(Object sharedTransaction);

}
