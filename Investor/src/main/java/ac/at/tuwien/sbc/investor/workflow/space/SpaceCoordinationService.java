package ac.at.tuwien.sbc.investor.workflow.space;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.investor.workflow.ICoordinationService;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    /** The core. */
    @Autowired
    MzsCore core;

    /** The capi. */
    @Autowired
    Capi capi;

    /** The investor depot container. */
    @Autowired
    @Qualifier("depotContainerMap")
    HashMap<String, ContainerReference> depotContainer;

    /** The order container. */
    @Autowired
    @Qualifier("orderContainerMap")
    HashMap<String, ContainerReference> orderContainer;

    /** The share container. */
    @Autowired
    @Qualifier("shareContainerMap")
    HashMap<String, ContainerReference> shareContainer;

    @Autowired
    @Qualifier("releaseContainerMap")
    HashMap<String, ContainerReference> releaseContainer;

    /** The notifications. */
    private ArrayList<Notification> notifications = new ArrayList<Notification>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getDepot(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getDepot(CoordinationListener cListener, String market, String depotId) {
        logger.info("Try to read investor with arguments: " + String.valueOf(depotId));

        ArrayList<DepotEntry> entries = null;
        try {
            entries = capi.read(depotContainer.get(market), KeyCoordinator.newSelector(depotId.toString()), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + depotId);
        }

        if (entries != null && !entries.isEmpty())
            //listener.onGetInvestor(entries.get(0));
            cListener.onResult(entries.get(0));
        else {
            cListener.onResult(null);
        }

    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getShares(java.util.ArrayList, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getShares(ArrayList<String> shareIds, String market, CoordinationListener cListener) {

        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        for (String shareId : shareIds) {
            try {
                ArrayList<ShareEntry> currentEntries = capi.read(shareContainer.get(market), KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, null);

                if (currentEntries != null && !currentEntries.isEmpty())
                    entries.add(currentEntries.get(0));
            } catch (MzsCoreException e) {
                logger.info("Share not found for: " + shareId);
            }
        }
        cListener.onResult(entries);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerDepotNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerDepotNotification(CoordinationListener cListener, String market) {

        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(depotContainer.get(market),
                    new DepotNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerOrderNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderNotification(CoordinationListener clistener, String market) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(orderContainer.get(market),
                    new OrderNotificationListener(clistener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerShareNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareNotification(CoordinationListener clistener, String market) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(shareContainer.get(market),
                    new ShareNotificationListener(clistener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#setDepot(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry)
     */
    @Override
    public void setDepot(DepotEntry de, String market) {

        logger.info("Try to write InvestorDepotEntry: " + de.getBudget().toString());
        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(1000, depotContainer.get(market).getSpace());

            try {
                capi.take(depotContainer.get(market), KeyCoordinator.newSelector(de.getId().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(de, KeyCoordinator.newCoordinationData(de.getId().toString()));
            capi.write(depotContainer.get(market), MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);

            capi.commitTransaction(tx);
        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            logger.info("Something went wrong writing a InvestorDepotEntry");
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#addOrder(ac.at.tuwien.sbc.domain.entry.OrderEntry)
     */
    @Override
    public void addOrder(OrderEntry oe, String market) {
        try {
            capi.write(new Entry(oe), orderContainer.get(market), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order");
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getOrders(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getOrders(String depotId, String market, CoordinationListener cListener) {

        logger.info("Try to read orders by template");
        OrderEntry template = new OrderEntry(null, depotId, null, null, null, null, null, null, null);
        ArrayList<OrderEntry> entries = null;
        try {
            entries = capi.read(orderContainer.get(market), LindaCoordinator.newSelector(template, MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
        }

        if (entries != null && !entries.isEmpty())
            //listener.onGetInvestor(entries.get(0));
            cListener.onResult(entries);
        else {
            cListener.onResult(null);
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#deleteOrder(java.util.UUID)
     */
    @Override
    public void deleteOrder(UUID orderID, String market) {

        OrderEntry template = new OrderEntry(orderID, null, null, null, null, null, null, null, null);
        try {
            logger.info("Try to delete order:" + orderID.toString());
            ArrayList<OrderEntry> orderEntries = capi.take(orderContainer.get(market), LindaCoordinator.newSelector(template, 1), MzsConstants.RequestTimeout.TRY_ONCE, null);

            if (orderEntries != null && !orderEntries.isEmpty()) {
                orderEntries.get(0).setStatus(OrderStatus.DELETED);
                capi.write(new Entry(orderEntries.get(0)), orderContainer.get(market), MzsConstants.RequestTimeout.ZERO, null);
            }
        }
        catch (MzsCoreException e) {logger.info("Not able to delete order:" + e.getMessage());}
    }

    @Override
    public void makeRelease(ReleaseEntry re, String market) {
        Entry entry = new Entry(re);
        try {
            capi.write(releaseContainer.get(market), MzsConstants.RequestTimeout.TRY_ONCE,null,entry);
        } catch (MzsCoreException e) {
            logger.error("Exception occurred while trying to write ReleaseEntry to container");
        }
    }

    private ArrayList<ContainerReference> getContainersAsList(HashMap<String, ContainerReference> map) {
        ArrayList<ContainerReference> list = new ArrayList<>();
        for (ContainerReference market : map.values()) {
           list.add(market);
        }
        return list;
    }

    /**
     * On pre destroy.
     */
    @PreDestroy
    public void onPreDestroy() {

        //destroy notifications
        for (Notification n : notifications) {
            try {
                n.destroy();
            } catch (MzsCoreException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * InvestorDepotNotificationListener.
     *
     * @see
     */
    public class DepotNotificationListener implements NotificationListener {

        /** The callback listener. */
        private CoordinationListener<ArrayList<DepotEntry>> callbackListener;

        /**
         * Instantiates a new investor depot notification listener.
         *
         * @param callbackListener the callback listener
         */
        public DepotNotificationListener(CoordinationListener<ArrayList<DepotEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<DepotEntry> depotEntries = new ArrayList<DepotEntry>();
            for (Serializable entry : entries) {
                depotEntries.add((DepotEntry)((Entry)entry).getValue());

            }
            callbackListener.onResult(depotEntries);
        }
    }

    /**
     * InvestorDepotNotificationListener.
     *
     * @see
     */
    public class OrderNotificationListener implements NotificationListener {

        /** The callback listener. */
        private CoordinationListener<ArrayList<OrderEntry>> callbackListener;

        /**
         * Instantiates a new order notification listener.
         *
         * @param callbackListener the callback listener
         */
        public OrderNotificationListener(CoordinationListener<ArrayList<OrderEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<OrderEntry> orderEntries = new ArrayList<OrderEntry>();
            for (Serializable entry : entries) {
                orderEntries.add((OrderEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(orderEntries);
        }
    }

    /**
     * InvestorDepotNotificationListener.
     *
     * @see
     */
    public class ShareNotificationListener implements NotificationListener {

        /** The callback listener. */
        private CoordinationListener<ArrayList<ShareEntry>> callbackListener;

        /**
         * Instantiates a new share notification listener.
         *
         * @param callbackListener the callback listener
         */
        public ShareNotificationListener(CoordinationListener<ArrayList<ShareEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<ShareEntry> shareEntries = new ArrayList<ShareEntry>();
            for (Serializable entry : entries) {
                shareEntries.add((ShareEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(shareEntries);
        }
    }
}
