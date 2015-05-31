package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Like {@link ac.at.tuwien.sbc.market.workflow.space.SpacePublisherService}
 * this layer offers methods to retrieve data from the underlying platform
 * (in this case an AMQP server)
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpPublisherService implements IMarketPublisherService {

    /** The routing key. */
    private final String routingKey = CommonRabbitConfiguration.MARKET_RPC;

    @Autowired
    @Qualifier("exchangeKey")
    private String exchangeKey;

    /** The template. */
    @Autowired
    private RabbitTemplate template;

    /** The order entry notification listener. */
    private CoordinationListener<OrderEntry> orderEntryNotificationListener;

    /** The transaction entry notification listener. */
    private CoordinationListener<TransactionEntry> transactionEntryNotificationListener;

    /** The share entry notification listener. */
    private CoordinationListener<ShareEntry> shareEntryNotificationListener;



    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getShares(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getShares(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        List<ShareEntry> shares = (List<ShareEntry>) template.convertSendAndReceive(exchangeKey, routingKey, request);

        if(shares != null) listener.onResult(shares);
        else listener.onResult(new ArrayList<ShareEntry>());
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getOrders(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getOrders(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_ORDER_ENTRIES, null);
        List<OrderEntry> orders = (List<OrderEntry>) template.convertSendAndReceive(exchangeKey, routingKey, request);

        if(orders != null) listener.onResult(orders);
        else listener.onResult(new ArrayList<OrderEntry>());
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getTransactions(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getTransactions(CoordinationListener listener) {
        RPCMessageRequest request = null;

        request = new RPCMessageRequest(RPCMessageRequest.Method.GET_TRANSACTION_ENTRIES, null);
        List<TransactionEntry> transactions = (List<TransactionEntry>) template.convertSendAndReceive(exchangeKey, routingKey, request);

        if(transactions != null) listener.onResult(transactions);
        else listener.onResult(new ArrayList<TransactionEntry>());
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerShareObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareObserver(CoordinationListener<ShareEntry> coordinationListener) {
        this.shareEntryNotificationListener = coordinationListener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerOrderObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {
        this.orderEntryNotificationListener = coordinationListener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerTransactionObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener) {
        this.transactionEntryNotificationListener = coordinationListener;
    }

    /**
     * On share entry notification.
     *
     * @param list the list
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        if (shareEntryNotificationListener != null) {
            for(ShareEntry entry : list) {
                shareEntryNotificationListener.onResult(entry);
            }
        }
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        if (orderEntryNotificationListener != null) {
            for(OrderEntry entry : list) {
                orderEntryNotificationListener.onResult(entry);
            }
        }
    }

    /**
     * On transaction entry notification.
     *
     * @param list the list
     */
    public void onTransactionEntryNotification(ArrayList<TransactionEntry> list) {
        if (transactionEntryNotificationListener != null) {
            for(TransactionEntry entry : list) {
                transactionEntryNotificationListener.onResult(entry);
            }
        }
    }
}
