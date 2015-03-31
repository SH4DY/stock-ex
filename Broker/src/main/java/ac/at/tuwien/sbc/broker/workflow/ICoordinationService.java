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

    public InvestorDepotEntry getInvestor(Integer investorId, Object sharedTransaction);

    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction) throws CoordinationServiceException;

    public void addOrder(OrderEntry oe, Object sharedTransaction) throws CoordinationServiceException;

    public OrderEntry getOrderByTemplate(OrderEntry oe, Object sharedTransaction);

    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Double price, Object sharedTransaction);

    public ArrayList<ShareEntry> readShares();

    public ReleaseEntry getReleaseEntry(Object sharedTransaction);

    public ShareEntry getShareEntry(String shareId, Object sharedTransaction);

    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException;

    public void registerReleaseNotification(CoordinationListener cListener);

    public void registerOrderNotification(CoordinationListener cListener);

    public void registerShareNotification(CoordinationListener cListener);

    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException;

    public Object createTransaction(long timeout);

    public void commitTransaction(Object sharedTransaction);

    public Boolean transactionIsRunning(Object sharedTransaction);

    public void rollbackTransaction(Object sharedTransaction);

}
