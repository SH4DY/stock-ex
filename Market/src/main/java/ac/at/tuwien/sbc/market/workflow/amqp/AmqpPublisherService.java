package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpPublisherService implements IMarketPublisherService {

    private final String routingKey = "marketRPC";

    @Autowired
    private RabbitTemplate template;

    private CoordinationListener<ShareEntry> shareEntryNotificationListener;

    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {

    }

    @Override
    public void getOrders(CoordinationListener listener) {

    }

    @Override
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener) {

    }

    @Override
    public void getTransactions(CoordinationListener listener) {

    }

    @Override
    public void registerShareObserver(CoordinationListener<ShareEntry> coordinationListener) {
        this.shareEntryNotificationListener = coordinationListener;
    }

    @Override
    public void getShares(CoordinationListener listener) {
        RPCMessageRequest request = null;
        List<ShareEntry> e = new ArrayList<>();

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        List<ShareEntry> shares = (List<ShareEntry>) template.convertSendAndReceive(routingKey, request);

        if(shares != null) listener.onResult(shares);
        else listener.onResult(new ArrayList<ShareEntry>());
    }

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        if (shareEntryNotificationListener != null) {
            for(ShareEntry entry : list) {
                shareEntryNotificationListener.onResult(entry);
            }
        }
    }
}
