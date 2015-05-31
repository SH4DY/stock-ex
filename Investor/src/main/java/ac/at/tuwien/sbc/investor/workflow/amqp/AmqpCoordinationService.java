package ac.at.tuwien.sbc.investor.workflow.amqp;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.investor.workflow.ICoordinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {

    /** The investor entry notification listener. */
    private HashMap<String, CoordinationListener<ArrayList<DepotEntry>>> depotEntryNotificationListener = new HashMap<>();
    
    /** The share entry notification listener. */
    private HashMap<String, CoordinationListener<ArrayList<ShareEntry>>> shareEntryNotificationListener = new HashMap<>();
    
    /** The order entry notification listener. */
    private HashMap<String, CoordinationListener<ArrayList<OrderEntry>>> orderEntryNotificationListener = new HashMap<>();

    /** The template. */
    @Autowired
    private HashMap<String,RabbitTemplate> templateMap;

    @Autowired
    @Qualifier("exchangeKeyMap")
    private HashMap<String, String> exchangeKeyMap;

    //private HashMap<String, >

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AmqpCoordinationService.class);


    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getDepot(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getDepot(CoordinationListener cListener, String market, String depotId) {

        DepotEntry entry = null;
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_DEPOT_ENTRY_BY_ID, new Object[]{depotId});
        ArrayList<DepotEntry> result = (ArrayList<DepotEntry>)templateMap.get(market)
                .convertSendAndReceive(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
        if (result != null && !result.isEmpty())
            entry = (DepotEntry)result.toArray()[0];

        cListener.onResult(entry);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getShares(java.util.ArrayList, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getShares(ArrayList<String> shareIds, String market, CoordinationListener cListener) {

        RPCMessageRequest request = null;
        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        for (String shareId : shareIds) {

            request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRY_BY_ID, new Object[]{shareId});
            ArrayList<ShareEntry> currentEntries = (ArrayList<ShareEntry>)templateMap.get(market)
                    .convertSendAndReceive(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
            if (currentEntries != null && !currentEntries.isEmpty())
                entries.add(currentEntries.get(0));

        }
        cListener.onResult(entries);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerDepotNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerDepotNotification(CoordinationListener cListener, String market) {
        depotEntryNotificationListener.put(market, cListener);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerOrderNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderNotification(CoordinationListener cListener, String market) {
        orderEntryNotificationListener.put(market, cListener);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerShareNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareNotification(CoordinationListener cListener, String market) {
       shareEntryNotificationListener.put(market, cListener);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#setDepot(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry)
     */
    @Override
    public void setDepot(DepotEntry de, String market) {

        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_DEPOT_ENTRY_BY_ID, new Object[]{de.getId()});
        templateMap.get(market).convertAndSend(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);

        //write investor
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_DEPOT_ENTRY, null, de);
        templateMap.get(market).convertAndSend(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#addOrder(ac.at.tuwien.sbc.domain.entry.OrderEntry)
     */
    @Override
    public void addOrder(OrderEntry oe, String market) {
        //write order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, oe);
        templateMap.get(market).convertAndSend(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getOrders(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getOrders(String depotId, String market, CoordinationListener cListener) {

        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_ORDER_ENTRIES_BY_DEPOT_ID, new Object[]{depotId});
        ArrayList<OrderEntry> result = (ArrayList<OrderEntry>)templateMap.get(market)
                .convertSendAndReceive(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
        cListener.onResult(result);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#deleteOrder(java.util.UUID)
     */
    @Override
    public void deleteOrder(UUID orderID, String market) {
        //delete order
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.TAKE_ORDER_BY_ORDER_ID, new Object[]{orderID});
        ArrayList<OrderEntry> result = (ArrayList<OrderEntry>)templateMap.get(market)
                .convertSendAndReceive(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);

        if (result != null && !result.isEmpty()) {
            result.get(0).setStatus(OrderStatus.DELETED);
            request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_ORDER_ENTRY, null, result.get(0));
            templateMap.get(market).convertAndSend(exchangeKeyMap.get(market),
                    CommonRabbitConfiguration.MARKET_RPC, request);
        }

    }

    @Override
    public void makeRelease(ReleaseEntry re, String market) {
        //write release entry
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_RELEASE_ENTRY, null, re);
        templateMap.get(market).convertAndSend(exchangeKeyMap.get(market), CommonRabbitConfiguration.MARKET_RPC, request);
    }

    /**
     * On investor entry notification.
     *
     * @param list the list
     * @param market
     */
    public void onDepotEntryNotification(ArrayList<DepotEntry> list, String market) {
        if (depotEntryNotificationListener != null)
            depotEntryNotificationListener.get(market).onResult(list);
    }

    /**
     * On share entry notification.
     *
     * @param list the list
     * @param market
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list, String market) {
        if (shareEntryNotificationListener != null)
            shareEntryNotificationListener.get(market).onResult(list);
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     * @param market
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list, String market) {
        if (orderEntryNotificationListener != null)
            orderEntryNotificationListener.get(market).onResult(list);
    }
}
