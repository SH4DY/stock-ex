package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.market.store.MarketStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<Object> handleMessage(RPCMessageRequest request) {

        ArrayList<Object> result = null;
        switch (request.getOp()) {
            case READ:
                result = doRead(request);
                break;
            case WRITE:
                doWrite(request);
                break;
            case TAKE:
                result = doTake(request);
                break;
            case DELETE:
                doDelete(request);
                break;
        }

        return result;
    }

    private ArrayList<Object> doRead(RPCMessageRequest request) {
        return store.retrieve(request.getClazz(), request.getQuery(), request.getShuffle(), request.getNumResults());
    }

    private void doWrite(RPCMessageRequest request) {
        store.add(request.getClazz(), request.getObject());
        //notify
        ArrayList<Object> notificationList = new ArrayList<>();
        notificationList.add(request.getObject());
        template.convertAndSend(CommonRabbitConfiguration.FANOUT_EXCHANGE, topicMap.get(request.getClass()), notificationList);
    }

    private  ArrayList<Object> doTake(RPCMessageRequest request) {
        ArrayList<Object> result = store.retrieve(request.getClazz(), request.getQuery(), request.getShuffle(), request.getNumResults());

        for (Object object : result)
            store.delete(request.getClazz(), object);

        return result;
    }

    private void doDelete(RPCMessageRequest request) {
        store.delete(request.getClazz(), request.getObject());
    }
}
