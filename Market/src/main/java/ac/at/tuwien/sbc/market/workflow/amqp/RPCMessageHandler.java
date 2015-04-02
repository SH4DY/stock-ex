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
        switch (request.getMethod()) {
            case GET_INVESTOR_DEPOT_ENTRY_BY_ID:
                result = getInvestorDepotEntry((Integer) request.getArgs()[0]);
                break;
            case DELETE_INVESTOR_DEPOT_ENTRY_BY_ID:
                deleteInvestorDepotEntry((Integer)request.getArgs()[0]);
                break;
            case WRITE_INVESTOR_DEPOT_ENTRY:
                writeInvestorDepotEntry(request.getEntry());
                break;

        }

        logger.info("DID CALL:" + request.getMethod());
        return result;
    }

    private ArrayList<SuperEntry> getInvestorDepotEntry(Integer investorId) {
        Query<InvestorDepotEntry> q = equal(CQAttributes.INVESTOR_INVESTOR_ID, investorId);
        return store.retrieve(InvestorDepotEntry.class, q, null, 1);
    }

    private void deleteInvestorDepotEntry(Integer investorId) {
        Query<InvestorDepotEntry> q = equal(CQAttributes.INVESTOR_INVESTOR_ID, investorId);
        ArrayList<SuperEntry> result = store.retrieve(InvestorDepotEntry.class, q, null, 1);

        for (Object object : result)
            store.delete(InvestorDepotEntry.class, object);
    }

    private void writeInvestorDepotEntry(SuperEntry investorDepotEntry) {
        store.add(InvestorDepotEntry.class, investorDepotEntry);
        ArrayList<SuperEntry> notificationList = new ArrayList<>();
        notificationList.add(investorDepotEntry);
        template.convertAndSend(CommonRabbitConfiguration.FANOUT_EXCHANGE, CommonRabbitConfiguration.INVESTOR_ENTRY_TOPIC);
    }


}
