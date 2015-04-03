package ac.at.tuwien.sbc.investor.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.investor.workflow.ICoordinationService;
import com.googlecode.cqengine.query.Query;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {

    private CoordinationListener<ArrayList<InvestorDepotEntry>> investorEntryNotificationListener;
    private CoordinationListener<ArrayList<ShareEntry>> shareEntryNotificationListener;
    private CoordinationListener<ArrayList<OrderEntry>> orderEntryNotificationListener;

    @Autowired
    private RabbitTemplate template;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AmqpCoordinationService.class);


    @Override
    public void getInvestor(Integer investorId, CoordinationListener cListener) {

        InvestorDepotEntry entry = null;
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{investorId});
        ArrayList<InvestorDepotEntry> result = (ArrayList<InvestorDepotEntry>)template.convertSendAndReceive("marketRPC", request);
        if (result != null && !result.isEmpty())
            entry = (InvestorDepotEntry)result.toArray()[0];

        cListener.onResult(entry);
    }

    @Override
    public void getShares(ArrayList<String> shareIds, CoordinationListener cListener) {

        RPCMessageRequest request = null;
        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        for (String shareId : shareIds) {

            request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRY_BY_ID, new Object[]{shareId});
            ArrayList<ShareEntry> currentEntries = (ArrayList<ShareEntry>)template.convertSendAndReceive("marketRPC", request);
            if (currentEntries != null && !currentEntries.isEmpty())
                entries.add(currentEntries.get(0));

        }
        cListener.onResult(entries);
    }

    @Override
    public void registerInvestorNotification(CoordinationListener cListener) {
        investorEntryNotificationListener = cListener;
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
    public void setInvestor(InvestorDepotEntry ide) {

        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{ide.getInvestorID()});
        template.convertAndSend("marketRPC", request);

        //write investor
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_INVESTOR_DEPOT_ENTRY, null, ide);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public void addOrder(OrderEntry oe) {
        //write order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, oe);
        template.convertAndSend("marketRPC", request);
    }

    @Override
    public void getOrders(Integer investorId, CoordinationListener cListener) {

        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_ORDER_ENTRIES_BY_INVESTOR_ID, new Object[]{investorId});
        ArrayList<OrderEntry> result = (ArrayList<OrderEntry>)template.convertSendAndReceive("marketRPC", request);
        cListener.onResult(result);
    }

    @Override
    public void deleteOrder(UUID orderID) {
        //delete order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_ORDER_BY_ORDER_ID, new Object[]{orderID});
        ArrayList<OrderEntry> result = (ArrayList<OrderEntry>)template.convertSendAndReceive("marketRPC", request);

        if (!result.isEmpty()) {
            result.get(0).setStatus(OrderStatus.DELETED);
            request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, result.get(0));
            template.convertAndSend("marketRPC", request);
        }

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
}
