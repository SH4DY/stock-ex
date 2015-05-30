package ac.at.tuwien.sbc.broker.workflow.amqp;

import ac.at.tuwien.sbc.broker.workflow.ICoordinationService;
import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 03/04/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {

    /** The investor entry notification listener. */
    private CoordinationListener<ArrayList<DepotEntry>> investorEntryNotificationListener;
    
    /** The share entry notification listener. */
    private CoordinationListener<ArrayList<ShareEntry>> shareEntryNotificationListener;
    
    /** The order entry notification listener. */
    private CoordinationListener<ArrayList<OrderEntry>> orderEntryNotificationListener;
    
    /** The release entry message listener. */
    private CoordinationListener<ArrayList<ReleaseEntry>> releaseEntryMessageListener;

    /** The template. */
    @Autowired
    private RabbitTemplate template;


    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getDepot(java.lang.Integer, java.lang.Object)
     */
    @Override
    public DepotEntry getDepot(String depotId, Object sharedTransaction) {

        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_DEPOT_ENTRY_BY_ID, new Object[]{depotId});
        ArrayList<DepotEntry> entries = (ArrayList<DepotEntry>)template.convertSendAndReceive(CommonRabbitConfiguration.MARKET_RPC, request);
        DepotEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#setDepot(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry, java.lang.Object, java.lang.Boolean)
     */
    @Override
    public void setDepot(DepotEntry de, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {

        if (de == null)
            return;

        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_DEPOT_ENTRY_BY_ID, new Object[]{de.getId()});
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);

        //write investor
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_DEPOT_ENTRY, null, de, isRollbackAction);
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#addOrder(ac.at.tuwien.sbc.domain.entry.OrderEntry, java.lang.Object, java.lang.Boolean)
     */
    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {
        //write order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, oe, isRollbackAction);
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getOrderByProperties(java.lang.String, ac.at.tuwien.sbc.domain.enums.OrderType, ac.at.tuwien.sbc.domain.enums.OrderStatus, java.lang.Double, java.lang.Object)
     */
    @Override
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Boolean prioritized, Double price, Object sharedTransaction) {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_ORDER_BY_PROPERTIES,
                                                          new Object[]{shareId, type, status, prioritized, price});
        ArrayList<OrderEntry> entries = (ArrayList<OrderEntry>)template.convertSendAndReceive(CommonRabbitConfiguration.MARKET_RPC, request);
        OrderEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#readShares()
     */
    @Override
    public ArrayList<ShareEntry> readShares() {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        ArrayList<ShareEntry> entries = (ArrayList<ShareEntry>)template.convertSendAndReceive(CommonRabbitConfiguration.MARKET_RPC, request);
        return entries;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getReleaseEntry(java.lang.Object)
     */
    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_RELEASE_ENTRY, null, null);
        ArrayList<ReleaseEntry> entries = (ArrayList<ReleaseEntry>)template.convertSendAndReceive(CommonRabbitConfiguration.MARKET_RPC, request);
        ReleaseEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getShareEntry(java.lang.String, java.lang.Object)
     */
    @Override
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction) {
        ShareEntry se = null;
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRY_BY_ID, new Object[]{shareId});
        ArrayList<ShareEntry> result = (ArrayList<ShareEntry>)template.convertSendAndReceive(CommonRabbitConfiguration.MARKET_RPC, request);

        if (result != null && !result.isEmpty())
            se = result.get(0);

        return se;

    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#setShareEntry(ac.at.tuwien.sbc.domain.entry.ShareEntry, java.lang.Object)
     */
    @Override
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException {
        //delete share
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_SHARE_ENTRY_BY_ID, new Object[]{se.getShareID()});
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);

        //write share
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_SHARE_ENTRY, null, se);
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerReleaseNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerReleaseNotification(CoordinationListener cListener) {
        releaseEntryMessageListener = cListener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerOrderNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderNotification(CoordinationListener cListener) {
        orderEntryNotificationListener = cListener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerShareNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareNotification(CoordinationListener cListener) {
        shareEntryNotificationListener = cListener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#addTransaction(ac.at.tuwien.sbc.domain.entry.TransactionEntry, java.lang.Object)
     */
    @Override
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException {
        //write share
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_TRANSACTION_ENTRY, null, te);
        template.convertAndSend(CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#createTransaction(long)
     */
    @Override
    public Object createTransaction(long timeout, String market) {
        return null;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#commitTransaction(java.lang.Object)
     */
    @Override
    public void commitTransaction(Object sharedTransaction) {

    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#rollbackTransaction(java.lang.Object)
     */
    @Override
    public void rollbackTransaction(Object sharedTransaction) {

    }

    /**
     * On investor entry notification.
     *
     * @param list the list
     */
    public void onInvestorEntryNotification(ArrayList<DepotEntry> list) {
        if (investorEntryNotificationListener != null)
            investorEntryNotificationListener.onResult(list);
    }

    /**
     * On share entry notification.
     *
     * @param list the list
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        if (shareEntryNotificationListener != null)
            shareEntryNotificationListener.onResult(list);
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        if (orderEntryNotificationListener != null)
            orderEntryNotificationListener.onResult(list);
    }

    /**
     * On release entry notification.
     *
     * @param list the list
     */
    public void onReleaseEntryNotification(ArrayList<ReleaseEntry> list) {
        if (releaseEntryMessageListener != null)
            releaseEntryMessageListener.onResult(list);
    }
}
