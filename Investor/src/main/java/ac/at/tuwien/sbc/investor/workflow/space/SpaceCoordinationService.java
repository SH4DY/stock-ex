package ac.at.tuwien.sbc.investor.workflow.space;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
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
    @Qualifier("investorDepotContainer")
    ContainerReference investorDepotContainer;

    /** The order container. */
    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    /** The share container. */
    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    /** The notifications. */
    private ArrayList<Notification> notifications = new ArrayList<Notification>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getInvestor(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getInvestor(Integer id, CoordinationListener cListener) {
        logger.info("Try to read investor with arguments: " + String.valueOf(id));

        ArrayList<InvestorDepotEntry> entries = null;
        try {
            entries = capi.read(investorDepotContainer, KeyCoordinator.newSelector(id.toString()), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + id);
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
    public void getShares(ArrayList<String> shareIds, CoordinationListener cListener) {

        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        for (String shareId : shareIds) {
            try {
                ArrayList<ShareEntry> currentEntries = capi.read(shareContainer, KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, null);

                if (currentEntries != null && !currentEntries.isEmpty())
                    entries.add(currentEntries.get(0));
            } catch (MzsCoreException e) {
                logger.info("Share not found for: " + shareId);
            }
        }
        cListener.onResult(entries);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#registerInvestorNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerInvestorNotification(CoordinationListener cListener) {

        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(investorDepotContainer,
                    new InvestorDepotNotificationListener(cListener),
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
    public void registerOrderNotification(CoordinationListener clistener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(orderContainer,
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
    public void registerShareNotification(CoordinationListener clistener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(shareContainer,
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
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#setInvestor(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry)
     */
    @Override
    public void setInvestor(InvestorDepotEntry ide) {

        logger.info("Try to write InvestorDepotEntry: " + ide.getBudget().toString());
        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(1000, investorDepotContainer.getSpace());

            try {
                capi.take(investorDepotContainer, KeyCoordinator.newSelector(ide.getInvestorID().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(ide, KeyCoordinator.newCoordinationData(ide.getInvestorID().toString()));
            capi.write(investorDepotContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);

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
    public void addOrder(OrderEntry oe) {
        try {
            capi.write(new Entry(oe), orderContainer, MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order");
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.ICoordinationService#getOrders(java.lang.Integer, ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void getOrders(Integer investorId, CoordinationListener cListener) {

        logger.info("Try to read orders by template");
        OrderEntry template = new OrderEntry(null, investorId, null, null, null, null, null, null, null);
        ArrayList<OrderEntry> entries = null;
        try {
            entries = capi.read(orderContainer, LindaCoordinator.newSelector(template, MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
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
    public void deleteOrder(UUID orderID) {

        OrderEntry template = new OrderEntry(orderID, null, null, null, null, null, null, null, null);
        try {
            logger.info("Try to delete order:" + orderID.toString());
            ArrayList<OrderEntry> orderEntries = capi.take(orderContainer, LindaCoordinator.newSelector(template, 1), MzsConstants.RequestTimeout.TRY_ONCE, null);

            if (orderEntries != null && !orderEntries.isEmpty()) {
                orderEntries.get(0).setStatus(OrderStatus.DELETED);
                capi.write(new Entry(orderEntries.get(0)), orderContainer, MzsConstants.RequestTimeout.ZERO, null);
            }
        }
        catch (MzsCoreException e) {logger.info("Not able to delete order:" + e.getMessage());}
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
     * @see InvestorDepotNotificationEvent
     */
    public class InvestorDepotNotificationListener implements NotificationListener {

        /** The callback listener. */
        private CoordinationListener<ArrayList<InvestorDepotEntry>> callbackListener;

        /**
         * Instantiates a new investor depot notification listener.
         *
         * @param callbackListener the callback listener
         */
        public InvestorDepotNotificationListener(CoordinationListener<ArrayList<InvestorDepotEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<InvestorDepotEntry> investorDeptEntries = new ArrayList<InvestorDepotEntry>();
            for (Serializable entry : entries) {
                investorDeptEntries.add((InvestorDepotEntry)((Entry)entry).getValue());

            }
            callbackListener.onResult(investorDeptEntries);
        }
    }

    /**
     * InvestorDepotNotificationListener.
     *
     * @see OrderNotificationEvent
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
     * @see ShareNotificationEvent
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
