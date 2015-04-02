package ac.at.tuwien.sbc.investor.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.investor.workflow.ICoordinationService;
import ac.at.tuwien.sbc.investor.workflow.ICoordinationServiceListener;
import com.googlecode.cqengine.query.Query;
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


    @PostConstruct
    public void onPostConstruct() {
        logger.info("I'M YOUR AmqpCoordinationService");
    }

    @Override
    public void setListener(ICoordinationServiceListener listener) {

    }

    @Override
    public void getInvestor(Integer investorId, CoordinationListener cListener) {

        logger.info("BEGIN GET");
        InvestorDepotEntry entry = null;
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{investorId});
        ArrayList<InvestorDepotEntry> result = (ArrayList<InvestorDepotEntry>)template.convertSendAndReceive("marketRPC", request);
        if (result != null && !result.isEmpty())
            entry = (InvestorDepotEntry)result.toArray()[0];

        logger.info("END GET");
        cListener.onResult(entry);
    }

    @Override
    public void getShares(ArrayList<String> shareIds, CoordinationListener cListener) {

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

        logger.info("BEGIN DELETE");
        //delete investor
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_INVESTOR_DEPOT_ENTRY_BY_ID, new Object[]{ide.getInvestorID()});
        template.convertAndSend("marketRPC", request);
        logger.info("END DELETE");
        //write investor
        logger.info("BEGIN SET");
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_INVESTOR_DEPOT_ENTRY, null, ide);
        template.convertAndSend("marketRPC", request);

        logger.info("END SET");
    }

    @Override
    public void addOrder(OrderEntry oe) {

    }

    @Override
    public void getOrders(Integer investorId, CoordinationListener cListener) {

    }

    @Override
    public void deleteOrder(UUID orderID) {

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
