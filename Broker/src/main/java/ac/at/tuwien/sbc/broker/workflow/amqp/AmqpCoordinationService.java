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
    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction) throws CoordinationServiceException {
        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{ide.getInvestorID()});
        template.convertAndSend("marketRPC", request);

        //write investor
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_INVESTOR_DEPOT_ENTRY, null, ide);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction) throws CoordinationServiceException {
        //write order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, oe);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public OrderEntry getOrderByTemplate(OrderEntry oe, Object sharedTransaction) {
        return null;
    }

    @Override
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Double price, Object sharedTransaction) {
        return null;
    }

    @Override
    public ArrayList<ShareEntry> readShares() {
        return null;
    }

    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {
        return null;
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

    }

    @Override
    public Object createTransaction(long timeout) {
        return null;
    }

    @Override
    public void commitTransaction(Object sharedTransaction) {

    }

    @Override
    public Boolean transactionIsRunning(Object sharedTransaction) {
        return null;
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

    public void onReleaseEntryMessage(ArrayList<ReleaseEntry> list) {
        if (releaseEntryMessageListener != null)
            releaseEntryMessageListener.onResult(list);
    }
}
