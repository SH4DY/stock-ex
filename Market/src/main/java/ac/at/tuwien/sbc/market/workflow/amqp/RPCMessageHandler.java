package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.market.store.MarketStore;
import com.googlecode.cqengine.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class RPCMessageHandler {

    @Autowired
    private MarketStore store;

    @Autowired
    private RabbitTemplate template;

    private HashMap<Class, String> topicMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(RPCMessageHandler.class);

    public RPCMessageHandler() {
        topicMap.put(InvestorDepotEntry.class, CommonRabbitConfiguration.INVESTOR_ENTRY_TOPIC);
        topicMap.put(ShareEntry.class, CommonRabbitConfiguration.SHARE_ENTRY_TOPIC);
        topicMap.put(OrderEntry.class, CommonRabbitConfiguration.ORDER_ENTRY_TOPIC);
        topicMap.put(TransactionEntry.class, CommonRabbitConfiguration.TRANSACTION_ENTRY_TOPIC);
    }

    public List<SuperEntry> handleMessage(RPCMessageRequest request) {

        logger.info("RECEIVED CALL:" + request.getMethod());
        //ArrayList<Object> result = null;
        List<SuperEntry> result = null;
        List<SuperEntry> notification = new ArrayList<SuperEntry>();
        switch (request.getMethod()) {
            case GET_INVESTOR_DEPOT_ENTRY_BY_ID:
                result = doGetInvestorDepotEntry((Integer)request.getArgs()[0]);
                break;
            case TAKE_INVESTOR_DEPOT_ENTRY_BY_ID:
                result = doTakeInvestorDepotEntry((Integer)request.getArgs()[0]);
                break;
            case DELETE_INVESTOR_DEPOT_ENTRY_BY_ID:
                doDeleteInvestorDepotEntry((Integer)request.getArgs()[0]);
                break;
            case WRITE_INVESTOR_DEPOT_ENTRY:
                doWriteInvestorDepotEntry(request.getEntry());
                notification.add(request.getEntry());
                template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.INVESTOR_ENTRY_TOPIC, notification);
                break;
            case TAKE_ORDER_BY_ORDER_ID:
                return doTakeOrderByOrderId((UUID)request.getArgs()[0]);
            case GET_ORDER_ENTRIES_BY_INVESTOR_ID:
                result = doGetOrdersByInvestorId((Integer)request.getArgs()[0]);
                break;
            case WRITE_ORDER_ENTRY:
                doWriteOrderEntry(request.getEntry());
                notification.add(request.getEntry());
                template.convertAndSend(CommonRabbitConfiguration.TOPIC_EXCHANGE, CommonRabbitConfiguration.ORDER_ENTRY_TOPIC, notification);
                break;
            case DELETE_ORDER_ENTRY_BY_ID:
                doDeleteOrderEntry((UUID)request.getArgs()[0]);
                break;
            case GET_SHARE_ENTRY_BY_ID:
                result = doGetShareEntry((String)request.getArgs()[0]);
                break;

        }

        logger.info("DID CALL:" + request.getMethod());
        return result;
    }

    private ArrayList<SuperEntry> doGetInvestorDepotEntry(Integer investorId) {
        Query<InvestorDepotEntry> q = equal(CQAttributes.INVESTOR_INVESTOR_ID, investorId);
        return store.retrieve(InvestorDepotEntry.class, q, null, 1);
    }

    private ArrayList<SuperEntry> doTakeInvestorDepotEntry(Integer investorId) {

        ArrayList<SuperEntry> result = doGetInvestorDepotEntry(investorId);
        if (!result.isEmpty())
            doDeleteInvestorDepotEntry(((InvestorDepotEntry)result.get(0)).getInvestorID());

        return result;
    }

    private void doDeleteInvestorDepotEntry(Integer investorId) {
        Query<InvestorDepotEntry> q = equal(CQAttributes.INVESTOR_INVESTOR_ID, investorId);
        ArrayList<SuperEntry> result = store.retrieve(InvestorDepotEntry.class, q, null, 1);

        for (Object object : result)
            store.delete(InvestorDepotEntry.class, object);
    }

    private void doWriteInvestorDepotEntry(SuperEntry investorDepotEntry) {
        store.add(InvestorDepotEntry.class, investorDepotEntry);
    }

    private ArrayList<SuperEntry> doTakeOrderByOrderId(UUID orderId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_ORDER_ID, orderId);
        ArrayList<SuperEntry> result = store.retrieve(OrderEntry.class, q, null, 1);

        if (!result.isEmpty())
            doDeleteOrderEntry(((OrderEntry)result.get(0)).getOrderID());

        return result;
    }

    private ArrayList<SuperEntry> doGetOrdersByInvestorId(Integer investorId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_INVESTOR_ID, investorId);
        return store.retrieve(OrderEntry.class, q, null, Integer.MAX_VALUE);
    };

    private void doWriteOrderEntry(SuperEntry orderEntry) {
        store.add(OrderEntry.class, orderEntry);
    }

    private void doDeleteOrderEntry(UUID orderId) {
        Query<OrderEntry> q = equal(CQAttributes.ORDER_ORDER_ID, orderId);
        ArrayList<SuperEntry> result = store.retrieve(OrderEntry.class, q, null, 1);

        for (Object object : result)
            store.delete(InvestorDepotEntry.class, object);
    }

    private ArrayList<SuperEntry> doGetShareEntry(String shareId) {
        Query<ShareEntry> q = equal(CQAttributes.SHARE_SHARE_ID, shareId);
        return store.retrieve(ShareEntry.class, q, null, 1);
    }


}
