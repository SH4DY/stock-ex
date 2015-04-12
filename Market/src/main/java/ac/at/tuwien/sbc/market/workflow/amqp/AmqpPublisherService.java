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
 * Like {@link ac.at.tuwien.sbc.market.workflow.space.SpacePublisherService}
 * this layer offers methods to retrieve data from the underlying platform
 * (in this case an AMQP server)
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpPublisherService implements IMarketPublisherService {

    private final String routingKey = "marketRPC";

    @Autowired
    private RabbitTemplate template;

    private CoordinationListener<OrderEntry> orderEntryNotificationListener;

    private CoordinationListener<TransactionEntry> transactionEntryNotificationListener;

    private CoordinationListener<ShareEntry> shareEntryNotificationListener;


    @Override
    public void getShares(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        List<ShareEntry> shares = (List<ShareEntry>) template.convertSendAndReceive(routingKey, request);

        if(shares != null) listener.onResult(shares);
        else listener.onResult(new ArrayList<ShareEntry>());
    }

    @Override
    public void getOrders(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_ORDER_ENTRIES, null);
        List<OrderEntry> orders = (List<OrderEntry>) template.convertSendAndReceive(routingKey, request);

        if(orders != null) listener.onResult(orders);
        else listener.onResult(new ArrayList<OrderEntry>());
    }

    @Override
    public void getTransactions(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_TRANSACTION_ENTRIES, null);
        List<TransactionEntry> transactions = (List<TransactionEntry>) template.convertSendAndReceive(routingKey, request);

        if(transactions != null) listener.onResult(transactions);
        else listener.onResult(new ArrayList<TransactionEntry>());
    }

    @Override
    public void registerShareObserver(CoordinationListener<ShareEntry> coordinationListener) {
        this.shareEntryNotificationListener = coordinationListener;
    }

    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {
        this.orderEntryNotificationListener = coordinationListener;
    }

    @Override
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener) {
        this.transactionEntryNotificationListener = coordinationListener;
    }

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        if (shareEntryNotificationListener != null) {
            for(ShareEntry entry : list) {
                shareEntryNotificationListener.onResult(entry);
            }
        }
    }

    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        if (orderEntryNotificationListener != null) {
            for(OrderEntry entry : list) {
                orderEntryNotificationListener.onResult(entry);
            }
        }
    }

    public void onTransactionEntryNotification(ArrayList<TransactionEntry> list) {
        if (transactionEntryNotificationListener != null) {
            for(TransactionEntry entry : list) {
                transactionEntryNotificationListener.onResult(entry);
            }
        }
    }
}
