package ac.at.tuwien.sbc.market.workflow.space;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import ac.at.tuwien.sbc.market.workflow.IMarketServiceListener;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 *  * Like {@link ac.at.tuwien.sbc.market.workflow.amqp.AmqpPublisherService}
 * this layer offers methods to retrieve data from the underlying platform
 * (in this case a MozartSpace)
 * Created by shady on 28/03/15.
 */
@Service
@Profile("space")
public class SpacePublisherService implements IMarketPublisherService {

    /** The capi. */
    @Autowired
    Capi capi;

    /** The core. */
    @Autowired
    MzsCore core;

    /** The order container. */
    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    /** The transaction container. */
    @Autowired
    @Qualifier("transactionContainer")
    ContainerReference transactionContainer;

    /** The share container. */
    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    /** The listener. */
    IMarketServiceListener listener;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpacePublisherService.class);


    /**
     * Register listener.
     *
     * @param listener the listener
     */
    public void registerListener(IMarketServiceListener listener){
        this.listener = listener;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerOrderObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {
        NotificationManager nm = new NotificationManager(core);
        Notification not = null;
        try {
            not = nm.createNotification(orderContainer,
                    new OrderAddedListener(coordinationListener),
                    Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO what to do with these notifications?
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getOrders(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getOrders(CoordinationListener listener){
        List<OrderEntry> readEntries = null;
        OrderEntry template = new OrderEntry(null, null, null, null, null, null, null, null);
        try {
            readEntries = capi.read(orderContainer, LindaCoordinator.newSelector(template,MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while retrieving orders");
        }

        if(readEntries != null && !readEntries.isEmpty()){
            listener.onResult(readEntries);
        }
        else{
            listener.onResult(null);
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerTransactionObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener) {
        NotificationManager nm = new NotificationManager(core);
        Notification not = null;
        try {
            not = nm.createNotification(transactionContainer,
                    new TransactionAddedListener(coordinationListener),
                    Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getTransactions(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getTransactions(CoordinationListener listener) {
        List<TransactionEntry> readEntries = null;
        try {
            readEntries = capi.read(transactionContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while retrieving transactions");
        }

        if(readEntries != null && !readEntries.isEmpty()){
            listener.onResult(readEntries);
        }
        else{
            listener.onResult(null);
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#registerShareObserver(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareObserver(CoordinationListener<ShareEntry> coordinationListener) {
        NotificationManager nm = new NotificationManager(core);
        Notification not = null;
        try {
            not = nm.createNotification(shareContainer,
                    new ShareAddedListener(coordinationListener),
                    Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketPublisherService#getShares(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getShares(CoordinationListener listener) {
        List<ShareEntry> readEntries = null;
        try {
            readEntries = capi.read(shareContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while retrieving shares");
        }

        if(readEntries != null && !readEntries.isEmpty()){
            listener.onResult(readEntries);
        }
        else{
            listener.onResult(null);
        }
    }

    /**
     * The listener interface for receiving orderAdded events.
     * The class that is interested in processing a orderAdded
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOrderAddedListener<code> method. When
     * the orderAdded event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OrderAddedEvent
     */
    private class OrderAddedListener implements org.mozartspaces.notifications.NotificationListener{
        
        /** The listener. */
        CoordinationListener<OrderEntry> listener;

        /**
         * Instantiates a new order added listener.
         *
         * @param listener the listener
         */
        public OrderAddedListener(CoordinationListener<OrderEntry> listener) {
            this.listener = listener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                    listener.onResult((OrderEntry)((Entry)entry).getValue());
            }
        }
    }

    /**
     * The listener interface for receiving transactionAdded events.
     * The class that is interested in processing a transactionAdded
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addTransactionAddedListener<code> method. When
     * the transactionAdded event occurs, that object's appropriate
     * method is invoked.
     *
     * @see TransactionAddedEvent
     */
    private class TransactionAddedListener implements org.mozartspaces.notifications.NotificationListener{
        
        /** The listener. */
        CoordinationListener<TransactionEntry> listener;

        /**
         * Instantiates a new transaction added listener.
         *
         * @param listener the listener
         */
        public TransactionAddedListener(CoordinationListener<TransactionEntry> listener) {
            this.listener = listener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                listener.onResult((TransactionEntry)((Entry)entry).getValue());
            }
        }
    }

    /**
     * The listener interface for receiving shareAdded events.
     * The class that is interested in processing a shareAdded
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addShareAddedListener<code> method. When
     * the shareAdded event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ShareAddedEvent
     */
    private class ShareAddedListener implements org.mozartspaces.notifications.NotificationListener{
        
        /** The listener. */
        CoordinationListener<ShareEntry> listener;

        /**
         * Instantiates a new share added listener.
         *
         * @param listener the listener
         */
        public ShareAddedListener(CoordinationListener<ShareEntry> listener) {
            this.listener = listener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                listener.onResult((ShareEntry)((Entry)entry).getValue());
            }
        }
    }
}
