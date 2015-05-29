package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.market.store.MarketStore;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.simple.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.*;

// TODO: Auto-generated Javadoc
/**
 * Acts as the central point to call methods. All the different actors
 * (companies, investors, brokers...) call it to retrieve data or set data.
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class RPCMessageHandler {

    /** The store. */
    @Autowired
    private MarketStore store;

    /** The template. */
    @Autowired
    private RabbitTemplate template;

    /** The topic map. */
    private HashMap<Class, String> topicMap = new HashMap<>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RPCMessageHandler.class);

    /**
     * Instantiates a new RPC message handler.
     */
    public RPCMessageHandler() {
        topicMap.put(DepotEntry.class, CommonRabbitConfiguration.INVESTOR_ENTRY_TOPIC);
        topicMap.put(ShareEntry.class, CommonRabbitConfiguration.SHARE_ENTRY_TOPIC);
        topicMap.put(OrderEntry.class, CommonRabbitConfiguration.ORDER_ENTRY_TOPIC);
        topicMap.put(TransactionEntry.class, CommonRabbitConfiguration.TRANSACTION_ENTRY_TOPIC);
    }

    /**
     * Main entry point for actors. Method is chosen based on request enum.
     * @param request Which method to call on the market.
     * @return Returns a list of SuperEntries if the request was a read operation. Else the result is undefined.
     */
    public List<SuperEntry> handleMessage(RPCMessageRequest request) {

        logger.info("RECEIVED CALL:" + request.getMethod());
        //ArrayList<Object> result = null;
        List<SuperEntry> result = null;
        List<SuperEntry> notification = new ArrayList<SuperEntry>();
        switch (request.getMethod()) {
            case GET_DEPOT_ENTRY_BY_ID:
                result = doGetInvestorDepotEntry((String)request.getArgs()[0]);
                break;
            case TAKE_DEPOT_ENTRY_BY_ID:
                result = doTakeInvestorDepotEntry((String)request.getArgs()[0]);
                break;
            case DELETE_DEPOT_ENTRY_BY_ID:
                doDeleteInvestorDepotEntry((String)request.getArgs()[0]);
                break;
            case WRITE_DEPOT_ENTRY:
                doWriteInvestorDepotEntry(request.getEntry());
                if (!request.getIsRollBackAction()) {
                    notification.add(request.getEntry());
                    template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.INVESTOR_ENTRY_TOPIC, notification);
                }
                break;
            case GET_ORDER_ENTRIES:
                result = doGetOrderEntries();
                break;
            case TAKE_ORDER_BY_ORDER_ID:
                return doTakeOrderByOrderId((UUID)request.getArgs()[0]);
            case GET_ORDER_ENTRIES_BY_DEPOT_ID:
                result = doGetOrdersByInvestorId((String)request.getArgs()[0]);
                break;
            case TAKE_ORDER_BY_PROPERTIES:
                Object[] args = request.getArgs();
                result = doTakeOrderByProperties((String)args[0], (OrderType)args[1], (OrderStatus)args[2], (Boolean)args[3] , (Double)args[4]);
                break;
            case READ_ORDER_BY_PROPERTIES:
                Object[] args_read = request.getArgs();
                result = doReadOrderByProperties((String)args_read[0], (OrderType)args_read[1], (OrderStatus)args_read[2]);
                break;
            case WRITE_ORDER_ENTRY:
                doWriteOrderEntry(request.getEntry());
                if (!request.getIsRollBackAction()) {
                    notification.add(request.getEntry());
                    template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.ORDER_ENTRY_TOPIC, notification);
                }
                break;
            case DELETE_ORDER_ENTRY_BY_ID:
                doDeleteOrderEntry((UUID)request.getArgs()[0]);
                break;
            case GET_SHARE_ENTRY_BY_ID:
                result = doGetShareEntry((String)request.getArgs()[0]);
                break;
            case GET_SHARE_ENTRIES:
                result = doGetShareEntries();
                break;
            case DELETE_SHARE_ENTRY_BY_ID:
                doDeleteShareEntry((String) request.getArgs()[0]);
                break;
            case WRITE_SHARE_ENTRY:
                doWriteShareEntry(request.getEntry());
                if (!request.getIsRollBackAction()) {
                    notification.add(request.getEntry());
                    template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.SHARE_ENTRY_TOPIC, notification);
                }
                break;
            case WRITE_RELEASE_ENTRY:
                doWriteReleaseEntry(request.getEntry());
                if (!request.getIsRollBackAction()) {
                    notification.add(request.getEntry());
                    template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.RELEASE_ENTRY_TOPIC, notification);
                }
                break;
            case GET_TRANSACTION_ENTRIES:
                result = doGetTransactionEntries();
                break;
            case TAKE_RELEASE_ENTRY:
                result =  doTakeReleaseEntry();
                break;
            case WRITE_TRANSACTION_ENTRY:
                doWriteTransactionEntry(request.getEntry());
                if (!request.getIsRollBackAction()) {
                    notification.add(request.getEntry());
                    template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.TRANSACTION_ENTRY_TOPIC, notification);
                }
                break;
        }

        logger.info("DID CALL:" + request.getMethod());
        return result;
    }

    /**
     * Do get investor depot entry.
     *
     * @param investorId the investor id
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetInvestorDepotEntry(String investorId) {
        Query<DepotEntry> q = equal(CQAttributes.DEPOT_ID, investorId);
        return store.retrieve(DepotEntry.class, q, null, 1);
    }

    /**
     * Do take investor depot entry.
     *
     * @param investorId the investor id
     * @return the array list
     */
    private ArrayList<SuperEntry> doTakeInvestorDepotEntry(String investorId) {

        ArrayList<SuperEntry> result = doGetInvestorDepotEntry(investorId);
        if (!result.isEmpty())
            doDeleteInvestorDepotEntry(((DepotEntry)result.get(0)).getId());

        return result;
    }

    /**
     * Do delete investor depot entry.
     *
     * @param investorId the investor id
     */
    private void doDeleteInvestorDepotEntry(String investorId) {
        Query<DepotEntry> q = equal(CQAttributes.DEPOT_ID, investorId);
        ArrayList<SuperEntry> result = store.retrieve(DepotEntry.class, q, null, 1);

        for (Object object : result)
            store.delete(DepotEntry.class, object);
    }

    /**
     * Do write investor depot entry.
     *
     * @param investorDepotEntry the investor depot entry
     */
    private void doWriteInvestorDepotEntry(SuperEntry investorDepotEntry) {
        store.add(DepotEntry.class, investorDepotEntry);
    }

    /**
     * Do get order entries.
     *
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetOrderEntries(){
        return store.retrieve(OrderEntry.class, null, null, Integer.MAX_VALUE);
    }

    /**
     * Do take order by order id.
     *
     * @param orderId the order id
     * @return the array list
     */
    private ArrayList<SuperEntry> doTakeOrderByOrderId(UUID orderId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_ORDER_ID, orderId);
        ArrayList<SuperEntry> result = store.retrieve(OrderEntry.class, q, null, 1);

        if (!result.isEmpty())
            doDeleteOrderEntry(((OrderEntry)result.get(0)).getOrderID());

        return result;
    }

    /**
     * Do take order by properties.
     *
     * @param shareId the share id
     * @param type the type
     * @param status the status
     * @param prioritized the prioritized
     * @param price the price
     * @return the array list
     */
    private ArrayList<SuperEntry> doTakeOrderByProperties(String shareId, OrderType type, OrderStatus status, Boolean prioritized, Double price) {

        SimpleQuery subQuery = lessThanOrEqualTo(CQAttributes.ORDER_LIMIT, price);
        if (type.equals(OrderType.BUY)) {
            subQuery = greaterThanOrEqualTo(CQAttributes.ORDER_LIMIT, price);
        }

        Query<OrderEntry> q = and(equal(CQAttributes.ORDER_SHARE_ID, shareId),
                equal(CQAttributes.ORDER_TYPE, type),
                equal(CQAttributes.ORDER_STATUS, status),
                equal(CQAttributes.ORDER_PRIORITIZED, prioritized),
                subQuery);

        ArrayList<SuperEntry> result = store.retrieve(OrderEntry.class, q, true, 1);

        for (Object object : result)
            store.delete(OrderEntry.class, object);

        return result;
    }

    /**
     * Do read order by properties.
     *
     * @param shareId the share id
     * @param type the type
     * @param status the status
     * @return the array list
     */
    private ArrayList<SuperEntry> doReadOrderByProperties(String shareId, OrderType type, OrderStatus status) {
        Query<OrderEntry> q = and(equal(CQAttributes.ORDER_SHARE_ID, shareId),
                                equal(CQAttributes.ORDER_TYPE, type),
                                equal(CQAttributes.ORDER_STATUS, status));


        return store.retrieve(OrderEntry.class, q, null, Integer.MAX_VALUE);
    }

    /**
     * Do get orders by investor id.
     *
     * @param investorId the investor id
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetOrdersByInvestorId(String investorId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_INVESTOR_ID, investorId);
        return store.retrieve(OrderEntry.class, q, null, Integer.MAX_VALUE);
    };

    /**
     * Do write order entry.
     *
     * @param orderEntry the order entry
     */
    private void doWriteOrderEntry(SuperEntry orderEntry) {
        store.add(OrderEntry.class, orderEntry);
    }

    /**
     * Do delete order entry.
     *
     * @param orderId the order id
     */
    private void doDeleteOrderEntry(UUID orderId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_ORDER_ID, orderId);
        ArrayList<SuperEntry> result = store.retrieve(OrderEntry.class, q, null, 1);
        for (Object object : result)
            store.delete(OrderEntry.class, object);
    }

    /**
     * Do get share entry.
     *
     * @param shareId the share id
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetShareEntry(String shareId) {
        Query<ShareEntry> q = equal(CQAttributes.SHARE_SHARE_ID, shareId);
        return store.retrieve(ShareEntry.class, q, null, 1);
    }

    /**
     * Do get share entries.
     *
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetShareEntries() {
        return store.retrieve(ShareEntry.class, null, null, Integer.MAX_VALUE);
    }

    /**
     * Do write release entry.
     *
     * @param releaseEntry the release entry
     */
    private void doWriteReleaseEntry(SuperEntry releaseEntry) {
        store.add(ReleaseEntry.class, releaseEntry);
    }

    /**
     * Do take release entry.
     *
     * @return the array list
     */
    private ArrayList<SuperEntry> doTakeReleaseEntry() {
        ArrayList<SuperEntry> result =  store.retrieve(ReleaseEntry.class, null, null, 1);
        if (!result.isEmpty())
            store.delete(ReleaseEntry.class, result.get(0));

        return result;
    }

    /**
     * Do delete share entry.
     *
     * @param shareId the share id
     */
    private void doDeleteShareEntry(String shareId) {
        Query<ShareEntry> q = equal(CQAttributes.SHARE_SHARE_ID, shareId);
        ArrayList<SuperEntry> result = store.retrieve(ShareEntry.class, q, null, 1);

        for (Object object : result)
            store.delete(ShareEntry.class, object);
    }

    /**
     * Do get transaction entries.
     *
     * @return the array list
     */
    private ArrayList<SuperEntry> doGetTransactionEntries(){
        return store.retrieve(TransactionEntry.class, null, null, Integer.MAX_VALUE);
    }

    /**
     * Do write share entry.
     *
     * @param shareEntry the share entry
     */
    private void doWriteShareEntry(SuperEntry shareEntry) {
        store.add(ShareEntry.class, shareEntry);
    }

    /**
     * Do write transaction entry.
     *
     * @param transactionEntry the transaction entry
     */
    private void doWriteTransactionEntry(SuperEntry transactionEntry) {
        store.add(TransactionEntry.class, transactionEntry);
    }
}
