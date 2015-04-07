package ac.at.tuwien.sbc.broker.workflow.amqp;

import ac.at.tuwien.sbc.broker.workflow.ICoordinationService;
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

/**
 * Created by dietl_ma on 03/04/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {

    private CoordinationListener<ArrayList<InvestorDepotEntry>> investorEntryNotificationListener;
    private CoordinationListener<ArrayList<ShareEntry>> shareEntryNotificationListener;
    private CoordinationListener<ArrayList<OrderEntry>> orderEntryNotificationListener;
    private CoordinationListener<ArrayList<ReleaseEntry>> releaseEntryMessageListener;

    @Autowired
    private RabbitTemplate template;


    @Override
    public InvestorDepotEntry getInvestor(Integer investorId, Object sharedTransaction) {

        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{investorId});
        ArrayList<InvestorDepotEntry> entries = (ArrayList<InvestorDepotEntry>)template.convertSendAndReceive("marketRPC", request);
        InvestorDepotEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    @Override
    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {

        if (ide == null)
            return;

        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{ide.getInvestorID()});
        template.convertAndSend("marketRPC", request);

        //write investor
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_INVESTOR_DEPOT_ENTRY, null, ide, isRollbackAction);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {
        //write order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, oe, isRollbackAction);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Double price, Object sharedTransaction) {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_ORDER_BY_PROPERTIES,
                                                          new Object[]{shareId, type, status, price});
        ArrayList<OrderEntry> entries = (ArrayList<OrderEntry>)template.convertSendAndReceive("marketRPC", request);
        OrderEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    @Override
    public ArrayList<ShareEntry> readShares() {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        ArrayList<ShareEntry> entries = (ArrayList<ShareEntry>)template.convertSendAndReceive("marketRPC", request);
        return entries;
    }

    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_RELEASE_ENTRY, null, null);
        ArrayList<ReleaseEntry> entries = (ArrayList<ReleaseEntry>)template.convertSendAndReceive("marketRPC", request);
        ReleaseEntry entry = null;

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    @Override
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction) {
        ShareEntry se = null;
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRY_BY_ID, new Object[]{shareId});
        ArrayList<ShareEntry> result = (ArrayList<ShareEntry>)template.convertSendAndReceive("marketRPC", request);

        if (result != null && !result.isEmpty())
            se = result.get(0);

        return se;

    }

    @Override
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException {
        //delete share
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_SHARE_ENTRY_BY_ID, new Object[]{se.getShareID()});
        template.convertAndSend("marketRPC", request);

        //write share
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_SHARE_ENTRY, null, se);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public void registerReleaseNotification(CoordinationListener cListener) {
        releaseEntryMessageListener = cListener;
    }

    @Override
    public void registerOrderNotification(CoordinationListener cListener) {
        orderEntryNotificationListener = cListener;
    }

    @Override
    public void registerShareNotification(CoordinationListener cListener) {
        shareEntryNotificationListener = cListener;
    }

    @Override
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException {
        //write share
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_TRANSACTION_ENTRY, null, te);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public Object createTransaction(long timeout) {
        return null;
    }

    @Override
    public void commitTransaction(Object sharedTransaction) {

    }

    @Override
    public void rollbackTransaction(Object sharedTransaction) {

    }

    public void onInvestorEntryNotification(ArrayList<InvestorDepotEntry> list) {
        if (investorEntryNotificationListener != null)
            investorEntryNotificationListener.onResult(list);
    }

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        if (shareEntryNotificationListener != null)
            shareEntryNotificationListener.onResult(list);
    }

    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        if (orderEntryNotificationListener != null)
            orderEntryNotificationListener.onResult(list);
    }

    public void onReleaseEntryNotification(ArrayList<ReleaseEntry> list) {
        if (releaseEntryMessageListener != null)
            releaseEntryMessageListener.onResult(list);
    }
}
