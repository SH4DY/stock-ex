package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This Service acts as the controller layer for the model layer.
 * It enables decoupling from underlying layers (space-based, message-based...)
 * by implementing an interface and using an underlying service which gets
 * auto-injected depending on the active profile.
 */
@Service
public class Workflow implements IMarketServiceListener {

    /** The market publisher service. */
    @Autowired
    IMarketPublisherService marketPublisherService;

    /** The observer. */
    @Autowired
    IMarketObserver observer;

    /**
     * 1. Supply the GUI with the initial existing orders (getAllOrders())
     * 2 .Connect the GUI to the profile-specific backend (setup...Notification())
     */
    @PostConstruct
    private void postConstruct(){
        getAllOrders();
        setupOrderNotification();

        getAllTransactions();
        setupTransactionNotifiction();

        getAllShares();
        setupShareNotification();
    }

    /**
     * Setup order notification.
     */
    private void setupOrderNotification() {
        marketPublisherService.registerOrderObserver(new CoordinationListener<OrderEntry>(){

            @Override
            public void onResult(OrderEntry orderEntry) {
                if(observer != null){
                    observer.onOrderAdded(orderEntry);
                }
            }
        });
    }

    /**
     * Gets the all orders.
     *
     * @return the all orders
     */
    private void getAllOrders(){
        marketPublisherService.getOrders(new CoordinationListener<List<OrderEntry>>() {
            @Override
            public void onResult(List<OrderEntry> entries) {
                if(entries != null){
                    for(OrderEntry entry : entries){
                        if(observer != null){
                            observer.onOrderAdded(entry);
                        }
                    }
                }
            }
        });
    }


    /**
     * Setup transaction notifiction.
     */
    private void setupTransactionNotifiction(){
        marketPublisherService.registerTransactionObserver(new CoordinationListener<TransactionEntry>() {
            @Override
            public void onResult(TransactionEntry transactionEntry) {
                if(observer != null) {
                    observer.onTransactionAdded(transactionEntry);
                }
            }
        });
    }

    /**
     * Gets the all transactions.
     *
     * @return the all transactions
     */
    private void getAllTransactions(){
        marketPublisherService.getTransactions(new CoordinationListener<List<TransactionEntry>>() {
            @Override
            public void onResult(List<TransactionEntry> entries) {
                if(entries != null){
                    for(TransactionEntry entry : entries){
                        if(observer != null){
                            observer.onTransactionAdded(entry);
                        }
                    }
                }
            }
        });
    }

    /**
     * Setup share notification.
     */
    private void setupShareNotification(){
        marketPublisherService.registerShareObserver(new CoordinationListener<ShareEntry>() {
            @Override
            public void onResult(ShareEntry shareEntry) {
                if (observer != null) {
                    observer.onShareAdded(shareEntry);
                }
            }
        });
    }

    /**
     * Gets the all shares.
     *
     * @return the all shares
     */
    private void getAllShares(){
        marketPublisherService.getShares(new CoordinationListener<List<ShareEntry>>() {
            @Override
            public void onResult(List<ShareEntry> entries) {
                if (entries != null) {
                    for (ShareEntry entry : entries) {
                        if (observer != null) {
                            observer.onShareAdded(entry);
                        }
                    }
                }
            }
        });
    }

}
