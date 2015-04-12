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

/**
 *  * Like {@link ac.at.tuwien.sbc.market.workflow.amqp.AmqpPublisherService}
 * this layer offers methods to retrieve data from the underlying platform
 * (in this case a MozartSpace)
 * Created by shady on 28/03/15.
 */
@Service
@Profile("space")
public class SpacePublisherService implements IMarketPublisherService {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    @Autowired
    @Qualifier("transactionContainer")
    ContainerReference transactionContainer;

    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    IMarketServiceListener listener;

    private static final Logger logger = LoggerFactory.getLogger(SpacePublisherService.class);


    public void registerListener(IMarketServiceListener listener){
        this.listener = listener;
    }

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

    private class OrderAddedListener implements org.mozartspaces.notifications.NotificationListener{
        CoordinationListener<OrderEntry> listener;

        public OrderAddedListener(CoordinationListener<OrderEntry> listener) {
            this.listener = listener;
        }

        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                    listener.onResult((OrderEntry)((Entry)entry).getValue());
            }
        }
    }

    private class TransactionAddedListener implements org.mozartspaces.notifications.NotificationListener{
        CoordinationListener<TransactionEntry> listener;

        public TransactionAddedListener(CoordinationListener<TransactionEntry> listener) {
            this.listener = listener;
        }

        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                listener.onResult((TransactionEntry)((Entry)entry).getValue());
            }
        }
    }

    private class ShareAddedListener implements org.mozartspaces.notifications.NotificationListener{
        CoordinationListener<ShareEntry> listener;

        public ShareAddedListener(CoordinationListener<ShareEntry> listener) {
            this.listener = listener;
        }

        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                listener.onResult((ShareEntry)((Entry)entry).getValue());
            }
        }
    }
}
