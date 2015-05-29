package ac.at.tuwien.sbc.broker.workflow.space;

import ac.at.tuwien.sbc.broker.workflow.ICoordinationService;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;
import org.mozartspaces.capi3.*;
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
import static org.mozartspaces.capi3.Matchmakers.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 28/03/15.
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

    /** The release container. */
    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference releaseContainer;

    /** The share container. */
    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    /** The order container. */
    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    /** The investor depot container. */
    @Autowired
    @Qualifier("depotContainer")
    ContainerReference depotContainer;

    /** The transaction container. */
    @Autowired
    @Qualifier("transactionContainer")
    ContainerReference transactionContainer;

    /** The notifications. */
    private ArrayList<Notification> notifications = new ArrayList<Notification>();



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);


    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getDepot(java.lang.Integer, java.lang.Object)
     */
    @Override
    public DepotEntry getDepot(String depotId, Object sharedTransaction) {
        logger.info("Try to read investor with arguments: " + String.valueOf(depotId));
        TransactionReference tx = (TransactionReference)sharedTransaction;
        ArrayList<DepotEntry> entries = null;
        DepotEntry entry = null;
        try {
            entries = capi.take(depotContainer, KeyCoordinator.newSelector(depotId.toString()), MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + depotId);
        }

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#setDepot(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry, java.lang.Object, java.lang.Boolean)
     */
    @Override
    public void setDepot(DepotEntry de, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {
        logger.info("Try to write InvestorDepotEntry: " + de.getBudget().toString());
        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {
            try {
                capi.take(depotContainer, KeyCoordinator.newSelector(de.getId().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e1) {}
            Entry entryToUpdate = new Entry(de, KeyCoordinator.newCoordinationData(de.getId().toString()));
            capi.write(depotContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);
        }
        catch (MzsCoreException e) {
            throw new CoordinationServiceException("Could not update InvestorDepotEntry");
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#addOrder(ac.at.tuwien.sbc.domain.entry.OrderEntry, java.lang.Object, java.lang.Boolean)
     */
    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction, Boolean isRollbackAction) throws CoordinationServiceException {

        TransactionReference tx = null;
        if (sharedTransaction != null)
             tx = (TransactionReference)sharedTransaction;

        try {
            logger.info("Try to write OrderEntry: " + oe.getShareID());
            //QueryCoordinator.newCoordinationData())
            capi.write(new Entry(oe), orderContainer, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order:" + e.getMessage());
            throw new CoordinationServiceException("Could not update OrderEntry");
        }
    }


    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getOrderByProperties(java.lang.String, ac.at.tuwien.sbc.domain.enums.OrderType, ac.at.tuwien.sbc.domain.enums.OrderStatus, java.lang.Double, java.lang.Object)
     */
    @Override
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Boolean prioritized, Double price, Object sharedTransaction) {
        TransactionReference tx = (TransactionReference)sharedTransaction;
        ArrayList<OrderEntry> entries = null;

        Matchmaker mShareId = Property.forName("shareID").equalTo(shareId);
        Matchmaker mType = Property.forName("type").equalTo(type);
        Matchmaker mStatus = Property.forName("status").equalTo(status);
        Matchmaker mPrioritized = Property.forName("prioritized").equalTo(prioritized);
        Matchmaker mLimit = ComparableProperty.forName("limit").lessThanOrEqualTo(price);
        if (type.equals(OrderType.BUY))
            mLimit = ComparableProperty.forName("limit").greaterThanOrEqualTo(price);

        Query q = new Query().filter(and(mShareId, mType, mStatus,mPrioritized, mLimit)).cnt(1);
        OrderEntry entry = null;
        try {

            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(QueryCoordinator.newSelector(q, MzsConstants.Selecting.COUNT_ALL));
            selectors.add(RandomCoordinator.newSelector(1));

            entries = capi.take(orderContainer,
                    selectors,
                    MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {logger.info("Try to get order by properties FAILED:" + e.getMessage());}

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#readShares()
     */
    @Override
    public ArrayList<ShareEntry> readShares() {

        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.read(shareContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return entries;
    }


    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getReleaseEntry(java.lang.Object)
     */
    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ReleaseEntry re = null;
        try {
            ArrayList<ReleaseEntry> entries = null;
            entries = capi.take(releaseContainer, FifoCoordinator.newSelector(), MzsConstants.RequestTimeout.ZERO, tx);

            if (entries != null && !entries.isEmpty())
                re = entries.get(0);

        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
        }

        return re;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#getShareEntry(java.lang.String, java.lang.Object)
     */
    @Override
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction) {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ShareEntry se = null;
        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.read(shareContainer, KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, tx);
        }
        catch (MzsCoreException e) {
            logger.info("Share not found for: " + shareId);
        }
        if (entries != null && !entries.isEmpty())
            se = entries.get(0);

        return se;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#setShareEntry(ac.at.tuwien.sbc.domain.entry.ShareEntry, java.lang.Object)
     */
    @Override
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {

            try {
                capi.take(shareContainer, KeyCoordinator.newSelector(se.getShareID()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(se, KeyCoordinator.newCoordinationData(se.getShareID()));
            capi.write(shareContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);
        }
        catch (MzsCoreException e) {
            throw new CoordinationServiceException("Something went wrong writing a ShareEntry");
        }
    }



    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerReleaseNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerReleaseNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(releaseContainer,
                    new ReleaseNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerOrderNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerOrderNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(orderContainer,
                    new OrderNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#registerShareNotification(ac.at.tuwien.sbc.domain.event.CoordinationListener)
     */
    @Override
    public void registerShareNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(shareContainer,
                    new ShareNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#addTransaction(ac.at.tuwien.sbc.domain.entry.TransactionEntry, java.lang.Object)
     */
    @Override
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException {
        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {
            capi.write(new Entry(te), transactionContainer, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing a TransactionEntry");
            throw new CoordinationServiceException("Could not write a TransactionEntry");
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#createTransaction(long)
     */
    @Override
    public Object createTransaction(long timeout) {


        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(timeout, new URI(CommonSpaceConfiguration.SPACE_URI));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tx;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#commitTransaction(java.lang.Object)
     */
    @Override
    public void commitTransaction(Object sharedTransaction) {

        if (sharedTransaction != null) {

            TransactionReference tx = (TransactionReference)sharedTransaction;
            try {
                capi.commitTransaction(tx);
            } catch (MzsCoreException e) {
                logger.info("Transaction failed at some point");
            }
            sharedTransaction = null;
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.broker.workflow.ICoordinationService#rollbackTransaction(java.lang.Object)
     */
    @Override
    public void rollbackTransaction(Object sharedTransaction) {
        if (sharedTransaction != null) {

            TransactionReference tx = (TransactionReference)sharedTransaction;
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e) {
                logger.info("Transaction failed at some point");
            }

            sharedTransaction = null;
        }
    }

    /**
     * InvestorDepotNotificationListener.
     *
     *
     */
    public class ReleaseNotificationListener implements NotificationListener {

        /** The callback listener. */
        private CoordinationListener<ArrayList<ReleaseEntry>> callbackListener;

        /**
         * Instantiates a new release notification listener.
         *
         * @param callbackListener the callback listener
         */
        public ReleaseNotificationListener(CoordinationListener<ArrayList<ReleaseEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        /* (non-Javadoc)
         * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
         */
        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<ReleaseEntry> releaseEntries = new ArrayList<ReleaseEntry>();
            for (Serializable entry : entries) {
                releaseEntries.add((ReleaseEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(releaseEntries);
        }
    }

    /**
     * OrderNotificationListener.
     *
     *
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
     * The listener interface for receiving shareNotification events.
     * The class that is interested in processing a shareNotification
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addShareNotificationListener<code> method. When
     * the shareNotification event occurs, that object's appropriate
     * method is invoked.
     *
     *
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
